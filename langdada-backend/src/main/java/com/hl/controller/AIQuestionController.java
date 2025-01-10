package com.hl.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hl.common.BaseResponse;
import com.hl.common.ErrorCode;
import com.hl.common.ResultUtils;
import com.hl.constant.UserConstant;
import com.hl.exception.ThrowUtils;
import com.hl.manager.AiManager;
import com.hl.model.dto.question.AiGenerateQuestionRequest;
import com.hl.model.dto.question.QuestionFrameWork;
import com.hl.model.entity.App;
import com.hl.model.enums.AppTypeEnum;
import com.hl.service.AppService;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/question/ai")
@Slf4j
public class AIQuestionController {

    @Resource
    private AppService appService;
    @Resource
    private AiManager aiManager;

    @PostMapping("/generate")
    public BaseResponse<List<QuestionFrameWork>> generate(@RequestBody AiGenerateQuestionRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        Long appId = request.getAppId();
        int questionNumber = request.getQuestionNumber();
        int optionNumber = request.getOptionNumber();

        // 校验应用是否存在
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null || app.getIsDelete() == 1, ErrorCode.PARAMS_ERROR, "应用不存在，无法创建或编辑问题");
        // 封装Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        // AI生成题目
        String result = aiManager.doSyncRequest(UserConstant.GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        // 截取需要的 JSON 信息
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        String json = result.substring(start, end + 1);
        List<QuestionFrameWork> questionContentDTOList = JSONUtil.toList(json, QuestionFrameWork.class);
        return ResultUtils.success(questionContentDTOList);
    }

    @GetMapping("/generate/sse")
    public SseEmitter generateSSE(AiGenerateQuestionRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 0L不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        Long appId = request.getAppId();
        int questionNumber = request.getQuestionNumber();
        int optionNumber = request.getOptionNumber();

        // 校验应用是否存在
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null || app.getIsDelete() == 1, ErrorCode.PARAMS_ERROR, "应用不存在，无法创建或编辑问题");
        // 封装Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        // AI生成题目
        Flowable<ModelData> flowable = aiManager.doRequestSSE(UserConstant.GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
        // 拼接完整题目
        StringBuilder stringBuilder = new StringBuilder();
        flowable.observeOn(Schedulers.io())
                .map(modelData ->modelData.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s",""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    List<Character> characters = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characters.add(c);
                    }
                    return Flowable.fromIterable(characters);
                })
                .doOnNext(item -> {
                    //  从第一个 { 开始拼接
                    if(item == '{'){
                        counter.incrementAndGet();
                    }
                    if(counter.get() > 0){
                        stringBuilder.append(item);
                    }
                    if(item == '}'){
                        counter.decrementAndGet();
                        // 当左括号等于右括号时，表示一个完整的 JSON 数据，可以发送给前端
                        if (counter.get() == 0) {
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                            // 清空 stringBuilder
                            stringBuilder.setLength(0);
                        }
                    }
                })
                .doOnError((e) -> log.error("sse error", e))
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }

    /**
     * 封装用户Prompt
     *
     * @param app            应用
     * @param questionNumber 问题数
     * @param optionNumber   选项数
     * @return 用户Prompt
     */
    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        return app.getAppName() + "\n" +
                app.getAppDesc() + "\n" +
                Objects.requireNonNull(AppTypeEnum.getEnumByType(app.getAppType())).getValue() + "\n" +
                questionNumber + "\n" +
                optionNumber;
    }
}

package com.hl.manager;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AiManager {

    @Resource
    private ClientV4 clientV4;

    // 较稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * 同步调用（答案较稳定）
     *
     * @param systemMessage 系统信息
     * @param userMessage  用户信息
     * @return 返回消息
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doSyncRequest(systemMessage, userMessage, STABLE_TEMPERATURE);
    }

    /**
     * 同步调用（答案较随机）
     *
     * @param systemMessage 系统信息
     * @param userMessage 用户信息
     * @return 返回消息
     */
    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doSyncRequest(systemMessage, userMessage, UNSTABLE_TEMPERATURE);
    }


    /**
     * 同步请求
     * @param systemMessage 系统信息
     * @param userMessage 用户信息
     * @param temperature 采样温度，控制输出的随机性，必须为正数取值范围是：[0.0, 1.0]，默认值为0.95。
     * @return 返回消息
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, temperature);
    }


    /**
     * 通用请求，构建消息
     * @param systemMessage 系统信息
     * @param userMessage 用户信息
     * @param temperature 采样温度，控制输出的随机性，必须为正数取值范围是：[0.0, 1.0]，默认值为0.95。
     * @return 返回消息
     */
    public String doRequest(String systemMessage, String userMessage, Float temperature) {
        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(systemChatMessage);
        messages.add(userChatMessage);
        return doRequest(messages, temperature);
    }


    /**
     * 通用请求，调用api，同步调用
     * @param messages 消息
     * @param temperature 采样温度，控制输出的随机性，必须为正数取值范围是：[0.0, 1.0]，默认值为0.95。
     * @return 返回消息
     */
    public String doRequest(List<ChatMessage> messages, Float temperature) {
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(false)
                .invokeMethod(Constants.invokeMethod)
                .temperature(temperature)
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        ChatMessage result = invokeModelApiResp.getData().getChoices().get(0).getMessage();
        return result.getContent().toString();
    }

    /**
     * 通用流式请求，构建消息
     * @param systemMessage 系统信息
     * @param userMessage 用户信息
     * @param temperature 采样温度，控制输出的随机性，必须为正数取值范围是：[0.0, 1.0]，默认值为0.95。
     * @return 返回消息
     */
    public Flowable<ModelData> doRequestSSE(String systemMessage, String userMessage, Float temperature) {
        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(systemChatMessage);
        messages.add(userChatMessage);
        return doRequestSSE(messages, temperature);
    }

    /**
     * 通用请求，调用api,流式调用
     * @param messages 消息
     * @param temperature 采样温度，控制输出的随机性，必须为正数取值范围是：[0.0, 1.0]，默认值为0.95。
     * @return 返回消息
     */
    public Flowable<ModelData> doRequestSSE(List<ChatMessage> messages, Float temperature) {
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(true)
                .invokeMethod(Constants.invokeMethod)
                .temperature(temperature)
                .messages(messages)
                .build();
        ModelApiResponse sseModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        return sseModelApiResp.getFlowable();
    }
}

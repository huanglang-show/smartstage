package com.hl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hl.mapper.QuestionMapper;
import com.hl.model.entity.Question;
import com.hl.service.QuestionService;
import org.springframework.stereotype.Service;

/**
* @author HL
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-10-16 14:24:40
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}





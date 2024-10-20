package com.hl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hl.mapper.UserAnswerMapper;
import com.hl.model.entity.UserAnswer;
import com.hl.service.UserAnswerService;
import org.springframework.stereotype.Service;

/**
* @author HL
* @description 针对表【user_answer(用户答题记录)】的数据库操作Service实现
* @createDate 2024-10-16 14:24:51
*/
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer>
    implements UserAnswerService{

}





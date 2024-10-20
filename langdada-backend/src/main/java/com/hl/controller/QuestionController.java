package com.hl.controller;

import com.hl.service.QuestionService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
@Api("题目管理")
public class QuestionController {

    private QuestionService questionService;
}

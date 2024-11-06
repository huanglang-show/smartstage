package com.hl.scoring;

import com.hl.model.entity.App;
import com.hl.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评分策略
 */
@Service
public interface ScoringStrategy {

    UserAnswer doScore(List<String> choices, App app);
}

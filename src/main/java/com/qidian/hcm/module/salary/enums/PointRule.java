package com.qidian.hcm.module.salary.enums;

import lombok.Getter;

import java.math.RoundingMode;

@Getter
public enum PointRule {

    round(RoundingMode.CEILING),//四舍五入
    floor(RoundingMode.FLOOR);//直接保留

    private RoundingMode roundingMode;

    PointRule(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
}

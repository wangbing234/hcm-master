package com.qidian.hcm.module.authorization.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lyn
 * @date 2018/9/11 15 07
 */
@Getter
@Setter
public class ConditionDTO implements Serializable {
    private String field;
    private String op;
    private String value;
    private String extraValue;
}

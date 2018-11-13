package com.qidian.hcm.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author lyn
 * @date 2018/8/29 15 59
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum YesNo {
    NO,
    YES;

    @JsonValue
    public Integer toValue() {
        return this.ordinal();
    }
}

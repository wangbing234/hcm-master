package com.qidian.hcm.module.custom.service;

import com.qidian.hcm.module.custom.entity.CustomizedField;

/**
 * @author lyn
 * @date 2018/7/31 17 12
 */
public interface FieldRowMapper<T> {
    T mapRow(CustomizedField entity);
}

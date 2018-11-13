package com.qidian.hcm.module.custom.service;

import com.qidian.hcm.module.custom.entity.CustomizedField;

/**
 * @author lyn
 * @date 2018/7/31 13 54
 */
public interface CustomizedFieldConverter<T> {

    CustomizedField convert(T t);

}

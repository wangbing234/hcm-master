package com.qidian.hcm.module.custom.service;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.module.custom.entity.CustomizedField;
import com.qidian.hcm.module.custom.enums.TargetType;
import com.qidian.hcm.module.custom.request.OrgCustomizedFieldRequest;
import com.qidian.hcm.module.custom.response.OrgCustomizedFieldResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomizedFieldDefinitionService extends CustomizedFieldService
        implements CustomizedFieldConverter<OrgCustomizedFieldRequest>, FieldRowMapper<OrgCustomizedFieldResponse> {

    /**
     * 保存自定义字段
     *
     * @param request 请求参数
     */
    public void save(OrgCustomizedFieldRequest request) {
        super.save(request, this);
    }

    /**
     * 查找自定义字段
     *
     * @param targetTypes 目标类型
     */
    public List<OrgCustomizedFieldResponse> findByTargetTypes(List<TargetType> targetTypes) {
        return super.findByTargetTypes(targetTypes, this);
    }

    /**
     * 查找自定义字段
     *
     * @param targetTypes 目标类型
     * @param enable      启用禁用状态
     */
    public List<OrgCustomizedFieldResponse> findByTargetTypes(List<TargetType> targetTypes, YesNo enable) {
        return super.findByTargetTypes(targetTypes, enable, this);
    }

    /**
     * 将前端请求参数对象转换为自定义字段对象
     *
     * @param request 前端请求参数
     * @return 自定义字段对象
     */
    @Override
    public CustomizedField convert(OrgCustomizedFieldRequest request) {
        CustomizedField entity = new CustomizedField();
        BeanUtils.copyProperties(request, entity);
        JSONObject attribute = new JSONObject();
        attribute.put("required", request.isRequired());
        attribute.put("label", request.getLabel());
        attribute.put("length", request.getLength());
        attribute.put("placeholder", request.getPlaceholder());
        attribute.put("options", request.getOptions());
        entity.setAttribute(attribute.toJSONString());
        if (entity.getId() == null) {
            entity.setEnable(YesNo.YES);
        }
        return entity;
    }

    /**
     * 将自定义字段实体对象映射成前端响应参数
     *
     * @param entity 自定义字段对象
     * @return 前端响应参数
     */
    @Override
    public OrgCustomizedFieldResponse mapRow(CustomizedField entity) {
        OrgCustomizedFieldResponse response = JSONObject.parseObject(entity.getAttribute(),
                OrgCustomizedFieldResponse.class);
        BeanUtils.copyProperties(entity, response);
        response.setActive(entity.getEnable());
        response.setFieldTypeName(response.getFieldType().getName());
        return response;
    }
}

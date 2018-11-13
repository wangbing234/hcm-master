package com.qidian.hcm.module.custom.repository;

import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.module.custom.entity.CustomizedField;
import com.qidian.hcm.module.custom.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author lyn
 * @date 2018/7/27 16 40
 */
public interface CustomizedFieldRespository extends JpaRepository<CustomizedField, Long> {

    List<CustomizedField> findByTargetTypeOrderByIdx(TargetType targetType);

    List<CustomizedField> findByTargetTypeAndTargetIdOrderByIdx(TargetType targetType, Long targetId);

    List<CustomizedField> findByTargetTypeAndTargetIdInOrderByIdx(TargetType targetType, List<Long> targetIds);


    List<CustomizedField> findByTargetTypeInOrderByIdx(List<TargetType> targetTypes);

    List<CustomizedField> findByTargetTypeInAndEnableOrderByIdx(List<TargetType> targetTypes, YesNo enable);

    CustomizedField findByTargetTypeAndCode(TargetType targetType, String code);


    void deleteCustomizedFieldByTargetId(Long targetId);

    @Modifying
    @Query(value = "DELETE  FROM CustomizedField  WHERE targetId=:targetId AND  id NOT IN :ids ")
    void deleteNotExistenceByIds(@Param("targetId")Long targetId, @Param("ids") List<Long> ids);
}

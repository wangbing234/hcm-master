package com.qidian.hcm.module.organization.repository;

import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.module.organization.entity.OrganizationEntity;
import com.qidian.hcm.module.organization.enums.OrganizationEnums;
import com.qidian.hcm.module.organization.response.OrganizationTreeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long>,
        JpaSpecificationExecutor<OrganizationEntity> {

    List<OrganizationEntity> findAllByParentIdAndEnable(long id, YesNo enable);

    List<OrganizationEntity> findAllByDeleted(YesNo deleted);

    @Query(value = "SELECT _d.* FROM organization _d " +
            "INNER JOIN position _p ON _p.department_id = _d.id " +
            "WHERE _p.id IN :ids AND _d.enable = 0", nativeQuery = true)
    List<OrganizationEntity> findAllDisabledDepartmentByPositionIdIn(@Param("ids") List<Long> ids);

    List<OrganizationEntity> findAllByParentIdAndEnableAndDeleted(Long parentId, YesNo enable, YesNo deleted);

    List<OrganizationEntity> findAllByParentIdInAndEnableAndDeleted(List<Long> parentIds, YesNo enable, YesNo deleted);

    List<OrganizationEntity> findAllByIdInAndEnableAndDeleted(Collection<Long> ids, YesNo enable, YesNo deleted);

    List<OrganizationEntity> findAllByPathLike(String path);

    Optional<OrganizationEntity> findByCodeAndType(String code, OrganizationEnums type);

    /**
     * 查找公司树形结构
     *
     * @return
     */
    @Query(value = "select new com.qidian.hcm.module.organization.response.OrganizationTreeResponse" +
            "(b.id,b.name,b.code,b.type,b.parentId,b.master) " +
            "from  OrganizationEntity b " +
            "where b.deleted=0 and  b.enable=1 order by b.parentId asc")
    List<OrganizationTreeResponse> findCompanyTree();

    /**
     * 查找公司树形结构
     *
     * @return
     */
    @Query(value = "select new com.qidian.hcm.module.organization.response.OrganizationTreeResponse" +
            "(b.id,b.name,b.code,b.type,b.parentId,b.master) " +
            "from  OrganizationEntity b " +
            "where b.deleted=0 and  b.enable=1 and b.type=:type order by b.parentId asc")
    List<OrganizationTreeResponse> findCompanyOrDepartmentTree(@Param("type") OrganizationEnums organizationEnums);

}

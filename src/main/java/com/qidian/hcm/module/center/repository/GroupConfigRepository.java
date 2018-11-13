package com.qidian.hcm.module.center.repository;

import com.qidian.hcm.module.center.entity.GroupConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupConfigRepository extends JpaRepository<GroupConfig, Long> {

    GroupConfig findByGroupId(Long groupId);

    GroupConfig findByTenantName(String tenentName);
}

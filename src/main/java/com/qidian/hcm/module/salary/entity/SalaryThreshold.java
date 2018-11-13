package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.salary.enums.ThresholdType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "salary_threshold")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class SalaryThreshold implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 税点
     */
    @Column(name = "point")
    private Integer point;

    /**
     * 中国公民个人所得税 chinese 外籍人个人所得税   foreign
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ThresholdType type;
}

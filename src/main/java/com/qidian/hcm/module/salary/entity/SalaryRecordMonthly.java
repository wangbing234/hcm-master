package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.common.enums.YesNo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 月薪资归档记录
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name = "salary_record_monthly")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class SalaryRecordMonthly implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 核算(归档)周期 yyyy-MM
     */
    @Column(name = "cycle")
    private String cycle;

    /**
     * 是否核算
     */
    @Column(name = "accounted")
    @Enumerated(EnumType.ORDINAL)
    private YesNo accounted;

    /**
     * 是否归档
     */
    @Column(name = "recorded")
    @Enumerated(EnumType.ORDINAL)
    private YesNo recorded;

    /**
     * 归档时间
     */
    @Column(name = "record_time")
    private Date recordTime;

    /**
     * 计薪周期开始时间
     */
    @Column(name = "cycle_start_time")
    private String cycleStartDate;

    /**
     * 计薪周期结束时间
     */
    @Column(name = "cycle_end_time")
    private String cycleEndDate;
}
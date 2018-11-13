package com.qidian.hcm.module.salary.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "employee_bank_info")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工银行卡信息数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeBankInfo implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 员工Id
     */
    @Column(name = "employee_id")
    private Long employeeId;
    /**
     * 卡号
     */
    @Column(name = "card_no")
    private String cardNo;
    /**
     * 开户行名称
     */
    @Column(name = "bank_name")
    private String bankName;
    /**
     * 开户行地址
     */
    @Column(name = "bank_address")
    private String bankAddress;

}
package com.qidian.hcm.module.salary.dto;

import com.qidian.hcm.common.utils.DateUtil;
import com.qidian.hcm.module.employee.enums.EmployeeStatus;
import com.qidian.hcm.module.employee.enums.EmployeeType;
import com.qidian.hcm.module.employee.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 外部薪资实体【公式配置调用】
 * 比如员工薪资：${employee.salary}
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
public class ExternalEmployeeDTO implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    /**
     * 员工编号
     */
    public String employeeNo = "";

    /**
     * 性别 male, female
     */
    public String gender = Gender.male.name();
    /**
     * 生日
     */
    public String birthday = "";

    /**
     * 入职日期
     */
    public Date hireDate;

    /**
     * 开始工作日期
     */
    public Date workDate;

    /**
     * 离职日期
     */
    public Date resignationDate;

    /**
     * 入职日期
     */
    public String hireDateStr = "";

    /**
     * 开始工作日期
     */
    public String workDateStr = "";

    /**
     * 离职日期
     */
    public String resignationDateStr = "";

    /**
     * 员工类型
     */
    public String type = EmployeeType.labor.name();
    /**
     * 员工状态：
     * 试用:probation,
     * 正式:formal,
     * 离职:former
     */
    public String status = EmployeeStatus.formal.name();

    /**
     * 薪酬
     */
    public Double salary = 0d;

    /**
     * 社保基数
     */
    public Double socialSecurityBase = 0d;

    /**
     * 公积金基数
     */
    public Double housingFundBase = 0d;

    /**
     * 当前循环周期
     */
    public String currentCycleMonth = "";

    /**
     * 公积金
     */
    public ExternalHousingFundPlanDTO housingFund = new ExternalHousingFundPlanDTO();

    /**
     * 社保
     */
    public ExternalSocialSecurityPlanDTO socialSecurity = new ExternalSocialSecurityPlanDTO();

    /**
     * 免征额
     */
    public ExternalThresholdDTO threshold = new ExternalThresholdDTO();

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
        this.hireDateStr = DateUtil.convertDateToStr(hireDate, DateUtil.DATE_FORMAT_YM);
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
        this.workDateStr = DateUtil.convertDateToStr(workDate, DateUtil.DATE_FORMAT_YM);
    }

    public void setResignationDate(Date resignationDate) {
        this.resignationDate = resignationDate;
        this.resignationDateStr = DateUtil.convertDateToStr(resignationDate, DateUtil.DATE_FORMAT_YM);
    }

    /**
     * 公积金是否在入离职临界点
     *
     * @return
     */
    public Boolean isMatchHousingFundPoint() {
        Date limitDate = getLimitPointDate(housingFund.getLimitPoint());
        return isMatchPoint(limitDate);
    }

    /**
     * 社保是否在入离职临界点
     */
    public Boolean isMatchSocialFundPoint() {
        Date limitDate = getLimitPointDate(socialSecurity.getLimitPoint());
        return isMatchPoint(limitDate);
    }

    private Boolean isMatchPoint(Date pointDate) {
        if (Objects.isNull(hireDate)) {
            return Boolean.FALSE;
        }
        //入职时间是否在临界点之前
        Boolean isBeforeHirePoint = compareDay(pointDate, hireDate);
        //不是离职状态并且
        if (!EmployeeStatus.former.name().equalsIgnoreCase(status)) {
            return isBeforeHirePoint;
        }
        //离职到比较
        return isBeforeHirePoint && compareDay(resignationDate, pointDate);
    }

    private Date getLimitPointDate(Integer limitPoint) {
        String housingPoint = this.currentCycleMonth + "-" + limitPoint;
        return DateUtil.parseDate(housingPoint, DateUtil.DATE_FORMAT_Y_M_D);
    }

    /**
     * 获取日期精确到天
     */
    private Boolean compareDay(Date start, Date end) {
        return new DateTime(start).withTimeAtStartOfDay().compareTo(new DateTime(end).withTimeAtStartOfDay()) > 0;
    }

}

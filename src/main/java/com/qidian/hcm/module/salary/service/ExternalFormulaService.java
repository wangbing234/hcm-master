package com.qidian.hcm.module.salary.service;

import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.utils.DateUtil;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.salary.entity.SalaryItem;
import com.qidian.hcm.module.salary.enums.SalarySettingEnum;
import com.qidian.hcm.module.salary.enums.SalaryType;
import com.qidian.hcm.module.salary.repository.SalaryItemRepository;
import com.qidian.hcm.module.salary.utils.SalaryItemsConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 公式服务类外部类【公式配置调用】
 * 比如获取税前加项公式：{formula.getTaxBeforeAdd()})
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class ExternalFormulaService {

    private static final String ZERO = "0";

    private static final char PLUS = '+';

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalarySettingService salarySettingService;

    public String getTaxBeforeAdd() {
        return findFormulaBySalaryType(SalaryType.taxBeforeAdd);
    }

    public String getTaxBeforeMinus() {
        return findFormulaBySalaryType(SalaryType.taxBeforeMinus);
    }

    public String getTaxAfterAdd() {
        return findFormulaBySalaryType(SalaryType.taxAfterAdd);
    }

    public String getTaxAfterMinus() {
        return findFormulaBySalaryType(SalaryType.taxAfterMinus);
    }

    public String getLaborCost() {
        return findFormulaBySalaryType(SalaryType.laborCost);
    }

    public String getExternal() {
        return findFormulaBySalaryType(SalaryType.external);
    }

    /**
     * 获取当月天数
     */
    public Number getMonthDays() {
        String currentCycleMonth = salarySettingService.getSalarySettingValue(SalarySettingEnum.CURRENT_CYCLE_MONTH);
        if (StringUtils.isBlank(currentCycleMonth)) {
            return 0;
        }
        return DateUtil.getMonthDays(currentCycleMonth);
    }

    /**
     * 获取本月工作天数
     * 【方便日期做扩展】
     */
    public Integer getWorkDays(Integer... days) {
        if (Objects.nonNull(days)) {
            return days[0];
        }
        return 21;
    }

    /**
     * 获取当前时间
     */
    public Date getCurrentDate() {
        return new Date();
    }


    /**
     * 格式化日期
     */
    public String formatDate(Date date,String format) {
        return DateUtil.convertDateToStr(date,format);
    }

    /**
     * 日期解析
     */
    public Date parseDate(String dateStr, String pattern) {
        return DateUtil.parseDate(dateStr,pattern);
    }

    /**
     * 日期减法 end-start
     * @param start
     * @param end
     * @param filed 年:1; 月:2; 周3; 日:4; 小时:5
     */
    public Integer minusDate(Date start, Date end, int filed) {
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(end);
        switch (filed) {
            case Calendar.YEAR://年
                return Years.yearsBetween(startTime, endTime).getYears();
            case Calendar.MONTH://月
                return Months.monthsBetween(startTime, endTime).getMonths();
            case Calendar.WEEK_OF_YEAR://周
                return Weeks.weeksBetween(startTime, endTime).getWeeks();
            case Calendar.WEDNESDAY:    //日
                return Days.daysBetween(startTime, endTime).getDays();
            case 5:    //小时
                return Hours.hoursBetween(startTime, endTime).getHours();
            default:
                throw new BizException(ResultCode.SALARY_ITEM_PARSE_ERROR);
        }
    }

    /**
     * +years
     */
    public Date plusYears(Date date, int num) {
        return new DateTime(date).plusYears(num).toDate();
    }

    /**
     * +months
     */
    public Date plusMonths(Date date, int num) {
        return new DateTime(date).plusMonths(num).toDate();
    }

    /**
     * +weeks
     */
    public Date plusWeeks(Date date, int num) {
        return new DateTime(date).plusWeeks(num).toDate();
    }


    /**
     * +days
     */
    public Date plusDays(Date date, int num) {
        return new DateTime(date).plusDays(num).toDate();
    }

    /**
     * +hours
     */
    public Date plusHours(Date date, int num) {
        return new DateTime(date).plusHours(num).toDate();
    }

    /**
     * 根据薪资类型获取薪资的
     * @param salaryType 薪资类型
     */
    private String findFormulaBySalaryType(SalaryType salaryType) {
        List<SalaryItem> salaryTypeList = salaryItemRepository.findByType(salaryType);
        if (CollectionUtils.isEmpty(salaryTypeList)) {
            return ZERO;
        }
        final String delimiter = SalaryItemsConst.SUFFIX + PLUS + SalaryItemsConst.PREFIX;
        String formula = salaryTypeList.stream().map(SalaryItem::getCode)
                .collect(Collectors.joining(delimiter, SalaryItemsConst.PREFIX, SalaryItemsConst.SUFFIX));
        return StringUtils.isNotBlank(formula) && formula.length() > 1 ? formula : ZERO;
    }

}

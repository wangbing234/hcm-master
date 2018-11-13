package com.qidian.hcm.module.salary.service;

import com.alibaba.fastjson.JSON;
import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.utils.DateUtil;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.repository.EmployeeRepository;
import com.qidian.hcm.module.salary.dto.*;
import com.qidian.hcm.module.salary.entity.*;
import com.qidian.hcm.module.salary.enums.PointRule;
import com.qidian.hcm.module.salary.enums.SalarySettingEnum;
import com.qidian.hcm.module.salary.repository.*;
import com.qidian.hcm.module.salary.response.SalaryItemMonthlyResponse;
import com.qidian.hcm.module.salary.utils.SalaryItemsConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl2.*;
import org.apache.commons.jexl2.parser.TokenMgrError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/**
 * 计算服务类
 *
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class CalculateService {

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeSalaryMonthlyRepository employeeSalaryMonthlyRepository;

    @Autowired
    private ExternalFormulaService externalFormulaService;

    @Autowired
    private SalarySettingService salarySettingService;

    @Autowired
    private SalaryThresholdRepository salaryThresholdRepository;

    @Autowired
    private HousingFundPlanRepository housingFundPlanRepository;

    @Autowired
    private SocialSecurityPlanRepository socialSecurityPlanRepository;

    @Autowired
    private EmployeeSalaryMonthlyService employeeSalaryMonthlyService;

    private final SecureRandom secureRandom = new SecureRandom();

    private final Pattern pattern = Pattern.compile(SalaryItemsConst.PARAM_PATTERN);

    private final ThreadLocal<JexlEngine> jexlEngine = new ThreadLocal<>();

    /**
     * 某些员工的所有薪资项（标记为记薪到调用）
     * @param employeeIds
     */
    @Transactional
    public void formulaCalculateByEmployee(List<Long> employeeIds) {
        Map<String, FormulaDTO> formulaRelation = getRelationFormulaDTOMap();
        //当前月份（薪资项不计算）
        Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMap = doFormulaCalculate(
                formulaRelation,employeeIds, null,null);
        saveMonthlyResultValue(salaryItemMap);
    }

    /**
     * 计算所有员薪资项（修改薪资项调用）
     * @param salaryItem
     */
    @Transactional
    public void formulaCalculateBySalaryItem(List<SalaryItem> salaryItem) {
        //当前月份（薪资项不计算）
        Map<String, FormulaDTO> formulaRelation = getRelationFormulaDTOMap();
        List<SalaryItem> salaryItemList = findParentSalaryItem(formulaRelation,
                salaryItem.stream().map(SalaryItem::getCode).collect(Collectors.toList()), Boolean.TRUE);
        if (!CollectionUtils.isEmpty(salaryItemList)) {
            Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMap = doFormulaCalculate(
                    formulaRelation, null, salaryItemList,null);
            saveMonthlyResultValue(salaryItemMap);
        }
    }

    /**
     * 计算单个员工薪资项
     * @param employeeId
     * @param salaryItem
     */
    @Transactional
    public void formulaCalculate(Long employeeId, List<SalaryItem> salaryItem, Boolean include) {
        //当前月份（薪资项不计算）
        Map<String, FormulaDTO> formulaRelation = getRelationFormulaDTOMap();
        List<SalaryItem> salaryItemList = findParentSalaryItem(formulaRelation,
                salaryItem.stream().map(SalaryItem::getCode).collect(Collectors.toList()), include);
        if (!CollectionUtils.isEmpty(salaryItemList)) {
            Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMap = doFormulaCalculate(
                    formulaRelation, newArrayList(employeeId), salaryItemList, null);
            saveMonthlyResultValue(salaryItemMap);
        }
    }

    /**
     * 计算所有员工次薪资项
     * @param likeStr 模糊查找到字符串
     */
    @Transactional
    public void formulaCalculate(List<Long> employeeIds, String likeStr) {
        //当前月份（薪资项不计算）
        Map<String, FormulaDTO> formulaRelation = getRelationFormulaDTOMap();
        List<SalaryItem> salaryItemList = findParentFormulaLike(formulaRelation, likeStr);
        if (!CollectionUtils.isEmpty(salaryItemList)) {
            Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMap = doFormulaCalculate(
                    formulaRelation, employeeIds, salaryItemList, null);
            saveMonthlyResultValue(salaryItemMap);
        }
    }

    /**
     * 计算所有薪资项
     */
    @Transactional
    public void formulaCalculate() {
        Map<String, FormulaDTO> formulaRelation = getRelationFormulaDTOMap();
        Map<Long, List<SalaryItemMonthlyResponse>> salaryMap = doFormulaCalculate(
                formulaRelation,null, null,null);
        saveMonthlyResultValue(salaryMap);
    }

    /**
     * 创建执行引擎
     */
    private void createThreadJexlEngine() {
        if (Objects.isNull(jexlEngine.get())) {
            jexlEngine.set(new JexlEngine());
        }
    }


    /**
     * 计算所有员薪资项
     * @param employeeId 员工id
     * @param salaryItem 薪资项
     */
    public List<SalaryItemMonthlyResponse> accountingCalculate(Long employeeId, SalaryItem salaryItem,Double value) {
        //当前月份（薪资项不计算）
        Map<String, FormulaDTO> formulaRelation = getRelationFormulaDTOMap();
        List<SalaryItem> parentSalaryItem = findParentSalaryItem(
                formulaRelation, newArrayList(salaryItem.getCode()), Boolean.FALSE);
        //如果没有被引用不需要计算
        if (CollectionUtils.isEmpty(parentSalaryItem)) {
            return Collections.EMPTY_LIST;
        }
        Map<String, Object> defaultValueMap = new HashMap<>();
        defaultValueMap.put(salaryItem.getCode(), value);
        return doFormulaCalculate(formulaRelation, newArrayList(employeeId), parentSalaryItem, defaultValueMap)
                .get(employeeId);
    }

    /**
     * 查找关联到薪资项
     * @param formulaRelation 公式关系map
     * @param likeStr         模糊查找到字符串
     */
    private List<SalaryItem> findParentFormulaLike(Map<String, FormulaDTO> formulaRelation, String likeStr) {
        List<SalaryItem> salaryItemList = salaryItemRepository.findByFormulaLike("%" + likeStr + "%");
        List<String> salaryItemCodes = salaryItemList.stream().map(SalaryItem::getCode).collect(Collectors.toList());
        return findParentSalaryItem(formulaRelation, salaryItemCodes, Boolean.TRUE);
    }

    /**
     * 查找关联到薪资项
     * @param formulaRelation 公式关系map
     * @param codeList        公式编码
     * @param include         是否包含自身
     */
    private List<SalaryItem> findParentSalaryItem(
            Map<String, FormulaDTO> formulaRelation, List<String> codeList, Boolean include) {
        List<String> resultCode = new ArrayList<>();
        codeList.forEach(code -> {
            List<String> parentPathCode = getParentPathCode(formulaRelation, code);
            resultCode.addAll(parentPathCode);
        });
        if (include) {
            resultCode.addAll(codeList);
        } else if (CollectionUtils.isEmpty(resultCode)) {
            return Collections.EMPTY_LIST;
        }
        return salaryItemRepository.findAllByCodeIn(resultCode);
    }

    /**
     * 保存所有薪资项
     */
    private void saveMonthlyResultValue(Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMonthlyMap) {
        //当前月份（薪资项不计算）
        String currentCycleMonth = salarySettingService.getSalarySettingValue(SalarySettingEnum.CURRENT_CYCLE_MONTH);

        Map<Long, EmployeeSalaryMonthly> employeeSalaryMonthlyMap = employeeSalaryMonthlyRepository
                .findByEmployeeIdInAndDate(salaryItemMonthlyMap.keySet(), currentCycleMonth).stream().collect(Collectors
                        .toMap(EmployeeSalaryMonthly::getEmployeeId, employeeSalaryMonthly -> employeeSalaryMonthly));

        List<EmployeeSalaryMonthly> employeeSalaryMonthlyList = new ArrayList<>();
        salaryItemMonthlyMap.forEach((employeeId, salaryItemMonthlyList) -> {
            EmployeeSalaryMonthly employeeSalaryMonthly = employeeSalaryMonthlyMap.get(employeeId);
            if (employeeSalaryMonthly == null) {
                employeeSalaryMonthly = new EmployeeSalaryMonthly();
                employeeSalaryMonthly.setEmployeeId(employeeId);
                employeeSalaryMonthly.setDate(currentCycleMonth);
                employeeSalaryMonthly.setInclude(employeeSalaryMonthlyService.isEmployeeSalaryInclude(employeeId));
            }
            List<SalaryItemMonthlyDTO> salaryItemMonthlyDTOList = JSON.parseArray(employeeSalaryMonthly
                    .getItemsResult(), SalaryItemMonthlyDTO.class);
            if (salaryItemMonthlyDTOList == null) {
                salaryItemMonthlyDTOList = new ArrayList<>();
            }
            for (SalaryItemMonthlyResponse salaryItemMonthlyResponse : salaryItemMonthlyList) {
                SalaryItemMonthlyDTO salaryItemMonthlyDTO = employeeSalaryMonthlyService
                        .getSalaryItemMonthlyDTO(salaryItemMonthlyDTOList, salaryItemMonthlyResponse.getId());
                BeanUtils.copyProperties(salaryItemMonthlyResponse, salaryItemMonthlyDTO);
            }
            employeeSalaryMonthly.setItemsResult(JSON.toJSONString(salaryItemMonthlyDTOList));
            employeeSalaryMonthlyList.add(employeeSalaryMonthly);
        });
        employeeSalaryMonthlyRepository.saveAll(employeeSalaryMonthlyList);
    }

    /**
     * 获取公式关系map结构
     */
    private Map<String, FormulaDTO> getRelationFormulaDTOMap() {
        createThreadJexlEngine();
        //构建公式编码对应表
        Map<String, FormulaDTO> formulaRelation = getFormulaDTOMap();
        buildFormulaRelation(formulaRelation);
        return formulaRelation;
    }

    /**
     * 计算所有员工薪资项核心逻辑
     * @param employeeIds 员工id集合
     * @param salaryItems 薪资项集合
     * @param defaultValueMap 薪资项默认值
     */
    private Map<Long, List<SalaryItemMonthlyResponse>> doFormulaCalculate(Map<String, FormulaDTO> formulaRelation,
            List<Long> employeeIds, List<SalaryItem> salaryItems,Map<String, Object> defaultValueMap) {
        createThreadJexlEngine();
        //获取需要计算的薪资项
        if (CollectionUtils.isEmpty(salaryItems)) {
            salaryItems = salaryItemRepository.findAllByInflow(Boolean.FALSE);
        }
        //获取需要的员工
        List<Employee> employeeList;
        if (CollectionUtils.isEmpty(employeeIds)) {
            employeeList = employeeRepository.findAll();
        } else {
            employeeList = employeeRepository.findAllById(employeeIds);
        }

        //获取输入字段考勤字段
        List<SalaryItem> salaryItemsInflow = salaryItemRepository.findAllByInflow(Boolean.TRUE);
        String currentCycleMonth = salarySettingService.getSalarySettingValue(SalarySettingEnum.CURRENT_CYCLE_MONTH);
        if (StringUtils.isBlank(currentCycleMonth)) {
            throw new BizException(ResultCode.SALARY_CURRENT_CYCLE_NOT_EXISTS);
        }
        //员工薪资项计算结果信息
        Map<Long, Map<String, Object>> employeeCodeValueMap = newHashMapWithExpectedSize(employeeList.size());
        //获取员工薪资项对应信息
        Map<Long, List<SalaryItemMonthlyDTO>> salaryMonthlyMap = getEmployeeMonthlyMap(employeeIds, currentCycleMonth);
        for (Employee employee : employeeList) {
            ExternalEmployeeDTO employeeInfoDTO = initEmployeeInfo(employee);
            employeeInfoDTO.setCurrentCycleMonth(currentCycleMonth);
            //中间结果缓存值，避免重复计算
            Map<String, Object> employeeCache = getSalaryItemsInflow(salaryItemsInflow,
                    salaryMonthlyMap.get(employee.getId()));
            if (!CollectionUtils.isEmpty(defaultValueMap)) {
                employeeCache.putAll(defaultValueMap);
            }
            for (SalaryItem salaryItem : salaryItems) {
                calculateOneEmployeeItems(formulaRelation, salaryItem, employeeInfoDTO, employeeCache);
            }
            employeeCodeValueMap.put(employee.getId(), employeeCache);
        }
        //排除考勤字段，考勤不用计算
        salaryItemsInflow.forEach(salaryItem -> employeeCodeValueMap.remove(salaryItem.getCode()));
        Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMonthlyMap = handleResult(employeeCodeValueMap);
        log.info(JSON.toJSONString(salaryItemMonthlyMap));
        return salaryItemMonthlyMap;
    }

    /**
     * 处理结果集数据
     * @param employeeCodeValueMap
     */
    private Map<Long, List<SalaryItemMonthlyResponse>> handleResult(
            Map<Long, Map<String, Object>> employeeCodeValueMap) {
        if (CollectionUtils.isEmpty(employeeCodeValueMap)) {
            return Collections.EMPTY_MAP;
        }
        List<SalaryItem> salaryItemList = null;
        Map<Long, List<SalaryItemMonthlyResponse>> salaryItemMonthlyMap
                = newHashMapWithExpectedSize(employeeCodeValueMap.size());
        Set<Map.Entry<Long, Map<String, Object>>> entrySet = employeeCodeValueMap.entrySet();
        for (Map.Entry<Long, Map<String, Object>> entry : entrySet) {
            if (salaryItemList == null) {
                salaryItemList = salaryItemRepository.findAllByCodeIn(entry.getValue().keySet());
            }
            List<SalaryItemMonthlyResponse> salaryItemMonthlyResponseList
                    = newArrayListWithExpectedSize(salaryItemList.size());
            salaryItemList.forEach(salaryItem -> {
                SalaryItemMonthlyResponse salaryItemMonthlyResponse = new SalaryItemMonthlyResponse();
                BeanUtils.copyProperties(salaryItem, salaryItemMonthlyResponse);
                Object value = entry.getValue().get(salaryItem.getCode());
                Double formatValue = formatDouble(value.toString(),
                        salaryItem.getPointScale(), salaryItem.getPointRule());
                salaryItemMonthlyResponse.setValue(formatValue);
                salaryItemMonthlyResponse.setModified(Boolean.FALSE);
                salaryItemMonthlyResponseList.add(salaryItemMonthlyResponse);
            });
            salaryItemMonthlyMap.put(entry.getKey(), salaryItemMonthlyResponseList);
        }
        return salaryItemMonthlyMap;
    }


    /**
     * 格式化结果数据
     */
    private static Double formatDouble(String value,Integer pointScale,PointRule pointRule) {
        BigDecimal bg = new BigDecimal(value).setScale(pointScale,pointRule.getRoundingMode());
        return bg.doubleValue();
    }

    /**
     * 初始化员工信息
     * @param employee 人事员工对象
     */
    private ExternalEmployeeDTO initEmployeeInfo(Employee employee) {
        ExternalEmployeeDTO employeeInfoDTO = new ExternalEmployeeDTO();
        BeanUtils.copyProperties(employee, employeeInfoDTO);
        EmployeeFinancial employeeFinancial = employee.getEmployeeFinancial();
        //设置默认财务信息
        if (Objects.isNull(employeeFinancial)) {
            employeeFinancial = new EmployeeFinancial();
            BeanUtils.copyProperties(employeeFinancial, employeeInfoDTO);
            return employeeInfoDTO;
        }
        //枚举拷贝
        employeeInfoDTO.setGender(employee.getGender() == null ? null : employee.getGender().name());
        employeeInfoDTO.setType(employee.getType() == null ? null : employee.getType().name());
        employeeInfoDTO.setStatus(employee.getStatus() == null ? null : employee.getStatus().name());

        BeanUtils.copyProperties(employeeFinancial, employeeInfoDTO);
        Long thresholdId = employeeFinancial.getThresholdId();
        if (Objects.nonNull(thresholdId)) {
            //设置免税额
            SalaryThreshold salaryThreshold = salaryThresholdRepository.findById(thresholdId)
                    .orElseThrow(() -> new BizException(ResultCode.SALARY_THRESHOLD_NOT_EXISTS));
            ExternalThresholdDTO externalThresholdDTO = employeeInfoDTO.getThreshold();
            BeanUtils.copyProperties(salaryThreshold, externalThresholdDTO);
            //枚举拷贝
            externalThresholdDTO.setType(salaryThreshold.getType().name());
        }
        Long housingFundPlanId = employeeFinancial.getHousingFundPlanId();
        if (Objects.nonNull(housingFundPlanId)) {
            //设置公积金
            HousingFundPlan housingFundPlan = housingFundPlanRepository.findById(housingFundPlanId)
                    .orElseThrow(() -> new BizException(ResultCode.EMPLOYEE_HOUSING_FUND_NOT_EXISTS));
            ExternalHousingFundPlanDTO externalHousingFundPlanDTO = employeeInfoDTO.getHousingFund();
            BeanUtils.copyProperties(housingFundPlan, externalHousingFundPlanDTO);
            //枚举拷贝
            externalHousingFundPlanDTO.setPointRule(housingFundPlan.getPointRule().name());
        }
        Long socialSecurityPlanId = employeeFinancial.getSocialSecurityPlanId();
        if (Objects.nonNull(socialSecurityPlanId)) {
            //设置社保
            SocialSecurityPlan socialSecurityPlan = socialSecurityPlanRepository.findById(socialSecurityPlanId)
                    .orElseThrow(() -> new BizException(ResultCode.EMPLOYEE_HOUSING_FUND_NOT_EXISTS));
            ExternalSocialSecurityPlanDTO externalSocialSecurityPlanDTO = employeeInfoDTO.getSocialSecurity();
            BeanUtils.copyProperties(socialSecurityPlan, externalSocialSecurityPlanDTO);
            //枚举拷贝
            externalSocialSecurityPlanDTO.setPointRule(socialSecurityPlan.getPointRule().name());
        }
        return employeeInfoDTO;
    }

    /**
     * 获取考勤字段
     * @param salaryItemsInflowList    员工考勤字段薪资项
     * @param salaryItemMonthlyDTOList 员工考勤字段薪资项值
     */
    private Map<String, Object> getSalaryItemsInflow(
            List<SalaryItem> salaryItemsInflowList, List<SalaryItemMonthlyDTO> salaryItemMonthlyDTOList) {
        if (CollectionUtils.isEmpty(salaryItemMonthlyDTOList)) {
            return new HashMap<>();
        }
        Map<String, Object> employeeCache = new HashMap<>();
        for (SalaryItem salaryItemsInflow : salaryItemsInflowList) {
            for (SalaryItemMonthlyDTO salaryItemMonthlyDTO : salaryItemMonthlyDTOList) {
                if (salaryItemsInflow.getCode().equals(salaryItemMonthlyDTO.getCode())) {
                    employeeCache.put(salaryItemsInflow.getCode(), salaryItemMonthlyDTO.getValue());
                }
            }
        }
        return employeeCache;
    }


    /**
     * 获取月度薪资项信息
     * @param employeeIds
     */
    private Map<Long, List<SalaryItemMonthlyDTO>> getEmployeeMonthlyMap(
            List<Long> employeeIds, String currentCycleMonth) {
        if (CollectionUtils.isEmpty(employeeIds) || StringUtils.isBlank(currentCycleMonth)) {
            return Collections.EMPTY_MAP;
        }
        List<EmployeeSalaryMonthly> employeeSalaryMonthlyList = employeeSalaryMonthlyRepository
                .findByEmployeeIdInAndDateAndIncludeIsTrue(employeeIds, currentCycleMonth);
        if (!CollectionUtils.isEmpty(employeeSalaryMonthlyList)) {
            return employeeSalaryMonthlyList.stream()
                    .filter(salaryMonthly -> StringUtils.isNotBlank(salaryMonthly.getItemsResult()))
                    .collect(Collectors.toMap(EmployeeSalaryMonthly::getEmployeeId, employeeSalaryMonthly
                            -> JSON.parseArray(employeeSalaryMonthly.getItemsResult(), SalaryItemMonthlyDTO.class)));
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 计算员工单个薪资项
     * @param formulaRelation 公式关系表
     * @param salaryItem      计算的薪资项
     * @param employeeInfoDTO 员工的基本信息DTO（公式配置可通过employee.属性调用）
     * @param employeeCache   计算缓存值
     */
    private void calculateOneEmployeeItems(Map<String, FormulaDTO> formulaRelation, SalaryItem salaryItem,
                                           ExternalEmployeeDTO employeeInfoDTO, Map<String, Object> employeeCache) {
        FormulaDTO formulaDTO = formulaRelation.get(salaryItem.getCode());
        if (Objects.isNull(formulaDTO)) {
            log.error("编码{},公式配置错误:[{}]",salaryItem.getCode(),salaryItem.getFormula());
            throw new BizException(ResultCode.SALARY_ITEM_CODE_NOT_EXISTS);
        }
        lispCalculateOneItems(formulaRelation, formulaDTO, employeeInfoDTO, employeeCache);
    }

    /**
     * 递归单个薪资项
     * @param formulaRelation 公式关系表
     * @param formulaDTO 计算的薪资项
     * @param employeeInfoDTO 员工的基本信息DTO（公式配置可通过employee.属性调用）
     * @param employeeCache  计算缓存值
     */
    private void lispCalculateOneItems(Map<String, FormulaDTO> formulaRelation, FormulaDTO formulaDTO,
                                   ExternalEmployeeDTO employeeInfoDTO, Map<String, Object> employeeCache) {
        List<String> children = formulaDTO.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach(childrenCode -> {
                FormulaDTO childrenFormula = formulaRelation.get(childrenCode);
                if (Objects.isNull(childrenFormula)) {
                    log.error("公式配置错误,[{}]",childrenCode);
                    throw new BizException(ResultCode.SALARY_ITEM_CODE_NOT_EXISTS);
                }
                lispCalculateOneItems(formulaRelation, childrenFormula, employeeInfoDTO, employeeCache);
            });
        }
        String formulaCode = formulaDTO.getCode();
        if (!employeeCache.containsKey(formulaCode)) {
            JexlContext jexlContext = new MapContext();
            jexlContext.set(SalaryItemsConst.EXTERNAL_EMPLOYEE, employeeInfoDTO);
            jexlContext.set(SalaryItemsConst.EXTERNAL_SERVICE, externalFormulaService);
            String formulaStr = parsedFormulaValue(formulaDTO.getFormula(), employeeCache);
            Number number = calculate(formulaStr, employeeCache, jexlContext,Boolean.TRUE);
            log.info("【{}】的值为{}", formulaCode, number);
            employeeCache.put(formulaDTO.getCode(), number);
        }
    }

    /**
     * 解析表达式
     * @param formula 公式
     */
    private String parsedFormula(String formula, Map<String, Object> map) {
        if (StringUtils.isBlank(formula) || formula.indexOf(SalaryItemsConst.METHOD_PREFIX) == -1) {
            return formula;
        }
        JexlContext context = new MapContext(map);
        context.set(SalaryItemsConst.EXTERNAL_SERVICE, externalFormulaService);
        UnifiedJEXL unifiedJEXL = new UnifiedJEXL(jexlEngine.get());
        UnifiedJEXL.Expression expression = unifiedJEXL.parse(formula);
        String result = expression.evaluate(context).toString();
        log.info("parsedFormula 解析之前: {}", formula);
        log.info("parsedFormula 解析之后: {}", result);
        return result;
    }

    /**
     * 解析表达式
     * @param formula 公式
     */
    private  String parsedFormulaValue(String formula,Map<String,Object> map) {
        if (StringUtils.isBlank(formula) || formula.indexOf(SalaryItemsConst.PREFIX) == -1) {
            return formula;
        }
        JexlContext context = new MapContext(map);
        UnifiedJEXL unifiedJEXL = new UnifiedJEXL(jexlEngine.get());
        UnifiedJEXL.Expression expression = unifiedJEXL.parse(formula);
        return expression.evaluate(context).toString();
    }

    /**
     * 通过DTO构建公式上下级关系
     */
    private void buildFormulaRelation(Map<String, FormulaDTO> formulaMap) {
        //建立父子关系，方便遍历关联关系
        List<FormulaDTO> formulaDTOList = formulaMap.values().stream().sorted((first, second) -> {
            String firstFormula = first.getFormula();
            String secondFormula = second.getFormula();
            if (StringUtils.isNotBlank(firstFormula) && firstFormula.indexOf(SalaryItemsConst.METHOD_PREFIX) > -1) {
                return -1;
            } else if (StringUtils.isNotBlank(secondFormula)
                    && secondFormula.indexOf(SalaryItemsConst.METHOD_PREFIX) > -1) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());

        for (FormulaDTO formulaDTO : formulaDTOList) {
            String formula = formulaDTO.getFormula();
            if (StringUtils.isNotBlank(formula)) {
                List<String> codeList = parseMethodParam(formulaDTO, formula);
                formulaDTO.setChildren(codeList);
            }
            setParentsList(formulaDTOList, formulaDTO);
        }
    }

    /**
     * 从数据库中获取自编码集合
     */
    private Map<String, FormulaDTO> getFormulaDTOMap() {
        return salaryItemRepository.findAll().stream()
                    .collect(Collectors.toMap(SalaryItem::getCode, salaryItem -> {
                        FormulaDTO formulaDTO = new FormulaDTO();
                        BeanUtils.copyProperties(salaryItem, formulaDTO);
                        return formulaDTO;
                    }));
    }


    /**
     * 获取父编码集合
     * @param formulaRelation
     * @param code
     */
    private List<String> getParentPathCode(Map<String, FormulaDTO> formulaRelation, String code) {
        List<String> parent = new ArrayList<>();
        lispParentPathCode(formulaRelation, parent, code);
        return parent;
    }

    /**
     * 递归父编码集合
     */
    private void lispParentPathCode(Map<String, FormulaDTO> formulaRelation, List<String> parentResult, String code) {
        FormulaDTO formulaDTO = formulaRelation.get(code);
        List<String> parents = formulaDTO.getParents();
        if (!CollectionUtils.isEmpty(parents)) {
            parents.forEach(item -> {
                if (!parentResult.contains(item)) {
                    parentResult.add(item);
                }
            });
            parents.forEach(item -> lispParentPathCode(formulaRelation, parentResult, item));
        }
    }

    /**
     * 验证是否循环引用
     * @param formulaRelation
     * @param code
     */
    private List<String> validateFormulaCircle(Map<String, FormulaDTO> formulaRelation, String code) {
        List<String> children = new ArrayList<>();
        lispChildrenPathCode(formulaRelation, children, code,code);
        return children;
    }

    /**
     * 递归子编码集合
     */
    private void lispChildrenPathCode(
            Map<String, FormulaDTO> formulaRelation, List<String> childrenResult, String code, String validateCode) {
        FormulaDTO formulaDTO = formulaRelation.get(code);
        if (Objects.isNull(formulaDTO)) {
            throw new BizException(ResultCode.SALARY_ITEM_CODE_NOT_EXISTS,
                    ResultCode.SALARY_ITEM_CODE_NOT_EXISTS.getMsg() + ":" + code);
        }
        List<String> children = formulaDTO.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach(item -> {
                if (!childrenResult.contains(item)) {
                    childrenResult.add(item);
                }
                if (childrenResult.contains(validateCode)) {
                    log.info("{}:{}", ResultCode.SALARY_ITEM_IS_CYCLE.getMsg(), code);
                    throw new BizException(ResultCode.SALARY_ITEM_IS_CYCLE);
                }
            });
            children.forEach(item -> lispChildrenPathCode(formulaRelation, childrenResult, item, validateCode));
        }
    }

    /**
     * 验证计算公式
     * @param salaryItem 薪资项
     */
    public void validateFormula(SalaryItem salaryItem) {
        createThreadJexlEngine();
        //构建公式关系
        String itemCode = salaryItem.getCode();
        Map<String, FormulaDTO> formulaRelation = getFormulaDTOMap();
        FormulaDTO updatedFormulaDTO = new FormulaDTO();
        BeanUtils.copyProperties(salaryItem, updatedFormulaDTO);
        formulaRelation.put(itemCode, updatedFormulaDTO);
        buildFormulaRelation(formulaRelation);
        //验证公式是否循环引用
        validateFormulaCircle(formulaRelation, itemCode);
        FormulaDTO resultFormulaDTO = formulaRelation.get(salaryItem.getCode());
        List<String> codeList = resultFormulaDTO.getChildren();
        Map<String, Object> formulaValueMap = null;
        if (!CollectionUtils.isEmpty(codeList)) {
            codeList.forEach(code -> {
                if (Objects.isNull(formulaRelation.get(code))) {
                    throw new BizException(ResultCode.SALARY_ITEM_CODE_NOT_EXISTS,
                            ResultCode.SALARY_ITEM_CODE_NOT_EXISTS.getMsg() + ":" + code);
                }
            });
            formulaValueMap = codeList.stream().collect(Collectors
                    .toMap(String::toString, str -> secureRandom.nextDouble() * 1000 + 1000D));
        }
        try {
            String resultFormula = parsedFormulaValue(resultFormulaDTO.getFormula(), formulaValueMap);
            Number number = calculate(resultFormula, null, getMockMapContext(),Boolean.FALSE);
            log.info(number.toString());
        } catch (TokenMgrError | JexlException e) {
            log.info("公式验证出错！");
            BizException exception = new BizException(ResultCode.SALARY_ITEM_PARSE_ERROR);
            exception.initCause(e);
            throw exception;
        }
    }

    /**
     * 获取校验模拟数据
     */
    private JexlContext getMockMapContext() {
        ExternalEmployeeDTO employee = new ExternalEmployeeDTO();
        Date finalDate = new Date();
        employee.setCurrentCycleMonth(DateUtil.convertDateToStr(finalDate, DateUtil.DATE_FORMAT_Y_M_D));
        employee.setHireDate(finalDate);
        employee.setWorkDate(finalDate);
        employee.setResignationDate(finalDate);
        employee.getHousingFund().setEffectDate(finalDate);
        employee.getSocialSecurity().setEffectDate(finalDate);
        //设置上下文信息
        JexlContext jexlContext = new MapContext();
        jexlContext.set(SalaryItemsConst.EXTERNAL_EMPLOYEE, employee);
        jexlContext.set(SalaryItemsConst.EXTERNAL_SERVICE, externalFormulaService);
        return jexlContext;
    }

    /**
     * 解析方法调用公式
     * @param parentFormulaDTO
     * @param formula
     */
    private List<String> parseMethodParam(FormulaDTO parentFormulaDTO, String formula) {
        List<String> codeList;
        //如果有 #{ 说明有变量
        if (formula.indexOf(SalaryItemsConst.METHOD_PREFIX) > -1) {
            codeList = findParameter(formula);
            formula = parsedFormula(formula, codeList.stream().collect(Collectors.toMap(String::toString,
                    str -> SalaryItemsConst.PREFIX + str + SalaryItemsConst.SUFFIX)));
            parentFormulaDTO.setFormula(formula);
        }
        //查找成功第二次解析
        codeList = findParameter(formula);
        return codeList;
    }

    /**
     * 设置父亲节点列表
     * @param formulaStream
     * @param formulaDTO
     */
    private void setParentsList(List<FormulaDTO> formulaStream, FormulaDTO formulaDTO) {
        String queryStr = SalaryItemsConst.PREFIX + formulaDTO.getCode() + SalaryItemsConst.SUFFIX;
        for (FormulaDTO childrenFormulaDTO : formulaStream) {
            //排除自生(查找父亲节点可能是自身)
            if (formulaDTO.equals(childrenFormulaDTO)) {
                continue;
            }
            String formula = childrenFormulaDTO.getFormula();
            if (StringUtils.isNotBlank(formula) && formula.indexOf(queryStr) > -1) {
                List<String> parentsFormulaL = formulaDTO.getParents();
                if (CollectionUtils.isEmpty(parentsFormulaL)) {
                    parentsFormulaL = new ArrayList<>();
                    formulaDTO.setParents(parentsFormulaL);
                }
                if (!parentsFormulaL.contains(childrenFormulaDTO.getCode())) {
                    parentsFormulaL.add(childrenFormulaDTO.getCode());
                }
            }
        }
    }

    /**
     * 查找变量
     * @param formula 公式
     */
    public  List<String> findParameter(String formula) {
        Matcher matcher = pattern.matcher(formula);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String groupCode = matcher.group();
            if (!list.contains(groupCode)) {
                list.add(groupCode);
            }
        }
        return list;
    }

    /**
     * 计算表达式
     * @param expression  结果表达式
     * @param parameter   参数
     * @param jexlContext 计算上下文
     * @param lenient 是否严格验证
     */
    public  Number calculate(
            String expression, Map<String, Object> parameter, JexlContext jexlContext, Boolean lenient) {
        JexlEngine jexlEngine = this.jexlEngine.get();
        jexlEngine.setLenient(lenient);
        Expression expr = jexlEngine.createExpression(expression);
        if (!CollectionUtils.isEmpty(parameter)) {
            Iterator iterator = parameter.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> elem = (Map.Entry<String, Object>) iterator.next();
                jexlContext.set(elem.getKey(), elem.getValue());
            }
        }
        Object object = expr.evaluate(jexlContext);
        return Objects.nonNull(object) ? (Number) object : 0;
    }

}

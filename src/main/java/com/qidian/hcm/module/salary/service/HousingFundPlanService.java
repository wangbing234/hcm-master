package com.qidian.hcm.module.salary.service;

import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.salary.dto.HousingFundPlanDTO;
import com.qidian.hcm.module.salary.entity.EmployeeFinancial;
import com.qidian.hcm.module.salary.entity.HousingFundPlan;
import com.qidian.hcm.module.salary.repository.EmployeeFinancialRepository;
import com.qidian.hcm.module.salary.repository.HousingFundPlanRepository;
import com.qidian.hcm.module.salary.utils.SalaryFormulaIKeyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

@Slf4j
@Service
public class HousingFundPlanService {

    @Autowired
    EmployeeFinancialRepository employeeFinancialRepository;

    @Autowired
    HousingFundPlanRepository housingFundPlanRepository;

    @Autowired
    private CalculateService calculateService;

    public void addHousingFundPlan(HousingFundPlanDTO housingFundPlanDTO) {
        validateNameExist(housingFundPlanDTO.getName(), 0L);
        HousingFundPlan housingFundPlan = new HousingFundPlan();
        BeanUtils.copyProperties(housingFundPlanDTO, housingFundPlan, "id");
        housingFundPlanRepository.save(housingFundPlan);
    }

    @Transactional
    public void updateHousingFundPlan(Long id, HousingFundPlanDTO housingFundPlanDTO) {
        HousingFundPlan housingFundPlan = getHousingFundPlanAndValidate(id);
        validateNameExist(housingFundPlanDTO.getName(), id);
        BeanUtils.copyProperties(housingFundPlanDTO, housingFundPlan);
        housingFundPlan.setId(id);
        housingFundPlanRepository.save(housingFundPlan);

        //计算公积金方案变化的员工薪资项
        List<EmployeeFinancial> employeeFinancialList = employeeFinancialRepository.findByHousingFundPlanId(id);
        if (!CollectionUtils.isEmpty(employeeFinancialList)) {
            calculateService.formulaCalculate(employeeFinancialList.stream()
                    .map(EmployeeFinancial::getEmployeeId)
                    .collect(Collectors.toList()), SalaryFormulaIKeyword.HOUSING_FUND);
        }
    }

    private HousingFundPlan getHousingFundPlanAndValidate(Long id) {
        return housingFundPlanRepository.findById(id)
                .orElseThrow(() -> new BizException(ResultCode.HOUSING_FUND_NOT_EXISTS));
    }

    private void validateNameExist(String name, Long id) {
        Optional<HousingFundPlan> housingFundPlanOptional = housingFundPlanRepository.findByNameAndIdNot(name, id);
        if (housingFundPlanOptional.isPresent()) {
            throw new BizException(ResultCode.HOUSING_FUND_NAME_EXISTS);
        }
    }

    public void deleteHousingFundPlan(Long id) {
        getHousingFundPlanAndValidate(id);
        //被引用无法删除
        List<EmployeeFinancial> employeeFinancialList = employeeFinancialRepository.findByHousingFundPlanId(id);
        if (!CollectionUtils.isEmpty(employeeFinancialList)) {
            throw new BizException(ResultCode.HOUSING_FUND_USED);
        }
        housingFundPlanRepository.deleteById(id);
    }


    public List<HousingFundPlanDTO> getHousingFundPlans() {
        List<HousingFundPlan> housingFundPlans = housingFundPlanRepository.findAll();
        List<HousingFundPlanDTO> housingFundPlanDTOS = newArrayListWithExpectedSize(housingFundPlans.size());
        housingFundPlans.forEach(item -> {
            HousingFundPlanDTO housingFundPlanDTO = new HousingFundPlanDTO();
            BeanUtils.copyProperties(item, housingFundPlanDTO);
            housingFundPlanDTOS.add(housingFundPlanDTO);
        });
        return housingFundPlanDTOS;
    }
}

UPDATE customized_employee_form SET switchable = 1;
UPDATE customized_employee_form SET switchable = 0 WHERE code IN ('position', 'job', 'contract');
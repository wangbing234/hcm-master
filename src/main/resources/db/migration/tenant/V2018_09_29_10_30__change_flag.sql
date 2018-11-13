update salary_item set formula=replace(formula,'${service.','#{service.');

UPDATE `salary_item` SET `formula`='${personalBasicHousingFund}+${personalAddingHousingFund}' WHERE `id`='23';

UPDATE `salary_item` SET `formula`='employee.salary' WHERE `id`='9';

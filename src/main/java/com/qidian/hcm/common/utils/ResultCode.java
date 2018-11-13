package com.qidian.hcm.common.utils;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResultCode {
    SUCCESS("0", "成功"),

    /** 错误码定义格式：错误模块编号 + 具体错误编号 10开头的跟系统相关 **/

    /**
     * 10开头的跟系统相关
     **/
    PARAM_ERROR("10001", "请求参数错误"),
    PARAM_INVALID("10002", "请求参数无效"),
    RESOURCE_NOT_FOUND("10003", "接口不存在"),
    NOT_FOUND_TENANT("10004", "找不到租户,请重新登陆"),
    TENANT_CONFIG_ERROR("10005", "租户数据错误"),
    CREATE_USER_ERROR("10006", "创建用户失败"),
    SYSTEM_ERROR("10999", "服务器内部错误"),

    /**
     * 11开头的跟权限相关
     **/
    UNAUTHORIZED("11001", "未认证，请登录"),
    PERMISSION_DENIED("11002", "无访问权限"),
    USER_NOT_EXIST("11003", "用户名不存在！"),
    WRONG_PASSWORD("11004", "密码错误！"),
    VERIFICATION_CODE_OVER_DUE("11005", "验证码超时！"),
    WRONG_VERIFICATION_CODE("11006", "验证码错误！"),
    DATASOURCE_NOT_FOUND("11007", "用户没有绑定数据源"),
    DUPLICATE_USER("11008", "用户名称重复，无法注册！"),
    DUPLICATE_USER_PHONE("11009", "手机号重复，无法注册！"),
    MESSAGE_SEND_FAILED("11010", "验证码发送失败"),
    DO_NOT_HAVE_ACCESS("11011", "没有访问权限"),

    /**
     * 12开头的跟组织管理相关--公司
     **/
    ROOT_COMPANY_NOT_DELETE("12001", "顶级公司不能删除"),
    COMPANY_IS_USED("12101", "公司存在生效的下级单位"),
    COMPANY_IS_NULL("12102", "公司不存在"),
    PARENT_COMPANY_IS_NULL("12103", "父级公司不存在"),
    FIELD_CODE_EXISTS("12004", "字段代码已存在"),
    COMPANY_IS_CYCLE("12105", "公司信息不能循环引用"),
    PARENT_COMPANY_IS_DISABLED("12106", "父公司已失效"),

    /**
     * 122--部门
     **/
    DEPARTMENT_IS_USED("12201", "部门存在生效的下级单位"),
    DEPARTMENT_IS_NULL("12202", "部门不存在"),
    DEPARTMENT_IS_CYCLE("12203", "部门信息不能循环引用"),
    PARENT_ORG_IS_NULL("12204", "父级公司或部门不存在"),
    PARENT_ORG_IS_DISABLED("12205", "父级公司或部门已失效"),
    ORG_CODE_EXISTS("12402", "组织架构code已经存在"),

    /**
     * 123--岗位
     **/
    DEACTIVATE_CHILD_POSITION_FIRST("12301", "失效/删除岗位时，必须先失效/删除下级岗位"),
    ACTIVE_PARENT_POSITION_FIRST("12302", "生效岗位时，必须先生效上级岗位"),
    ACTIVE_DEVELOPMENT_FIRST("12303", "生效岗位时，必须先生效部门"),
    ACTIVE_GRADE_FIRST("12304", "生效岗位时，必须先生效职级"),
    POSITION_IS_CYCLE("12305", "岗位信息不能循环引用"),
    POSITION_IS_USED_BY_EMPLOYEE("12306", "岗位中还存在在职员工，请检查！"),
    POSITION_NOT_EXISTS("12307", "岗位信息不存在"),
    POSITION_ALREADY_EXISTS("12308", "不可添加重复的岗位"),
    DELETE_POSITION_FAILED("12310","岗位删除失败！"),


    /**
     * 124--职级
     */
    DEACTIVATE_OR_DELETE_POSITION_USED_BY_GRADE_FIRST("12401", "失效/删除职级时，必须先失效/删除与职级关联的岗位"),
    GRADE_ALREADY_EXISTS("12402", "不可添加重复的职级"),
    GRADE_NOT_EXISTS("12403", "职级不存在"),


    /**
     * 13开头的人事相关
     **/
    CUSTOMIZED_FORM_IS_NULL("13101", "自定义表单不存在"),
    CUSTOMIZED_FIELD_IS_NULL("13102", "自定义字段不存在"),
    EMPLOYEE_NOT_EXISTS("13104", "找不到对应的员工"),
    EMPLOYEE_NO_EXISTS("13105", "重复的员工编号"),
    HANDOVER_MAN_NOT_EXISTS("13106", "离职时必须输出有效的交接人"),
    HANDOVER_MAN_CAN_NOT_BE_YOURSELF("13107", "交接人不能为自己"),
    POSITION_OF_THE_EMPLOYEE_NOT_FOUND("13108", "找不到该员工的当前岗位，可能是数据已损坏"),
    FORM_TYPE_INCORRECT("13109", "表单类型错误"),
    CAN_NOT_DELETE_BASIC_FORM("13110", "八大基本信息不允许删除"),
    LEADER_IS_CYCLE("13111", "主管不能循环引用"),
    NOT_MATCH_QUALIFY_CONTRACT("13112", "未找到匹配的转正合同"),


    /**
     * 14开头的文件相关
     **/
    FILE_NOT_FOUND("14101", "文件不存在"),
    FILE_SIZE_LIMIT("14102", "文件大小超出限制"),
    FILE_SAVE_TO_DISK_FAILED("14103", "文件保存到磁盘失败"),

    /**
     * 15角色权限相关
     */
    ROLE_NAME_EXISTS("15001", "角色名称已经存在"),
    ROLE_NOT_EXISTS("15002", "角色不存在"),
    MENU_NOT_EXISTS("15003", "菜单不存在"),

    /**
     * 16薪酬--社保相关
     */
    SOCIAL_SECURITY_NOT_EXISTS("16001", "社保方案不存在"),
    SOCIAL_SECURITY_NAME_EXISTS("16002", "社保方案名称不能重复"),
    SOCIAL_SECURITY_USED("16003", "社保方案在被使用中"),


    /**
     * 17薪酬--公积金
     */

    HOUSING_FUND_NOT_EXISTS("17001", "公积金方案不存在"),
    HOUSING_FUND_NAME_EXISTS("17002", "公积金名称不能重复"),
    HOUSING_FUND_USED("17003", "公积金在被使用中"),

    /**
     * 18薪酬--设置
     */

    SALARY_THRESHOLD_NOT_EXISTS("18001", "免税额信息不存在"),
    SALARY_CURRENT_CYCLE_NOT_EXISTS("18002", "当前计薪月份信息不存在"),
    SALARY_START_CYCLE_DATE_NOT_EXISTS("18003", "计薪周期开始日期不存在"),
    /**
     * 19薪酬--员工
     */
    EMPLOYEE_THRESHOLD_NOT_EXISTS("19001", "无法找到员工免征额信息"),
    EMPLOYEE_SOCIAL_SECURITY_NOT_EXISTS("19002", "无法找到员工社保信息"),
    EMPLOYEE_HOUSING_FUND_NOT_EXISTS("19003", "无法找到员工公积金信息"),
    /**
     * 20薪酬--基本信息
     */
    SALARY_NOT_ACCOUNTED("20001", "未核算"),
    SALARY_CURRENT_MONTH_RECORD_NOT_EXISTS("20002", "本月薪资记录不存在"),

    /**
     * 20薪酬--薪资项
     */
    SALARY_ITEM_NAME_EXISTS("20002", "薪资项名称不能重复"),
    SALARY_ITEM_USED("20003", "薪资项在被使用中"),
    SALARY_ITEM_NOT_EDIT("20004", "无法编辑此薪资项"),
    SALARY_ITEM_PARSE_ERROR("20005", "公式解析错误"),
    SALARY_ITEM_CODE_NOT_EXISTS("20006", "公式编码不存在"),
    SALARY_ITEM_IS_CYCLE("20007", "薪资项公式循环引用"),
    SALARY_ITEM_NOT_EXISTS("20001", "薪资项不存在");


    private String code;
    private final String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

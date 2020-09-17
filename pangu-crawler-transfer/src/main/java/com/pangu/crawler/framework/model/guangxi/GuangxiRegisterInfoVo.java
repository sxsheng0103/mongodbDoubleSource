package com.pangu.crawler.framework.model.guangxi;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Author: jp
 * @Date: 2019/12/25
 * @Description:
 */
@Data
public class GuangxiRegisterInfoVo {

    //纳税人识别号
    @JSONField(name = "NSRSBH")
    private String nsrsbh;

    //财务负责人姓名
    @JSONField(name = "CWFZRMC")
    private String cwfzrxm;

    //财务负责人电子邮箱
    private String cwfzrdzyx;

    //财务负责人身份证件种类名称
    @JSONField(name = "CWFZR_ZJLX_MC")
    private String cwfzrsfzjzlmc;

    //财务负责人身份证件种类代码
    @JSONField(name = "CWFZR_ZJLX_DM")
    private String cwfzrsfzjzldm;

    //财务负责人身份证件号码
    @JSONField(name = "CWFZR_ZJHM")
    private String cwfzrsfzjhm;

    //财务负责人固定电话
    @JSONField(name = "CWFZR_DHHM")
    private String cwfzrgddh;

    //财务负责人移动电话
    @JSONField(name = "CWFZR_YDDHHM")
    private String cwfzryddh;

    //法定代表人姓名
    @JSONField(name = "FDDBRMC")
    private String fddbrxm;

    //法定代表人电子邮箱
    private String fddbrdzyx;

    //法定代表人身份证件种类名称
    @JSONField(name = "FRZJLX_MC")
    private String fddbrsfzjzlmc;

    //法定代表人身份证件种类代码
    @JSONField(name = "FRZJLX_DM")
    private String fddbrsfzjzldm;

    //法定代表人身份证件号码
    @JSONField(name = "ZJHM")
    private String fddbrsfzjhm;

    //法定代表人固定电话
    @JSONField(name = "FR_DHHM")
    private String fddbrgddh;

    //法定代表人移动电话
    @JSONField(name = "FR_YDDHHM")
    private String fddbryddh;

    //行业名称
    @JSONField(name = "HY_MC")
    private String hymc;

    //行业代码
    private String hydm;

    //经营范围
    @JSONField(name = "JYFW")
    private String jyfw;

    //办税人姓名
    @JSONField(name = "BSRMC")
    private String bsrxm;

    //办税人电子邮箱
    private String bsrdzyx;

    //办税人身份证件名称
    @JSONField(name = "BSR_ZJLX_MC")
    private String bsrsfzjzlmc;

    //办税人身份证件代码
    @JSONField(name = "BSR_ZJLX_DM")
    private String bsrsfzjzldm;

    //办税人身份证件号码
    @JSONField(name = "BSR_ZJHM")
    private String bsrsfzjhm;

    //办税人固定电话
    @JSONField(name = "BSR_DHHM")
    private String bsrgddh;

    //办税人移动电话
    @JSONField(name = "BSR_YDDHHM")
    private String bsryddh;

    //纳税人状态名称
    private String nsrztmc;

    //纳税人状态代码
    @JSONField(name = "NSRZT_DM")
    private String nsrztdm;

    //纳税人类型名称
    @JSONField(name = "DJZCLX_MC")
    private String nsrlxmc;

    //纳税人类型代码
    @JSONField(name = "DJZCLX_DM")
    private String nsrlxdm;

    //主管税务所(科、分局)
    @JSONField(name = "ZGSWSKFJ_MC")
    private String zgswskfjmc;

    //税收管理员名称
    private String ssglymc;

    //生产经营地址
    @JSONField(name = "SCJYDZ")
    private String scjydz;
}

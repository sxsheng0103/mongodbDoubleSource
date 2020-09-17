package com.pangu.crawler.framework.model.sichuan;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Author: jp
 * @Date: 2019/12/25
 * @Description:
 */
@Data
public class SichuanRegisterInfoVo {

    //纳税人识别号
    @JSONField(name = "shxydm")
    private String nsrsbh;

    //财务负责人姓名
    private String cwfzrxm;

    //财务负责人电子邮箱
    private String cwfzrdzyx;

    //财务负责人身份证件种类名称
    @JSONField(name = "cwfzrsfzjzlDm")
    private String cwfzrsfzjzlmc;

    //财务负责人身份证件种类代码
    private String cwfzrsfzjzldm;

    //财务负责人身份证件号码
    private String cwfzrsfzjhm;

    //财务负责人固定电话
    private String cwfzrgddh;

    //财务负责人移动电话
    private String cwfzryddh;

    //法定代表人姓名
    private String fddbrxm;

    //法定代表人电子邮箱
    private String fddbrdzyx;

    //法定代表人身份证件种类名称
    @JSONField(name = "fddbrsfzjlxDm")
    private String fddbrsfzjzlmc;

    //法定代表人身份证件种类代码
    private String fddbrsfzjzldm;

    //法定代表人身份证件号码
    private String fddbrsfzjhm;

    //法定代表人固定电话
    private String fddbrgddh;

    //法定代表人移动电话
    private String fddbryddh;

    //行业名称
    @JSONField(name = "hydl")
    private String hymc;

    //行业代码
    private String hydm;

    //经营范围
    private String jyfw;

    //办税人姓名
    private String bsrxm;

    //办税人电子邮箱
    private String bsrdzyx;

    //办税人身份证件名称
    @JSONField(name = "bsrsfzjzlDm")
    private String bsrsfzjzlmc;

    //办税人身份证件代码
    private String bsrsfzjzldm;

    //办税人身份证件号码
    private String bsrsfzjhm;

    //办税人固定电话
    private String bsrgddh;

    //办税人移动电话
    private String bsryddh;

    //纳税人状态名称
    @JSONField(name = "nsrzt")
    private String nsrztmc;

    //纳税人状态代码
    private String nsrztdm;

    //纳税人类型名称
    @JSONField(name = "nsrzg")
    private String nsrlxmc;

    //纳税人类型代码
    private String nsrlxdm;

    //主管税务所(科、分局)
    @JSONField(name = "zgswksfjmc")
    private String zgswskfjmc;

    //税收管理员名称
    @JSONField(name = "zgswry")
    private String ssglymc;

    //生产经营地址
    private String scjydz;
}

package com.pangu.crawler.framework.model.guangxi;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Author: jp
 * @Date: 2019/12/25
 * @Description:
 */
@Data
public class GuangxiTaxsDetermineVo {

    //征收项目代码
    @JSONField(name = "ZSXMDM")
    private String zsxmDm;

    //征收项目名称
    @JSONField(name = "ZSXMMC")
    private String zsxmMc;

    //认定有效期起
    @JSONField(name = "RDYXQQ")
    private String rdyxqq;

    //认定有效期止
    @JSONField(name = "RDYXQZ")
    private String rdyxqz;

    //税率或单位税额
    private String slhdwse;

    //征收子目代码
    private String zszmDm;

    //征收子目名称
    private String zszmMc;

    //征收代理方式名称
    private String zsdlfsmc;

    //国地税标志
    private String gdsbz;

    //征收品名代码
    @JSONField(name = "ZSPM_DM")
    private String zspmDm;

    //征收品目名称
    @JSONField(name = "ZSPMMC")
    private String zspmMc;

    //征收率
    @JSONField(name = "ZSL")
    private String zsl;

    //申报期限代码
    private String sbqxDm;

    //申报期限名称
    @JSONField(name = "SBQXMC")
    private String sbqxMc;

    //纳税人期限代码
    private String nsqxDm;

    //纳税人期限名称
    @JSONField(name = "NSQXMC")
    private String nsqxMc;
}

package com.pangu.crawler.framework.model;

import lombok.Data;

/**
 * @Author: jp
 * @Date: 2019/12/25
 * @Description:
 */
@Data
public class TaxsDetermineVo {

    //征收项目代码
    private String zsxmdm;

    //征收项目名称
    private String zsxmmc;

    //认定有效期起
    private String rdyxqq;

    //认定有效期止
    private String rdyxqz;

    //税率或单位税额
    private String slhdwse;

    //征收子目代码
    private String zszmdm;

    //征收子目名称
    private String zszmmc;

    //征收代理方式名称
    private String zsdlfsmc;

    //国地税标志
    private String gdsbz;

    //征收品名代码
    private String zspmdm;

    //征收品目名称
    private String zspmmc;

    //征收率
    private String zsl;

    //申报期限代码
    private String sbqxdm;

    //申报期限名称
    private String sbqxmc;

    //纳税人期限代码
    private String nsqxdm;

    //纳税人期限名称
    private String nsqxmc;
}
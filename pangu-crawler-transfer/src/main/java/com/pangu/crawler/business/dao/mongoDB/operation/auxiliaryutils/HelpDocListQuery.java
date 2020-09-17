package com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author sheng.ding
 * @Date 2020/7/21 9:17
 * @Version 1.0
 **/

@Getter
@Setter
public class HelpDocListQuery implements Serializable {

    private Integer page = 1;

    private Integer pageSize = 20;

    /**
     * 纳税人识别号
     */
    private String objid;

    /**
     * 纳税人识别号
     */
    private String nsrsbh;

    /**
     * 纳税人地区
     */
    private String nsrdq;

    /**
     * 税种
     */
    private String datasz;

    /**
     * 税种
     */
    private String rulesz;

    /**
     * 申报日期
     */
    private String sbrq;

    /**
     * 申报状态
     */
    private String sbzt;

    /**
     * 表单id
     */
    private String formid;

    /**
     * type
     */
    private String type;

    /**
     * type
     */
    private String zfbj;

    /**
     *
     */
    private String status;
}
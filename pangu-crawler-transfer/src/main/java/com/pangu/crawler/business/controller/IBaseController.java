package com.pangu.crawler.business.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public interface IBaseController {

    /**
     * 登录并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    void login(String trace, String nsrsbh, JSONObject jsonObject) throws Exception;

    /**
     * 查询登记并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    JSON queryRegister(String trace,String nsrsbh,JSONObject jsonObject) throws Exception;

    /**
     * 查询历史数据并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    JSON queryHistoricalData(String trace,String nsrsbh,JSONObject jsonObject) throws Exception;
    
    
    /**
     * 查询历史数据列表(不包含具体的报表数据)
     * @throws Exception TODO
     */
    JSON queryHistoricalDataList(String trace,String nsrsbh,JSONObject jsonObject) throws Exception;

    
    /**
     * 查询税费种认定并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    JSON queryTaxsDetermine(String trace,String nsrsbh,JSONObject jsonObject) throws Exception;
    
    /**
     * 查询发票信息并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    JSON queryFapiaoInfo(String trace,String nsrsbh,JSONObject jsonObject) throws Exception;


    /**
     * 查询本月清册并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     * @throws Exception TODO
     */
    public JSON queryThisMonthQingce(String trace, String nsrsbh, JSONObject jsonObject) throws Exception;
    
    
    /**
     * 查询缴款信息
     * @param trace
     * @param nsrsbh
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSON queryJiaoKuanXinXi(String trace, String nsrsbh, JSONObject jsonObject) throws Exception;
    
    /**
     * 临时历史数据查询
     * @param trace
     * @param nsrsbh
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSON queryTemporaryHistoricalData(String trace, String nsrsbh, JSONObject jsonObject) throws Exception;

    /**
     * 欠税信息查询
     * @param trace
     * @param nsrsbh
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSON queryQianShuiInfo(String trace, String nsrsbh, JSONObject jsonObject) throws Exception;

}

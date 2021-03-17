package com.pangu.crawler.sbptpicture.service;

import com.pangu.crawler.sbptpicture.enums.StorageEnum;
import com.pangu.crawler.sbptpicture.mongo.AsyncQueryBusinessPictureEntity;
import java.util.List;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2021/3/17 17:11
 * @Version 1.0
 **/
public interface IReportService {

    /**
     * @param params
     * @return
     * @throws Exception
     * @查询申报图片以及其他信息接口
     */
    public List<AsyncQueryBusinessPictureEntity> queryHistoricalData(Map<String, String> params) throws Exception;


    /**
     * @param params
     * @return
     * @throws Exception
     * @description 保存申报图片信息接口
     */
    public Map<String, Object> savereportcation(Map<String, String> params) throws Exception;

    /**
     * @param releationid
     * @param screenbase64
     * @return
     * @throws Exception
     * @description 修改申报图片信息接口
     */
    public Map<String, Object> updatesbimage(String releationid,String screenbase64) throws Exception;
    /**
     * @param params
     * @return
     * @throws Exception
     * @description 请求报文保存
     */
    public Map<String, Object> requestReportContent(Map<String, String> params,String contents) throws Exception;

    /**
     * @param params
     * @return
     * @throws Exception
     * @description 结果报文保存
     */
    public Map<String, Object> responseReportContent(Map<String, String> params,String contents) throws Exception;

    /**
     * @param params
     * @return
     * @throws Exception
     * @description 详情信息保存
     */
    public Map<String, Object> detailReportContent(Map<String, String> params,String contents) throws Exception ;

    /**
     *
     */
    Map<String, Object> saveDataToStoreMedia(Map<String, String> params,String contents) throws Exception ;

    /**
     *
     * @param storageEnum
     * @param params
     * @param contents
     * @return
     * @throws Exception
     * @description 保存图片信息到其他媒体
     */
    Map<String, Object> saveDataToStoreMedia(StorageEnum storageEnum, Map<String, String> params, String contents) throws Exception;

    /**
     * @param params
     * @param contents
     * @return
     * @throws Exception
     * @description 保存图片信息到本地磁盘
     */
    //202012-914100007286653122-cwbbqykjzzyzx-sbtj-lsh-token
    Map<String, Object> saveDataToStoreLocalDist(Map<String, String> params,String contents) throws Exception;

}

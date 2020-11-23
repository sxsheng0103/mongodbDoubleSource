package com.pangu.crawler.sbptpicture.service;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.sbptpicture.enums.StorageEnum;
import com.pangu.crawler.sbptpicture.mongo.AsyncQueryBusinessPictureEntity;
import com.pangu.crawler.sbptpicture.mongo.AsyncQueryBusinessPictureOperation;
import com.pangu.crawler.sbptpicture.utils.AesEncryptUtil;
import com.pangu.crawler.sbptpicture.utils.Token;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportService {

	@Autowired
    AsyncQueryBusinessPictureOperation asyncQueryBusinessPictureOperation;

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @查询规则数据
	 */
	public List<AsyncQueryBusinessPictureEntity> queryHistoricalData(Map<String, String> params) throws Exception {
		Criteria cxyj =  new Criteria();
		if(StringUtils.isNotEmpty(params.get("jglx"))){
			cxyj.and("jglx").is(params.get("jglx"));
		}
		if(StringUtils.isNotEmpty(params.get("releationid"))){
			cxyj.and("releationid").is(params.get("releationid"));
		}
		if(StringUtils.isNotEmpty(params.get("name"))){
			cxyj.and("name").is(params.get("name"));
		}
		if(StringUtils.isNotEmpty(params.get("id"))){
			cxyj.and("_id").is(params.get("id"));
		}
		if(StringUtils.isNotEmpty(params.get("lsh"))){
			cxyj.and("lsh").is(params.get("lsh"));
		}
		List<AsyncQueryBusinessPictureEntity> data = asyncQueryBusinessPictureOperation.findByParam(cxyj);
		return data;
	}


	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @description 修改规则文件服务
	 */
	public Map<String, Object> savereportcation(Map<String, String> params) throws Exception {
		return asyncQueryBusinessPictureOperation.save(params);
	}


	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @description 请求报文保存
	 */
	public Map<String, Object> requestReportContent(Map<String, String> params,String contents) throws Exception {
		return saveDataToStoreMedia(params,contents);
	}

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @description 结果报文保存
	 */
	public Map<String, Object> responseReportContent(Map<String, String> params,String contents) throws Exception {
		return saveDataToStoreMedia(params,contents);
	}

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @description 详情信息保存
	 */
	public Map<String, Object> detailReportContent(Map<String, String> params,String contents) throws Exception {
		return saveDataToStoreMedia(params,contents);
	}


	/**
	 *
	 */
	private Map<String, Object> saveDataToStoreMedia(Map<String, String> params,String contents) throws Exception {
		return saveDataToStoreMedia(null,params,contents);
	}

	private Map<String, Object> saveDataToStoreMedia(StorageEnum storageEnum,Map<String, String> params,String contents) throws Exception {
		if(storageEnum == null){
			return saveDataToStoreLocalDist(params,AesEncryptUtil.encrypt(contents));
		}else{

		}
		return null;
	}

	@Value("${serversaveaddr}")
	String serversaveaddr;
	//2020-12-914100007286653122-cwbbqykjzzyzx-sbtj-token
	private  Map<String, Object> saveDataToStoreLocalDist(Map<String, String> params,String contents) {
		Map<String, Object> result = new HashMap<String, Object>();
		try{
			String serversaveaddr = null;
			if(this.serversaveaddr!=null&&StringUtils.isEmpty(this.serversaveaddr)){
				serversaveaddr = this.serversaveaddr;
			}else{
				serversaveaddr = "C://serversaveaddr";
			}
			String yearArchive = TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdfy);
			String monthArchive = TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdfy);
			String nsrsbh = StringUtils.isEmpty(params.get("nsrsbh"))?"default":params.get("nsrsbh");
			String sz = StringUtils.isEmpty(params.get("sz"))?"default":params.get("sz");
			String business = StringUtils.isEmpty(params.get("business"))?"default":params.get("business");
			String token = Token.createJWToten();
			String diretory = serversaveaddr+ File.separator+yearArchive+ File.separator+monthArchive
					+File.separator+nsrsbh+ File.separator+sz+File.separator+business;
			if(new File(diretory).isDirectory()){
			}else{
				new File(diretory).mkdirs();
			}
			FileUtils.write(new File(diretory+File.separator+nsrsbh+sz+business+token),contents,"UTF-8");
			result.put("code","fail");
			result.put("message","fail");
		}catch (Exception e){
			log.error("保存报文数据失败!"+e.getMessage());
			result.put("code","fail");
			result.put("message","保存报文数据失败!"+e.getMessage());
		}finally {
			return result;
		}
	}
}


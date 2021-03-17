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
public class DistReportService implements IReportService{

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

	public Map<String, Object> updatesbimage(String releationid,String screenbase64) throws Exception {
		Map<String,String> map = new HashMap<String,String>(1);
		map.put("releationid",releationid);
		List<AsyncQueryBusinessPictureEntity> data = queryHistoricalData(map);
		Map<String,Object> result = new HashMap<String,Object>(2);
		if(data.size()==0){
			result.put("code","fail");
			result.put("message","未找到对应数!");
		}else if(data.size()==1){
			result = asyncQueryBusinessPictureOperation.updateImage(data.get(0),screenbase64);
		}else{
			result.put("code","fail");
			result.put("message","找到多条数据!"+data.size());
		}
		return result;
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
    public Map<String, Object> saveDataToStoreMedia(Map<String, String> params,String contents) throws Exception {
		return saveDataToStoreMedia(null,params,contents);
	}

	public Map<String, Object> saveDataToStoreMedia(StorageEnum storageEnum,Map<String, String> params,String contents) throws Exception {
		if(storageEnum == null){
			return saveDataToStoreLocalDist(params,contents);
		}else{

		}
		return null;
	}

	@Value("${serversaveaddr}")
	String serversaveaddr;
	//202012-914100007286653122-cwbbqykjzzyzx-sbtj-lsh-token
    public  Map<String, Object> saveDataToStoreLocalDist(Map<String, String> params,String contents) {
		Map<String, Object> result = new HashMap<String, Object>();
		try{
			String serversaveaddr = null;
			if(this.serversaveaddr!=null&&StringUtils.isEmpty(this.serversaveaddr)){
				serversaveaddr = this.serversaveaddr;
			}else{
				serversaveaddr = "C://serversaveaddr";
			}
			String yearArchive = TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdfy);
			String monthArchive = TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdfm);
			String nsrsbh = StringUtils.isEmpty(params.get("nsrsbh"))?"default":params.get("nsrsbh");
			String sz = StringUtils.isEmpty(params.get("sz"))?"default":params.get("sz");
			String content = AesEncryptUtil.encrypt(contents);
			String business = StringUtils.isEmpty(params.get("business"))?"default":params.get("business");
			String lsh = StringUtils.isEmpty(params.get("lsh"))?"default":params.get("lsh");
			String token = Token.createJWToten();
			String diretory = serversaveaddr+ File.separator+yearArchive+monthArchive + File.separator+nsrsbh
					+ File.separator+sz+File.separator+business+File.separator+lsh+File.separator;
			if(new File(diretory).isDirectory()){
			}else{
				new File(diretory).mkdirs();
			}
			FileUtils.write(new File(diretory+yearArchive+monthArchive+"-"+nsrsbh+"-"+sz+"-"+business+"-"+lsh+"-"+token),content,"UTF-8");
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


	public static void main(String[] args) {
		Map<String, String> result = new HashMap<String, String>();
		DistReportService re = new DistReportService();
			result.put("sz","sz");
			result.put("lsh","lsh");
			result.put("jglx","jglx");
			result.put("name","name");
			result.put("nsrsbh","nsrsbh");
			result.put("nsrmc","nsrmc");
			result.put("nsrdq","nsrdq");
		re.saveDataToStoreLocalDist(result,"asdasd");
	}
}


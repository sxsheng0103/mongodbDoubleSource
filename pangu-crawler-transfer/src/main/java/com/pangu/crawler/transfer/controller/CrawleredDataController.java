package com.pangu.crawler.transfer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.enums.SzEnum;
import com.pangu.crawler.transfer.service.CrawleredDataService;
import com.pangu.crawler.transfer.service.TransferRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.pangu.crawler.framework.cookie.CookieOperation.NONE;

@Slf4j
@Controller
public class CrawleredDataController{

	/***
	 * 查询爬取的结果存量数据
	 */
	@Autowired
	CrawleredDataService dataService;

	@Autowired
	TransferRuleService transferRuleService;
    @ResponseBody
	@RequestMapping("queryStockData")
	public JSON queryHistoricalData(Integer page, Integer limit,HttpServletRequest request) throws Exception {
		Map<String,String> map = new HashMap<String,String>(7);
		if(StringUtils.isNotEmpty(request.getParameter("nsrsbh"))){
			map.put("nsrsbh",request.getParameter("nsrsbh"));
		}
		if(StringUtils.isNotEmpty(request.getParameter("nsrdq"))){
			map.put("nsrdq",request.getParameter("nsrdq"));
		}
		if(StringUtils.isNotEmpty(request.getParameter("sz"))){
			map.put("datasz",request.getParameter("sz"));
		}
		if(StringUtils.isNotEmpty(request.getParameter("craw_datatype"))){
			map.put("type",request.getParameter("craw_datatype"));
		}

		if(StringUtils.isNotEmpty(request.getParameter("zfbj"))){
			map.put("zfbj",request.getParameter("zfbj"));
		}
		if(StringUtils.isNotEmpty(request.getParameter("id"))){
			map.put("id",request.getParameter("id"));
		}
		if(StringUtils.isNotEmpty(request.getParameter("state"))){
			map.put("status",request.getParameter("state"));
		}
		String cols = "";
		if(StringUtils.isNotEmpty(request.getParameter("cols"))){
			cols = request.getParameter("cols");
		}
		JSONObject json = new JSONObject();
    	try{
			Paging<AsyncQueryHistoricalDataInfoEntity> paging = dataService.queryHistoricalData(cols,map,page,limit);
			json.put("code","0");
			json.put("msg","");
			json.put("count",paging.getTotalCount());
			json.put("data",paging.getJsondata());
		}catch (DataAccessResourceFailureException e){
			json.put("code","fail");
			json.put("msg","数据库连接失败");
			log.error("数据库连接失败");
		}catch (Exception e){
			json.put("code","fail");
			json.put("msg",e.getMessage());
			log.error(e.getMessage());
		}

		return json;
	}

	@ResponseBody
	@PostMapping("updateStockDataByid")
	public Map<String,String> updateStockDataByid(HttpServletRequest request) throws Exception {
		Map<String,String> map = new HashMap<String,String>(7);
		Map<String,String> result = new HashMap<String,String>(3);
		Set<String> ids = null;
		String errorid = "";
		try{
			if(StringUtils.isNotEmpty(request.getParameter("ids"))){
				ids = new HashSet<String>(Arrays.asList(request.getParameter("ids").split(",")));
			}
			if(StringUtils.isNotEmpty(request.getParameter("zhState"))){
				map.put("zhState",request.getParameter("zhState"));
			}
			for(String id:ids){
				errorid = id;
				if(StringUtils.isNotEmpty(id));
				map.put("id",id);
				dataService.updateStockDataByid(map);
			}
			result.put("code","success");
			result.put("message","操作成功");
		}catch (Exception e){
			e.printStackTrace();
			result.put("code","success");
			result.put("message","操作失败!"+errorid+":"+e.getMessage());
		}finally {
			return result;
		}
	}
	@ResponseBody
	@PostMapping("updatetransfer")
	public Map<String,String> updtransfer(HttpServletRequest request) throws Exception {
		String id = "";
		Map<String,String> result = new HashMap<String,String>(3);
		try{
			if(StringUtils.isNotEmpty(request.getParameter("id"))){
				id = request.getParameter("id");
				transferRuleService.executeTransferService(id);
				result.put("code","success");
				result.put("message","操作完成");
			}else{
				result.put("code","fail");
				result.put("message","id为空");
			}
		}catch (Exception e){
			e.printStackTrace();
			result.put("code","success");
			result.put("message","操作失败!"+e.getMessage());
		}finally {
			return result;
		}
	}

}
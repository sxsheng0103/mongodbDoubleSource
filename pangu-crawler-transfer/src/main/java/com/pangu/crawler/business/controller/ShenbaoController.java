package com.pangu.crawler.business.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.service.async.BusinessException;
import com.pangu.crawler.business.service.async.ParamsCheck;
import com.pangu.crawler.framework.model.ResultBean;
import com.pangu.crawler.framework.utils.AppUtil;
import com.pangu.crawler.framework.utils.TraceHelp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@Api(tags = "【申报业务接口】")
public class ShenbaoController {
	@Autowired
	private AppUtil appUtil;
	
	@ResponseBody
	@PostMapping("/shenbaoZuoFei")
	@ApiOperation(value = "异步登陆时传入", notes = "处理短信或者验证码,刷新页面等")
	public String shenbaoZuoFei(@RequestBody String str) throws Exception{
		String trace = TraceHelp.uuid();
		JSONObject jsonObject =JSONObject.parseObject(str);
		try {
			ParamsCheck.requestCheck(new String[] {"nsrsbh#String","sz#String","nsrdq#String"}, jsonObject);
			return appUtil.getShenbaoInterface("Shenbao_"+jsonObject.getString("nsrdq")).ShenbaoZuoFei(trace, jsonObject.getString("nsrsbh"), jsonObject);
		}catch (BusinessException e) {
			return ResultBean.ParamsFAIL( e.getMessage()).toString();
		} catch (Exception e) {
			log.error("asyncQueryRegister发生异常",e);
			return ResultBean.FAIL(500).toString();
		}

	}
	
	

}

package com.pangu.crawler.transfer.service;

import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryBusinessTransferRule;
import com.pangu.crawler.transfer.enums.AreaEnum;
import com.pangu.crawler.transfer.enums.FormEnum;
import com.pangu.crawler.transfer.enums.SzEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommonConfigService {

	@Autowired
	AsyncQueryBusinessTransferRule asyncQueryBusinessTransferRule;


	public Map<String,String> getDqMap(String code) throws Exception {
		return AreaEnum.getMapDq();
	}

	public Map<String,String> getSzMap(String dq) throws Exception {
		return SzEnum.getMapSz();
	}

	public Map<String,String> getSzMap(String[] szs) throws Exception {
		return SzEnum.getMapSz(szs);
	}

	public Map<String,String> getDqMap(String[] codes) throws Exception {
		return AreaEnum.getMapDq(codes);
	}

	public Map<String,String> getMapForm(String dq,String sz) throws Exception {
		return AreaEnum.getMapDq();
	}

	public Map<String,String> getFormsBysz(String szcode) throws Exception {
		return FormEnum.getMapForms(szcode).getForms();
	}

}
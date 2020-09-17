package com.pangu.crawler.transfer.service;

import com.pangu.crawler.business.dao.mongoDB.entity.AsyncTaskTimerEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryTaskTimerOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class TaskManagerService {


	@Autowired
	AsyncQueryTaskTimerOperation asyncQueryTaskTimerOperation;

	/**
	 * @查询规则数据
	 * @param params
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public Paging<AsyncTaskTimerEntity> queryHistoricalData(Map<String,String> params, Integer page, Integer limit) throws Exception {
		HelpDocListQuery docListQuery= new HelpDocListQuery();
		docListQuery.setPage(page);
		docListQuery.setPageSize(limit);
		docListQuery.setObjid(params.get("id"));
        docListQuery.setDatasz(params.get("taskname"));
        docListQuery.setStatus(params.get("taskstatus"));
		Paging<AsyncTaskTimerEntity>  paging= asyncQueryTaskTimerOperation.findTaskTimerDataList(docListQuery);
		return paging;
	}

	/**
	 * @description 修改规则文件服务
	 * @param rules
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> updateTaskDataByid(List<String> rules,Map<String, String> params) throws Exception {
		return null;//asyncQueryTaskTimerOperation.updateTaskDataByid(rules,params);
	}

	/**
	 * @descritiopn 更新定时任务信息
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> updateTaskDataByid(Map<String,String> params) throws Exception {
		Map<String,String> map = new HashMap<String,String>(7);
		map.put("id",params.get("id"));
		Map<String,String> result = new HashMap<>(2);
		Paging<AsyncTaskTimerEntity> paging = queryHistoricalData(map,null,null);
		map.put("startTime",params.get("startTime"));
		map.put("endTime",params.get("endTime"));
		map.put("taskid",params.get("taskid"));
		if(paging.getData().size()>0){
			AsyncTaskTimerEntity task = paging.getData().get(0);
			if((params.get("start")!=null&&params.get("start").equals("start"))||params.get("updatestatus")!=null){
			}else if(task.getStatus().equals("1")){
				result.put("code","fail");
				result.put("message","该定时任务正在启动中,请先停掉再做修改操作!");
				return result;
			}
		}else if(params.get("update")!=null&&!params.get("update").equals("add")){
			result.put("code","fail");
			result.put("message","对应名称定时任务的id没有获取到,请从新操作!");
			return result;
		}else if(params.get("update")!=null&&params.get("update").equals("add")){
			List<AsyncTaskTimerEntity> dataInfoEntity = asyncQueryTaskTimerOperation.findTaskTimerDataList(new HelpDocListQuery()).getData();
			for(AsyncTaskTimerEntity e :dataInfoEntity){
				if(e.getName().equals(params.get("name"))){
					result.put("code","fail");
					result.put("message","对应名称定时任务已经存在请更换任务名称!");
					return result;
				}
			}
		}
		return asyncQueryTaskTimerOperation.updateTaskDataByid(params);
	}

}
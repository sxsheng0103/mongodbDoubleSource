package com.pangu.crawler.transfer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncTaskTimerEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.TaskManagerService;
import com.pangu.crawler.transfer.service.TransferRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/7/20 12:13
 * @Version 1.0
 **/
@Controller
@Slf4j
@RequestMapping("task")
public class TaskManagerController {
    /***
     * 定时任务调度配置管理
     */
    @Autowired
    TransferRuleService transferRuleService;
    @Autowired
    TaskManagerService taskManagerService;
    @ResponseBody
    @RequestMapping("/queryTaskData")
    public JSON queryHistoricalData(Integer page, Integer limit, HttpServletRequest request) throws Exception {

        JSONObject json = new JSONObject();
        try {
            Map<String,String> map = new HashMap<String,String>(7);
            if(StringUtils.isNotEmpty(request.getParameter("taskname"))){
                map.put("taskname",request.getParameter("taskname"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("taskstatus"))){
                map.put("taskstatus",request.getParameter("taskstatus"));
            }
            Paging<AsyncTaskTimerEntity> paging = taskManagerService.queryHistoricalData(map,page,limit);
            json.put("code","0");
            json.put("msg","");
            json.put("count",paging.getTotalCount());
            json.put("data",paging.getJsondata());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return json;
        }
    }

    @ResponseBody
    @PostMapping("updateTaskTimer")
    public Map<String,String> updateTaskTimer(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String id = request.getParameter("task_id");
            String name = request.getParameter("task_name");
            String update = request.getParameter("task_update");
            String isDelay = request.getParameter("task_isDelay");
            String delayTime = request.getParameter("task_delayTime");
            String expressType = request.getParameter("task_expressType");
            String expressContent = request.getParameter("task_expressContent");
            String task_nsrdqs = request.getParameter("task_nsrdq");
            String task_szs = request.getParameter("task_sz");
            Map<String,String> params = new HashMap<String,String>();
            params.put("id",id);
            params.put("name",name);
            params.put("update",update);
            params.put("isDelay",isDelay);
            params.put("delayTime",delayTime);
            params.put("expressType",expressType);
            params.put("expressContent",expressContent);
            params.put("task_nsrdqs",task_nsrdqs);
            params.put("task_szs",task_szs);
            result = taskManagerService.updateTaskDataByid(params);
        }catch (Exception e){
            e.printStackTrace();
            log.error("添加任务失败!"+e.getMessage() );
        }finally {
            return result;
        }
    }

    @ResponseBody
    @PostMapping("startTask")
    public Map<String,String> startTaskTimer(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String id = request.getParameter("id");
            Map<String,String> params = new HashMap<String,String>();
            params.put("id",id);
            Paging<AsyncTaskTimerEntity> tasks = taskManagerService.queryHistoricalData(params,null,null);
            if(tasks.getData()!=null&&tasks.getData().size()>0){
                AsyncTaskTimerEntity task = tasks.getData().get(0);
                if(task.getStatus().equals("1")){
                    result.put("code","fail");
                    result.put("message","该任务在启动中,请勿重新启动");
                }else{
                    String taskid = transferRuleService.startSchedule(task);
                    result.put("code","success");
                    result.put("message","启动成功");
                    params.put("status","1");
                    params.put("update","update");
                    params.put("startTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    params.put("endTime","");
                    params.put("taskid",taskid);
                    result = taskManagerService.updateTaskDataByid(params);
                }
            }else{
                result.put("code","fail");
                result.put("message","未找到定时任务");
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error("启动任务失败!"+e.getMessage() );
            result.put("code","fail");
            result.put("message","启动任务失败"+e.getMessage());
        }finally {
            return result;
        }
    }

    @ResponseBody
    @PostMapping("stopTask")
    public Map<String,String> stopTaskTimer(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String id = request.getParameter("id");
            Map<String,String> params = new HashMap<String,String>();
            params.put("id",id);
            Paging<AsyncTaskTimerEntity> tasks = taskManagerService.queryHistoricalData(params,null,null);
            if(tasks.getData()!=null&&tasks.getData().size()>0){
                AsyncTaskTimerEntity task = tasks.getData().get(0);
                if(!task.getStatus().equals("1")){
                    result.put("code","fail");
                    result.put("message","该任务未启动,请刷新页面后操作");
                }else{
                    result = transferRuleService.stopSchedule(task);
                    if(result.get("code").equals("success")){
                        params.put("status","0");
                        params.put("update","update");
                        params.put("updatestatus","updatestatus");
                        params.put("endTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        result = taskManagerService.updateTaskDataByid(params);
                        result.put("code","success");
                        result.put("message","已停止");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("停止任务失败!"+e.getMessage() );
            result.put("code","fail");
            result.put("message","停止任务失败"+e.getMessage());
        }finally {
            return result;
        }
    }

}

//package com.pangu.crawler.transfer.utils;
//
//import com.pangu.crawler.business.service.async.ShedualManager;
//import com.pangu.crawler.framework.utils.UUIDUtils;
//import org.quartz.JobDataMap;
//import org.quartz.JobKey;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
//import org.springframework.scheduling.quartz.JobDetailFactoryBean;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @Author sheng.ding
// * @Date 2020/8/10 14:59
// * @Version 1.0
// **/
//@RequestMapping("task")
//@Controller
//public class TaskController {
//    /**
//     * 启动任务 需要自己完善逻辑，这里我用uuid作为taskCode 保证唯一
//     * 启动之前要通过数据库查询是否任务已经启动，如果启动了就不能启动了
//     * 启动成功了 要把数据库的任务状态改为启动中
//     */
//    @RequestMapping(value = "/start")
//    public void start() {
//        String uuid = UUIDUtils.get20UUID();
//        System.out.println(uuid);
//        startTask(uuid);
//
//    }
//
//    /**
//     * 停止任务 需要自己完善逻辑
//     * @param taskCode 传入启动任务时设置的taskCode参数
//     */
//    @RequestMapping(value = "/stop")
//    public void stop(String taskCode) {
//        endTask(taskCode);
//    }
//
//    /**
//     * 开始任务调度
//     *
//     * @param taskCode 任务名称 需要唯一标识，停止任务时需要用到
//     */
//    private void startTask(String taskCode){
//        //任务开始的corn表达式
//        String cronExpress = "*/5 * * * * ?";
//        if (cronExpress.indexOf("null") == -1) {
//            try {
//                JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
//                jobDetailFactoryBean.setName(taskCode);
//                jobDetailFactoryBean.setGroup(Scheduler.DEFAULT_GROUP);
//                //TaskJob.class 是任务所要执行操作的类
//                jobDetailFactoryBean.setJobClass(ShedualManager.class);
//                //任务需要的参数可以通过map方法传递，
//                Map map = new HashMap();
//                map.put("value", "我在传递参数");
//                jobDetailFactoryBean.setJobDataMap(getJobDataMap(map));
//                jobDetailFactoryBean.afterPropertiesSet();
//                CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
//                cronTriggerFactoryBean.setBeanName(taskCode);
//                cronTriggerFactoryBean.setCronExpression(cronExpress);
//                cronTriggerFactoryBean.setGroup(Scheduler.DEFAULT_GROUP);
//                cronTriggerFactoryBean.setName("cron_" + taskCode);
//                cronTriggerFactoryBean.afterPropertiesSet();
//                scheduler.scheduleJob(jobDetailFactoryBean.getObject(), cronTriggerFactoryBean.getObject());
//                System.out.println(taskCode+"任务启动成功");
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println(taskCode+"任务启动失败");
//            }
//        }
//    }
//
//
//    /**
//     * 结束任务调度
//     *
//     * @param taskCode
//     */
//    private void endTask(String taskCode)  {
//        try {
//            scheduler.deleteJob(JobKey.jobKey(taskCode, Scheduler.DEFAULT_GROUP));
//            System.out.println(taskCode+"任务停止成功");
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//            System.out.println(taskCode+"任务停止失败");
//        }
//    }
//
//
//    /**
//     * 将HashMap转为JobDataMap
//     * @param params
//     * @return
//     */
//    private JobDataMap getJobDataMap(Map<String, String> params) {
//        JobDataMap jdm = new JobDataMap();
//        Set<String> keySet = params.keySet();
//        Iterator<String> it = keySet.iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            jdm.put(key, params.get(key));
//        }
//        return jdm;
//    }
//
//
//
//    @Autowired
//    private Scheduler scheduler;
//}

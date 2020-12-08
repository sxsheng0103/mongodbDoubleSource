package utils;

/**
 * @Author sheng.ding
 * @Date 2020/8/10 14:53
 * @Version 1.0
 **/
/**
 * @PersistJobDataAfterExecution 和 @DisallowConcurrentExecution
 * 表示不让某个定时任务并发执行保证上一个任务执行完后，再去执行下一个任务
 */
/*@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class ShedualManager  implements Job {



    @Override
    public void execute(JobExecutionContext context)  {
        JobDataMap jobDataMap=context.getJobDetail().getJobDataMap();
        System.out.println("11"+new Date()+"    "+jobDataMap.getString("value")+"  "+context.getJobDetail().getKey());
    }


}*/

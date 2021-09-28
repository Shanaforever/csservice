package com.centricsoftware.task.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 定时任务
 * fixedRate：定义一个按一定频率执行的定时任务
 * fixedDelay：定义一个按一定频率执行的定时任务，与上面不同的是，改属性可以配合initialDelay， 定义该任务延迟执行时间。
 * cron：通过表达式来配置任务执行时间
 *
 * 一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。按顺序依次为：
 *
 * 秒（0~59）
 * 分钟（0~59）
 * 3 小时（0~23）
 * 4 天（0~31）
 * 5 月（0~11）
 * 6 星期（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
 * 年份（1970－2099）
 *
 * 每隔5秒执行一次：0/5 * * * * ?
 * 每隔1分钟执行一次：0 /1 * * ?
 * 0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
 * 0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
 *
 * 0 0 12 ? * WED 表示每个星期三中午12点
 *
 * “0 0 12 * * ?” 每天中午12点触发
 * “0 15 10 ? * *” 每天上午10:15触发
 * “0 15 10 * * ?” 每天上午10:15触发
 * “0 15 10 * * ? *” 每天上午10:15触发
 * “0 15 10 * * ? 2005” 2005年的每天上午10:15触发
 * “0 * 14 * * ?” 在每天下午2点到下午2:59期间的每1分钟触发
 * “0 0/5 14 * * ?” 在每天下午2点到下午2:55期间的每5分钟触发
 * “0 0/5 14,18 * * ?” 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
 * “0 0-5 14 * * ?” 在每天下午2点到下午2:05期间的每1分钟触发
 * “0 10,44 14 ? 3 WED” 每年三月的星期三的下午2:10和2:44触发
 * “0 15 10 ? * MON-FRI” 周一至周五的上午10:15触发
 * “0 15 10 15 * ?” 每月15日上午10:15触发
 * “0 15 10 L * ?” 每月最后一日的上午10:15触发
 * “0 15 10 ? * 6L” 每月的最后一个星期五上午10:15触发
 * “0 15 10 ? * 6L 2002-2005” 2002年至2005年的每月的最后一个星期五上午10:15触发
 * “0 15 10 ? * 6#3” 每月的第三个星期五上午10:15触发
 *
 * 快速编写cron表达式
 * @see <a href="http://cron.qqe2.com">http://cron.qqe2.com</a>
 * @author zheng.gong
 * @date 2020/4/21
 */
@Component
@Slf4j
public class TaskJob {

    /**
     * 按照标准时间来算，每隔 10s 执行一次
     */
    @Scheduled(cron = "${task.test}")
    public void job1() throws Exception {
//        log.info("【开始测试查询链接1】");
//        String queryXML = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"Style\" />\n" +
//                "<Attribute Path=\"\" Id=\"Node Name\" Op=\"EQ\" SValue=\"X2030019短袖T恤\" />";
//        Document document = NodeUtil.queryByXML(queryXML);
//        List<String> list = C8ResponseXML.resultNodeCNLs(document);
//        list.forEach(log::info);
        log.info("----------------------------从启动后每隔一段时间执行一次任务--------------------------");
    }

    /**
     * 从启动时间开始，间隔 60s 执行
     * 固定间隔时间
     */
//    @Scheduled(fixedRate = 60000)
//    public void job2() throws Exception{
//        log.info("【开始测试查询链接2】");
//        String queryXML = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"Style\" />\n" +
//                "<Attribute Path=\"\" Id=\"Node Name\" Op=\"EQ\" SValue=\"X2030019短袖T恤\" />";
//        Document document = NodeUtil.queryByXML(queryXML);
//        List<String> list = C8ResponseXML.resultNodeCNLs(document);
//        list.forEach(log::info);
//    }

//    /**
//     * 从启动时间开始，延迟 5s 后间隔 4s 执行
//     * 固定等待时间
//     */
//    @Scheduled(fixedDelay = 4000, initialDelay = 5000)
//    public void job3() throws Exception{
//        log.info("【开始测试查询链接3】");
//        String queryXML = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"Style\" />\n" +
//                "<Attribute Path=\"\" Id=\"Node Name\" Op=\"EQ\" SValue=\"X2030019短袖T恤\" />";
//        Document document = NodeUtil.queryByXML(queryXML);
//        List<String> list = C8ResponseXML.resultNodeCNLs(document);
//        list.forEach(log::info);
//    }
}
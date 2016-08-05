package com.smily.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smily.common.DateTimeUtils;

@Component
public class ScheduledTasks {
	private Log log = LogFactory.getLog(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		Calendar now = Calendar.getInstance();
		log.info("now.get(Calendar.YEAR) = " + now.get(Calendar.YEAR));
		log.info("now.get(Calendar.MONTH) = " + now.get(Calendar.MONTH));
		log.info("now.get(Calendar.DATE) = " + now.get(Calendar.DATE));
		log.info("now.get(Calendar.DAY_OF_MONTH) = " + now.get(Calendar.DAY_OF_MONTH));
		log.info("now.get(Calendar.DAY_OF_WEEK) = " + now.get(Calendar.DAY_OF_WEEK));
		log.info("now.get(Calendar.DAY_OF_WEEK_IN_MONTH) = " + now.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		log.info("now.get(Calendar.DAY_OF_YEAR) = " + now.get(Calendar.DAY_OF_YEAR));
		log.info("now.get(Calendar.WEEK_OF_MONTH) = " + now.get(Calendar.WEEK_OF_MONTH));
		log.info("now.get(Calendar.WEEK_OF_YEAR) = " + now.get(Calendar.WEEK_OF_YEAR));

		log.info("DateTimeUtils.getTheLastDayOfMonth() = " + DateTimeUtils.getTheLastDayOfMonth(now.getTime()));

		System.out.println("当前时间：" + dateFormat.format(now.getTime()));
	}

	@Scheduled(cron = "30 * * * * *")
	public void test() {
		log.info("test() has been executed...");
	}

}
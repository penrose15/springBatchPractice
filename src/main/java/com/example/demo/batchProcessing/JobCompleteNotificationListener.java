package com.example.demo.batchProcessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompleteNotificationListener implements JobExecutionListener {
    private static final Logger log =
            LoggerFactory.getLogger(JobCompleteNotificationListener.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompleteNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("select first_name, last_name from people ",
                    (rs, row) -> new Person(
                            rs.getString(1),
                            rs.getString(2)))
                    .forEach(person -> log.info("found<{{}}> in the database ", person));
        }
    }
}

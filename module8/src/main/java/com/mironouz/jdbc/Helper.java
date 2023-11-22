package com.mironouz.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Service
public class Helper {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public void failedUpdateWithDelay() {
        jdbcTemplate.update("update ids set id=?", 2);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    // Can be fixed by
    // @Transactional(isolation = Isolation.READ_COMMITTED)
    public void getOnce() {
        var values = jdbcTemplate.queryForList("select * from ids");
        System.out.println(values + " - should be 1");
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    // Can be fixed by
    // @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void getTwiceWithDelay() {
        var values = jdbcTemplate.queryForList("select * from ids");
        System.out.println(values + " - should be 1");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        values = jdbcTemplate.queryForList("select * from ids");
        System.out.println(values + " - should be 1");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    // Can be fixed by
    // @Transactional(isolation = Isolation.SERIALIZABLE)
    public void getTwiceWithDelayAndCondition() {
        var values = jdbcTemplate.queryForList("select * from ids where id < 2");
        System.out.println(values.size() + " - should be 1");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        values = jdbcTemplate.queryForList("select * from ids where id < 2");
        System.out.println(values.size() + " - should be 1");
    }
}

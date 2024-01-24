package com.mironouz.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class JdbcApplication implements CommandLineRunner {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	Helper helper;

	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}

	@Override
	public void run(String... args) throws InterruptedException {
		jdbcTemplate.update("insert into ids values(?)", 1);

		imitateDirtyRead();

		imitateNonRepeatableRead();

		imitatePhantomRead();
	}

	private void imitateDirtyRead() throws InterruptedException {
		System.out.println("Dirty Read Imitation");

		new Thread(() -> helper.failedUpdateWithDelay()).start();
		helper.getOnce();

		Thread.sleep(5000);
	}

	private void imitateNonRepeatableRead() throws InterruptedException {
		System.out.println("Non Repeatable Read Imitation");

		new Thread(() -> helper.getTwiceWithDelay()).start();

		Thread.sleep(100);

		jdbcTemplate.update("update ids set id=?", 2);

		Thread.sleep(5000);
	}

	private void imitatePhantomRead() throws InterruptedException {
		System.out.println("Phantom Read Imitation");

		jdbcTemplate.update("update ids set id=?", 1);

		new Thread(() -> helper.getTwiceWithDelayAndCondition()).start();

		Thread.sleep(100);

		for (int i = 0; i < 100; i++) {
			jdbcTemplate.update("insert into ids values(?)", 1);
		}

		Thread.sleep(5000);
	}
}

package vn.edu.clevai.bplog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.edu.clevai.common.api.config.EnableRestCommonApi;
import vn.edu.clevai.common.proxy.config.EnableAllProxyService;

@Slf4j
@EnableScheduling
@EnableAllProxyService
@EnableRestCommonApi
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class ClevaiBpLogServiceApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(ClevaiBpLogServiceApplication.class, args);
		log.info("Start clevai-bp-log-service");
	}

	@Override
	public void run(String... args) {
	}
} 
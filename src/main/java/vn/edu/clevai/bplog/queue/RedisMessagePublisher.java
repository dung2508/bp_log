package vn.edu.clevai.bplog.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.dto.redis.BpeEventDTO;
import vn.edu.clevai.bplog.dto.redis.BppProcessDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;

@Service
@Slf4j
public class RedisMessagePublisher implements MessagePublisher {

	@Autowired
	private RedisTemplate<?, ?> redisTemplate;

	@Autowired
	private ChannelTopic topic;

	@Override
	public void publish(Object message) {
		if (message instanceof BpeEventDTO) {
			log.trace("Send BpeEventDTO");
			redisTemplate.convertAndSend(topic.getTopic(), message);
		} else if (message instanceof BppProcessDTO) {
			log.trace("Send BppProcessDTO");
			redisTemplate.convertAndSend(topic.getTopic(), message);
		} else if (message instanceof BpsStepDTO) {
			log.trace("Send BpsStepDTO");
			redisTemplate.convertAndSend(topic.getTopic(), message);
		} else {
			log.warn("Cant process because message type does not match!!!");
		}
	}

}

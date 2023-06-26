package vn.edu.clevai.bplog.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.redis.BpeEventDTO;
import vn.edu.clevai.bplog.dto.redis.BppProcessDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;
import vn.edu.clevai.bplog.service.bplog.BppLog;
import vn.edu.clevai.common.api.model.DebuggingDTO;

@Slf4j
@Service
public class RedisMessageSubscriber implements MessageListener {

	private RedisSerializer<?> serializer;
	private BppLog bppLog;

	public RedisMessageSubscriber(RedisSerializer<?> serializer, BppLog bppLog) {
		this.serializer = serializer;
		this.bppLog = bppLog;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		Object obj = serializer.deserialize(message.getBody());
		try {
			log.info("Receive message {}", obj);
			if (obj instanceof BpeEventDTO) {
				BpeEventDTO dto = (BpeEventDTO) obj;
				log.info("Receive message bpe{}", dto);
				bppLog.saveBpeEvent(dto);
			} else if (obj instanceof BppProcessDTO) {
				BppProcessDTO process = (BppProcessDTO) obj;
				log.info("Receive message bpp{}", process);
				bppLog.saveBppProcess(process);
			} else if (obj instanceof BpsStepDTO) {
				BpsStepDTO step = (BpsStepDTO) obj;
				log.info("Receive message bps{}", step);
				bppLog.saveBpsStep(step);
			}
		} catch (Exception e) {
			log.error("Error when consume message {}: {}", obj, DebuggingDTO.build(e));
		}
	}

}

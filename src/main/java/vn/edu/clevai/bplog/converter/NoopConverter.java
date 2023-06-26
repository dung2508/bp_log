package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;

import java.util.List;

@Slf4j
public class NoopConverter extends BaseXConverter {
	public NoopConverter() {
		super(
				null,
				null,
				null,
				null,
				null,
				null
		);
	}

	@Override
	protected void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		/* Do nothing here. */
		log.info("Nothing to convert to X by {}", getClass().getSimpleName());
	}
}

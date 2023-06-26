package vn.edu.clevai.bplog.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.annotation.BPLogParamName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfdlDifficultgradeResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleBpLogComponent {

	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_XGG)
	public void doTestBpLog(String xst, @BPLogParamName("xClass_level_id") List<String> xClassLevelId,
							Map<String, String> code) {

	}

	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_XGG)
	public Map<String, String> doTestBpLog(String xst, @BPLogParamName("xClass_level_id") List<String> xClassLevelId) {
		Map<String, String> code = new HashMap<String, String>();
		code.put("kkkk", "mmmmm");
		// just parser with return is Object(Not in primitive String, Integer, long ...
		// AND Collection, Map)
		return code;
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_XGG)
	public InputSample doTestBpLog(String xst) {
		InputSample sample = InputSample.builder().build();
		sample.setData(Arrays.asList("1", "2", "5", "6"));
		Map<String, String> map = new HashMap<String, String>();
		map.put("aaaa", "bbbbb");
		sample.setXClassLevelId(map);
		return sample;
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.GET_DFDL_FROM_X)
	public BpDfdlDifficultgradeResponse getDFDLFromX(@BPLogParamName("XDFDL") Integer xdfdl) {
		return BpDfdlDifficultgradeResponse.builder().code("Hello world").description("Test").createdAt(DateUtils.now())
				.build();
	}

	@Data
	@Builder
	public static class InputSample {

		@BPLogParamName("xClass_level_id")
		private Map<String, String> xClassLevelId;

		@BPLogParamName("code")
		private List<String> data;
	}
}

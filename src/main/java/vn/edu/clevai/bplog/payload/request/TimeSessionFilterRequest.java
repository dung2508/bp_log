package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.edu.clevai.common.api.payload.request.BaseFilter;

import java.sql.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class TimeSessionFilterRequest extends BaseFilter {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private Date timeShiftDate;
}

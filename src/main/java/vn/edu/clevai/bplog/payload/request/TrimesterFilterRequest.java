package vn.edu.clevai.bplog.payload.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.edu.clevai.common.api.payload.request.BaseFilter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class TrimesterFilterRequest extends BaseFilter {

    private String nameLike;

    private String codeLike;

}

package components.core.pipe.child;

import java.util.List;
import components.core.filter.CommonFilter;
import components.core.pipe.CommonPipe;
import framework.FilterRegistry;

/** Source를 담당하는 Pipe */
public class SourcePipe extends CommonPipe {

	public SourcePipe(FilterRegistry registry) {
		super(registry);
	}

	@Override
	public String name() {
		return "SourcePipe";
	}

	@Override
	public List<CommonFilter> buildFilters(List<String> activeFilterNames) {
		// Source 영역에서 켜야 할 필터들만 인스턴스화
		return registry.instantiateByNames(activeFilterNames);
	}
}

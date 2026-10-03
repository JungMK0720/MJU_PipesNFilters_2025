package components.core.pipe.child;

import java.util.List;
import components.core.filter.CommonFilter;
import components.core.pipe.CommonPipe;
import framework.FilterRegistry;

/** 필터를 담당하는 Pipe */
public class MiddlePipe extends CommonPipe {

	public MiddlePipe(FilterRegistry registry) {
		super(registry);
	}

	@Override
	public String name() {
		return "MiddlePipe";
	}

	@Override
	public List<CommonFilter> buildFilters(List<String> activeFilterNames) {
		return registry.instantiateByNames(activeFilterNames);
	}
}

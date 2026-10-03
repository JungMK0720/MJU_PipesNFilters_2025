package framework;

import annotation.PipeType;
import components.core.filter.CommonFilter;

/**
 * FilterDescriptor ComponentScanner가 읽은 필터의 메타정보를 저장한다.
 */
public class FilterDescriptor {
	public final Class<? extends CommonFilter> type;
	public final String name;
	public final PipeType pipeType;
	public final String domain;

	public FilterDescriptor(Class<? extends CommonFilter> type, String name, PipeType pipeType, String domain) {
		this.type = type;
		this.name = (name == null || name.isBlank()) ? type.getSimpleName() : name;
		this.pipeType = pipeType;
		this.domain = (domain == null) ? "" : domain;
	}

	@Override
	public String toString() {
		return String.format("FilterDescriptor{name='%s', type=%s, pipeType=%s, domain='%s'}", name,
				type.getSimpleName(), pipeType, domain);
	}
}

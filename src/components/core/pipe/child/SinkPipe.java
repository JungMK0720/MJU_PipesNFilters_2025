package components.core.pipe.child;

import java.util.List;
import java.util.ArrayList;

import components.core.filter.CommonFilter;
import components.core.filter.child.sink.SinkFilter;
import components.core.pipe.CommonPipe;
import framework.FilterRegistry;

/** Sink를 담당하는 Pipe */
public class SinkPipe extends CommonPipe {
	private final String output;

	public SinkPipe(FilterRegistry registry, String outputFile) {
		super(registry);
		this.output = outputFile;
	}

	@Override
	public String name() {
		return "SinkPipe";
	}

	@Override
	public List<CommonFilter> buildFilters(List<String> activeFilterNames) {
		var list = new ArrayList<CommonFilter>();
		// SinkFilter는 항상 마지막에 존재
		SinkFilter sink = new SinkFilter();
		sink.setOutputFile(output);
		list.add(sink);
		return list;
	}
}

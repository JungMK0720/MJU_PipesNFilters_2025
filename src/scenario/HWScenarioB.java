package scenario;

import java.util.List;
import components.core.filter.CommonFilter;
import framework.FilterRegistry;
import framework.Scenario;

public enum HWScenarioB implements Scenario {
    B1(
        "SystemB.txt",
        List.of(
            "SourceStudents",
            "PrerequisiteSatisfiedFilter",
            "SinkFilter"
        )
    );

    private final String outputFile;
    private final List<String> filterOrder;

    HWScenarioB(String outputFile, List<String> filterOrder) {
        this.outputFile = outputFile;
        this.filterOrder = filterOrder;
    }

    public String outputFile() { return outputFile; }
    public List<String> filterOrder() { return filterOrder; }

    public List<CommonFilter> instantiateChain(FilterRegistry registry) {
        return registry.instantiateByNames(filterOrder);
    }
}

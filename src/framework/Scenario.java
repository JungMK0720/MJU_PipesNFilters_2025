package framework;

import java.util.List;
import components.core.filter.CommonFilter;

/** 모든 시나리오 공통 인터페이스 */
public interface Scenario {
    String outputFile();
    List<String> filterOrder();
    List<CommonFilter> instantiateChain(FilterRegistry registry);
}

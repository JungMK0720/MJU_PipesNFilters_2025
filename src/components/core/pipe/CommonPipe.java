package components.core.pipe;

import java.util.List;
import components.core.filter.CommonFilter;
import framework.FilterRegistry;

/** 모든 Pipe의 공통 추상 클래스 */
public abstract class CommonPipe {
    protected final FilterRegistry registry;

    public CommonPipe(FilterRegistry registry) {
        this.registry = registry;
    }

    /** Pipe 이름 */
    public abstract String name();

    /** Pipe가 포함할 Filter 목록을 조립 */
    public abstract List<CommonFilter> buildFilters(List<String> activeFilterNames) throws Exception;
}
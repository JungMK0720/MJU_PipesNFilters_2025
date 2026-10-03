package scenario;

import java.util.List;
import components.core.filter.CommonFilter;
import framework.FilterRegistry;
import framework.Scenario;

/**
 * HWScenario
 * 숙제 1~3의 Pipes & Filters 실행 시나리오 정의.
 * 현재 프로젝트 구조 기반 (Source / 3 Filters / Sink)
 */
public enum HWScenario implements Scenario {

    /** 숙제 1 : CS 전공 학생에게 과목 추가 + 비 CS 2013학번 삭제 */
    A1(
        "SystemA-1.txt",
        List.of(
            "SourceFilter",               // 입력
            "AddSubjectDeptCS",           // CS 학생 과목 추가
            "DeleteSubjectExceptDeptCS",  // 2013 + 비CS 과목 제거
            "SinkFilter"                  // 출력
        )
    ),

    /** 숙제 2 : EE 전공 학생에게 23456 과목 자동 추가 */
    A2(
        "SystemA-2.txt",
        List.of(
            "SourceFilter",
            "AddSubjectDeptEE",
            "SinkFilter"
        )
    ),

    /** 숙제 3 : 2013학번 중 CS 아닌 학생의 17651, 17652 삭제 */
    A3(
        "SystemA-3.txt",
        List.of(
            "SourceFilter",
            "DeleteSubjectExceptDeptCS",
            "SinkFilter"
        )
    );

    /* ============================================================
       필드 및 생성자
     ============================================================ */
    private final String outputFile;
    private final List<String> filterOrder;

    HWScenario(String outputFile, List<String> filterOrder) {
        this.outputFile = outputFile;
        this.filterOrder = filterOrder;
    }

    public String outputFile() {
        return outputFile;
    }

    public List<String> filterOrder() {
        return filterOrder;
    }

    /**
     * 시나리오에 정의된 이름 순서대로 Filter 인스턴스 생성
     */
    public List<CommonFilter> instantiateChain(FilterRegistry registry) {
        return registry.instantiateByNames(filterOrder);
    }
}

package framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import components.core.filter.CommonFilter;

/**
 * FilterRegistry (필터 등록소)
 * ComponentScanner가 찾아낸 모든 FilterDescriptor를 저장하고,
 * 시나리오(HWScenario)가 요청한 이름 순서대로 Filter 인스턴스를 생성해주는 역할을 한다.
 * 활성화 여부는 HWScenario에서 관리한다.
 */
public class FilterRegistry {

    /** @ComponentScanner가 등록한 전체 필터 메타 정보 목록 */
    private final List<FilterDescriptor> descriptors = new ArrayList<>();

    /** 필터 등록 */
    public void registerAll(List<FilterDescriptor> list) {
        descriptors.clear();
        descriptors.addAll(list);
        System.out.printf("[FilterRegistry] %d filter(s) registered.%n", list.size());
    }

    /** 이름으로 FilterDescriptor 조회 */
    public Optional<FilterDescriptor> findByName(String name) {
        for (FilterDescriptor d : descriptors) {
            if (d.name.equals(name)) return Optional.of(d);
        }
        return Optional.empty();
    }

    /**
     * 시나리오에서 지정한 이름 순서대로 인스턴스 생성
     * - 이름이 잘못되면 생략한다.
     */
    public List<CommonFilter> instantiateByNames(List<String> names) {
        List<CommonFilter> out = new ArrayList<>();
        for (String n : names) {
            Optional<FilterDescriptor> opt = findByName(n);
            if (opt.isEmpty()) {
                System.out.printf("[FilterRegistry] ⚠ Unknown filter name: %s (skip)%n", n);
                continue;
            }
            try {
                Class<? extends CommonFilter> clazz = opt.get().type;
                CommonFilter filter = clazz.getDeclaredConstructor().newInstance();
                out.add(filter);
                System.out.printf("[FilterRegistry] ✅ Instantiated: %s%n", n);
            } catch (Exception e) {
                System.out.printf("[FilterRegistry] ❌ Instantiate failed: %s (%s)%n",
                        n, e.getMessage());
            }
        }
        return out;
    }

    /** 등록된 필터 목록 문자열 출력 (디버깅용) */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FilterRegistry (등록된 필터 목록):\n");
        for (FilterDescriptor d : descriptors) {
            sb.append("  ").append(d).append("\n");
        }
        return sb.toString();
    }
}

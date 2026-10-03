package components.core.filter.child.merge;

import java.io.*;
import java.util.*;
import annotation.PipesFilter;
import annotation.PipeType;
import components.core.filter.impl.CommonFilterImpl;

@PipesFilter(name = "MergeFilter", type = PipeType.MIDDLE, domain = "Courses")
public class MergeFilter extends CommonFilterImpl {

    private static final Map<String, List<String>> prerequisiteMap = new HashMap<>();

    public static Map<String, List<String>> getPrerequisiteMap() {
        return prerequisiteMap;
    }

    @Override
    public boolean specificComputationForFilter() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;

        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length < 3) continue;

            String courseId = tokens[0];
            // 교수명, 과목명 이후의 토큰이 선수과목 리스트
            List<String> prereqs = new ArrayList<>();
            for (int i = 3; i < tokens.length; i++) {
                prereqs.add(tokens[i]);
            }

            prerequisiteMap.put(courseId, prereqs);
        }

        System.out.println("[MergeFilter] Loaded " + prerequisiteMap.size() + " course(s) with prerequisites");
        return true;
    }
}

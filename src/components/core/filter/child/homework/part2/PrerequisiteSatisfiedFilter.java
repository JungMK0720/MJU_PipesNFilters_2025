package components.core.filter.child.homework.part2;

import java.io.*;
import java.util.*;
import annotation.PipesFilter;
import annotation.PipeType;
import components.core.filter.child.loader.PrerequisiteLoader;
import components.core.filter.impl.CommonFilterImpl;

@PipesFilter(name = "PrerequisiteSatisfiedFilter", type = PipeType.MIDDLE, domain = "Students")
public class PrerequisiteSatisfiedFilter extends CommonFilterImpl {

    @Override
    public boolean specificComputationForFilter() throws IOException {
        Map<String, List<String>> prereqMap = PrerequisiteLoader.getPrerequisiteMap();

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter satisfied = new BufferedWriter(new FileWriter("Output-1.txt"));
        BufferedWriter unsatisfied = new BufferedWriter(new FileWriter("Output-2.txt"));

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length < 4) continue;

            Set<String> studentCourses = new HashSet<>(Arrays.asList(tokens).subList(4, tokens.length));
            boolean allSatisfied = true;

            for (String taken : studentCourses) {
                List<String> prereqs = prereqMap.get(taken);
                if (prereqs != null) {
                    for (String p : prereqs) {
                        if (!studentCourses.contains(p)) {
                            allSatisfied = false;
                            break;
                        }
                    }
                }
                if (!allSatisfied) break;
            }

            if (allSatisfied) satisfied.write(line + "\n");
            else unsatisfied.write(line + "\n");
        }

        satisfied.close();
        unsatisfied.close();

        System.out.println("[PrerequisiteSatisfiedFilter] Completed writing Output-1/Output-2");
        return true;
    }
}

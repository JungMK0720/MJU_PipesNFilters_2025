package components.core.filter.child.homework.part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import annotation.domain.DeptFilter;
import components.core.filter.impl.CommonFilterImpl;

@DeptFilter(name = "DeleteSubjectExceptDeptCS", department = "CS", description = "2013학번 + CS 아닌 학생의 17651, 17652 과목 제거")
public class DeleteSubjectExceptDeptCS extends CommonFilterImpl {

    @Override
    public boolean specificComputationForFilter() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length < 4) continue;

            String id = tokens[0];
            String dept = tokens[3];
            boolean is2013 = id.startsWith("2013");

            if (is2013 && !dept.equals("CS")) {
                // 17651, 17652 제거
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 4; i++) sb.append(tokens[i]).append(" ");
                for (int i = 4; i < tokens.length; i++) {
                    if (!tokens[i].equals("17651") && !tokens[i].equals("17652"))
                        sb.append(tokens[i]).append(" ");
                }
                bw.write(sb.toString().trim());
            } else {
                bw.write(line);
            }
            bw.newLine();
        }

        bw.flush();
        return true;
    }
}
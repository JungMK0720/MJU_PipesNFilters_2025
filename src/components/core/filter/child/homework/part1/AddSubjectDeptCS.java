package components.core.filter.child.homework.part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import annotation.domain.StudentsFilter;
import components.core.filter.impl.CommonFilterImpl;

@StudentsFilter(name = "AddSubjectDeptCS", description = "모든 CS 학생에게 12345, 23456 과목 자동 추가")
public class AddSubjectDeptCS extends CommonFilterImpl {

    @Override
    public boolean specificComputationForFilter() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length < 4) continue;
            
            String dept = tokens[3];
            if (dept.equals("CS")) {
                boolean has12345 = false, has23456 = false;
                for (int i = 4; i < tokens.length; i++) {
                    if (tokens[i].equals("12345")) has12345 = true;
                    if (tokens[i].equals("23456")) has23456 = true;
                }

                StringBuilder sb = new StringBuilder();
                for (String token : tokens) sb.append(token).append(" ");
                if (!has12345) sb.append("12345 ");
                if (!has23456) sb.append("23456 ");
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
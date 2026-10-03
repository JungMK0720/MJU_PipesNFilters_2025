package components.homework;

import framework.LifeCycleManager;
import scenario.HWScenarioB;
import components.core.filter.child.loader.PrerequisiteLoader;
import java.io.IOException;

public class HWSystemB {
    public static void main(String[] args) {
        System.out.println("[HWSystemB] Running System B...");

        try {
            // 1️. 선수 과목 미리 로드
            PrerequisiteLoader.preloadCourses("Courses.txt");

            // 2️. 이제 Students.txt만 필터링
            LifeCycleManager manager = new LifeCycleManager();
            manager.runScenario(HWScenarioB.B1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

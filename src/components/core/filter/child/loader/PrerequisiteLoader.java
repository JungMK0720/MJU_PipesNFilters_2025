package components.core.filter.child.loader;

import java.io.*;
import java.util.*;
import annotation.PipesFilter;
import annotation.PipeType;
import components.core.filter.impl.CommonFilterImpl;

@PipesFilter(name = "PrerequisiteLoader", type = PipeType.SOURCE, domain = "Courses")
public class PrerequisiteLoader extends CommonFilterImpl {

	private static final Map<String, List<String>> prereqMap = new HashMap<>();

	public static Map<String, List<String>> getPrerequisiteMap() {
		return prereqMap;
	}

	public static void preloadCourses(String filePath) throws IOException {
		prereqMap.clear();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.trim().split("\\s+");
				if (tokens.length < 3)
					continue;

				String courseId = tokens[0];
				List<String> prereqs = new ArrayList<>();
				for (int i = 3; i < tokens.length; i++) {
					prereqs.add(tokens[i]);
				}
				prereqMap.put(courseId, prereqs);
			}
		}
		System.out.println("[PrerequisiteLoader] Preloaded prerequisite map: " + prereqMap.size() + " entries");
	}

	@Override
	public boolean specificComputationForFilter() {
		return true;
	}
}

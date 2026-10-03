package components.core.filter.child.source;

import annotation.PipesFilter;
import annotation.PipeType;
import annotation.source.SourceFile;
import components.core.filter.SourceFilter;

// Courses.txt 소스 (System B 등에서 사용)
@PipesFilter(name = "SourceCourses", type = PipeType.SOURCE, domain = "Courses")
@SourceFile("Courses.txt")
public class SourceCourses extends SourceFilter {
}

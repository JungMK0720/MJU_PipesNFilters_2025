package components.core.filter.child.source;

import annotation.PipesFilter;
import annotation.PipeType;
import annotation.source.SourceFile;
import components.core.filter.SourceFilter;

// Students.txt 소스
@PipesFilter(name = "SourceStudents", type = PipeType.SOURCE, domain = "Students")
@SourceFile("Students.txt")
public class SourceStudents extends SourceFilter {
}

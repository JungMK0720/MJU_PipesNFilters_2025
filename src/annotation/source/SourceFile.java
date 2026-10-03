package annotation.source;

import java.lang.annotation.*;
import annotation.PipesFilter;
import annotation.PipeType;

/**
 * Source 필터 어노테이션
 * - 파일명 지정용 (Spring의 @Repository 같은 역할)
 */
@PipesFilter(type = PipeType.SOURCE, description = "Source File Reader")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SourceFile {
    String value() default "Students.txt";
}

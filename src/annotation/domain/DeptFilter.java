package annotation.domain;

import java.lang.annotation.*;
import annotation.PipesFilter;
import annotation.PipeType;

/**
 * 학과 관련 필터 그룹
 * - 메타 어노테이션 구조 (Spring의 @Service와 유사)
 */
@PipesFilter(type = PipeType.MIDDLE, description = "학과 관련 필터")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeptFilter {
    String name() default "";
    String department() default "";
    String description() default "";
}

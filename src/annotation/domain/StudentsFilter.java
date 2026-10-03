package annotation.domain;

import java.lang.annotation.*;
import annotation.PipesFilter;
import annotation.PipeType;

/**
 * 학생 관련 필터 그룹
 * - 내부적으로 @PipesFilter를 상속받는 메타 어노테이션
 * - Spring의 @Controller와 유사하게 동작
 */
@PipesFilter(type = PipeType.MIDDLE, description = "학생 도메인 관련 필터")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StudentsFilter {
    String name() default "";   
    String description() default "";
}

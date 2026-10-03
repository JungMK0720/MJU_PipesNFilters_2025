package annotation;

import java.lang.annotation.*;

/**
 * @Year: 24JAN2026
 * @Author: Minkyu Jung
 *
 * 모든 필터의 공통 메타 어노테이션(최상위 어노테이션) - Spring의 @Component 같은 역할
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PipesFilter {
	String name() default "";
	PipeType type() default PipeType.MIDDLE;
	String description() default "";
	String domain() default "";
}

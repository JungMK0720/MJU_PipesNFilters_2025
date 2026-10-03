package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 스테레오타입 전용 메타 어노테이션. - 도메인/타입을 반복 선언하지 않게 도와주는 용도. - ex) @StudentsFilter에서 사용되는 어노테이션
 * 어노테이션에 부착해서 사용.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface PipesStereotype {
	PipeType type() default PipeType.MIDDLE;
	String domain() default "";
}

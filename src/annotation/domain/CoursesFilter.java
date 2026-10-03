package annotation.domain;

import annotation.PipeType;
import annotation.PipesStereotype;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 과목 정보 관련 필터에 부착하는 도메인 스테레오타입 */
@PipesStereotype(type = PipeType.MIDDLE, domain = "Courses")
@Retention(RUNTIME)
@Target(TYPE)
public @interface CoursesFilter {}

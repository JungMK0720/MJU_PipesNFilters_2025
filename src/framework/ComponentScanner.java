package framework;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import annotation.PipeType;
import annotation.PipesFilter;
import components.core.filter.CommonFilter;

/**
 * ComponentScanner
 * "components" 패키지 하위에서
 * @PipesFilter 어노테이션이 붙은 Filter 클래스를 자동으로 탐색하여
 * 프레임워크에 등록할 수 있도록 FilterDescriptor 리스트로 반환한다.
 */
public class ComponentScanner {

    /**
     * scan() - 지정된 basePackage 하위에서 모든 @PipesFilter 클래스 검색
     * @param basePackage 예: "components"
     * @return FilterDescriptor 목록
     */
    public static List<FilterDescriptor> scan(String basePackage) {
        try {
            List<Class<?>> classes = findClasses(basePackage);
            List<FilterDescriptor> results = new ArrayList<>();

            for (Class<?> clazz : classes) {
                // CommonFilter 상속 여부 검사
                if (!CommonFilter.class.isAssignableFrom(clazz)) continue;
                @SuppressWarnings("unchecked")
                Class<? extends CommonFilter> filterClass = (Class<? extends CommonFilter>) clazz;

                // @PipesFilter 메타데이터 추출
                Optional<FilterDescriptor> descriptor = extractMetadata(filterClass);
                descriptor.ifPresent(results::add);
            }

            // 이름 중복 제거
            Map<String, FilterDescriptor> nameDuplRM = new LinkedHashMap<>();
            for (FilterDescriptor d : results) {
                nameDuplRM.put(d.name, d);
            }

            return new ArrayList<>(nameDuplRM.values());

        } catch (Exception e) {
            throw new RuntimeException("Component scan failed for package: " + basePackage, e);
        }
    }

    /**
     * extractMetadata() - 클래스의 @PipesFilter / @PipesStereotype 정보 추출
     */
    private static Optional<FilterDescriptor> extractMetadata(Class<? extends CommonFilter> filterClass) {
        // 1) 클래스에 직접 달린 @PipesFilter 우선
        PipesFilter direct = filterClass.getAnnotation(PipesFilter.class);
        if (direct != null) {
            String name = (direct.name() == null || direct.name().isBlank())
                    ? filterClass.getSimpleName()
                    : direct.name();
            PipeType type = direct.type();
            String domain = direct.domain() == null ? "" : direct.domain();
            return Optional.of(new FilterDescriptor(filterClass, name, type, domain));
        }

        // 2) 메타 어노테이션 경로 (@StudentsFilter, @DeptFilter, @SourceFile 등)
        for (Annotation ann : filterClass.getAnnotations()) {
            PipesFilter meta = ann.annotationType().getAnnotation(PipesFilter.class);
            if (meta != null) {
                // 기본값은 메타 @PipesFilter의 값
                PipeType type = meta.type();
                String domain = meta.domain() == null ? "" : meta.domain();

                // name 결정: 1. 메타 @PipesFilter.name 우선
                String name = meta.name();

                // 2. 메타 어노테이션 자체에 name() 속성이 있으면 그 값으로 오버라이드
                //    (ex): @StudentsFilter(name="AddSubjectDeptCS"))
                String overrideFromAnn = readStringAttributeIfExists(ann, "name");
                if (overrideFromAnn != null && !overrideFromAnn.isBlank()) {
                    name = overrideFromAnn;
                }

                // 3. 그래도 비었으면 클래스 simpleName
                if (name == null || name.isBlank()) {
                    name = filterClass.getSimpleName();
                }

                return Optional.of(new FilterDescriptor(filterClass, name, type, domain));
            }
        }

        // 3) @PipesFilter도 메타도 없으면 스킵
        return Optional.empty();
    }

    private static String readStringAttributeIfExists(Annotation ann, String attrName) {
        try {
            var m = ann.annotationType().getMethod(attrName);
            Object v = m.invoke(ann);
            if (v instanceof String) return (String) v;
        } catch (NoSuchMethodException ignore) {
            // 어노테이션에 해당 속성이 없으면 무시
        } catch (Exception e) {
            System.out.println("[ComponentScanner] meta attribute read failed: " + e.getMessage());
        }
        return null;
    }


    /* ============================================================
     * findClasses() — 실제 파일 시스템에서 클래스 목록 탐색
     * ============================================================ */
    private static List<Class<?>> findClasses(String basePackage) throws Exception {
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL res = resources.nextElement();
            String filePath = decode(res.getFile());
            dirs.add(new File(filePath));
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClassesInDirectory(directory, basePackage));
        }
        return classes;
    }

    // 한글/공백 경로 처리
    private static String decode(String filePath) throws UnsupportedEncodingException {
        return URLDecoder.decode(filePath, "UTF-8");
    }

    // 리플렉션을 위한 코드
    private static List<Class<?>> findClassesInDirectory(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) return classes;
        File[] files = directory.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                String className = packageName + '.' +
                        file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }
}

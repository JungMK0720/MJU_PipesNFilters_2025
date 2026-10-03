# PNF — Pipes & Filters Framework

Java로 직접 구현한 **Pipes & Filters 아키텍처 프레임워크**입니다.
어노테이션 기반 컴포넌트 스캔, 시나리오 기반 필터 체인 조립, 필터별 스레드 실행/모니터링을 제공하며,
학생 수강 데이터(`Students.txt`, `Courses.txt`)를 처리하는 두 개의 예제 시스템(System A / B)이 포함되어 있습니다.

## 주요 특징

- **어노테이션 기반 필터 등록** — `@PipesFilter`와 메타 어노테이션(`@StudentsFilter`, `@DeptFilter`, `@SourceFile`)을 붙이면 `components` 패키지 하위에서 자동으로 탐색·등록됩니다 (Spring의 `@Component` / `@Controller`와 유사).
- **시나리오 중심 조립** — `Scenario` 구현체(enum)에 출력 파일과 필터 이름 순서만 선언하면 체인이 만들어집니다. 필터 코드를 수정하지 않고 파이프라인 구성을 바꿀 수 있습니다.
- **스레드 + Piped Stream** — 각 필터는 독립 스레드로 실행되며 `PipedInputStream` / `PipedOutputStream`으로 연결됩니다.
- **런타임 ON/OFF** — 필터를 비활성화하면 해당 필터는 데이터를 그대로 통과(`passThrough`)시킵니다.
- **생명주기 관리** — `LifeCycleManager`가 인스턴스화 → 연결 → 실행 → 데몬 스레드 모니터링 → join을 처리하고, 여러 시나리오를 배치로 순차 실행합니다.

## 아키텍처

```
          ┌──────────────────────── LifeCycleManager ────────────────────────┐
          │                                                                  │
 Scenario ─► ComponentScanner ─► FilterRegistry ─► 필터 인스턴스 생성 ─► 연결 ─► 실행/모니터링
 (이름 순서)   (@PipesFilter 탐색)   (이름 → Descriptor)                      │
          └──────────────────────────────────────────────────────────────────┘

 [Source] ──pipe──► [Filter 1] ──pipe──► [Filter 2] ──pipe──► [Sink]
  (Thread)            (Thread)             (Thread)           (Thread)
```

| 구성 요소 | 역할 |
|---|---|
| `ComponentScanner` | `components` 패키지를 리플렉션으로 순회하며 `@PipesFilter`(직접/메타)가 붙은 `CommonFilter` 클래스를 `FilterDescriptor`로 수집 |
| `FilterRegistry` | 전체 `FilterDescriptor`를 보관하고, 시나리오가 지정한 이름 순서대로 필터 인스턴스를 생성 (알 수 없는 이름은 건너뜀) |
| `LifeCycleManager` | 체인 구성·연결·스레드 실행·모니터링·ON/OFF 토글·종료 |
| `Scenario` | `outputFile()`, `filterOrder()`, `instantiateChain()`을 정의하는 시나리오 인터페이스 |
| `CommonFilter` / `CommonFilterImpl` | 필터 공통 인터페이스 / 파이프 연결, `run()`, `passThrough()` 기본 구현 |

### 어노테이션

| 어노테이션 | 설명 |
|---|---|
| `@PipesFilter` | 최상위 메타 어노테이션. `name`, `type`(`SOURCE`/`MIDDLE`/`SINK`), `description`, `domain` |
| `@PipesStereotype` | 도메인/타입 반복 선언을 줄이기 위한 스테레오타입 전용 어노테이션 |
| `@StudentsFilter`, `@DeptFilter`, `@SourceFile` | `@PipesFilter`를 메타로 가지는 도메인별 어노테이션 |

## 프로젝트 구조

```
pnf
├── pom.xml
├── Students.txt / Courses.txt          # 입력 데이터
├── SystemA-1.txt ~ SystemA-3.txt       # System A 출력 결과
└── src                                  # (Eclipse 레이아웃 유지: sourceDirectory = src)
    ├── annotation/                      # PipesFilter, PipeType, 도메인 어노테이션
    ├── framework/                       # ComponentScanner, FilterRegistry, LifeCycleManager, Scenario
    ├── scenario/                        # HWScenario (A1~A3), HWScenarioB (B1)
    └── components/
        ├── core/
        │   ├── filter/                  # CommonFilter, SourceFilter, Sink/Source/Merge/Middle 및 homework 필터
        │   └── pipe/                    # CommonPipe, Source/Middle/SinkPipe
        └── homework/                    # 실행 진입점 HWSystemA, HWSystemB
```

## 예제 시스템

### System A — 학생 레코드 변환

입력 형식: `학번 성 이름 학과 과목1 과목2 ...`

| 시나리오 | 필터 체인 | 동작 | 출력 |
|---|---|---|---|
| `A1` | Source → `AddSubjectDeptCS` → `DeleteSubjectExceptDeptCS` → Sink | CS 학생에게 12345, 23456 추가 + 2013학번 비CS 학생의 17651, 17652 삭제 | `SystemA-1.txt` |
| `A2` | Source → `AddSubjectDeptEE` → Sink | EE 학생에게 23456 추가 | `SystemA-2.txt` |
| `A3` | Source → `DeleteSubjectExceptDeptCS` → Sink | 2013학번 비CS 학생의 17651, 17652 삭제 | `SystemA-3.txt` |

### System B — 선수과목 충족 검사

`Courses.txt`(`과목번호 담당교수 과목명 [선수과목...]`)를 먼저 `PrerequisiteLoader`로 적재한 뒤,
`Students.txt`의 각 학생이 수강한 모든 과목의 선수과목을 이수했는지 검사합니다.

- 체인(`B1`): `SourceStudents` → `PrerequisiteSatisfiedFilter` → `SinkFilter`
- 충족 학생 → `Output-1.txt`, 미충족 학생 → `Output-2.txt`

## 빌드 및 실행

요구 사항: **JDK 17+**, Maven 3.x

```bash
# 빌드
mvn compile

# System A 실행 (A1, A2, A3 배치)
java -cp target/classes components.homework.HWSystemA

# System B 실행
java -cp target/classes components.homework.HWSystemB
```

> 입력/출력 파일을 상대 경로로 읽고 쓰므로 **프로젝트 루트에서 실행**해야 합니다.
> `ComponentScanner`는 파일 시스템의 `.class` 디렉터리를 순회하므로 JAR이 아닌 `target/classes`(또는 IDE의 출력 폴더)로 실행하세요.

## 새 필터 추가하기

1. `CommonFilterImpl`을 상속하고 `specificComputationForFilter()`를 구현합니다.
2. `@PipesFilter(name = "...", type = PipeType.MIDDLE)` 또는 도메인 어노테이션을 붙입니다.
3. `components` 패키지 하위에 두면 자동 등록됩니다.
4. 시나리오의 `filterOrder`에 필터 이름을 원하는 위치에 추가합니다.

```java
@StudentsFilter(name = "MyFilter", description = "예시 필터")
public class MyFilter extends CommonFilterImpl {
    @Override
    public boolean specificComputationForFilter() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        String line;
        while ((line = br.readLine()) != null) {
            bw.write(line);   // 변환 로직
            bw.newLine();
        }
        bw.flush();
        return true;
    }
}
```

## 참고

- 필터 이름은 `@PipesFilter.name` → 메타 어노테이션의 `name` → 클래스 단순명 순으로 결정되며, 중복 이름은 마지막 등록이 우선합니다.
- 런타임 필터 토글은 `LifeCycleManager.toggleFilter(name, enabled)`로 제어합니다.

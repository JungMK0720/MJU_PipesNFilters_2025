package framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import components.core.filter.CommonFilter;
import components.core.filter.SourceFilter;
import components.core.filter.child.sink.SinkFilter;
import components.core.filter.impl.CommonFilterImpl;

/**
 * LifeCycleManager (생명주기 관리자)
 * Pipes & Filters 기반 Batch Orchestrator.
 *  - 시나리오(HWScenario)를 받아 필터 체인 생성 -> 연결 -> 실행 -> 모니터링
 *  - 다회 실행 (배치) 안정성 보장
 */
public class LifeCycleManager {

    /** 전체 필터 체인 (Source → ... → Sink) */
    private final List<CommonFilter> chain = new ArrayList<>();
    /** 각 필터 스레드 목록 */
    private final List<Thread> threads = new ArrayList<>();
    /** 모니터링 플래그 */
    private volatile boolean monitoring = true;
    /** 런타임 ON/OFF 상태 테이블 */
    private final java.util.concurrent.ConcurrentMap<String, Boolean> filterStates =
            new java.util.concurrent.ConcurrentHashMap<>();

    /** 여러 시나리오를 순차 배치 실행 */
    public void runBatch(Scenario... scenarios) {
        for (Scenario s : scenarios) {
            runScenario(s);
        }
    }

    /** 시나리오 한 번 실행 */
    public void runScenario(Scenario s) {
        try {
            resetForNextRun();
            FilterRegistry reg = new FilterRegistry();
            reg.registerAll(ComponentScanner.scan("components"));
            chain.clear();
            chain.addAll(s.instantiateChain(reg));

            chain.stream()
                 .filter(f -> f instanceof components.core.filter.child.sink.SinkFilter)
                 .findFirst()
                 .ifPresent(f -> ((components.core.filter.child.sink.SinkFilter) f).setOutputFile(s.outputFile()));

            connectChain();
            startAndMonitor();
            System.out.println("[LifeCycle] Scenario finished: " + s.getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            monitoring = false;
        }
    }

    /** 필터 체인 연결 (Source -> ... -> Sink) */
    private void connectChain() throws IOException {
        if (chain.size() < 2) {
            System.out.println("[LifeCycle] No enough filters found. (chain < 2)");
            TerminateNMoveSink();
            return;
        }
        System.out.println("[LifeCycle] Connecting filters...");
        for (int i = 0; i < chain.size() - 1; i++) {
            chain.get(i).connectOutputTo(chain.get(i + 1));
        }
    }

    /** 실행 + 모니터 + 조인 */
    private void startAndMonitor() {
        System.out.println("[LifeCycle] Starting filters...");
        threads.clear();

        for (CommonFilter f : chain) {
            filterStates.putIfAbsent(f.getClass().getSimpleName(), true);
            Thread t = new Thread(() -> {
                try {
                    if (isEnabled(f)) {
                        System.out.printf("[LifeCycle] %s is On!!%n",
                                          f.getClass().getSimpleName());
                        f.run();
                    } else {
                        System.out.printf("[LifeCycle] %s OFF → PassThrough%n",
                                          f.getClass().getSimpleName());
                        if (f instanceof CommonFilterImpl cfi) cfi.passThrough();
                    }
                } catch (IOException e) {
                    System.out.println("[LifeCycle] 필터 오류: " + e.getMessage());
                }
            }, f.getClass().getSimpleName());
            threads.add(t);
            t.start();
        }

        Thread monitor = new Thread(this::monitorThreads, "LifeCycle-Monitor");
        monitoring = true;
        monitor.setDaemon(true);
        monitor.start();
        System.out.println("[LifeCycle] Daemon Thread is monitoring...");

        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        monitoring = false;
    }

    /** 현재 필터가 활성화됐는지 */
    private boolean isEnabled(CommonFilter f) {
        return filterStates.getOrDefault(f.getClass().getSimpleName(), true);
    }

    /** 필터 토글 */
    public void toggleFilter(String name, boolean enabled) {
        filterStates.put(name, enabled);
        System.out.printf("[LifeCycle] Filter '%s' -> %s%n",
                name, enabled ? "ENABLED" : "DISABLED");
    }

    /** 모니터링 */
    private void monitorThreads() {
        System.out.println("[Monitor] Monitoring started.");
        while (monitoring) {
            try {
                Thread.sleep(100);
                long alive = threads.stream().filter(Thread::isAlive).count();
                System.out.printf("[Monitor] Alive Threads: %d / %d%n",
                                  alive, threads.size());
            } catch (InterruptedException e) { break; }
        }
        System.out.println("[Monitor] Monitoring stopped.");
    }

    /** 상태 초기화 */
    private void resetForNextRun() {
        threads.clear();
        chain.clear();
    }

    /** 필터 없을 때 백업 경로 */
    private void TerminateNMoveSink() {
        try {
            CommonFilter src = new SourceFilter();
            CommonFilter sink = new SinkFilter("NoFiltered.txt");
            src.connectOutputTo(sink);

            Thread t1 = new Thread(src, "SourceFilter");
            Thread t2 = new Thread(sink, "SinkFilter");
            t1.start(); t2.start();
            t1.join(); t2.join();

            System.out.println("[LifeCycle] 원본 파일을 그대로 출력했습니다. (NoFiltered.txt)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 시스템 중단 */
    public void shutdown() {
        monitoring = false;
        threads.forEach(Thread::interrupt);
        System.out.println("[LifeCycle] Shutdown requested.");
    }
}
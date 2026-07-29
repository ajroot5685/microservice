# 로컬 부트 매니저

7개 서비스를 매번 IDE로 기동/종료하는 번거로움을 없애기 위해 만든 로컬 프로세스 제어 대시보드입니다.
브라우저에서 각 서비스를 켜고/끄고 로그를 실시간으로 스트리밍하며, 재기동 시 살아있던 프로세스는 자동 복구합니다.

![local_boot_manager](../image/local_boot_manager.png)

## 사용 기술

- Spring Boot 4 + Thymeleaf (뷰) + WebFlux + WebMVC + WebSocket
- Java 가상 스레드(Virtual Threads)
- `ProcessBuilder` (`gradlew.bat bootRun`)
- PowerShell `Get-Content -Wait` (파일 tail)

## 핵심 포인트

- **프로세스 생명주기 관리**
    - 서비스별로 `ProcessBuilder("cmd.exe /c gradlew.bat bootRun")`로 기동하고 PID를 파일에 저장합니다.
    - 종료 시에는 `ProcessHandle.descendants()`로 자식 프로세스까지 트리 전체를 종료합니다.
    - 그래도 살아있으면 `taskkill /F /T`로 강제 종료합니다.
- **재기동 시 프로세스 자동 복구**
    - `@PostConstruct`에서 저장된 PID를 검사합니다.
    - OS에 살아있으면 로그 스트림만 다시 이어붙이므로, 대시보드를 재시작해도 실행 중인 서비스는 그대로 유지됩니다.
- **WebSocket 실시간 로그**
    - PowerShell `Get-Content -Wait -Tail 0`로 로그 파일을 tail해서 `LogWebSocketHandler`를 통해 브라우저에 push합니다.
    - 각 서비스별로 로그 스트림이 분리됩니다.
- **가상 스레드 활용**
    - 서비스 기동·로그 스트리밍처럼 블로킹 I/O가 많은 작업을 `Thread.startVirtualThread()`로 처리합니다.
    - 스레드 자원 부담을 최소화합니다.
- **매니저 자신의 로그도 브로드캐스트**
    - Logback 루트 로거에 커스텀 Appender를 붙여 매니저 자신의 로그도 대시보드로 실시간 스트리밍합니다.

## 관련 커밋

- `1-6` 로컬 부트 매니저 플랫폼 개발
- `1-8` config 서비스 로컬 부트 매니저에 추가 및 에러 수정
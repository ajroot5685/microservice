# 관측성(Observability) - 시도 및 폐기

분산 추적과 모니터링 대시보드를 붙이려던 시도입니다.
스택 자체를 세우는 지점까지 진행했으나 **Spring Boot 4 호환성 이슈로 폐기**했습니다.

## 시도한 스택

- OpenTelemetry (자동 계측 + Collector)
- Grafana + Tempo / Prometheus 계열 대시보드

## 폐기: 분산 추적과 모니터링 대시보 구축 (2-2, Revert됨)

- **폐기 사유**
    - OpenTelemetry의 Spring Boot 스타터·자동설정이 Spring Boot 4와 아직 제대로 호환되지 않았습니다.
    - 4.x 환경에서 정상 계측이 어렵고, 회피 방안을 유지하며 붙잡기엔 배보다 배꼽이 커서 config 파일까지 함께 되돌렸습니다.
- **다시 검토할 시점**
    - OpenTelemetry Java Instrumentation / Spring Boot 스타터 계열이 Spring Boot 4 지원을 마친 뒤 재시도합니다.
- **커밋 추적**
    - `87b121b` 2-2. 분산 추적 서비스 및 모니터링 대시보드 구축
    - `93144e8` open telemetry config 파일 업로드
    - `cb169a8` Revert "2-2. 분산 추적 서비스 및 모니터링 대시보드 구축"
    - `2fa3e6c` Revert "open telemetry config 파일 업로드"
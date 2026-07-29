# 서비스 간 통신 방식의 진화

서비스 간 동기 호출 방식을 단계적으로 교체하며 각 방식의 특성을 비교했습니다.
최종적으로 **`@HttpExchange` + Eureka 로드밸런서 + `RestClient`** 조합에 정착했습니다.

## 사용 기술

- Spring Cloud OpenFeign -> Spring 6 `@HttpExchange` (선언형 인터페이스)
- `RestClient` + `LoadBalancerInterceptor` (Eureka 서비스명 기반 로드밸런싱)

## 핵심 포인트

- **`@HttpExchange`로 이관**
    - OpenFeign은 별도 스타터·어노테이션에 의존적입니다.
    - Spring 6부터 제공하는 `@HttpExchange`는 프레임워크에 내장되어 의존성이 가볍고, `HttpServiceProxyFactory`로 유연하게 구성할 수 있습니다.
- **로드밸런싱 직접 조립**
    - `RestClient.builder()`에 `LoadBalancerInterceptor`를 주입해 `http://CATALOG-SERVICE` 같은 논리 URL을 Eureka 인스턴스로 해석하도록 만듭니다.
- **`userId` 자동 전파 인터셉터**
    - `ClientHttpRequestInterceptor`에서 `ScopedValue<String> USER_ID`를 읽어 다운스트림 호출에 `userId` 헤더로 자동 부착합니다.
    - 인증 컨텍스트가 서비스 경계를 넘어 유지됩니다.
- **에러 핸들링 통일**
    - `RestClientErrorHandler`로 4xx/5xx 응답을 도메인 예외(`CatalogNotFoundException`, `OutOfStockException`)로 매핑합니다.

## 폐기: gRPC 마이그레이션 (1-14, Revert됨)

`@HttpExchange` -> gRPC로 옮기는 커밋을 만들었다가 되돌렸습니다.

- **폐기 사유**
    - 가벼운 사이드 프로젝트 규모에 비해 유지보수 부담이 지나치게 컸습니다.
    - `.proto` 정의 -> 생성 스텁 -> 요청/응답 매핑 클래스 -> 서버·클라이언트 어댑터로 이어지면서 서비스 하나당 클래스 수가 급증합니다.
    - HTTP/JSON 대비 얻는 이점(스키마 강제·성능)보다 관리 비용이 더 커서 되돌렸습니다.
- **다시 검토할 시점**
    - 스키마 강제·양방향 스트리밍이 실제로 요구되는 규모나 도메인이라면 재검토할 가치가 있습니다.

## 관련 커밋

- `1-11` 내부 서비스 간 통신 - OpenFeign
- `1-13` OpenFeign -> HttpExchange 마이그레이션
- `1-14` HttpExchange -> gRPC 마이그레이션 (Revert)
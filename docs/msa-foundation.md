# MSA 뼈대 - 서비스 디스커버리 + 게이트웨이 + JWT

Eureka 서비스 디스커버리와 Spring Cloud Gateway를 조합한 마이크로서비스의 기반입니다.
인증은 유저 서비스가 JWT를 발급하고, 게이트웨이 필터에서 검증한 뒤 확인된 `userId`를 다운스트림 서비스로 전파하는 구조로 통일했습니다.

## 사용 기술

- Spring Boot 4.0.5 / Java 25 / Spring Cloud 2025.1.1
- Eureka Server / Spring Cloud Gateway (WebFlux)
- Spring Security + `jjwt` 0.12.5
- Java 21+ `ScopedValue` (요청 스코프 컨텍스트 전파)

## 핵심 포인트

- **게이트웨이에서 인증을 한 번만**
    - `AuthorizationHeaderFilter`가 JWT를 검증하고, 페이로드의 `userId`를 요청 헤더에 실어 다운스트림으로 넘깁니다.
    - 다운스트림 서비스는 토큰을 다시 파싱하지 않습니다.
- **`ScopedValue` 기반 유저 컨텍스트**
    - `UserContextFilter`가 `userId` 헤더를 `ScopedValue<String>`에 바인딩합니다.
    - 서비스·리포지토리 어느 계층에서든 `UserContext.getUserId()`로 접근 가능하며, `ThreadLocal`과 달리 불변·구조적 스코프라 훨씬 안전합니다.
- **화이트리스트로 인증 우회**
    - `/signup`, `/login`, `/actuator/health`는 필터에서 스킵합니다.
- **JWT는 대칭키(HS256)**
    - 시크릿은 config 서비스를 통해 주입합니다 (`token.yml`).

## 관련 커밋

- `1-1` 서비스 디스커버리·유저 서비스·API 게이트웨이 생성 및 연결 + 필터 설정
- `1-2` 유저 서비스 기본 구현
- `1-3` 유저 서비스 시큐리티 설정 및 JWT 로직 추가
- `1-4` api gateway 필터 정리
- `1-5` 오더/카탈로그 서비스 1차 완성, ScopedValue 사용
- `1-10` 유저 서비스 userId 필터 추가
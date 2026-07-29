# 중앙 설정 관리와 실시간 전파

각 서비스의 설정을 별도 디렉토리(`config-files/`)에 두고 config 서비스가 서빙합니다.
Spring Cloud Bus와 RabbitMQ를 이용해 재기동 없이 설정 변경을 실시간 전파합니다.

## 사용 기술

- Spring Cloud Config Server
- Spring Cloud Bus (AMQP)
- RabbitMQ

## 핵심 포인트

- **단일 진실 공급원**
    - DB 접속·JWT 시크릿·Eureka·Actuator 같은 공통 설정은 `config-files/common/*.yml`에 둡니다.
    - 서비스별 `yml`에서 `spring.config.import`로 재사용합니다.
- **실시간 전파**
    - `/actuator/busrefresh`를 호출하면 RabbitMQ를 통해 모든 서비스가 동시에 설정을 다시 읽습니다.
    - 서비스 재기동이 필요 없습니다.
- **부팅 순서**
    - config 서비스가 가장 먼저 뜨고, 다른 서비스는 `configserver:http://localhost:8888`로부터 설정을 받아온 뒤 기동합니다.

## 관련 커밋

- `1-7` 중앙 관리 config 서비스 추가
- `1-8` config 서비스 로컬 부트 매니저에 추가 및 에러 수정
- `1-9` 마이크로서비스 설정 실시간 일괄 전파
- `c955e92` cloud bus 충돌로 인한 group 지정
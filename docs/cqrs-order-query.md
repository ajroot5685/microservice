# CQRS - 주문 조회 레이어 분리

주문의 쓰기(MySQL)와 조회(MongoDB)를 분리했습니다.
주문 생성 이벤트를 Kafka로 흘려 MongoDB에 조회용 모델을 적재하고, 조회 API는 MongoDB를 향하도록 이원화했습니다.

## 사용 기술

- MySQL 8 (쓰기, `OrderEntity` / JPA)
- MongoDB 7 (조회, `OrderDocument` / Spring Data MongoDB)
- Kafka `order-create` 토픽으로 쓰기 -> 조회 동기화

## 핵심 포인트

- **저장소 이원화**
    - `OrderRepository`(JPA)로 트랜잭션성 쓰기를 처리합니다.
    - `OrderMongoRepository`로 조회에 최적화된 문서를 조회합니다.
- **이벤트 기반 동기화**
    - 주문 생성 시 order 서비스 내부의 Kafka 컨슈머가 자신이 발행한 이벤트를 소비해 `OrderDocument`를 MongoDB에 저장합니다.
    - 쓰기 성공과 조회 반영이 느슨하게 연결됩니다.
- **인덱스 설계**
    - `OrderDocument.userId`에 `@Indexed`를 부여합니다.
    - 사용자별 주문 목록 조회를 MongoDB 쪽에서 빠르게 처리합니다.

## 폐기: Elasticsearch 상품 검색 (2-4, Revert됨)

CQRS 확장으로 상품 검색을 Elasticsearch로 옮기려던 시도를 되돌렸습니다.

- **폐기 사유**
    - Spring Boot 4와 Spring Data Elasticsearch가 아직 제대로 호환되지 않는 시점이었습니다.
    - 스타터·클라이언트·자동설정이 4.x에서 정상 동작하지 않아 진행이 불가능했습니다.
- **다시 검토할 시점**
    - Spring Data Elasticsearch가 Spring Boot 4 지원을 마친 뒤 재시도합니다.

## 관련 커밋

- `2-3` CQRS - 주문 조회 레이어 분리
- `56b9763` 주문 조회 시 createdAt 응답 필드 추가
- `e345ab9` mongodb config 파일 업로드
- ~~`2-4 CQRS — 상품 검색 elastic search 적용~~ (Revert)
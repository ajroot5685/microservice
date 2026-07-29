# Kafka 기반 비동기 이벤트 - 주문 이벤트로 재고 갱신

주문이 생성되면 order 서비스가 `order-create` 토픽에 이벤트를 발행합니다.
catalog 서비스가 이를 구독해 재고를 감소시킵니다.
두 서비스 사이의 결합은 이벤트로 끊어, catalog 장애가 order 생성 흐름을 막지 않도록 분리했습니다.

## 사용 기술

- Apache Kafka (KRaft 모드, ZooKeeper 미사용)
- spring-kafka
- Kafka UI (모니터링)

## 핵심 포인트

- **주문 생성 시 catalog 검증 → 이벤트 발행**
    - `OrderService.createOrder()`가 `catalogServiceClient.getCatalog()`로 상품 존재/재고를 먼저 확인합니다.(`OutOfStockException`, `CatalogNotFoundException`)
    - 통과하면 주문을 저장한 뒤 Kafka로 이벤트를 발행합니다.
- **catalog 서비스의 리스너**
    - `@KafkaListener(topics = "order-create")`로 메시지를 수신합니다.
    - `productId`로 엔티티를 조회한 뒤 `stock -= qty`로 갱신 저장합니다.
- **KRaft 모드로 단순화**
    - `docker-compose.yml`에서 ZooKeeper 없이 Kafka 단일 노드가 컨트롤러와 브로커를 동시에 담당하도록 구성했습니다.
    - 로컬 자원 부담을 최소화합니다.

## 관련 커밋

- `1-12` Kafka - order 주문 시 비동기로 catalog 재고 업데이트
- `1-15` 주문 시 catalog 데이터 검증
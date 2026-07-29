# Redis Sorted Set 기반 선착순 대기열

한정 재고 상품의 선착순 주문을 처리하기 위한 대기열 시스템입니다.
Redis Sorted Set의 스코어를 요청 시각으로 활용해 자연스러운 FIFO를 구현하고, 재고 감소와 대기열 pop을 Lua 스크립트로 원자화했습니다.

## 사용 기술

- Redis 7 (Sorted Set + Hash)
- Redis Lua 스크립트 (`pop_queue.lua`)
- Spring `@Scheduled` (워커)

## 핵심 포인트

- **대기열 등록**
    - 유저가 `POST /orders/{productId}/queue`를 호출하면 `ZADD queue:{productId} <ts> <userId>`를 실행합니다.
    - 스코어(요청 시각)로 순위가 정해지므로 별도 카운터가 필요 없습니다.
- **원자적 재고 감소 + 대기 pop**
    - `pop_queue.lua` 안에서 `HGET catalog:X stock` -> 재고 확인 -> `ZPOPMIN queue:X` -> `HINCRBY stock -1`을 단일 Redis 호출로 실행합니다.
    - 경쟁 상태 자체가 발생할 수 없습니다.
      ```lua
      if tonumber(stock) <= 0 then return nil end
      local user = redis.call('ZPOPMIN', KEYS[2], 1)
      redis.call('HINCRBY', KEYS[1], 'stock', -1)
      return user[1]
      ```
- **워커 루프**
    - `QueueWorker`가 `@Scheduled(fixedDelay = 500)`로 스크립트를 실행합니다.
    - 유저를 한 명씩 처리해 주문을 저장합니다.
- **순위 조회 API**
    - `GET /orders/{productId}/queue`로 자신의 현재 대기 순번을 확인합니다 (`ZRANK + 1`).

## 관련 커밋

- `2-5` Redis sorted set 활용 - 대기열 시스템
- `b2d6518` redis config 파일 업로드
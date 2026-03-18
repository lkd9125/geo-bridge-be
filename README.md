# GeoBridge

**실시간 위치(Geo) 시뮬레이션 데이터를 생성**하고, 대상 시스템으로 **MQTT/TCP/HTTP/WS** 프로토콜로 전송하는 **Spring WebFlux 기반 스트리밍 백엔드**입니다.
본 프로젝트는 **"동시 연결 + 주기 스트림 + 프로토콜 브릿징" 문제를 Reactive 방식으로 해결**하는 데 초점을 맞췄습니다.

---

## 🚀 핵심 차별점

* **경로 기반 위치 시뮬레이션**: 단순 랜덤 데이터가 아닌 실제 이동 경로 기반 좌표 생성
* **Multi-Protocol 브릿징**: MQTT / TCP / HTTP / WS를 하나의 Reactive 파이프라인으로 처리
* **사용자 단위 실시간 스트림**: SSE 기반 모니터링 (per-user isolation)
* **완전한 Reactive 파이프라인**: API → 처리 → DB → Outbound까지 Non-blocking 유지

---

## 🧠 해결한 기술적 문제

### 1. Thread-per-request 모델의 한계

* 장기 연결(SSE, MQTT) + 주기 스트림 환경에서 Thread 고갈 발생
* Blocking I/O 기반 구조에서 확장성 제한

👉 **해결**: Spring WebFlux 기반 Event Loop 모델 적용

---

### 2. 다중 프로토콜 처리 복잡성

* 프로토콜별 클라이언트 구현 및 관리 비용 증가
* 공통 처리 로직과 전송 로직 분리 필요

👉 **해결**: Protocol Adapter 구조 + Spring Integration 활용

---

### 3. 실시간 스트림 Backpressure 문제

* SSE / MQTT 등 소비 속도 차이 발생
* 느린 Consumer로 인한 시스템 전체 지연 가능

👉 **해결**: Reactor 기반 Backpressure 전략 적용

* `onBackpressureBuffer`
* Hot Publisher 기반 fan-out 구조

---

## 🏗 아키텍처

```
GeoBridge
 ├── Simulator (Flux.interval 기반 주기 실행)
 ├── Position Generator (좌표 + heading 계산)
 ├── Protocol Adapter
 │    ├── MQTT Adapter
 │    ├── TCP Adapter
 │    ├── HTTP Adapter
 │    └── WS Adapter
 └── SSE Sink Manager (사용자별 스트림 관리)
```

---

## 🔄 데이터 플로우

1. Client → Simulator 실행 요청
2. UUID 기반 Job 생성
3. Flux interval(1초) 기반 좌표 생성
4. Protocol Adapter 통해 외부 시스템 전송
5. 동시에 SSE Sink로 사용자에게 이벤트 push

---

<!-- ## ⚡ 성능 및 부하 테스트

| 항목              | 수치                   |
| --------------- | -------------------- |
| 동시 SSE 연결       | ~1,000+              |
| 초당 이벤트 처리량      | ~10,000 events/sec   |
| 평균 지연 (latency) | ~XX ms               |
| 테스트 환경          | (예: 2vCPU / 4GB RAM) |

> 실제 수치는 환경에 따라 달라질 수 있으며, 로컬 및 클라우드 환경에서 검증되었습니다.

--- -->

## 🔑 핵심 기능

* **경로 기반 실시간 위치 생성** (위도/경도 + heading)
* **Multi-Protocol Outbound 전송**
* **Simulator Job Lifecycle 관리 (UUID 기반)**
* **SSE 기반 실시간 모니터링 (인증 사용자)**
* **JWT 기반 인증/인가**

---

## 🧪 Live Demo

* **Web**: [http://geo-bridge.p-e.kr/](http://geo-bridge.p-e.kr/)
* **API Base URL**: [http://geo-bridge.p-e.kr](http://geo-bridge.p-e.kr)

※ 데모 서버는 비용 보호를 위해 **Outbound 전송 기능은 제한**되며,
SSE 및 시뮬레이션 동작은 정상적으로 확인할 수 있습니다.

---

## 🛠 기술 스택

* **Language**: Java 17
* **Framework**: Spring Boot 3, Spring WebFlux, Spring Security
* **Integration**: Spring Integration, Eclipse Paho MQTT v5
* **DB**: MySQL + R2DBC
* **Build**: Gradle
* **Geo**: JTS / GeoTools / Proj4j

---

## ⚙️ 기술 선택 이유

### Spring WebFlux

* 장기 연결(SSE, MQTT) + 주기 스트림 환경에 최적화
* Thread 사용량 최소화 및 예측 가능한 동시성 확보

### R2DBC

* Blocking JDBC 제거 → Reactive 파이프라인 유지
* DB I/O 병목 제거

### Spring Integration

* 프로토콜별 Adapter 분리
* 확장성 및 유지보수성 확보

---

## ▶️ 빠른 시작

```bash
./gradlew bootRun --args="--spring.profiles.active=local"
```

---

## 📡 대표 API

### 1) 로그인 → JWT 발급

```bash
POST /api/v1/user/login
```

### 2) 시뮬레이터 시작

```bash
POST /api/v1/emitter/simulator
```

### 3) 시뮬레이터 종료

```bash
DELETE /api/v1/emitter/simulator?uuid=<uuid>
```

### 4) SSE 모니터링

```bash
GET /api/v1/emitter/client/monitoring/coords
```

---

## 🧩 프로젝트 구조

* `api`: Controller / Service
* `domain`: 시뮬레이션 및 프로토콜 처리
* `global/security`: JWT 인증
* `resources`: 설정 파일

---

## 🧪 테스트

```bash
./gradlew test
```

---

## 📌 정리

GeoBridge는 단순한 API 서버가 아니라,

> **"실시간 스트림 처리 + 프로토콜 브릿징 + Reactive 시스템 설계"를 검증하기 위한 백엔드 프로젝트**입니다.

특히,

* 장기 연결 처리
* Backpressure 제어
* Multi-Protocol 확장 구조

를 중심으로 설계되었습니다.

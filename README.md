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

## ⚡ 성능 검증 및 확장성 성과

> RabbitMQ(Docker)를 동일 호스트에서 실행하고, 시뮬레이터 **1,000개를 동시 기동**한 환경에서 부하 테스트를 수행했습니다.

---

### 🧩 문제 상황

GeoBridge는 다음과 같은 조건을 동시에 만족해야 했습니다.

- 장기 연결 (SSE, MQTT 등)
- 시뮬레이터별 주기적 메시지 생성 (1초 단위)
- 다중 프로토콜로의 동시 전송

기존 Thread-per-request 기반 구조에서는  
동시 연결 수 증가 시 **스레드 점유 및 컨텍스트 스위칭 비용 증가로 인한 병목 발생 가능성**이 존재했습니다.

---

### ⚙️ 해결 전략

- **Spring WebFlux 기반 Event Loop 모델 적용**
- 시뮬레이터를 **경량 Reactive Stream(Flux)** 으로 설계
- 각 연결이 **Thread를 점유하지 않도록 Non-blocking 구조 유지**
- RabbitMQ를 활용하여 실제 메시징 환경과 유사한 조건에서 검증

---

### 📊 성능 측정 결과

| 항목 | 결과 |
|------|------|
| 환경 | Apple MacBook M1 (로컬 단일 머신) |
| 메시지 브로커 | RabbitMQ (Docker) |
| 동시 시뮬레이터 수 | 1,000개 |
| CPU 사용률 | **10% 미만** |
| 힙 메모리 사용량 | **500MB 미만** (`-Xms2g -Xmx2g` 기준) |

---

### 🔍 결과 분석

- 1,000개의 동시 시뮬레이터 환경에서도 **CPU 사용률이 낮게 유지되어 스레드 병목이 발생하지 않음을 확인**
- 메모리 사용량이 안정적으로 유지되어 **대량 연결 환경에서도 예측 가능한 리소스 사용 패턴 확보**
- Event Loop 기반 Non-blocking 구조를 선택한 설계가 **고동시성 환경에서도 안정적으로 동작함을 검증**

---

### 🚀 성과

- **대규모 동시 연결 환경에서의 Non-blocking 아키텍처 효과 검증**
- 시뮬레이터 수 증가 시에도 **선형 확장 가능한 구조 설계 및 확인**
- GeoBridge가 **병목 지점이 되지 않도록 설계되었음을 실측 기반으로 검증**

---

> ⚠️ 로컬 환경에서의 비공식 테스트이며, 페이로드 크기 및 네트워크 조건에 따라 결과는 달라질 수 있습니다.

---

## 🏗 아키텍처

```
GeoBridge
├── Simulator (Flux.interval 기반 주기 실행)
├── Position Generator (좌표 + heading 계산)
├── Protocol Adapter
│ ├── MQTT Adapter
│ ├── TCP Adapter
│ ├── HTTP Adapter
│ └── WS Adapter
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

## 🔑 핵심 기능

* **경로 기반 실시간 위치 생성** (위도/경도 + heading)
* **Multi-Protocol Outbound 전송**
* **Simulator Job Lifecycle 관리 (UUID 기반)**
* **SSE 기반 실시간 모니터링 (인증 사용자)**
* **JWT 기반 인증/인가**

---

## 🧪 Live Demo

* **Web**: http://geo-bridge.p-e.kr/
* **API Base URL**: http://geo-bridge.p-e.kr

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
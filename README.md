## 요구사항 분석

- **userId 기준으로 선착순 30명에게 특강 신청 기능을 제공한다. (기능1)**
    - 선착순의 기준은 어떻게 할 것인가?
        - DB에 입력된 순서로 한다.
    - 동시성 문제는 어떻게 해결한 것인가?
        - 분산 서버 환경이라는 조건이 있기 때문에 DB 락을 사용한다.
    - 동일한 신청자는 동일한 강의에 대해서 한 번의 수강 신청만 성공할 수 있다.
    - 중복 신청 된다면 맨 처음의 수강 신청을 제외한 나머지 요청들은 실패한다.
    - 한 강의에 이미 신청자가 30명이 초과 되면 이후 신청자는 요청을 실패한다.
- **날짜별로 현재 신청 가능한 특강 목록을 조회하는 기능을 제공한다. (기능2)**
    - 로그인 된 사용자가 이미 신청한 강의를 제외하고 강연 시작일이 입력받은 날짜인 강연들의 목록을 제공한다.
    - 각 항목은 특강 ID 및 이름, 강연자 정보를 보여주어야 한다.
    - 사용자는 각 특강에 신청하기 전에 목록을 조회해 볼 수 있다.
- **특정 userId로 신청 완료된 특강 목록을 조회하는 기능을 제공한다. (기능3)**
    - 각 항목은 특강 ID 및 이름, 강연자 정보를 보여주어야 한다.

<br>

## API 명세서

- 특강 신청 API
    - POST `/api/lectures/apply`
    - REQUEST:
    
    ```jsx
    {
    	"lectureId" : long
    }
    ```
    

- 특강 취소 API
    - POST `/api/lectures/cancel`
    - REQUEST:
    
    ```jsx
    {
    	"lectureId" : long
    }
    ```
    

- (사용자별) 날짜별로 특강 신청 가능한 목록 조회 API
    - GET `/api/lectures?status=available&date={date}`
    
- (사용자별) 특강 신청이 완료된 목록 조회 API
    - GET `/api/lectures?status=applied`

<br>

## ERD

![image](https://github.com/user-attachments/assets/57cde6e9-2600-4bd0-8146-e99731cbdac3)

### ERD 구성 이유

  강연에 대한 정보를 저장하는 `Lecture` 테이블과 수강 이력을 저장하는 `ENROLLMENT_HISTORY` 테이블로 DB를 구성하였다.

  처음에는 `Lecture` 테이블에 현재 수강 신청 인원 수를 저장하는 컬럼을 두지 않고, `ENROLLMENT_HISTORY` 테이블의 데이터를 `COUNT`하는 방식으로 구현하려 했다. 그러나 이 방식은 `COUNT`를 할 때마다 동시성 문제로 인해 전체 테이블에 락을 걸어야 하므로 성능에 큰 부담을 줄 수 있다는 우려가 있었다. 이를 해결하기 위해 `Lecture` 테이블에 현재 수강 신청 인원 수를 추적하는 컬럼을 추가하였다.

  또한, 강의 취소 기능을 구현하는 과정에서 수강 이력 테이블에 `status` 컬럼을 추가하였고, 이를 통해 사용자가 강의를 신청하거나 취소한 상태를 관리할 수 있게 하였다. `ENROLLMENT_HISTORY` 테이블은 사용자와 강의 간의 맵핑 관계를 저장하고, 수강 이력 정보를 한 곳에 기록할 수 있도록 설계하였다. 이를 통해 특정 유저가 어떤 강의에 신청 중인지 확인할 때, 강의 ID와 유저 ID를 기준으로 등록일을 최신순으로 정렬하여 최상위 데이터를 조회하여 확인할 수 있다.

<br>

## 패키지 구조

```jsx
com.example.project
 ├── presentation
 │    ├── controller
 │    └── dto
 ├── domain
 │    ├── code
 │    ├── entity
 │    ├── repository
 │    ├── service
 │    └── usecase
 ├── infra
 │    └── repository
 │          └── impl
```
<br>

## 동시성 처리에 대한 접근 과정

### 조건

- 분산 서버 환경에서도 문제 없어야 함.
    - 분산 서버 환경일 경우, 여러 서버가 동시에 동일한 데이터에 접근할 수 있게 된다.

### 방법

1. 분산 락(Distributed Lock)
    - Redis 기반(SETNX 이용)
    - Zookeeper 기반 락(Ephemeral Node와 Watchers 기능 이용)
2. 레벨드락(Level locking)
    - 비관적 락(Pessimistic Locking)
        - 트랜잭션끼리의 충돌이 발생한다고 가정하고 우선 락을 거는 방법
        - DB에서 제공하는 락기능 사용
        - SELECT FOR UPDATE 사용 = DB에 락 거는 방법
        
        ```jsx
        SELECT * FROM courses WHERE course_id = ? FOR UPDATE;
        ```
        
        - 특정ROW에 배타적 LOCK을 거는 행위
        - JPA에서는 @Lock 어노테이션 사용
    - 낙관적 락(Optimistic Locking)
        - 각 레코드에 version 필드를 추가하여 업데이트 시 버전 번호를 확인한다.
        - 데이터가 변경되면 버전이 변경되므로 다른 서버가 해당 데이터를 업데이트하려 하면 충돌이 발생한다.
        
        ```jsx
        UPDATE courses 
        SET available_slots = available_slots - 1, 
        version = version + 1 
        WHERE course_id = ? AND version = ?;
        ```
        
3. 메세지 큐(Message Queues)
    - RabbtMQ, Kafka 등을 사용하여 요청을 큐에 넣고, 각 서버가 큐에서 하나씩 처리하는 방법

### 결론

  분산 서버 환경에서는 Redis나 Kafka를 사용하거나, 데이터베이스 락을 적용하는 방법이 있었다. 이번 프로젝트에서는 Redis와 Kafka를 사용하지 않기로 했으므로, DB 락을 사용해야만 했다. DB 락을 선택하는 데 있어, 낙관적 락과 비관적 락 두 가지 방법을 선택할 수 있었다.

  낙관적 락의 경우 버전을 관리하는 컬럼을 추가해야 한다는 부담이 있었다. 반면, 비관적 락은 데이터베이스에서 특정 행에 대해 락을 거는 방식으로 구현이 간단하고, 별도의 컬럼 추가 없이도 동시성 문제를 해결할 수 있었다. 이로 인해 비관적 락이 본 프로젝트에 적합하다고 판단하여 선택했다.

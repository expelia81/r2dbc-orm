# r2dbc-orm 버전 1
## 라이브러리 동작 순서
### 쿼리 생성
- 조회하려는 객체와 연관관계를 매핑해 객체를 만든다.(v1 완료)
- TODO : 쿼리는 캐시될 수 있어야한다. (v2 예정)
- TODO : static으로 구현된 현재에서 인터페이스 임시 구현체를 주입하는 구조로 변경한다.
- TODO : OneToMany / ManyToMany를 할 수 있는 깊이를 선택할 수 있도록 한다.
### 쿼리 실행
- r2dbc database client를 통해 쿼리를 수행한다. (v1 완료)
  - TODO : database client를 파라미터가 아니라 주입할 구현체에서 주입받아 사용한다.
### 쿼리 결과 매핑
- 수행된 결과물을 객체에 동적으로 매핑한다. (v1 완료)

### 연관 관계 매핑 및 규합
- 매핑된 객체의 연관 관계를 탐색하고, 적절한 관계끼리 묶는다. (단, OneToMany 속에 있는 다중 관계는 허용하지않는다.) (v1 완료)
- TODO : OneToMany / ManyToMany를 할 수 있는 깊이를 선택할 수 있도록 한다.
- TODO : OneToMany 속의 OneToMany도 구현할 수 있도록 한다.



# order-project

## 프로젝트 구조 및 설계

### 도메인 주도 설계
> 마이크로 서비스 아키텍처의 컨셉을 생각하면 비즈니스 도메인 중심으로 서비스를 모델링하고 구현하는 것이 중요하다.  
> 이 때 각각의 복잡한 도메인을 모델링하고 표현력있게 설계하는 것을 도메인 주도 설계(DDD)라고 한다.  

### Layer 구조
> 레이어간의 참조 관계에서는 단방향 의존을 유지하고 계층간 호출에서는 인터페이스를 통한 호출이 되도록 한다.

|Layer|Description|주요 객체
|-----|----|----|
|사용자 인터페이스 (interfaces)|사용자에게 정보를 보여주고 사용자의 명령을 해석하는 책임을 진다.|Controller, Dto, Mapper(Converter)
|응용 계층 (Application)|수행할 작업을 정의, 도메인 객체가 문제를 해결하게 한다.|Facad
|도메인 계층 (domain)|업무 개념과 업무 상황에 대한 정보, 업무 규칙을 표현하는 일을 책임진다. 업무 상황을 반영하는 상태를 제어하고 사용하며, 상태 저장과 관련된 기술적인 세부사항은 인프라 스트럭처에 위임한다.|Entity, Service, Command, Criteria, Info, Reader, Store, Exceutor, Factory
인프라 스트럭쳐 계층(infrastructure)|상위 계층을 지원하는 기술적 기능을 제공한다.|ReaderImpl, StoreImpl, Spring JPA, RedisConnector...)


### Layer 간 참조 관계
- Layer 간의 참조 관계에서 application 과 Infrastructure 는 domain layer 를 바라
보게 하고 양방향 참조는 허용하지 않게 한다.  
- domain layer 는 low level 의 기술에 상관없이 독립적으로 존재할 수 있어야 한다.  
- 이를 위해 대부분의 주요 로직은 추상화되고, runtime 시에는 DIP 개념을 활용하
여 실제 구현체가 동작하게 한다.  


### Domain Layer
1. domain layer 에서의 Service 에서는 해당 도메인의 전체 흐름을 파악할 수 있도록 구
현되어야 한다.  
    - 이를 위해서는 추상화 레벨을 많이 높여야 한다.
        - 도메인 로직에서는 어떤 기술을 사용했는지는 중요하지 않다. 어떤 업무를 어
떤 순서로 처리했는지가 더욱 중요한 관심사이다
        - 도메인 업무는 적절한 interface 를 사용하여 추상화하고 실제 구현은 다른
layer 에 맡기는게 맞다.

    - 세세한 기술 구현은 Service 가 아니라 Infrastructure 의 implements 클래스에
위임하고, Service 에서는 이를 활용하기 위한 interface 를 선언하고 사용한다.
        - DIP 를 활용하여 도메인이 사용하는 interface 의 실제 구현체를 주입 받아
(injection) 사용할 수 있도록 한다.
        - 영속화된 객체를 로딩하기 위해 Spring JPA 를 사용할 수도 있지만 MyBatis
를 사용할 수도 있는 것이다. domain layer 에서는 객체를 로딩하기 위한 추상
화된 interface 를 사용하고, 실제 동작은 하위 layer 의 기술 구현체에 맡긴다
는 것이 핵심이다.  


2. Service 간에는 참조 관계를 가지지 않도록 한다.
    - Service 내의 로직은 추상화 수준을 높게 가져가고, 각 추상화의 실제 구현체는 잘게 쪼개어 만들면 도메인의 전체 흐름이 파악되면서도 로직이 간결하게 유지되는 코드를 가져갈
수 있다.


### Infrastructure Layer
1. domain layer 에 선언되고 사용되는 추상화된 interface 를 실제로 구현하여 runtime
시에는 실제 로직이 동작하게 한다.

2. 세세한 기술 스택을 활용하여 domain 의 추상화된 interface 를 구현하는 것이므로 비교적 구현에서의 자유도를 높게 가져갈 수 있다

3. Service 간의 참조 관계는 막았지만, Infrastructure layer 에서의 구현체 간에는 참조
관계를 허용한다
    - Infrastructure 에서의 구현체는 domain layer 에 선언된 interface 를 구현하는
경우가 대부분이므로 Service 에 비해 의존성을 많이 가지지 않게 된다.
    - 로직의 재활용을 위해 Infrastructure 내의 구현체를 의존 관계로 활용해도 된다.


### Application Layer 
1. transaction 으로 묶여야 하는 도메인 로직과 그 외의 로직을 aggregation 하는 역할로 한정 짓는다.

### Interface Layer
1. 사용자에게 정보를 보여주고 사용자의 명령을 해석하는 책임을 진다.
2. API 를 설계할 때에는 없어도 되는 Request Parameter 는 제거하고, 외부에 리턴하
는 Response 도 최소한을 유지하도록 노력하자
3. http, gRPC, 비동기 메시징과 같은 서비스간 통신 기술은 Interfaces layer 에서만 사
용되도록 하자
    - 가령 json 처리 관련 로직이나 http cookie 파싱 로직 등이 Domain layer 에서 사
용되는 식의 구현은 피해야 한다.
    - 그렇게 하지 않으면 언제든지 교체될 수 있는 외부 통신 기술로 인해 domain 로직
까지 변경되어야 하는 상황이 발생한다

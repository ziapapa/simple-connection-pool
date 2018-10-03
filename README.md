# simple-connection-pool
### Feature
#### 1. JSON 파일 설정에 따라 여러 개의 커넥션 풀을 구성.
> * poolName : 커넥션 풀을 구분하는 이름
> * maxConnections : 최대 연결 커넥션 풀의 갯수
> * minConnections : 최소 연결 커넥션 풀의 갯수
> * increment : 최소에서 추가 커넥션 요청이 있을 경우 증가 갯수
> * userName : db userName
> * password : db password
> * url : db url
> * driverClassName : db driver
> * connectionWaitTimeOut : 커넥션이 없을 경우 대기시간
> * validationQuery : 유효성 체크 Query
> * defaultPool : 디폴트가 true 일경우 커넥션 풀 이름 없이 호출 할 수 있음 (하나만 존재해야함)
##### 1.1 예시( dbpool.json )
<pre><code>
[
    {
        "poolName": "db1",
        "maxConnections": 10,
        "minConnections": 5,
        "increment": 1,
        "userName": "testA",
        "password": "test1234",
        "url": "jdbc:mysql://localhost:3306/world?serverTimezone=UTC&useSSL=false&autoReconnect=true&validationQuery=select 1",
        "driverClassName": "com.mysql.cj.jdbc.Driver",
        "connectionWaitTimeOut": 1000,
        "validationQuery": "select 1",
        "defaultPool": "true"
    },
    {
        "poolName": "db2",
        "maxConnections": 20,
        "minConnections": 5,
        "increment": 1,
        "userName": "testB",
        "password": "test1234",
        "url": "jdbc:mysql://localhost:3306/world?serverTimezone=UTC&useSSL=false&autoReconnect=true&validationQuery=select 1",
        "driverClassName": "com.mysql.cj.jdbc.Driver",
        "connectionWaitTimeOut": 1000,
        "validationQuery": "select 1",
        "defaultPool": "false"
    }
]

</code></pre>

#### 2. 사용된 커넥션은 반환되어 재사용 됨.
#### 3. Pool의 리소스를 모니터링 할 있음.
#### 4. 사용자 이벤트로 신규 리소스 할당을 중지 가능함.
#### 5. 멀티스레드 환경에서도 Pool 동기화됨.
#### 6. minConnections 커넥션 수의 이상이 요청될 경우 increment 만큼씩 커넥션을 추가 할당함. (maxConnections 까지)
#### 7. maxConnections을 초과하는 요청이 있을 경우 connectionWaitTimeOut 만큼 대기후 커넥션을 할당 또는 TimeOutException 을 발생 시킴.
#### 8. 커넥션을 얻을 때 마다 유효성 체크를 하고 오류가 발생할 경우 Retry 3회까지 진행함.
#### 9. logging은 logback 을 사용하였음.
#### 10. JUnit를 사용하여 단위 테스트 작성함.

***

### Example

* Initialize as a json file
```
PoolFactory pf = new PoolFactory("dbpool.json");
```
* getConnectionPool
```
ConnectionPool cp = PoolFactory.getPool();
or
ConnectionPool cp = PoolFactory.getPool("poolName");
```
* getConnection
```
Connection conn = cp.getConnection();
or 
Connection conn = PoolFactory.getPool().getConnection();
```
* Monitor
```
PoolMonitor cm = PoolFactory.getPoolMonitor();
or
PoolMonitor cm = PoolFactory.getPoolMonitor("poolName");

cm.getCurrentPoolSize();
cm.getFreeConnections();
cm.getUseConnections();
```

## Test Code
<pre><code>
PoolFactory pf = new PoolFactory("dbpool.json");

ConnectionPool cp = PoolFactory.getPool();
PoolMonitor cm = PoolFactory.getPoolMonitor();
Connection conn = cp.getConnection();

//monitor after getConnection 
System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
System.out.println("FreeConnections : " + cm.getFreeConnections());
System.out.println("UseConnections : " + cm.getUseConnections());

Statement stmt = conn.createStatement();
boolean bool = stmt.execute(cm.getConfigMonitor().getValidationQuery());
System.out.println("execute : " + bool);
stmt.close();
cp.returnConnection(conn);

//monitor after returnConnection
System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
System.out.println("FreeConnections : " + cm.getFreeConnections());
System.out.println("UseConnections : " + cm.getUseConnections());
</code></pre>


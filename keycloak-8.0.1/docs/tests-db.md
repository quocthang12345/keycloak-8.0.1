Thử nghiệm với các cơ sở dữ liệu khác nhau 
===========================

MySQL
-----

Cách đơn giản nhất để thử nghiệm với MySQL là sử dụng chính thức [MySQL docker image](https://registry.hub.docker.com/_/mysql/).

Bắt đầu với MySQL:

    docker run --name mysql -e MYSQL_DATABASE=keycloak -e MYSQL_USER=keycloak -e MYSQL_PASSWORD=keycloak -e MYSQL_ROOT_PASSWORD=keycloak -d mysql
   
Chạy tests:

    mvn install -Dkeycloak.connectionsJpa.url=jdbc:mysql://`docker inspect --format '{{ .NetworkSettings.IPAddress }}' mysql`/keycloak -Dkeycloak.connectionsJpa.driver=com.mysql.jdbc.Driver -Dkeycloak.connectionsJpa.user=keycloak -Dkeycloak.connectionsJpa.password=keycloak    
    
Dừng MySQl:

    docker rm -f mysql
    
    
PostgreSQL
----------

Cách đơn giản nhất để thử nghiệm với PostgreSQL là sử dụng chính thức  [PostgreSQL docker image](https://registry.hub.docker.com/_/postgres/).

Bắt đầu với PostgreSQL:

    docker run --name postgres -e POSTGRES_DATABASE=keycloak -e POSTGRES_USER=keycloak -e POSTGRES_PASSWORD=keycloak -e POSTGRES_ROOT_PASSWORD=keycloak -d postgres
   
Chạy tests:

    mvn install -Dkeycloak.connectionsJpa.url=jdbc:postgresql://`docker inspect --format '{{ .NetworkSettings.IPAddress }}' postgres`:5432/keycloak -Dkeycloak.connectionsJpa.driver=org.postgresql.Driver -Dkeycloak.connectionsJpa.user=keycloak -Dkeycloak.connectionsJpa.password=keycloak    
    
Dừng PostgreSQL:

    docker rm -f postgres
    
MariaDB
-------

Cách đơn giản nhất để thử nghiệm với mariadb là sử dụng chính thức [MariaDB docker image](https://registry.hub.docker.com/_/mariadb/).

Bắt đầu với MariaDB:

    docker run --name mariadb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=keycloak -e MYSQL_USER=keycloak -e MYSQL_PASSWORD=keycloak -d mariadb:10.1
   
Chạy tests:

    mvn install -Dkeycloak.connectionsJpa.url=jdbc:mariadb://`docker inspect --format '{{ .NetworkSettings.IPAddress }}' mariadb`/keycloak -Dkeycloak.connectionsJpa.driver=org.mariadb.jdbc.Driver -Dkeycloak.connectionsJpa.user=keycloak -Dkeycloak.connectionsJpa.password=keycloak    
    
Dừng MySQl:

    docker rm -f mariadb

Sử dụng hồ sơ tích hợp để chạy thử nghiệm cơ sở dữ liệu bằng các vùng chứa docker 
-------

Dự án cung cấp hồ sơ cụ thể để chạy thử nghiệm cơ sở dữ liệu bằng các vùng chứa. Dưới đây là một mẫu các hồ sơ được triển khai. Để có danh sách đầy đủ, vui lòng gọi (`mvn help:all-profiles -pl testsuite/integration-arquillian | grep -- db- | grep -v allocator`):

* `db-mysql`
* `db-postgres`

Như một ví dụ, để chạy thử nghiệm bằng MySQL container docker trên máy chủ xác thực sóng.

    mvn -f testsuite/integration-arquillian clean verify -Pdb-mysql

Nếu bạn muốn chạy thử nghiệm bằng cách sử dụng phân phối keycloak được định cấu hình trước (thay vì Undertow):

    mvn -f testsuite/integration-arquillian clean verify -Pdb-mysql,jpa,auth-server-wildfly

Lưu ý rằng bạn phải luôn kích hoạt hồ sơ 'jpa' khi sử dụng máy chủ xác thực. 

Nếu lệnh mvn thất bại vì bất kỳ lý do nào, nó cũng có thể không xóa vùng chứa 
Phải được xóa theo cách thủ công. 

Cho các cơ sở dữ liệu oracle, không có trình điều khiển jdbc hay hình ảnh công khai 
Do hạn chế cấp phép và yêu cầu chuẩn bị môi trường. Anh  
Đầu tiên cần tải trình điều khiển jdbc và cài đặt nó cho người ủy thác maven địa phương 
(feel free để chỉ định GAV và tập tin theo người mà bạn sẽ download): 

    mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0 -Dpackaging=jar -Dfile=ojdbc7.jar -DgeneratePom=true

Sau đó xây dựng hình ảnh docker trên mỗi hướng dẫn 
https://github.com/oracle/docker-images/tree/master/OracleDatabase. Cuối cùng 
Bước này chạy có thể yêu cầu cập nhật `jdbc.mvn.groupId`,
`jdbc.mvn.artifactId`, và `jdbc.mvn.version` Theo các thông số bạn dùng trong lệnh trên , và `docker.database.image` Nếu bạn dùng khác tên hoặc thẻ cho hình ảnh. 

Lưu ý rằng các vùng chứa docker có thể chiếm một số không gian ngay cả sau khi chấm dứt, và 
Đặc biệt với cơ sở dữ liệu có thể là một gigabyte. Vì thế 
Bạn nên chạy "docker system prune" đôi khi để đòi lại không gian đó. 


Sử dụng DB Allocator Service
-------

Testsuite có thể sử dụng dịch vụ db allocator để phân bổ và phát hành cơ sở dữ liệu mong muốn. 
Vì một số thuộc tính cơ sở dữ liệu (such là url jdbc, tên người dùng hoặc password) cần được sử dụng khi xây dựng máy chủ xác thực, 
Việc phân bổ và phân bổ cần phải xảy ra khi xây dựng dự án "integration-arquillian" của dự án (Thay vì `tests/base` khi nó xảy ra trong các trường hợp khác ). 

Để sử dụng dịch vụ db allocator, bạn phải sử dụng hồ sơ `jpa` với một trong các `db-allocator-*`. Đây là ví dụ đầy đủ để chạy JPA với Auth Server Wildfly và MSSQL 2016: 

```
mvn -f testsuite/integration-arquillian/pom.xml clean verify \
    -Pjpa,auth-server-wildfly,db-allocator-db-mssql2016 \
    -Ddballocator.uri=<<db-allocator-servlet-url>> \
    -Ddballocator.user=<<db-allocator-user>> \
    -Dmaven.test.failure.ignore=true
```

Sử dụng `-Dmaven.test.failure.ignore=true` Không được yêu cầu nghiêm ngặt nhưng được khuyến nghị cao. Sau khi chạy thử nghiệm Plugin DB Allocator sẽ phát hành cơ sở dữ liệu được cấp phát. 

**Chú ý**: Nếu bạn đã giết một ví dụ đầu tiên của maven chắc chắn với tổ hợp phím CTRL-C hoặc 'kill - 9' command) 
Vui lòng thả ra cơ sở dữ liệu được cấp phát. Vui lòng kiểm tra `dballocator.uri` và thêm '?operation=report" vào cuối url. 
Tìm DB của bạn trong GUI và thả nó theo cách thủ công. 

Dưới đây là một mẫu các hồ sơ được triển khai. Để có danh sách đầy đủ, vui lòng gọi  (`mvn help:all-profiles -pl testsuite/integration-arquillian | grep -- db-allocator-db-`):

* `db-allocator-db-postgres` - Thử nghiệm với Postgres 9. 6. x 
* `db-allocator-db-mysql` - Thử nghiệm với MySQL 5.7 

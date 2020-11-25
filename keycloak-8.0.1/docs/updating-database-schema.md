Updating Database Schema
========================

Keycloak hỗ trợ tự động di chuyển cơ sở dữ liệu sang một phiên bản mới. Điều này được thực hiện bằng cách áp dụng một hoặc nhiều bộ thay đổi 
Với cơ sở dữ liệu hiện tại. Điều này có nghĩa là nếu bạn cần làm bất kỳ thay đổi nào đối với lược đồ cơ sở dữ liệu bạn cần tạo 
Thay đổi có thể thay đổi giản đồ cũng như bất kỳ dữ liệu hiện có nào. 

Điều này bao gồm các thay đổi đối với:
 
* Realm entities
* User entities
* User session entities
* Event entities

 
Tạo sự thay đổi 
-------------------------

Chúng tôi sử dụng Liquibase để hỗ trợ cập nhật cơ sở dữ liệu. Các bộ thay đổi nằm trong 
[`model/jpa/src/main/resources/META-INF`](../model/jpa/src/main/resources/META-INF).
Có một tập tin riêng cho mỗi bản phát hành đòi hỏi phải thay đổi cơ sở dữ liệu. 

Để tạo sự thay đổi theo cách thủ công, hãy thêm tệp mới vào vị trí ở trên cùng với tên  `jpa-changelog-<version>.xml`. Tập tin này có thể chứa một  `change-set` với `id` bằng với phiên bản tiếp theo để phát hành và  `author` Đặt thành email của bạn địa chỉ. Sau đó hãy xem tài liệu Liquibase về cách viết tập tin này. Thêm tham chiếu vào tệp này trong 
[`jpa-changelog-master.xml`](../model/jpa/src/main/resources/META-INF/jpa-changelog-master.xml).
Tệp phải có một thay đổi duy nhất change-set và id của change-set sẽ là phiên bản tiếp theo để phát hành. 

Bạn cũng có thể có Liquibase và Hibernate tạo ra một cái cho bạn. Để làm theo các bước này: 

1. Delete existing databases  
   `rm keycloak*h2.db`
2. Create a database of the old format:  
   `mvn -f connections/jpa-liquibase/pom.xml liquibase:update -Durl=jdbc:h2:keycloak`
3. Make a copy of the database:  
   `cp keycloak.h2.db keycloak-old.h2.db`    
3. Run KeycloakServer to make Hibernate update the schema:  
   `mvn -f testsuite/utils/pom.xml exec:java -Pkeycloak-server -Dkeycloak.connectionsJpa.url='jdbc:h2:keycloak' -Dkeycloak.connectionsJpa.databaseSchema='development-update'`
4. Wait until server is completely started, then stop it
5. View the difference:                                       
   `mvn -f connections/jpa-liquibase/pom.xml liquibase:diff -Durl=jdbc:h2:keycloak-old -DreferenceUrl=jdbc:h2:keycloak`
6. Create a change-set file:
   `mvn -f connections/jpa-liquibase/pom.xml liquibase:diff -Durl=jdbc:h2:keycloak-old -DreferenceUrl=jdbc:h2:keycloak -Dliquibase.diffChangeLogFile=changelog.xml`    
    
Điều này sẽ tạo ra nhật ký `changelog.xml`. Khi đã tạo chỉnh sửa tệp và kết hợp tất cả các tập tin `change-sets` vào một nhóm đơn lẻ và thay đổi' id' thành phiên bản tiếp theo để phát hành và `author` đến địa chỉ email của bạn. Vậy thì hãy thực hiện theo các bước trên để sao chép vào đúng vị trí và cập nhật bản thay đổi `jpa-changelog-master.xml`. Anh phải theo cách thủ công thêm mục nhập vào `change-sets` để cập nhật dữ liệu hiện có nếu yêu cầu. 

Khi bạn có cập nhật thay đổi - tập trung có thể xác thực giản đồ cho bạn. Lần đầu tiên chạy: 

    rm -rf keycloak*h2.db
    mvn -f testsuite/utils/pom.xml exec:java -Pkeycloak-server -Dkeycloak.connectionsJpa.url='jdbc:h2:keycloak' -Dkeycloak.connectionsJpa.databaseSchema='update'
    
Khi máy chủ bắt đầu hoàn toàn, hãy dừng lại và chạy: 
    
    mvn -f testsuite/utils/pom.xml exec:java -Pkeycloak-server -Dkeycloak.connectionsJpa.url='jdbc:h2:keycloak' -Dkeycloak.connectionsJpa.databaseSchema='development-validate'


Testing database migration
--------------------------

Lấy dữ liệu từ một phiên bản cũ của Keycloak bao gồm các ứng dụng demo. Bắt đầu máy chủ với cái này và thử nghiệm. 

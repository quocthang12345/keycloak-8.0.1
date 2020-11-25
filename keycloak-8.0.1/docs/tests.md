Thực hiện kiểm thử 
===============

Browser
-------

Testsuite sử dụng selen. Theo mặc định, nó sử dụng HtmlUnit WebDriver, nhưng cũng có thể được thực hiện bằng chrome hoặc firefox. 

Để chạy thử nghiệm với firefox thêm  `-Dbrowser=firefox` hoặc với Chrome thì thêm `-Dbrowser=chrome`

Database
--------

Theo mặc định, testsuite sử dụng cơ sở dữ liệu h2 được nhúng để thử nghiệm với các cơ sở dữ liệu khác  (Database Testing)[tests-db.md].

Test utils
==========

Tất cả utils có thể được thực hiện từ thư mục testsuite / utils: 

    cd testsuite/utils

Keycloak server
---------------

Để bắt đầu một máy chủ Keycloak cơ bản để thử nghiệm: 

    mvn exec:java -Pkeycloak-server
    
or run org.keycloak.testsuite.KeycloakServer Từ IDE yêu thích của bạn !
     
Khi bắt đầu máy chủ, nó cũng có thể nhập một realm từ tệp json: 

    mvn exec:java -Pkeycloak-server -Dimport=testrealm.json
    
Khi bắt đầu máy chủ, giao thông https có thể được thiết lập bởi cài đặt keystore chứa chứng chỉ máy chủ và https port, tùy chọn cài đặt truststore. 

    mvn exec:java -Pkeycloak-server \
        -Djavax.net.ssl.trustStore=/path/to/truststore.jks \
        -Djavax.net.ssl.keyStore=/path/to/keystore.jks \
        -Djavax.net.ssl.keyStorePassword=CHANGEME \
        -Dkeycloak.port.https=8443

### Default admin account

Quản trị viên mặc định trong master realm được tạo bằng thông tin đăng nhập: 
* Username: `admin`
* Password: `admin`

Máy chủ bài kiểm tra keycloak sẽ tự động tạo người dùng vương quốc mới khi các điều kiện sau được đáp ứng 
* Thuộc tính `keycloak.createAdminUser` đặt thành `true` (Mặc định là " true " nếu không hiện diện)
* Không có người dùng hiện tại trong the master realm

### Live edit of html and styles

Máy chủ thử nghiệm Keycloak có thể tải các tài nguyên trực tiếp từ hệ tập tin thay vì classpath. Điều này cho phép chỉnh sửa html, kiểu và cập nhật hình ảnh mà không khởi động lại máy chủ. Để sử dụng các tài nguyên từ hệ tập tin bắt đầu bằng: 

    mvn exec:java -Pkeycloak-server -Dresources
    
Bạn cũng có thể chỉ định thư mục chủ đề được sử dụng bởi máy chủ: 

    mvn exec:java -Pkeycloak-server -Dkeycloak.theme.dir=<PATH TO THEMES DIR>
    
Sử dụng các chủ đề ví dụ chạy máy chủ bằng: 

    mvn exec:java -Pkeycloak-server -Dkeycloak.theme.dir=examples/themes
    
**Chú ý:** Nếu `keycloak.theme.dir` Chỉ định các chủ đề mặc định (base, rcue và keycloak) được tải từ classpath 

TOTP codes
----------

Để tạo mã totp mà không có google authenticator:

    mvn exec:java -Ptotp
    
hoặc run org.keycloak.testsuite.TotpGenerator từ IDE yêu thích của bạn!

Khi bắt đầu copy/paste bí mật totp và nhấn enter. Để sử dụng bí mật mới chỉ copy/paste và nhấn lại enter. 

Mail server
-----------

Để bắt đầu một máy chủ thư thử nghiệm cho việc kiểm tra gửi email:

    mvn exec:java -Pmail-server
    
or run org.keycloak.testsuite.MailServer from your favourite IDE!

Để định cấu hình keycloak để sử dụng các thuộc tính hệ thống sau: 

    keycloak.mail.smtp.from=auto@keycloak.org
    keycloak.mail.smtp.host=localhost
    keycloak.mail.smtp.port=3025
    
Ví dụ: nếu sử dụng máy chủ thử nghiệm utils keycloak bắt đầu bằng: 

    mvn exec:java -Pkeycloak-server -Dkeycloak.mail.smtp.from=auto@keycloak.org -Dkeycloak.mail.smtp.host=localhost -Dkeycloak.mail.smtp.port=3025
    
LDAP server
-----------

Để bắt đầu một máy chủ ldap dựa trên việc kiểm tra ldap:
    
    mvn exec:java -Pldap
    
Có các thuộc tính hệ thống khác bạn có thể sử dụng để định cấu hình lớp (see ldapembeddedserver cho details). Sau khi xong, bạn có thể tạo ra nhà cung cấp liên bang ldap 
Trong bảng điều khiển dành cho quản trị viên Keycloak với cài đặt như: 
* Vendor: Other
* Connection URL: ldap://localhost:10389
* User DN Suffix: ou=People,dc=keycloak,dc=org
* Bind DN: uid=admin,ou=system
* Bind credential: secret

Kerberos server
---------------

Để bắt đầu ApacheDS dựa trên Kerberos server để thử nghiệm Kerberos + LDAP, chạy câu lệnh:
    
    mvn exec:java -Pkerberos
    
Có các thuộc tính hệ thống khác mà bạn có thể sử dụng để định cấu hình (see ldapembeddedserver và kerberosembeddedserver cho các mục đích details) nhưng các giá trị thử nghiệm phải tốt. 
Theo mặc định máy chủ ldap sẽ chạy trên localhost: 10389 và kerberos trên localhost: 6088. 

Cách thay thế là bắt đầu Kerberos với một vương quốc thay thế  KC2.COM thay vì mặc định KEYCLOAK.ORG.
Sau đó máy chủ ApacheDS sẽ được khởi động bằng tất cả các cổng dịch chuyển bằng 1000 (EG. LDAP on 11389, Kerberos KDC on 7088). 
Điều này cho phép bắt đầu 2 máy chủ kerberos song song với việc kiểm tra những thứ như kerberos 

    mvn exec:java -Pkerberos -Dkeycloak.kerberos.realm=KC2.COM
 

Một khi kerberos đang chạy, bạn có thể tạo ra nhà cung cấp liên bang LDAP trong bảng điều khiển quản trị Keycloak với các cài đặt như đã đề cập trong mục LDAP trước. 
Nhưng ngoài ra, bạn có thể cho phép Kerberos xác thực trong nhà cung cấp LDAP với cài đặt như: 

* Kerberos realm: KEYCLOAK.ORG
* Server Principal: HTTP/localhost@KEYCLOAK.ORG
* KeyTab: $KEYCLOAK_SOURCES/testsuite/integration-arquillian/tests/base/src/test/resources/kerberos/http.keytab (Replace $KEYCLOAK_SOURCES with correct absolute path of your sources)

Khi bạn làm điều này, bạn cũng nên đảm bảo rằng tập tin cấu hình client Kerberos được cấu hình đúng với miền KEYCLOAK.ORG  
Hãy xem [../testsuite/integration-arquillian/tests/base/src/test/resources/kerberos/test-krb5.conf](../testsuite/integration-arquillian/tests/base/src/test/resources/kerberos/test-krb5.conf) .Vị trí của tập tin cấu hình kerberos 
Là nền tảng phụ thuộc  (In linux it's file `/etc/krb5.conf` )

Sau đó, bạn cần định cấu hình trình duyệt của mình để cho phép SPNEGO/Kerberos đăng nhập từ  `localhost` .

Các bước chính xác lại là một trình duyệt phụ thuộc vào trình duyệt.Xem ví dụ firefox [http://www.microhowto.info/howto/configure_firefox_to_authenticate_using_spnego_and_kerberos.html](http://www.microhowto.info/howto/configure_firefox_to_authenticate_using_spnego_and_kerberos.html) . 
URI `localhost` nên được cho phép trong `network.negotiate-auth.trusted-uris` lựa chọn cấu hình. 

Đối với Chrome, bạn chỉ cần chạy trình duyệt bằng lệnh tương tự như các chi tiết về (nhiều mô tả trong Chrome tài liệu): 

```
/usr/bin/google-chrome-stable --auth-server-whitelist="localhost"
```


Cuối cùng thử nghiệm tích hợp bằng cách truy xuất tấm vé kerberos. Trong nhiều hệ điều hành, bạn có thể đạt được điều này bằng cách chạy lệnh từ CMD như: 
                                          
```
kinit hnelson@KEYCLOAK.ORG
```
                        
và cung cấp password `secret`

bây giờ khi bạn truy cập `http://localhost:8081/auth/realms/master/account` Bạn nên tự động đăng nhập như người dùng `hnelson` .

Simple loadbalancer
-------------------

Bạn có thể chạy class `SimpleUndertowLoadBalancer` từ IDE. Theo mặc định, nó thực thi những con sóng được nhúng để chạy trên  `http://localhost:8180`, Giao tiếp với 2 nút keycloak đang chạy trên `http://localhost:8181` và `http://localhost:8182` . Xem javadoc để biết thêm chi tiết. 
 

Tạo nhiều người dùng hoặc phiên ngoại tuyến 
-------------------------------------
Chạy testsuite với lệnh như thế này: 

```
mvn exec:java -Pkeycloak-server -DstartTestsuiteCLI
```

Ngoài ra, nếu bạn muốn sử dụng cơ sở dữ liệu mysql của mình, hãy làm như các giá trị thuộc tính(thay thế giá trị thuộc tính theo kết nối db của bạn ):

```
mvn exec:java -Pkeycloak-server -Dkeycloak.connectionsJpa.url=jdbc:mysql://localhost/keycloak -Dkeycloak.connectionsJpa.driver=com.mysql.jdbc.Driver -Dkeycloak.connectionsJpa.user=keycloak -Dkeycloak.connectionsJpa.password=keycloak -DstartTestsuiteCLI
```

Sau đó một khi CLI bắt đầu, bạn có thể sử dụng lệnh 'help' để xem tất cả các lệnh có sẵn. 

### Creating many users

Để tạo nhiều người dùng bạn có thể sử dụng lệnh `createUsers` 
Ví dụ: điều này sẽ tạo ra 500 người dùng  `test0, test1, test2, ... , test499` trong realm `demo` Và mỗi 100 người dùng trong giao dịch riêng. Tất cả người dùng sẽ được cấp vai trò của realm  `user` và `admin` :

```
createUsers test test demo 0 500 100 user,admin
```

Kiểm tra số người dùng: 

```
getUsersCount demo
```

Kiểm tra xem liệu người dùng cụ thể có thực sự được tạo ra: 

```
getUser demo test499
```

### Creating many offline sessions

Để tạo nhiều phiên ngoại tuyến bạn có thể sử dụng lệnh  `persistSessions` . Ví dụ tạo số phiên 50000 (mỗi 500 trong số các transaction riêng biệt) với lệnh: 

```
persistSessions 50000 500
```

Sau khi người dùng hoặc phiên được tạo, bạn có thể khởi động lại để đảm bảo nhập các phiên ngoại tuyến sẽ được kích hoạt và bạn có thể thấy tác động đến thời gian khởi động. Sau khi khởi động lại bạn có thể sử dụng lệnh: 

```
size
```

Đối với tổng số phiên trong infinispan (Nó sẽ là 2 lần khi có 1 phiên khách hàng cho mỗi phiên người dùng được tạo)




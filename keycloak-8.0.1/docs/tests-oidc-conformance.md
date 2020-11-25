Executing OIDC Conformance Testsuite
====================================

Chạy và định cấu hình Keycloak trên Openshift 
---------------------------------------
Bước đầu tiên là chạy Keycloak server trong môi trường, nơi nó có sẵn trực tuyến, vì thế các OIDC Conformance Testsuite testsuite có thể kết nối với nó. 

1) Hãy nhìn vào https://github.com/keycloak/openshift-keycloak-cartridge vì cách chạy Keycloak trên Openshift. Theo hướng dẫn cho đến khi bạn có phiên bản Openshift với keycloak 2.3.0.CR1 hoặc sau đó có sẵn trên một số URL như  https://keycloak-mposolda.rhcloud.com/auth .
 
 
2) Người dùng quản trị cần được tạo thủ công trên dòng lệnh trên hộp mực Openshift. Vậy thì cần phải khởi động lại. Xem tài liệu Keycloak để biết chi tiết. 


3) Để chạy thành công thử nghiệm OP-Rotation-RP-Sig, nó sẽ tốt nếu bạn định cấu hình lại minTimeBetweenRequests mặc định cho publicKeyStorage. Trong $KEYCLOAK_HOME/standalone/configuration/standalone.xml thực hiện những thay đổi đó: 
```
<spi name="publicKeyStorage">
    <provider name="infinispan" enabled="true">
        <properties>
            <property name="minTimeBetweenRequests" value="-1"/>
        </properties>
    </provider>
</spi>
```            
và sau đó chạy server.
            
Lý do: Keycloak hỗ trợ quay và tải các khóa của khách hàng xuống từ jwks_url đã định cấu hình. Tuy nhiên, theo mặc định có 10 giây 
Giữa 2 yêu cầu tải xuống các khóa công khai để tránh các cuộc tấn công của DoS. 
Máy kiểm tra OIDC  OP-Rotation-RP-Sig đăng ký khách hàng, sau đó người dùng đăng nhập cho lần truy cập lần đầu (Tải xuống các khóa khách hàng mới) và 
Sau đó immediatelly xoay các phím client và dự kiến yêu cầu refreshToken thành công với khóa mới. Đây chỉ là kịch bản thử nghiệm thôi. 
Trong môi trường sản xuất thực tế, các khách hàng sẽ không thể làm điều gì như thế này. Do đó chỉ vì mục đích thử nghiệm là sự bảo vệ DoS bị tắt do tập 1 tại đây. 
 
                                                                                                               
4) Đăng nhập vào bảng điều khiển dành cho quản trị viên và tạo một số người dùng với các xác nhận quyền sở hữu cơ bản đã được điền đầy đủ(email, first name, last name). 
Chúng tôi đề xuất rằng tên người dùng hoặc email của ông sẽ được gắn với tên miền của bạn.("john@keycloak-mposolda.rhcloud.com"), vì thế trong bài kiểm tra OP-Req-login_hint, bạn sẽ có thể đăng nhập như người dùng "hinted". 


5) Cho phép đăng ký khách hàng động vô danh từ máy chủ OIDC. Trong bảng điều khiển quản trị sẽ truy cập "Client registration" - > "Client registration policies" - > "Trusted hosts" và thêm máy chủ lưu trữ tin cậy mới: 
 ```
 op.certification.openid.net
 ```

Nhấn "+" , rồi "Save".

Điều này cần thiết bởi vì theo mặc định, việc đăng ký khách hàng động vô danh không được phép từ bất kỳ máy chủ nào, do đó cũng được vô hiệu hóa hiệu quả. 
Lưu ý rằng các thanh ghi có tính chất thực tế phù hợp với máy khách mới cho mỗi thử nghiệm. Vì vậy, bạn có thể xóa chính sách "Consent required" nếu không muốn xem màn hình đồng ý trong hầu hết mọi thử nghiệm. 
Bạn cũng có thể tăng giới hạn bởi chính sách của các khách hàng (mặc định là 200 , vì vậy có khả năng sẽ là sufficient). 


Run conformance testsuite
-------------------------

Hướng dẫn đầy đủ về  http://openid.net/certification/testing/ . 

Điều tôi đã làm là 

1) Go to https://op.certification.openid.net:60000/


2) Fill issuer `https://keycloak-mposolda.rhcloud.com/auth/realms/master`


3) Configured the testing instance like this (second line are my answers):

Q: Does the OP have a .well-known/openid-configuration endpoint?
A: Yes

Q: Do the provider support dynamic client registration?
A: Yes

Q: Which subject type do you want to use by default?
A: Public

Q: Which response type should be used by default?
A: Code (this is just for OIDC Basic profile)

Q: Select supported features:
A: JWT signed with algorithm other than "none"

Q: Test specific request parameters:
Login Hint: john (this means that OP-Req-login_hint test will use user like "john@keycloak-mposolda.rhcloud.com" as it automatically attaches domain name to it for some reason).

Không còn gì khác 
 

4) Sau khi thiết lập, bạn sẽ được chuyển hướng đến ứng dụng thử nghiệm. Kiểu như  `https://op.certification.openid.net:60720/` Và có thể kiểm tra cá nhân. 
Một số kiểm tra yêu cầu một số thao tác thủ công (eg. delete cookies).Những người thực tế cần phải hướng dẫn bạn. 


Cập nhật hộp mực openshift với Keycloak mới nhất 
---------------------------------------------------

Một khi một số vấn đề được khắc phục ở phía keycloak, bạn có thể muốn doublecheck nếu OIDC về mặt phù hợp. Vì vậy, bạn có thể muốn thử nghiệm với JARs từ gần nhất 
Keycloak master thay vì "official release" Keycloak JARs từ cartridge.
 
Openshift cho phép kết nối ssh và khởi động lại hộp mực. Nên bạn có thể dùng cái này trên laptop của bạn  (Ví dụ với sự khắc phục trong module keycloak-services). 

ở laptop của bạn
````bash
cd $KEYCLOAK_SOURCES
cd services
mvn clean install
scp target/keycloak-server-spi-2.1.0-SNAPSHOT.jar 51122e382d5271c5ca0000bc@keycloak-mposolda.rhcloud.com:/tmp/
ssh 51122e382d5271c5ca0000bc@keycloak-mposolda.rhcloud.com
````

Rồi trên máy: 

1) Cập nhật version trong `/var/lib/openshift/51122e382d5271c5ca0000bc/wildfly/modules/system/add-ons/keycloak/org/keycloak/keycloak-server-spi/main/modules.xml`
 
2) Thay thế JAR và khởi động lại server:

````bash
cp /tmp/keycloak-server-spi-2.1.0-SNAPSHOT.jar /var/lib/openshift/51122e382d5271c5ca0000bc/wildfly/modules/system/add-ons/keycloak/org/keycloak/keycloak-server-spi/main/
ps aux | grep java
kill -9 <PID>
cd /var/lib/openshift/51122e382d5271c5ca0000bc/wildfly/bin
./standalone.sh -b 127.3.168.129 -bmanagement=127.3.168.129 -Dh2.bindAddress=127.3.168.129
````

Chờ máy chủ bắt đầu. Sau đó chạy lại bài kiểm tra OIDC với cartridge đã cập nhật. 
Một khả năng khác là thử nghiệm với Wildfly Openshift cartridge và luôn cài đặt các lớp phủ keycloak mới nhất. 

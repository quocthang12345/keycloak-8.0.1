# Changing the Default *keycloak-subsystem* Configuration

Nếu bạn cần phải thay đổi cấu hình phân hệ mặc định của hệ thống phân con mặc định được đóng gói với phân bố của chúng tôi, bạn sẽ cần phải chỉnh sửa tệp này: 
https://github.com/keycloak/keycloak/blob/master/wildfly/server-subsystem/src/main/config/default-server-subsys-config.properties

Tập tin này chứa một thuộc tính đa dòng chứa khai báo xml phân hệ. Lọc maven được sử dụng để đọc thuộc tính này và đưa vào mọi nơi cần thiết. Chỉnh sửa tập tin này cũng sẽ chăm sóc việc truyền bá nó cho các phân bố như máy chủ - dist và demo - dist. 

Ngoài ra, bạn cần tạo ra các lệnh cli cho mỗi thay đổi bằng cách chỉnh sửa tập tin này: 
https://github.com/keycloak/keycloak/blob/master/wildfly/server-subsystem/src/main/resources/cli/default-keycloak-subsys-config.cli

Đoạn mã này được sử dụng trong các tập lệnh do phân phối lớp phủ yêu cầu. 

## Updating an SPI
Những thay đổi bạn có thể làm là khi bạn cần thêm một SPI mới, thay đổi một SPI hiện có hoặc add/change một nhà cung cấp trong vòng SPI. 
Tất cả các nguyên tố trong khai báo SPI đều là tùy chọn, nhưng một khai báo đầy đủ 
Có vẻ như 
````xml
<spi name="example">
     <default-provider>myprovider</default-provider>
     <provider name="myprovider" enabled="true">
         <properties>
             <property name="key" value="value"/>
         </properties>
     </provider>
     <provider name="mypotherrovider" enabled="true">
         <properties>
             <property name="key" value="value2"/>
         </properties>
     </provider>
</spi>
````
ở đây chúng tôi có hai nhà cung cấp định nghĩa SPI "example".  
'nhà cung cấp mặc định' được liệt kê là' myprovider'. Tuy nhiên nó tùy thuộc vào việc quyết định nó sẽ như thế nào 
xử lý vụ này. Một số SPIs cho phép nhiều hơn một nhà cung cấp và một số. Vậy nên 
'nhà cung cấp mặc định' có thể giúp SPIs chọn. 

Cũng để ý rằng mỗi nhà cung cấp định nghĩa tập hợp cấu hình của riêng nó thuộc tính. Thực tế là cả hai nhà cung cấp trên đều có một tài sản gọi là 'lockWaitTimeout' chỉ là trùng hợp ngẫu nhiên. 

## Values of type *List*
Kiểu của mỗi giá trị thuộc tính được dịch bởi nhà cung cấp.  Tuy nhiên 
Có một ngoại lệ. Hãy xem xét nhà cung cấp' jpa' cho API ' eventStore' 
````xml
<spi name="eventsStore">
     <provider name="jpa" enabled="true">
         <properties>
             <property name="exclude-events" value="[&quot;EVENT1&quot;,&quot;EVENT2&quot;]"/>
         </properties>
     </provider>
</spi>
````
Chúng ta thấy rằng giá trị bắt đầu và kết thúc bằng dấu ngoặc vuông. Điều đó có nghĩa là giá trị sẽ được chuyển đến nhà cung cấp như một danh sách. Trong ví dụ này 
hệ thống sẽ qua cung cấp danh sách với hai giá trị phần tử' EVENT1' và' EVENT2'. Để thêm nhiều giá trị vào danh sách, chỉ tách từng phần tử danh sách với dấu phẩy. Thật không may, bạn cần phải thoát khỏi các dấu ngoặc kép xung quanh mỗi phần tử danh sách 
`&quot;`.

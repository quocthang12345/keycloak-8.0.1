# Thông tin về giấy phép phụ thuộc 

## Tại sao chúng ta phải theo dõi thông tin bằng giấy phép? 

Chúng ta cần phải theo dõi các giấy phép áp dụng cho mỗi bên thứ ba (non - Keycloak) dependency (Maven hoặc Otherwise) dùng Keycloak sử dụng. Thông tin này có thể được sử dụng để kiểm tra xem có xung đột hay các vấn đề khác có thể tạo ra một encumberance hợp pháp cho người dùng hay nhà phát triển của Keycloak. 

## Cách xác định thông tin bằng giấy phép phụ thuộc 

Vì hệ sinh thái maven không duy trì siêu dữ liệu cấp giấy phép chất lượng cao, không có giấy phép quy trình tự động (giống plugin maven trong module example) là chấp nhận được đối với việc xác định phép áp dụng cho một phần phụ thuộc của maven cụ thể. Giấy phép thuộc phần phụ thuộc không maven, (Javscript, hình ảnh, phông chữ, etc.) cũng phải được xác định theo cách thủ công. 

Để xác định bằng thủ công một giấy phép, bản sao / thanh toán mã nguồn ở thẻ hoặc cam kết áp dụng cho phiên bản phụ thuộc bạn đang thêm hoặc cập nhật (licenses đôi khi thay đổi giữa versions). Điều này dễ dàng hơn nhiều để làm ngay lập tức, đặc biệt là đối với các phụ thuộc không phải maven, như sau đây có thể không rõ ràng khi các tập tin xuất phát từ phiên bản nào. Sau khi bạn có nguồn, hãy nhìn vào readme và tìm kiếm các tệp giấy phép. Đối với các dự án maven, bạn cũng có thể nhìn vào quả bông, nhưng thông tin bằng giấy phép của nó không phải lúc nào cũng xuất hiện hay chính xác. Nó thường hiển nhiên là cái giấy phép, nhưng đôi khi một phần của nguồn có giấy phép khác, hoặc có thể có nhiều giấy phép áp dụng cho phụ thuộc của bạn. 

## Cách lưu trữ thông tin bằng giấy phép 

Thông thường, mỗi zip nhận được distibuted đến người dùng cần chứa xml và các tệp giấy phép cá nhân, cộng với tệp html được tạo vào thời gian xây dựng. 

Các tệp xml và cá nhân được duy trì trong git. Khi bạn thay đổi hoặc thêm phụ thuộc vào phần: 

- the server, modify `distribution/feature-packs/server-feature-pack/src/main/resources/licenses/rh-sso/license.xml`.
- an adapter, modify `distribution/{saml-adapters,adapters}/*/*/src/main/resources/licenses/rh-sso/licenses.xml`, for example `distribution/saml-adapters/as7-eap6-adapter/eap6-adapter-zip/src/main/resources/licenses/rh-sso/licenses.xml`.

Maven dependencies go into a `licenseSummary/dependencies/dependency` element, and non-maven dependencies go into a `licenseSummary/others/other` element.

Dưới đây là một số ví dụ về phụ thuộc maven: 

```xml
    <dependency>
      <groupId>org.sonatype.plexus</groupId>
      <artifactId>plexus-sec-dispatcher</artifactId>
      <version>1.3</version>
      <licenses>
        <license>
          <name>Apache Software License 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url> <!-- Source repo contains no license file -->
        </license>
      </licenses>
    </dependency>
    <dependency>
      <groupId>org.antlr</groupId>
      <artifactId>antlr-runtime</artifactId>
      <version>3.5</version>
      <licenses>
        <license>
          <name>BSD 3-clause New or Revised License</name>
          <url>https://raw.githubusercontent.com/antlr/antlr3/antlr-3.5/runtime/Python/LICENSE</url>
        </license>
      </licenses>
    </dependency>
```

và non-maven dependencies:

```xml
    <other>
      <description>jQuery</description>
      <locations>
        <file>themes/keycloak/common/resources/lib/jquery/jquery-1.10.2.js</file>
      </locations>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>https://raw.githubusercontent.com/jquery/jquery/1.10.2/MIT-LICENSE.txt</url>
        </license>
      </licenses>
    </other>
    <other>
      <description>AngularJS</description>
      <locations>
        <directory>themes/keycloak/common/resources/lib/angular</directory>
      </locations>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>https://raw.githubusercontent.com/angular/angular.js/v1.4.4/LICENSE</url>
        </license>
      </licenses>
    </other>
```

Hãy nhìn vào các licenses.xml  trong kho này để lấy thêm ví dụ. 

Sau khi sửa đổi license XML, bạn nên chạy `download-license-files.sh` một lần nữa. Script này sẽ cập nhật các tệp giấy phép cá nhân được lưu trữ trong cùng thư mục như xml. 

Dòng lệnh ví dụ: 

```
$ distribution/licenses-common/download-license-files.sh distribution/feature-packs/server-feature-pack/src/main/resources/licenses/rh-sso/licenses.xml
```

Các lệnh shell sau đây phải có sẵn cho tập lệnh nầy để làm việc: 

- curl
- dos2unix
- sha256sum
- xmlstarlet

### Product builds

RH-SSO được xây dựng trên một hệ thống xây dựng nội bộ. Nếu bạn đã thêm hoặc cập nhật dữ liệu giấy phép cho sản phẩm, bạn có thể cần nhập phần phụ thuộc tương ứng vào hệ thống xây dựng nội bộ này. 

## Building from source

Đảm bảo máy bạn đã cài đặt JDK 8 (hoặc mới hơn), Maven 3.1.1 (hoặc mới hơn) và đã cài đặt git

    java -version
    mvn -version
    git --version
    
Đầu tiên, clone the Keycloak repository:
    
    git clone https://github.com/keycloak/keycloak.git
    cd keycloak
    
Để build Keycloak, hãy chạy:

    mvn install
    
Điều này sẽ xây dựng tất cả các modules và chạy testsuite. 

Để build gói ZIP phân phối, hãy chạy lệnh:

    mvn install -Pdistribution
    
Một khi đã hoàn tất các bạn sẽ tìm thấy lưu trữ phân phối trong  `distribution`.

Chỉ để xây dựng máy chủ chạy: 

    mvn -Pdistribution -pl distribution/server-dist -am -Dmaven.test.skip clean install


## Starting Keycloak

Để bắt đầu Keycloak trong quá trình phát triển đầu tiên được chỉ định ở trên, sau đó chạy: 

    mvn -f testsuite/utils/pom.xml exec:java -Pkeycloak-server 

Khi chạy testsuite, theo mặc định, một tài khoản có tên người dùng là `admin` và mật khẩu là `admin`sẽ được tạo trong master realm khi bắt đầu.

Để bắt đầu Keycloak từ phân phối máy chủ trước tiên hãy xây dựng phân phối theo chỉ định ở trên, sau đó chạy: 

    tar xfz distribution/server-dist/target/keycloak-<VERSION>.tar.gz
    cd keycloak-<VERSION>
    bin/standalone.sh
    
Để dừng máy chủ , nhấn tổ hợp phím `Ctrl + C`.


## Working with the codebase

Chúng tôi hiện không thực thi kiểu mã trong Keycloak, nhưng một tham chiếu tốt là kiểu mã được sử dụng bởi Wildfly. Có thể lấy từ  [Wildfly ide-configs](https://github.com/wildfly/wildfly-core/tree/master/ide-configs).Để thêm chuẩn định dạng, hãy xem đoạn văn bản sau[instructions](http://community.jboss.org/wiki/ImportFormattingRules).

Nếu thay đổi của bạn yêu cầu cập nhật cho cơ sở dữ liệu [Updating Database Schema](updating-database-schema.md).

Nếu thay đổi của bạn yêu cầu giới thiệu các phụ thuộc mới hoặc cập nhật các phiên bản phụ thuộc thì vui lòng thảo luận về điều này trước the
dev mailing list. Chúng tôi không chấp nhận các phụ thuộc mới để được thêm nhẹ, vì vậy, hãy thử sử dụng những gì có sẵn. 

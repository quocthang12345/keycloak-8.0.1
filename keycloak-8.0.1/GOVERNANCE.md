# Quản trị keycloak 

* [Vision](#vision) 
* [Maintainers](#maintainers) 
* [Contributing](#contributing)

## Mục tiêu

Keycloak nhằm dễ sử dụng và nhẹ nhàng. Dự án được thành lập để giúp các nhà phát triển ứng dụng dễ dàng 
Để bảo vệ các ứng dụng và dịch vụ hiện đại. 
Quy tắc 80 / 20, khẳng định các yêu cầu đến từ các trường hợp sử dụng, là một phần cốt lõi của tầm nhìn phía sau 
Keycloak. Chúng tôi thực sự tin rằng nếu keycloak hỗ trợ tất cả các trường hợp sử dụng theo mặc định thì nó sẽ béo lên và khó sử dụng. 
Keycloak nhằm mục đích là opinionated và làm cho nó dễ dàng đạt được các trường hợp sử dụng chung, trong khi vẫn bật các trường hợp sử dụng ít phổ biến hơn thông qua tiện ích tùy chỉnh. 


## Những dự án

Keycloak consists of several projects:

* [Keycloak](https://github.com/keycloak/keycloak) - Keycloak Server và Java adapters
* [Keycloak Documentation](https://github.com/keycloak/keycloak-documentation) - Tài liệu Keycloak
* [Keycloak QuickStarts](https://github.com/keycloak/keycloak-quickstarts) - Bắt đầu nhanh với Keycloak
* [Keycloak Containers](https://github.com/keycloak/keycloak-containers) - Kho ảnh Keycloak
* [Keycloak Gatekeeper](https://github.com/keycloak/keycloak-gatekeeper) - Dịch vụ proxy để bảo mật ứng dụng và dịch vụ với Keycloak
* [Keycloak Node.js Connect](https://github.com/keycloak/keycloak-nodejs-connect) - Node.js adapter Keycloak
* [Keycloak Node.js Admin Client](https://github.com/keycloak/keycloak-nodejs-admin-client) - Node.js library Keycloak Admin REST API

Mô hình quản trị tương tự áp dụng cho tất cả các dự án. Tuy nhiên, danh sách maintainers có thể khác nhau theo dự án. 



## Bảo trì

Danh sách maintainers có thể được tìm thấy trong tập tin [MAINTAINERS.md](MAINTAINERS. md) trong kho cho riêng lẻ các dự án được liệt kê trong phần [Projects](#projects). 

### Trách nhiệm bảo trì 

Một nhà duy trì là một người đã cho thấy sự hiểu biết sâu sắc về tầm nhìn, các đặc điểm và codebase. Nó là trách nhiệm thúc đẩy dự án tiến lên, khuyến khích cộng tác và đóng góp, và nói chung là giúp đỡ cộng đồng. 

Trách nhiệm của một nhà duy trì bao gồm, nhưng không giới hạn trong :

* Tham gia thảo luận thiết kế 
* tích cực giám sát danh sách thư, diễn đàn người dùng và trò chuyện 
* đóng góp mã chất lượng cao 
* hãy duy trì tri thức sâu sắc về tầm nhìn, đặc điểm và khả năng sinh học 
* xem xét yêu cầu kéo theo cá nhân hoặc ủy quyền cho các chuyên gia trong khu vực có liên quan 
* giúp cộng đồng 

### Trở thành một nhà bảo trì 

Để trở thành một nhà duy trì, bạn cần phải chứng minh những điều sau: 
* hiểu rõ về tầm nhìn, các đặc điểm và codebase 
* đóng góp các tính năng lớn hơn 
* đóng góp sửa chữa lỗi 
* tham gia thảo luận thiết kế 
* tham gia vào yêu cầu kéo dài 
* khả năng cộng tác với đội 
* giúp cộng đồng 
Một nhà duy trì mới phải được đề xuất bằng cách gửi một email đến  [developer mailing list]((https://lists.jboss.org/mailman/listinfo/keycloak-dev).
Email phải bao gồm bằng chứng về danh sách trên. 
Sau đó, nhà duy trì sẽ thảo luận về đề xuất. Nếu bất cứ ai có thể có hoặc muốn thêm thông tin, thì nhà duy trì 
Sẽ liên lạc trực tiếp với ứng cử viên để thảo luận thêm. 
Đối với ứng cử viên được chấp nhận là một nhà duy trì ít nhất 2 / 3 trong số các nhà duy trì hiện có phải phê duyệt ứng cử viên. 


### Thay đổi trong Maintainership 

Maintainers có thể bị xóa nếu ít nhất 2/3 đồng ý maintainers với nhau 


## Sự thay đổi trong đóng góp 

Quá trình xem xét các thay đổi được đề xuất khác nhau tùy thuộc vào kích thước và tác động của thay đổi. 

### Thay đổi nhỏ 

Thay đổi nhỏ là lỗi của lỗi, một sự tăng cường nhỏ hoặc thêm vào các tính năng hiện tại. 
Để đề xuất thay đổi nhỏ, chỉ cần tạo một vấn đề trong các vấn đề [issue tracker](https://issues.jboss.org/browse/KEYCLOAK) và 
Gửi yêu cầu kéo. 
Một nhà duy trì sẽ chịu trách nhiệm cho việc phê duyệt yêu cầu kéo dài. Nhà duy trì có thể đánh giá sâu sắc 
Yêu cầu hoặc ủy quyền cho chuyên gia trong lĩnh vực tương ứng. 
Nếu thay đổi này có tác động lớn hơn nó phải tuân theo quy trình cho những thay đổi lớn hơn. 

### Larger Changes

Đối với những thay đổi lớn hơn tất cả những người đóng góp và người đóng góp nên có cơ hội xem xét thay đổi. Chuyện này xong rồi 
Gửi email đến [developer mailing list](https://lists.jboss.org/mailman/listinfo/keycloak-dev) để bắt đầu một vụ thảo luận xung quanh thay đổi. 
Vì tính năng mới mà chúng tôi khuyên bạn nên tạo đề xuất thiết kế. Không có yêu cầu nghiêm ngặt về nội dung hoặc bố cục, 
Nhưng ít nhất nó phải che đậy động lực, sử dụng các trường hợp và cách sử dụng tính năng này. Một đề xuất thiết kế được tạo ra 
Gửi quảng cáo đến [design proposals repository](https://github.com/keycloak/keycloak-community/tree/master/design). 
Người đóng góp có thể quyết định gửi yêu cầu kéo trước khi thảo luận về danh sách thư, và việc tạo ra thiết kế đề xuất. Tuy nhiên, sự thay đổi sẽ không được chấp nhận cho đến khi nó được thảo luận trên danh sách gửi thư. 
Nếu có bất kỳ sự phản đối nào đối với sự thay đổi họ có thể được giải quyết qua các cuộc thảo luận về danh sách gửi thư hoặctrong yêu cầu kéo. Nếu không thể thực hiện một nghị quyết có thể được chấp nhận nếu ít nhất 2 / 3 của maintainers chấp thuận thay đổi. 

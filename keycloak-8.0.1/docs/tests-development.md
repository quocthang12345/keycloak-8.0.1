## Writing tests

Chúng tôi tập trung chủ yếu vào các thử nghiệm cấp độ integration/functional . Bài kiểm tra đơn vị được tránh và chỉ được đề xuất cho cách ly 
Các lớp như utils nhỏ. Chúng tôi không sử dụng bất kỳ khuôn khổ chế nhạo nào và chúng tôi sẽ không chấp nhận bất kỳ khoản đóng góp nào thêm vào 
Khung chế nhạo. 
Khi viết bài kiểm tra xin vui lòng theo dõi cách tiếp cận như chúng tôi đã thực hiện trong các thử nghiệm khác. Có nhiều cách để 
Phần mềm thử nghiệm và chúng tôi đã chọn của chúng tôi, nên hãy đánh giá cao điều đó. 
Các xét nghiệm chính được cung cấp trong testsuite / integration - arquillian / base / base. Hầu hết các bài kiểm tra máy chủ đều ở đây. 
Các thử nghiệm tích hợp có thể được thực hiện từ IDE theo cách bạn chạy thử nghiệm đơn vị. Khi chạy khỏi 
IDE máy chủ Keycloak được nhúng tự động. 
Một bài kiểm tra tốt để bắt đầu nhìn vào org.keycloak.testsuite.forms.LoginTest. Đó là một điều hợp lý để hiểu 
Bài kiểm tra này. 
Khi phát triển thử nghiệm tùy thuộc vào tính năng hoặc nâng cao bạn đang thử nghiệm bạn có thể tìm thấy tốt nhất để thêm vào 
Thử nghiệm hiện tại, hoặc viết một bài kiểm tra từ scratch. Vì sau đó, chúng tôi khuyên bạn nên tìm một thử nghiệm khác gần với cái gì 
Bạn cần và sử dụng nó như một cơ sở. 
Tất cả các bài kiểm tra không cần phải kiểm tra hộp đen như testsuite deploys một đặc tính đặc biệt cho máy chủ keycloak 
Cho phép chạy mã trong máy chủ. Điều này cho phép các thử nghiệm có thể thực thi các chức năng không bị lộ 
APIs cũng như truy cập dữ liệu không bị lộ. Cho một ví dụ về cách làm việc này tại org.keycloak.testsuite.runonserver.RunOnServerTest. 

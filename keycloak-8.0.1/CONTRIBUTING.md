# Cộng đồng keycloak 

Keycloak là một dạng mã nguồn mở và là giải pháp quản lý truy cập cho các ứng dụng hiện đại và dịch vụ. 

## Xây dựng và làm việc với codebase 

Chi tiết để xây dựng từ nguồn và làm việc với codebase được cung cấp trong  [building and working with the code base](docs/building.md) guide.

## Đóng góp vào keycloak 

Keycloak là một dự án theo hướng nguồn mở và chúng tôi chào mừng các đóng góp cũng như phản hồi từ cộng đồng.  

Chúng tôi có một vài hướng dẫn để giúp bạn thành công với sự đóng góp của bạn cho keycloak. 

Đây là danh sách kiểm tra nhanh cho một PR tốt, thêm chi tiết bên dưới: 

1. [Keycloak Dev Mailing List](https://lists.jboss.org/mailman/listinfo/keycloak-dev)
2. A JIRA associated with the PR
3. One feature/change per PR
4. One commit per PR
5. PR rebased on master (`git rebase`, not `git pull`) 
5. Commit message is prefixed by JIRA number
6. No changes to code not directly related to your PR
7. Includes functional/integration test
8. Includes documentation

Sau khi bạn đã gửi thông tin pr của mình cho vui lòng theo dõi nhận xét / phản hồi. Chúng tôi dự trữ quyền đóng cửa không hoạt động nếu 
Bạn không trả lời trong vòng 2 tuần, (hãy nhớ rằng bạn luôn có thể mở một PR mới nếu kết thúc do inactivity). 

Ngoài ra, hãy nhớ rằng chúng ta sẽ nhận được một lượng lớn PRS và cũng có mã để viết chúng ta, vì vậy chúng ta có thể 
Không thể phản ứng với pr của bạn ngay lập tức. Nơi tốt nhất để ping chúng ta là trên sợi dây bạn bắt đầu trong danh sách gửi thư của dev. 

### Tìm thứ gì đó để làm việc 

Nếu bạn muốn đóng góp cho keycloak, nhưng không chắc chắn phải làm gì, bạn có thể tìm một số lượng mở 
Các vấn đề đang chờ đóng góp trong 
[Keycloak JIRA](https://issues.jboss.org/projects/KEYCLOAK/versions/12340167).

### Mở một cuộc thảo luận về Keycloak Dev Mailing List

Như keycloak là một dự án theo hướng cộng đồng mà chúng tôi yêu cầu cộng tác viên gửi mô tả về những gì họ đang lên kế hoạch 
liên lạc qua [Keycloak Dev Mailing List](https://lists.jboss.org/mailman/listinfo/keycloak-dev).

Chúng tôi khuyên bạn nên bắt đầu thảo luận trước khi gửi PR. Qua danh sách gửi thư mà bạn có thể có giá trị 
Phản hồi cả từ nhóm keycloak core cũng như cộng đồng rộng lớn. 

### Tạo issue in Keycloak JIRA

Hãy dành thời gian để viết một cách thích hợp bao gồm một bản tóm tắt tốt và mô tả. 

Hãy nhớ đây có thể là điều đầu tiên mà người đánh giá của bạn sẽ xem xét để có được ý tưởng về những gì bạn đang đề xuất 
Và cũng sẽ được cộng đồng sử dụng trong tương lai để tìm hiểu về những tính năng và tính năng mới được bao gồm trong tương lai 
Bản phát hành mới. 

Note: Keycloak Node.js Admin Client uses GitHub issues and not the Keycloak JIRA.

### Triển khai

Chi tiết để xây dựng từ nguồn và làm việc với codebase được cung cấp 
[building and working with the code base](docs/building.md) guide.

Không định dạng hoặc tắt mã không liên quan trực tiếp đến đóng góp của bạn. Nếu bạn làm điều này sẽ đáng kể 
Tăng cường nỗ lực của chúng tôi trong việc xem xét pr của bạn. Nếu bạn có nhu cầu mạnh mẽ về mã thì gửi một PR riêng biệt cho 
Refactoring. 

### Kiểm thử

Chi tiết để triển khai thử nghiệm được cung cấp  [writing tests](docs/tests-development.md) guide.

Không thêm khung chế nhạo hoặc khuôn khổ thử nghiệm khác chưa thành phần của testsuite. Làm ơn viết thử nghiệm 
Như chúng ta đã viết thử nghiệm. 

### Tài liệu

Chúng tôi yêu cầu đóng góp để bao gồm tài liệu có liên quan. Bên cạnh PR của bạn thay đổi mã, hãy chuẩn bị PR [Keycloak Documentation](https://github.com/keycloak/keycloak-documentation).

Trong mô tả PR của bạn bao gồm liên kết đến PR  [Keycloak Documentation](https://github.com/keycloak/keycloak-documentation).

### Gửi PR của bạn

Khi chuẩn bị pr của bạn để đảm bảo bạn có một cam kết và chi nhánh của bạn là rebased trên chi nhánh chính từ 
Kho dự án. 

Điều này có nghĩa là sử dụng lệnh 'git rebase' và không phải 'git' khi tích hợp thay đổi từ chính đến branch của bạn.Xem tại 
[Git Documentation](https://git-scm.com/book/en/v2/Git-Branching-Rebasing) để biết thêm chi tiết.

Chúng tôi yêu cầu bạn chen vào một cam kết. Bạn có thể làm điều này với lệnh `git rebase -i HEAD~X` 
Là số cam kết bạn muốn siết chặt.Có thể xem tại[Git Documentation](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History)
để biết thêm chi tiết.

Điều trên giúp chúng tôi xem xét pr của bạn và cũng giúp chúng tôi duy trì kho lưu trữ. Nó cũng được yêu cầu 
Quá trình sáp nhập tự động của chúng ta. 

Chúng tôi cũng yêu cầu thông báo cam kết có số lượng phát hành Keycloak JIRA (example cam kết 
 "KEYCLOAK-9876 tính năng mới tuyệt vời của tôi" ). 

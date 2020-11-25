## Updating dependencies for login, admin console, and old account console

Chỉnh sửa `src/main/package.json` để cập nhật phiên bản phụ thuộc. Sau đó, hãy chạy các lệnh sau để tải xuống các phụ thuộc mới: 

    cd themes
    mvn clean install -Pnpm-update

Phần trên sẽ tải xuống phụ thuộc NPM đầy đủ vào `src/main/node_modules`. Mục đích chính của thư mục này là chúng ta có mã nguồn đầy đủ cho các phần phụ thuộc trong tương lai. Điều này sẽ được xóa trong tương lai dưới dạng hệ thống xây dựng nội bộ sẽ chăm sóc điều này. 

Tiếp theo sẽ sao chép các phụ thuộc vào `src/main/resources/theme/keycloak/common/resources/node_modules`. Dưới đây sẽ sử dụng bộ lọc trong khi sao chép để xóa các tệp mà chúng tôi không bao gồm (tài liệu mẫu phân phối và thử nghiệm cho dependencies). 

Trước khi thực hiện các thay đổi về thay đổi trong `src/main/resources/theme/keycloak/common/resources/node_modules` đảm bảo rằng nó chưa được thêm các phụ thuộc (Phụ thuộc bắc cầu) không sử dụng mới vào phân phối và thêm bất kỳ tệp nào không cần thiết trong phân phối (đây là importat khi toàn bộ node_modules được tải xuống là 176M trong khi phụ thuộc được lọc 42M)


## Updating dependencies for the new account console

TBD



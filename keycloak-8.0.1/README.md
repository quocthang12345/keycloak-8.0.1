# Keycloak

Keycloak là một dạng mã nguồn mở và giải pháp quản lý truy cập cho các ứng dụng hiện đại và dịch vụ. 
Kho này chứa mã nguồn cho máy chủ Keycloak, Java adapter và Javascript adapter. 


## Help and Documentation

* [Documentation](https://www.keycloak.org/documentation.html)
* [User Mailing List](https://lists.jboss.org/mailman/listinfo/keycloak-user) - Mailing list for help and general questions about Keycloak
* [JIRA](https://issues.jboss.org/projects/KEYCLOAK) - Issue tracker for bugs and feature requests


## Báo cáo về lỗ hổng an ninh 

Nếu bạn tìm thấy lỗ hỏng bảo mật trong hệ thống, vui lòng truy cập vào [instructions on how to properly report it](http://www.keycloak.org/security.html)


## Reporting an issue

Nếu bạn tin rằng bạn đã khám phá ra một khiếm khuyết trong keycloak, hãy mở một vấn đề trong các vấn đề [Issue Tracker](https://issues.jboss.org/projects/KEYCLOAK). 
Hãy nhớ cung cấp bản tóm tắt tốt, mô tả cũng như các bước để tái tạo sự cố. 


## Getting started

Để chạy keycloak tải tệp xuống từ  [website](https://www.keycloak.org/downloads.html). Giải nén và chạy dòng lệnh:

    bin/standalone.[sh|bat] 

Ngoài ra, bạn có thể sử dụng Docker bằng cách chạy:

    docker run jboss/keycloak
    
Để biết thêm chi tiết tham khảo  [Keycloak Documentation](https://www.keycloak.org/documentation.html).


## Building from Source

Để build từ source hãy truy cập vào [building and working with the code base](docs/building.md) guide.


### Testing

Để kiểm thử hãy truy cập vào [running tests](docs/tests.md) guide.


### Writing Tests

Để write tests hãy truy cập vào [writing tests](docs/tests-development.md) guide.


## Contributing

Trước khi đóng góp cho keycloak hãy đọc [contributing guidelines](CONTRIBUTING.md)


## Other Keycloak Projects

* [Keycloak](https://github.com/keycloak/keycloak) - Keycloak Server and Java adapters
* [Keycloak Documentation](https://github.com/keycloak/keycloak-documentation) - Documentation for Keycloak
* [Keycloak QuickStarts](https://github.com/keycloak/keycloak-quickstarts) - QuickStarts for getting started with Keycloak
* [Keycloak Containers](https://github.com/keycloak/keycloak-containers) - Container images for Keycloak
* [Keycloak Gatekeeper](https://github.com/keycloak/keycloak-gatekeeper) - Proxy service to secure apps and services with Keycloak
* [Keycloak Node.js Connect](https://github.com/keycloak/keycloak-nodejs-connect) - Node.js adapter for Keycloak
* [Keycloak Node.js Admin Client](https://github.com/keycloak/keycloak-nodejs-admin-client) - Node.js library for Keycloak Admin REST API


## License

* [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

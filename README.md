# Email List MVC - Chapter 02

Bộ mã nguồn dựng lại theo bài Email List trong Chapter 02 slides và cấu trúc đã hướng dẫn: Maven, JDK 21, Tomcat 9, package `murach`. Giao diện giữ các nội dung tiếng Anh của bài học.

## 1. Cấu trúc

Các đường dẫn sau tính từ thư mục `ch02email/`:

| File | Vai trò |
| --- | --- |
| `pom.xml` | Cấu hình Maven, JDK 21, đóng gói WAR và thư viện |
| `src/main/java/murach/business/User.java` | Model: lưu email, tên, họ; JavaBean với getter/setter |
| `src/main/java/murach/data/UserDB.java` | Tầng dữ liệu: lưu danh sách tạm trong RAM |
| `src/main/java/murach/email/EmailListServlet.java` | Controller: nhận request và chuyển đến View |
| `src/main/webapp/index.html` | View: biểu mẫu đăng ký |
| `src/main/webapp/thanks.jsp` | View: hiển thị dữ liệu vừa nhập |
| `src/main/webapp/styles/main.css` | Định dạng hai trang |
| `src/main/webapp/WEB-INF/web.xml` | Ánh xạ `/emailList` tới servlet |
| `TOAN_BO_MA_NGUON.md` | Nội dung đầy đủ của 8 file code để tiện đọc và sao chép |

## 2. Điều kiện chạy

- JDK 21 (không chỉ JRE).
- Maven 3.6.3 trở lên, hoặc Maven được IntelliJ cung cấp.
- Apache Tomcat 9.0.x. Dự án dùng `javax.servlet`; dùng Tomcat 9 cho bộ mã này.
- Kết nối Internet ở lần build đầu để Maven tải dependencies.

`UserDB` trong bản này chưa kết nối PostgreSQL. Dữ liệu lưu tạm trong `CopyOnWriteArrayList`, mất khi dừng/khởi động lại ứng dụng hoặc triển khai lại WAR. Gửi cùng một email nhiều lần sẽ thêm nhiều bản ghi. `selectAll()` dùng để lấy danh sách trong mã Java, không có trang liệt kê người dùng.

## 3. Mở trong IntelliJ

1. Giải nén ZIP. Chọn **File > Open** và mở thư mục `ch02email` có `pom.xml`.
2. Chọn **File > Project Structure > Project > SDK: JDK 21**.
3. Nếu IntelliJ hỏi loại project, chọn Maven. Nếu có thông báo **Load Maven Project**, nhấn vào đó.
4. Mở cửa sổ **Maven**, nhấn **Reload All Maven Projects** và đợi tải thư viện xong.
5. Kiểm tra Maven runner/importer dùng JDK 21 nếu máy có nhiều JDK.

## 4. Build bằng Terminal

Mở Terminal ngay trong thư mục `ch02email`:

```powershell
java -version
mvn -version
mvn clean package
```

Kết quả: `target/ch02email.war`.

Nếu `mvn` báo không được nhận diện, bạn vẫn có thể build bằng Maven tích hợp của IntelliJ: mở cửa sổ **Maven > Lifecycle**, chạy **clean**, sau đó **package**. Không cần đổi sang Gradle.

## 5. Chạy Tomcat 9 trên Windows

Ví dụ bạn giải nén Tomcat vào `D:\apache-tomcat-9`. Hãy thay đường dẫn ví dụ này bằng đường dẫn thật trên máy bạn.

Trong PowerShell, ở thư mục project:

```powershell
$tomcatPath = 'D:\apache-tomcat-9'
Copy-Item '.\target\ch02email.war' "$tomcatPath\webapps\ch02email.war" -Force
& "$tomcatPath\bin\catalina.bat" run
```

Tomcat cũng cần chạy bằng JDK 21 vì project biên dịch cho Java 21. Nếu `JAVA_HOME` chưa đúng, đặt trong Terminal hiện tại trước khi chạy (thay bằng đường dẫn JDK thật):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
```

Đợi log báo server đã khởi động rồi mở:

<http://localhost:8080/ch02email/>

Giữ Terminal Tomcat đang chạy. Nhấn **Ctrl+C** để dừng. Sau khi sửa code, build lại, dừng Tomcat, chép WAR mới và chạy lại.

Nếu chạy trên Linux/macOS, chép WAR vào thư mục `webapps/` của Tomcat và chạy `bin/catalina.sh run` từ thư mục Tomcat.

## 6. Cách kiểm tra bài

1. Mở địa chỉ ở trên, nhập email, first name, last name.
2. Nhấn **Join Now**: trình duyệt POST tới `/ch02email/emailList`.
3. Servlet đọc `action=add`, tạo `User`, gọi `UserDB.insert(user)`.
4. Servlet đặt `user` vào request và forward sang `/thanks.jsp`.
5. JSP hiện thông tin vừa nhập; nút **Return** gửi `action=join` để về form.
6. Thử nhập tên tiếng Việt có dấu để kiểm tra UTF-8.

`thanks.jsp` dùng JSTL `<c:out>` để hiển thị các giá trị `${user.email}`, `${user.firstName}`, `${user.lastName}` dưới dạng văn bản an toàn. Thư viện JSTL đã có trong `pom.xml`.

## 7. Các lỗi dễ gặp

| Lỗi | Cách xử lý |
| --- | --- |
| `package javax.servlet does not exist` | Reload Maven và kiểm tra dependency `javax.servlet-api` trong `pom.xml`. |
| `release version 21 not supported` | Maven đang dùng JDK cũ; chọn JDK 21 cho Maven runner/`JAVA_HOME`. |
| `UnsupportedClassVersionError` | Tomcat đang chạy bằng Java cũ hơn 21; sửa `JAVA_HOME` rồi chạy lại. |
| 404 | Kiểm tra Tomcat đang chạy, WAR đã deploy và URL là `/ch02email/`. Xem log nếu WAR deploy lỗi. |
| 405 khi gửi biểu mẫu từ server tĩnh | Chạy WAR trên Tomcat. Server tĩnh chỉ hiển thị HTML, không xử lý servlet/JSP. |
| Port 8080 đã dùng | Dừng tiến trình Tomcat/server cũ hoặc đổi cổng connector trong `conf/server.xml`. |

Không chạy bằng cách mở trực tiếp `index.html` với `file:///...`, `python -m http.server` hay Live Server nếu muốn nút Join Now gọi Java Servlet.

## 8. Nguồn và phạm vi

- Bài học: `Chapter 02 slides.pdf` (Murach's Java Servlets/JSP, Chapter 2).
- Các lớp, đường dẫn và luồng `join`/`add` theo bài học. `UserDB` lưu RAM là phần bổ sung để có thể chạy độc lập; tài liệu không cung cấp đầy đủ lớp lưu dữ liệu này.
- Bản dựng lại bổ sung UTF-8, kiểm tra trường rỗng ở server, JSTL `c:out` và cấu hình Maven để dùng trong IntelliJ.
- Tomcat 9: <https://tomcat.apache.org/whichversion.html>
- Maven WAR Plugin 3.4.0: <https://maven.apache.org/plugins-archives/maven-war-plugin-3.4.0/plugin-info.html>

Toàn bộ mã nguồn ứng dụng nằm trong ZIP; JDK, Maven, Tomcat và thư viện do Maven tải không đóng gói kèm. Thư mục `target/` được tạo khi build.

## 9. Kiểm tra bộ mã nguồn

Đã biên dịch bằng JDK 21, đóng gói WAR và chạy trên Tomcat 9.0.111. Đã kiểm tra 9 trường hợp: form đăng ký, action mặc định, gửi biểu mẫu và render JSP, tên tiếng Việt, hiển thị an toàn ký tự HTML, nút Return, CSS, trường rỗng và action không hợp lệ. Việc biên dịch kiểm tra dùng trực tiếp javac; Maven package chưa hoàn tất trong môi trường kiểm tra do tải dependencies chậm.

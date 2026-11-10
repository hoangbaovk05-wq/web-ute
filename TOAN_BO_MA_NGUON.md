# Toàn bộ mã nguồn ch02email

Các đường dẫn bên dưới tính từ thư mục `ch02email/`. Mỗi mục là nội dung đầy đủ của một file.

Môi trường: Maven + JDK 21 + Tomcat 9. Xem `README.md` để mở trên IntelliJ và chạy.

| File | Ngôn ngữ |
| --- | --- |
| `pom.xml` | xml |
| `src/main/java/murach/business/User.java` | java |
| `src/main/java/murach/data/UserDB.java` | java |
| `src/main/java/murach/email/EmailListServlet.java` | java |
| `src/main/webapp/index.html` | html |
| `src/main/webapp/thanks.jsp` | jsp |
| `src/main/webapp/styles/main.css` | css |
| `src/main/webapp/WEB-INF/web.xml` | xml |

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>murach</groupId>
    <artifactId>ch02email</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>Chapter 02 - Email List MVC</name>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Tomcat 9 cung cap Servlet API khi chay ung dung. -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
            <scope>provided</scope>
        </dependency>

        <!-- JSTL: c:out hien thi an toan noi dung nguoi dung nhap. -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>ch02email</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```


## src/main/java/murach/business/User.java

```java
package murach.business;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String email;

    public User() {
        this("", "", "");
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```


## src/main/java/murach/data/UserDB.java

```java
package murach.data;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import murach.business.User;

public final class UserDB {
    // Luu tam trong RAM. Du lieu mat khi ung dung/Tomcat khoi dong lai.
    private static final List<User> USERS = new CopyOnWriteArrayList<>();

    private UserDB() {
    }

    public static int insert(User user) {
        USERS.add(Objects.requireNonNull(user, "user"));
        return 1;
    }

    public static List<User> selectAll() {
        return List.copyOf(USERS);
    }
}
```


## src/main/java/murach/email/EmailListServlet.java

```java
package murach.email;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import murach.business.User;
import murach.data.UserDB;

public class EmailListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "join";
        }

        String url;
        if (action.equals("join")) {
            url = "/index.html";
        } else if (action.equals("add")) {
            String firstName = getText(request, "firstName");
            String lastName = getText(request, "lastName");
            String email = getText(request, "email");

            // Kiem tra o server, ke ca khi bo qua required tren HTML.
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Please enter your email, first name and last name.");
                return;
            }

            User user = new User(firstName, lastName, email);
            UserDB.insert(user);

            request.setAttribute("user", user);
            url = "/thanks.jsp";
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Unknown action.");
            return;
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private String getText(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
```


## src/main/webapp/index.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css">
</head>
<body>
    <h1>Join our email list</h1>
    <p>To join our email list, enter your name and email address below.</p>

    <form action="emailList" method="post" accept-charset="UTF-8">
        <input type="hidden" name="action" value="add">

        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required><br>

        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" name="firstName" required><br>

        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" name="lastName" required><br>

        <input type="submit" value="Join Now" id="submit">
    </form>
</body>
</html>
```


## src/main/webapp/thanks.jsp

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css">
</head>
<body>
    <h1>Thanks for joining our email list</h1>
    <p>Here is the information that you entered:</p>

    <label>Email:</label>
    <span><c:out value="${user.email}" /></span><br>

    <label>First Name:</label>
    <span><c:out value="${user.firstName}" /></span><br>

    <label>Last Name:</label>
    <span><c:out value="${user.lastName}" /></span><br>

    <p>To enter another email address, click the Return button below.</p>
    <form action="emailList" method="get">
        <input type="hidden" name="action" value="join">
        <input type="submit" value="Return">
    </form>
</body>
</html>
```


## src/main/webapp/styles/main.css

```css
body {
    font-family: Arial, Helvetica, sans-serif;
    font-size: 11pt;
    margin: 2em;
    max-width: 48em;
}

h1 {
    color: teal;
}

label {
    float: left;
    width: 7em;
    margin-bottom: 0.5em;
}

input[type="text"],
input[type="email"] {
    box-sizing: border-box;
    width: 18em;
    max-width: 100%;
    margin-left: 0.5em;
    margin-bottom: 0.5em;
}

span {
    display: inline-block;
    margin-left: 0.5em;
    margin-bottom: 0.5em;
    overflow-wrap: anywhere;
}

br {
    clear: both;
}

#submit {
    margin-left: 7.5em;
}

@media (max-width: 480px) {
    body {
        margin: 1em;
    }

    label {
        float: none;
        display: block;
        width: auto;
    }

    input[type="text"],
    input[type="email"],
    span,
    #submit {
        margin-left: 0;
    }
}
```


## src/main/webapp/WEB-INF/web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <display-name>Chapter 02 - Email List MVC</display-name>

    <servlet>
        <servlet-name>EmailListServlet</servlet-name>
        <servlet-class>murach.email.EmailListServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>EmailListServlet</servlet-name>
        <url-pattern>/emailList</url-pattern>
    </servlet-mapping>

    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>

    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
    </welcome-file-list>
</web-app>
```

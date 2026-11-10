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

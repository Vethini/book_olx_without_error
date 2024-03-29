package com.user.servlet;
import java.io.IOException;
import java.util.UUID;

import com.DB.DBConnect;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.entity.*;
import com.DAO.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAOImpl userDAO = new UserDAOImpl(DBConnect.getConn());
        HttpSession session = request.getSession();
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if("admin@gmail.com".equals(email) && "admin".equals(password)) {
				User user = new User();
				user.setName("Admin");
				session.setAttribute("userObj", user);
				response.sendRedirect("admin/home.jsp");
			}else {

            User user = userDAO.getUserByEmail(email);

            if (user != null && userDAO.checkEncryptedPassword(password, user)) {
                // Generate a token
                String token = generateToken();
                // Save the token in the database
                userDAO.updateToken(user.getEmail(), token);
                
                session.setAttribute("userObj", user);
                session.setAttribute("token", token);
                response.sendRedirect("index.jsp");
            } else {
                session.setAttribute("failedMsg", "Invalid Email Id & Password.");
                response.sendRedirect("login.jsp");
            }
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String generateToken() {
        // Generate a random token (you can use UUID.randomUUID() for this)
        // For simplicity, let's just return a random string
        return UUID.randomUUID().toString();
    }

}
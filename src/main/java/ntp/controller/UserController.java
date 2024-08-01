package ntp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntp.filter.AuthList;
import ntp.filter.AuthenHandling;

@WebServlet(name = "UserController", urlPatterns = { "/user", "/user-detail", "/user-add", "/user-edit" })
public class UserController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthenHandling AuthenHandling = new AuthenHandling();
		int roleId = AuthenHandling.getRoleOfUser(req);
		String servletPath = req.getServletPath();

		if ("/user".equals(servletPath) && roleId != AuthList.STAFF.getValue()) {
			req.getRequestDispatcher("user.jsp").forward(req, resp);
		} else if ("/user-detail".equals(servletPath) && roleId == AuthList.ADMIN.getValue()) {
			req.getRequestDispatcher("user-detail.jsp").forward(req, resp);
		} else if ("/user-add".equals(servletPath) && roleId == AuthList.ADMIN.getValue()) {
			req.getRequestDispatcher("user-add.jsp").forward(req, resp);
		} else if ("/user-edit".equals(servletPath) && roleId == AuthList.ADMIN.getValue()) {
			req.getRequestDispatcher("user-edit.jsp").forward(req, resp);
		} else {
			req.getRequestDispatcher("403.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
}

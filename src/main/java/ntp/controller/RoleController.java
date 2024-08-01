package ntp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntp.filter.AuthList;
import ntp.filter.AuthenHandling;

@WebServlet(name = "RoleController", urlPatterns = { "/role", "/role-add", "/role-edit" })
public class RoleController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthenHandling authenHandling = new AuthenHandling();
		int roleId = authenHandling.getRoleOfUser(req);

		String servletPath = req.getServletPath();

		if (roleId != AuthList.ADMIN.getValue()) {
			req.getRequestDispatcher("403.jsp").forward(req, resp);
		} else {
			switch (servletPath) {
			case "/role":
				req.getRequestDispatcher("role.jsp").forward(req, resp);
				break;
			case "/role-add":
				req.getRequestDispatcher("role-add.jsp").forward(req, resp);
				break;
			case "/role-edit":
				req.getRequestDispatcher("role-edit.jsp").forward(req, resp);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
}

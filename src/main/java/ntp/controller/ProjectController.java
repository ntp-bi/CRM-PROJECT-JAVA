package ntp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntp.filter.AuthList;
import ntp.filter.AuthenHandling;

@WebServlet(name = "ProjectController", urlPatterns = { "/project", "/project-add", "/project-edit",
		"/project-detail" })
public class ProjectController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthenHandling authenHandling = new AuthenHandling();
		int roleID = authenHandling.getRoleOfUser(req);

		String servletPath = req.getServletPath();

		if (roleID == AuthList.STAFF.getValue()) {
			try {
				req.getRequestDispatcher("403.jsp").forward(req, resp);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			switch (servletPath) {
			case "/project":
				req.getRequestDispatcher("project.jsp").forward(req, resp);
				break;
			case "/project-detail":
				req.getRequestDispatcher("project-detail.jsp").forward(req, resp);
				break;
			case "/project-add":
				req.getRequestDispatcher("project-add.jsp").forward(req, resp);
				break;
			case "/project-edit":
				req.getRequestDispatcher("project-edit.jsp").forward(req, resp);
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

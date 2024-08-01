package ntp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = { "/home", "/index", "/profile", "/profile-task-update", "/user", "/user-add", "/user-edit",
		"/user-detail", "/role", "/role-add", "/role-edit", "/project", "/project-add", "/project-edit",
		"/project-detail", "/task", "/task-add", "/task-edit" })
public class AuthenFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;

		Cookie[] cookies = httpReq.getCookies();
		if (cookies != null && cookies.length > 0) {
			AuthenHandling authenHandling = new AuthenHandling();
			if (authenHandling.isLoggedIn(cookies)) {
				chain.doFilter(request, response);
			} else {
				httpRes.sendRedirect(httpReq.getContextPath() + "/login");
			}
		} else {
			httpRes.sendRedirect(httpReq.getContextPath() + "/login");
		}

	}
}

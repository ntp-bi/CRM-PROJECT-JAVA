package ntp.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntp.model.UserModel;
import ntp.payload.Response;
import service.AuthService;
import service.LoginService;

public class AuthenHandling {
	final String COOKIE_NAME = "email";

	public Response getUserInfo(HttpServletRequest req) {
		Response response = new Response();
		CookieHandling cookieHandling = new CookieHandling();
		Cookie cookie = cookieHandling.getCookie(req);

		if (cookie != null) {
			AuthService authService = new AuthService();
			UserModel user = authService.getUserByEmail(cookie.getValue());
			if (user != null) {
				response.setStatus(200);
				response.setMessage("Lấy thông tin user thành công");
				response.setData(user);

				return response;
			} else {
				response.setMessage("Không tìm thấy user");
			}
		} else {
			response.setMessage("Không tìm thấy cookie");
		}
		response.setStatus(404);
		response.setData(null);

		return response;
	}

	public Response logOut(HttpServletRequest req, HttpServletResponse resp) {
		Response response = new Response();
		CookieHandling cookieHandling = new CookieHandling();
		Cookie cookie = cookieHandling.getCookie(req);

		if (cookie != null) {
			response.setStatus(200);
			cookieHandling.deleteCookie(resp, cookie);
			response.setMessage("Đăng xuất thành công");
			response.setData(true);
		} else {
			response.setStatus(400);
			response.setMessage("Đăng xuất thất bại");
			response.setData(false);
		}
		return response;
	}

	public Response verifyLoginAccount(HttpServletResponse resp, String email, String password) {
		Response response = new Response();
		LoginService loginService = new LoginService();
		if (loginService.checkLogin(email, password)) {
			CookieHandling cookieHandling = new CookieHandling();
			cookieHandling.addCookie(resp, email);
			response.setStatus(200);
			response.setMessage("Đăng nhập thành công");
			response.setData(true);
		} else {
			response.setStatus(200);
			response.setMessage("Đăng nhập thất bại");
			response.setData(false);
		}
		return response;
	}

	public boolean isLoggedIn(Cookie[] cookies) {
		boolean isLoggedin = false;
		for (Cookie ck : cookies) {
			if (COOKIE_NAME.equals(ck.getName()) && !("".equals(ck.getValue()))) {
				isLoggedin = true;
				break;
			}
		}
		return isLoggedin;
	}

	public int getRoleOfUser(HttpServletRequest req) {
		CookieHandling cookieHandling = new CookieHandling();
		Cookie cookie = cookieHandling.getCookie(req);
		
		String email = cookie.getValue();
		AuthService authService = new AuthService();
		return authService.getRoleByEmail(email);
	}
}

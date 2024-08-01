package ntp.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import ntp.filter.AuthenHandling;
import ntp.model.RoleModel;
import ntp.model.UserModel;
import ntp.payload.Response;
import service.RoleService;

@WebServlet(name = "RoleApi", urlPatterns = { "/api/role", "/api/role-add", "/api/role-edit" })
public class RoleApi extends HttpServlet {
	UserModel user = new UserModel();
	String roleID = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Response> responseList = new ArrayList<Response>();

		Response response = new Response();
		String servletPath = req.getServletPath();

		switch (servletPath) {
		case "/api/role":
			responseList = doGetOfRole(req);
			break;
		case "/api/role-add":
			responseList = doGetOfAddRole(req);
			break;
		case "/api/role-edit":
			responseList = doGetOfEditRole(req);
			break;
		default:
			response.setStatus(404);
			response.setMessage("Không tồn tại URL");

			responseList.add(response);
			break;
		}

		Gson gson = new Gson();
		String dataJson = gson.toJson(responseList);

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

		PrintWriter printWriter = resp.getWriter();
		printWriter.print(dataJson);
		printWriter.flush();
		printWriter.close();

	}

	private List<Response> doGetOfRole(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy toàn bộ danh sách role
		response = getAllRole();
		responseList.add(response);

		return responseList;
	}

	private Response getAllRole() {
		Response response = new Response();
		RoleService roleService = new RoleService();
		List<RoleModel> listRole = roleService.getAllRole();

		if (listRole.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy danh sách tất cả quyền thành công!");
			response.setData(listRole);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách tất cả quyền thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<Response> doGetOfAddRole(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user đăng nhập
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		return responseList;
	}

	private List<Response> doGetOfEditRole(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user đăng nhập
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin role
		response = getRoleById(roleID);
		responseList.add(response);

		return responseList;
	}

	private Response getRoleById(String roleID) {
		Response response = new Response();

		if (roleID != null && !"".equals(roleID)) {
			RoleService roleService = new RoleService();
			RoleModel role = roleService.getRoleById(Integer.parseInt(roleID));

			response.setStatus(200);
			response.setMessage("Lấy thông tin quyền thành công!");
			response.setData(role);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy thông tin quyền thất bại!");
			response.setData(null);
		}

		return response;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response response = new Response();

		String function = req.getParameter("function");
		String servletPath = req.getServletPath();

		switch (servletPath) {
		case "/api/role":
			response = doPostOfRole(req, resp, function);
			break;
		case "/api/role-add":
			response = doPostOfAddRole(req, resp, function);
			break;
		case "/api/role-edit":
			response = doPostOfEditRole(req, resp, function);
			break;
		default:
			response.setStatus(404);
			response.setMessage("Không tồn tại URL");
			break;
		}

		Gson gson = new Gson();
		String dataJson = gson.toJson(response);

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

		PrintWriter printWriter = resp.getWriter();
		printWriter.print(dataJson);
		printWriter.flush();
		printWriter.close();
	}

	private Response doPostOfRole(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "goToAddRole":
			response = goToAddRole(req);
			break;
		case "goToEditRole":
			roleID = req.getParameter("roleID");
			response = goToEditRole(roleID);
			break;
		case "deleteRole":
			int roleID = Integer.parseInt(req.getParameter("roleID"));
			response = deleteRole(roleID);
			break;
		default:
			break;
		}

		return response;
	}

	private Response goToAddRole(HttpServletRequest req) {
		Response response = new Response();

		response.setStatus(200);
		response.setMessage("Truy cập vào trang thêm quyền!");
		response.setData("/CRM-PROJECT/role-add");

		return response;
	}

	private Response doPostOfAddRole(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "addRole":
			response = addRole(req);
			break;
		default:
			break;
		}

		return response;
	}

	private Response addRole(HttpServletRequest req) {
		Response response = new Response();

		RoleModel role = new RoleModel();
		role.setName(req.getParameter("name"));
		role.setDescription(req.getParameter("description"));

		if ("".equals(role.getName())) {
			response.setStatus(400);
			response.setMessage("Tên quyền chưa được nhập!");
			response.setData(-1);

			return response;
		}

		RoleService roleService = new RoleService();
		if (roleService.addRole(role)) {
			response.setStatus(200);
			response.setMessage("Thêm quyền thành công!");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Thêm quyền thất bại!");
			response.setData(0);
		}

		return response;
	}

	private Response goToEditRole(String roleID) {
		Response response = new Response();

		if (roleID != null && !"".equals(roleID)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang chỉnh sửa quyền!");
			response.setData("/CRM-PROJECT/role-edit");
		} else {
			response.setStatus(400);
			response.setMessage("Không tìm thấy ID của quyền muốn chỉnh sửa");
			response.setData(null);
		}

		return response;
	}

	private Response doPostOfEditRole(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "editRole":
			response = editRole(req);
			break;
		default:
			break;
		}

		return response;
	}

	private Response editRole(HttpServletRequest req) {
		Response response = new Response();

		String roleID = req.getParameter("id");
		if ("0".equals(roleID)) {
			response.setStatus(400);
			response.setMessage("Không tìm thấy dữ liệu quyền!");
			response.setData(-2);

			return response;
		}

		RoleModel role = new RoleModel();
		role.setId(Integer.parseInt(roleID));
		role.setName(req.getParameter("name"));
		role.setDescription(req.getParameter("description"));

		if ("".equals(role.getName())) {
			response.setStatus(400);
			response.setMessage("Tên quyền trống!");
			response.setData(-1);

			return response;
		}

		RoleService roleService = new RoleService();
		if (roleService.updateRole(role)) {
			response.setStatus(200);
			response.setMessage("Chỉnh sửa quyền thành công!");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Chỉnh sửa quyền thất bại!");
			response.setData(0);
		}

		return response;
	}

	private Response deleteRole(int roleID) {
		Response response = new Response();
		RoleService roleService = new RoleService();

		if (!roleService.checkExistingOfUserByRoleId(roleID)) {
			if (roleService.delelteRole(roleID)) {
				response.setStatus(200);
				response.setMessage("Xóa quyền thành công!");
				response.setData(1);
			} else {
				response.setStatus(400);
				response.setMessage("Xóa quyền thất bại!");
				response.setData(0);
			}
		} else {
			response.setStatus(400);
			response.setMessage("Không thể xóa quyền này!");
			response.setData(-1);
		}

		return response;
	}
}

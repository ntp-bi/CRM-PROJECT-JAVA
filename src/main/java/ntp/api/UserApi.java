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

import ntp.filter.AuthList;
import ntp.filter.AuthenHandling;
import ntp.model.RoleModel;
import ntp.model.StatusList;
import ntp.model.TaskModel;
import ntp.model.UserModel;
import ntp.payload.Response;
import service.HomeService;
import service.ProfileService;
import service.RoleService;
import service.UserService;

@WebServlet(name = "UserApi", urlPatterns = { "/api/user", "/api/user-add", "/api/user-edit", "/api/user-detail" })
public class UserApi extends HttpServlet {
	UserModel user = new UserModel();
	String memberID = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Response> responseList = new ArrayList<Response>();
		Response response = new Response();

		String servletPath = req.getServletPath();

		switch (servletPath) {
		case "/api/user":
			responseList = doGetOfUser(req);
			break;
		case "/api/user-add":
			responseList = doGetOfAddUser(req);
			break;
		case "/api/user-edit":
			responseList = doGetOfEditUser(req);
			break;
		case "/api/user-detail":
			responseList = doGetOfDetailUser(req);
			break;
		default:
			response.setStatus(404);
			response.setMessage("Không tìm thấy URL");

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

	private List<Response> doGetOfUser(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin toàn bộ user
		response = getAllUser();
		responseList.add(response);

		return responseList;
	}

	private Response getAllUser() {
		Response response = new Response();
		UserService userService = new UserService();

		List<UserModel> listUser = userService.getAllUser();

		if (listUser.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy danh sách thành viên thành công!");
			response.setData(listUser);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách thành viên thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<Response> doGetOfAddUser(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy danh sách role
		response = getRoleList();
		responseList.add(response);

		return responseList;
	}

	private Response getRoleList() {
		Response response = new Response();
		RoleService roleService = new RoleService();

		List<RoleModel> listRole = roleService.getAllRole();

		if (listRole.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy danh sách quyền thành công!");
			response.setData(listRole);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách quyền thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<Response> doGetOfEditUser(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user đang đăng nhập
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin user để edit
		response = getUserById(memberID);
		responseList.add(response);

		// Lấy dạnh sách role để edit
		response = getRoleList();
		responseList.add(response);

		memberID = null;
		return responseList;
	}

	private Response getUserById(String memberID) {
		Response response = new Response();

		if (memberID != null && !"".equals(memberID)) {
			UserService userService = new UserService();
			UserModel user = userService.getUserById(Integer.parseInt(memberID));

			response.setStatus(200);
			response.setMessage("Lấy thông tin thành viên thành công!");
			response.setData(user);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy thông tin thành viên thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<Response> doGetOfDetailUser(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user đang đăng nhập
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin chi tiết của thành viên
		response = getUserById(memberID);
		responseList.add(response);

		// Lấy status task by userID
		response = getTaskStatusByUserId(memberID);
		responseList.add(response);

		// Lấy thông tin danh sách công việc by userID
		response = getTaskListByUserId(memberID);
		responseList.add(response);

		memberID = null;
		return responseList;
	}

	private Response getTaskStatusByUserId(String memberID) {
		Response response = new Response();

		int[] listStatus = { 0, 0, 0 }; // 0-unbegun qty, 1-doing qty, 2-finish qty

		if (user != null) {
			HomeService homeService = new HomeService();
			List<Integer> listTaskStatus = homeService.getTaskStatusByUserId(user.getId());

			for (int i : listTaskStatus) {
				if (i == StatusList.UNBEGUN.getValue()) {
					listStatus[0]++;
				} else if (i == StatusList.DOING.getValue()) {
					listStatus[1]++;
				} else if (i == StatusList.FINISH.getValue()) {
					listStatus[2]++;
				}
			}

			if (listTaskStatus.size() > 0) {
				listStatus[0] = Math.round(listStatus[0] * 100.0f / listTaskStatus.size());
				listStatus[1] = Math.round(listStatus[1] * 100.0f / listTaskStatus.size());
				listStatus[2] = Math.round(listStatus[2] * 100.0f / listTaskStatus.size());
			}

			response.setStatus(200);
			response.setMessage("Lấy danh sách thống kê công việc của người dùng thành công!");
			response.setData(listStatus);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách thống kê công việc của người dùng thất bại!");
			response.setData(null);
		}

		return response;
	}

	private Response getTaskListByUserId(String memberID) {
		Response response = new Response();

		if (user != null) {
			ProfileService profileService = new ProfileService();
			List<TaskModel> listTask = profileService.getTaskListByUserId(Integer.parseInt(memberID));

			response.setStatus(200);
			response.setMessage("Lấy danh sách công việc thành công!");
			response.setData(listTask);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách công việc thất bại!");
			response.setData(null);
		}

		return response;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response response = new Response();

		String servletPath = req.getServletPath();
		String function = req.getParameter("function");
		switch (servletPath) {
		case "/api/user":
			response = doPostOfUser(req, resp, function);
			break;
		case "/api/user-add":
			response = doPostOfAddUser(req, resp, function);
			break;
		case "/api/user-edit":
			response = doPostOfEditUser(req, resp, function);
			break;
		case "/api/user-detail":
			response = doPostOfDetailUser(req, resp, function);
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

	private Response doPostOfUser(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "goToAddUser":
			response = goToAddUser();
			break;
		case "goToEditUser":
			memberID = req.getParameter("memberID");
			response = goToEditUser(memberID);
			break;
		case "goToDetailUser":
			memberID = req.getParameter("memberID");
			response = goToDetailUser(memberID);
			break;
		case "deleteUser":
			int memberId = Integer.parseInt(req.getParameter("memberID"));
			response = deleteUser(memberId);
		default:
			break;
		}

		return response;
	}

	private Response goToAddUser() {
		Response response = new Response();

		response.setStatus(200);
		response.setMessage("Truy cập vào trang thêm thành viên!");
		response.setData("/CRM-PROJECT/user-add");

		return response;
	}

	private Response doPostOfAddUser(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "addUser":
			response = addUser(req);
			break;
		default:
			break;
		}

		return response;
	}

	private Response addUser(HttpServletRequest req) {
		Response response = new Response();
		UserService userService = new UserService();

		UserModel user = new UserModel();
		user.setEmail(req.getParameter("email"));
		user.setAvatar(req.getParameter("avatar"));
		user.setFullname(req.getParameter("fullname"));
		user.setPassword(req.getParameter("password"));

		RoleModel role = new RoleModel();
		role.setId(Integer.parseInt(req.getParameter("role-id")));
		user.setRole(role);

		String confirmPassword = req.getParameter("confirm-password");

		if (!user.getPassword().equals(confirmPassword)) {
			response.setStatus(400);
			response.setMessage("Xác nhận mật khẩu không khớp!");
			response.setData(-1);

			return response;
		} else if ("".equals(user.getEmail())) {
			response.setStatus(400);
			response.setMessage("Email chưa được nhập!");
			response.setData(-2);

			return response;
		}

		if (userService.addUser(user)) {
			response.setStatus(200);
			response.setMessage("Thêm thành viên thành công!");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Thêm thành viên thất bại!");
			response.setData(0);
		}

		return response;
	}

	private Response deleteUser(int memberId) {
		Response response = new Response();
		UserService userService = new UserService();

		if (user.getRole().getId() == AuthList.ADMIN.getValue()) {
			if (userService.checkExistingOfTaskByUserId(memberId)) {
				response.setStatus(400);
				response.setMessage("Không thể xóa thành viên này!");
				response.setData(-1);
			} else if (userService.checkExistingOfProjectByLeaderId(memberId)) {
				response.setStatus(400);
				response.setMessage("Không thể xóa thành viên này!");
				response.setData(-2);
			} else {
				if (userService.deleteUser(memberId)) {
					response.setStatus(200);
					response.setMessage("Xóa thành viên thành công!");
					response.setData(1);
				} else {
					response.setStatus(400);
					response.setMessage("Xóa thành viên thất bại!");
					response.setData(0);
				}
			}

		} else {
			response.setStatus(403);
			response.setMessage("Người dùng không có quyền xóa!");
			response.setData(403);
		}

		return response;
	}

	private Response goToEditUser(String memberID) {
		Response response = new Response();

		if (memberID != null && !"".equals(memberID)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang chỉnh sửa thành viên!");
			response.setData("/CRM-PROJECT/user-edit");
		} else {
			response.setStatus(400);
			response.setMessage("Không tìm thấy ID của thành viên muốn chỉnh sửa");
			response.setData(null);
		}

		return response;
	}

	private Response doPostOfEditUser(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "editUser":
			response = editUser(req);
			break;
		default:
			break;
		}

		return response;
	}

	private Response editUser(HttpServletRequest req) {
		Response response = new Response();

		String userId = req.getParameter("id");
		if ("0".equals(userId)) {
			response.setStatus(400);
			response.setMessage("Không tìm thấy dữ liệu thành viên");
			response.setData(-3);

			return response;
		}

		UserModel user = new UserModel();
		user.setId(Integer.parseInt(userId));
		user.setEmail(req.getParameter("email"));
		user.setPassword(req.getParameter("password"));
		user.setFullname(req.getParameter("fullname"));
		user.setAvatar(req.getParameter("avatar"));

		RoleModel role = new RoleModel();
		role.setId(Integer.parseInt(req.getParameter("role-id")));
		user.setRole(role);

		String confirmPassword = req.getParameter("confirm-password");

		if (!user.getPassword().equals(confirmPassword)) {
			response.setStatus(400);
			response.setMessage("Xác nhận mật khẩu không khớp");
			response.setData(-1);

			return response;
		} else if ("".equals(user.getEmail())) {
			response.setStatus(400);
			response.setMessage("Email bị bỏ trống");
			response.setData(-2);

			return response;
		}

		UserService userService = new UserService();
		if (userService.editUser(user)) {
			response.setStatus(200);
			response.setMessage("Chỉnh sửa thông tin thành viên thành công");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Chỉnh sửa thông tin thành viên thất bại");
			response.setData(0);
		}

		return response;
	}

	private Response goToDetailUser(String memberID) {
		Response response = new Response();

		if (memberID != null && !"".equals(memberID)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang chi tiết thành viên!");
			response.setData("/CRM-PROJECT/user-detail");
		} else {
			response.setStatus(400);
			response.setMessage("Không tìm thấy ID của thành viên muốn xem");
			response.setData(null);
		}

		return response;

	}

	private Response doPostOfDetailUser(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;

		default:
			break;
		}

		return response;
	}
}

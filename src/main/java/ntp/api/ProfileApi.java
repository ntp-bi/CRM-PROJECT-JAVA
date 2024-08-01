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
import ntp.model.StatusList;
import ntp.model.StatusModel;
import ntp.model.TaskModel;
import ntp.model.UserModel;
import ntp.payload.Response;
import service.HomeService;
import service.ProfileService;
import service.StatusService;

@WebServlet(name = "ProfileApi", urlPatterns = { "/api/profile", "/api/profile-task-update" })
public class ProfileApi extends HttpServlet {
	UserModel user = new UserModel();
	String taskID = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Response> responseList = new ArrayList<Response>();

		Response response = new Response();
		String servletPath = req.getServletPath();

		switch (servletPath) {
		case "/api/profile":
			responseList = doGetOfProfile(req);
			break;
		case "/api/profile-task-update":
			responseList = doGetOfProfileTaskUpdate(req);
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

	private List<Response> doGetOfProfile(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy status task by userID
		response = getTaskStatusByUserId();
		responseList.add(response);

		// Lấy thông tin danh sách công việc by userID
		response = getTaskListByUserId();
		responseList.add(response);

		return responseList;
	}

	private Response getTaskStatusByUserId() {
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

	private Response getTaskListByUserId() {
		Response response = new Response();

		if (user != null) {
			ProfileService profileService = new ProfileService();
			List<TaskModel> listTask = profileService.getTaskListByUserId(user.getId());

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

	private List<Response> doGetOfProfileTaskUpdate(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin công việc by ID in PROFILE
		response = getTaskByIdInProfile(taskID);
		responseList.add(response);

		// Lấy tất cả danh sách trạng thái
		response = getAllStatus();
		responseList.add(response);

		return responseList;
	}

	private Response getTaskByIdInProfile(String taskID) {
		Response response = new Response();

		if (taskID != null && !"".equals(taskID)) {
			ProfileService profileService = new ProfileService();
			TaskModel task = profileService.getTaskByIdInProfile(Integer.parseInt(taskID));

			response.setStatus(200);
			response.setMessage("Lấy dữ liệu công việc thành công!");
			response.setData(task);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy dữ liệu công việc thất bại!");
			response.setData(null);
		}

		return response;
	}

	private Response getAllStatus() {
		Response response = new Response();

		StatusService statusService = new StatusService();
		List<StatusModel> listStatus = statusService.getAllStatus();

		if (listStatus.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy danh sách trạng thái công việc thành công!");
			response.setData(listStatus);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách trạng thái công việc thất bại!");
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
		case "/api/profile":
			response = doPostOfProfile(req, resp, function);
			break;
		case "/api/profile-task-update":
			response = doPostOfProfileTaskUpdate(req, resp, function);
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

	private Response doPostOfProfile(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;

		case "goToEditTask":
			taskID = req.getParameter("taskID");
			response = goToUpdateTask(taskID);
			break;

		default:
			break;
		}

		return response;
	}

	private Response goToUpdateTask(String taskID) {
		Response response = new Response();

		if (taskID != null && !"".equals(taskID)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang cập nhập công việc!");
			response.setData("/CRM-PROJECT/profile-task-update");
		} else {
			response.setStatus(404);
			response.setMessage("Không tìm thấy ID của công việc muốn cập nhập!");
			response.setData(null);
		}

		return response;
	}

	private Response doPostOfProfileTaskUpdate(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "saveStatusProfile":
			response = editStatusOfTask(req);
			break;

		default:
			break;
		}

		return response;
	}

	private Response editStatusOfTask(HttpServletRequest req) {
		Response response = new Response();

		String taskID = req.getParameter("taskID");
		String statusID = req.getParameter("statusID");

		if ("0".equals(taskID)) {
			response.setStatus(400);
			response.setMessage("Không tìm thấy dữ liệu công việc");
			response.setData(-1);

			return response;
		}

		ProfileService profileService = new ProfileService();
		boolean result = profileService.editStatusOfTask(Integer.parseInt(taskID), Integer.parseInt(statusID));

		if (result) {
			response.setStatus(200);
			response.setMessage("Cập nhập trạng thái công việc thành công!");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Cập nhập trạng thái công việc thất bại!");
			response.setData(0);
		}

		return response;
	}
}

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
import ntp.model.ProjectModel;
import ntp.model.StatusList;
import ntp.model.StatusModel;
import ntp.model.TaskModel;
import ntp.model.UserModel;
import ntp.payload.Response;
import service.ProjectService;
import service.StatusService;
import service.TaskService;
import service.UserService;

@WebServlet(name = "TaskApi", urlPatterns = { "/api/task", "/api/task-add", "/api/task-edit" })
public class TaskApi extends HttpServlet {
	UserModel user = new UserModel();
	String taskID = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Response> responseList = new ArrayList<Response>();

		Response response = new Response();
		String servletPath = req.getServletPath();

		switch (servletPath) {
		case "/api/task":
			responseList = doGetOfTask(req);
			break;
		case "/api/task-add":
			responseList = doGetOfAddTask(req);
			break;
		case "/api/task-edit":
			responseList = doGetOfEditTask(req);
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

	private List<Response> doGetOfTask(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin nhiệm vụ
		response = getAllTask();
		responseList.add(response);

		return responseList;
	}

	private Response getAllTask() {
		Response response = new Response();
		TaskService taskService = new TaskService();
		List<TaskModel> listTask;

		if (user.getRole().getId() == AuthList.ADMIN.getValue()) {
			listTask = taskService.getAllTask();
		} else {
			listTask = taskService.getAllTaskByLeaderIdOfProject(user.getId());
		}

		if (listTask.size() > 0) {
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

	private List<Response> doGetOfAddTask(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		Response response = new Response();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy danh sách dự án
		response = getAllProject();
		responseList.add(response);

		// Lấy danh sách user ngoại trừ admin
		response = getAllUserExceptAdmin();
		responseList.add(response);

		return responseList;
	}

	private Response getAllProject() {
		Response response = new Response();

		ProjectService projectService = new ProjectService();
		List<ProjectModel> listProject = projectService.getAllProject();

		if (listProject.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy danh sách dự án thành công!");
			response.setData(listProject);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách dự án thất bại!");
			response.setData(null);
		}

		return response;
	}

	private Response getAllUserExceptAdmin() {
		Response response = new Response();

		TaskService taskService = new TaskService();
		List<UserModel> listUser = taskService.getAllUserExceptAdmin();

		if (listUser.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy danh sách user thành công!");
			response.setData(listUser);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách user thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<Response> doGetOfEditTask(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin nhiệm vụ
		response = getTaskById(taskID);
		responseList.add(response);

		// Lấy danh sách dự án
		response = getAllProject();
		responseList.add(response);

		// Lấy danh sách user ngoại trừ admin
		response = getAllUserExceptAdmin();
		responseList.add(response);

		// Lấy danh sách status
		response = getAllStatus();
		responseList.add(response);

		taskID = null;

		return responseList;
	}

	private Response getTaskById(String taskID) {
		Response response = new Response();

		TaskService taskService = new TaskService();
		TaskModel task = taskService.getTaskById(Integer.parseInt(taskID));

		if (taskID != null && "".equals(taskID)) {
			response.setStatus(200);
			response.setMessage("Lấy thông tin nhiệm vụ thành công!");
			response.setData(task);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy thông tin nhiệm vụ thất bại!");
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
			response.setMessage("Lấy danh sách trạng thái thành công!");
			response.setData(listStatus);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách trạng thái thất bại!");
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
		case "/api/task":
			response = doPostOfTask(req, resp, function);
			break;
		case "/api/task-add":
			response = doPostOfAddTask(req, resp, function);
			break;
		case "/api/task-edit":
			response = doPostOfEditTask(req, resp, function);
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

	private Response doPostOfTask(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "goToAddTask":
			response = goToAddTask(req);
			break;
		case "goToEditTask":
			taskID = req.getParameter("taskID");
			response = goToEditTask(taskID);
			break;
		case "deleteTask":
			int taskId = Integer.parseInt(req.getParameter("taskID"));
			response = deleteTask(taskId);
			break;
		default:
			break;
		}

		return response;
	}

	private Response goToAddTask(HttpServletRequest req) {
		Response response = new Response();

		response.setStatus(200);
		response.setMessage("Truy cập vào trang thêm nhiệm vụ!");
		response.setData("/CRM-PROJECT/task-add");

		return response;
	}

	private Response doPostOfAddTask(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "addTask":
			response = addTask(req);
			break;
		default:
			break;
		}

		return response;
	}

	private Response addTask(HttpServletRequest req) {
		Response response = new Response();
		TaskModel task = checkMissingInputAdd(req);

		if (task == null) {
			response.setStatus(400);
			response.setMessage("Dữ liệu chưa được nhập đầy đủ!");
			response.setData(-1);

			return response;
		}

		TaskService taskService = new TaskService();
		if (taskService.addTask(task)) {
			response.setStatus(200);
			response.setMessage("Thêm nhiệm vụ thành công!");
			response.setData(1);
		} else {
			response.setStatus(404);
			response.setMessage("Thêm nhiệm vụ thất bại!");
			response.setData(0);
		}

		return response;
	}

	private TaskModel checkMissingInputAdd(HttpServletRequest req) {
		boolean missingInput = false;

		TaskModel task = new TaskModel();
		task.setName(req.getParameter("taskName"));
		task.setStart_date(req.getParameter("start-date"));
		task.setEnd_date(req.getParameter("end-date"));

		if ("".equals(task.getName()) || "".equals(task.getStart_date()) || "".equals(task.getEnd_date())) {
			missingInput = true;
		}

		String projectId = req.getParameter("projectID");
		if ("".equals(projectId) || projectId == null) {
			missingInput = true;
		} else {
			ProjectModel project = new ProjectModel();
			project.setId(Integer.parseInt(projectId));
			task.setProject(project);
		}

		String userId = req.getParameter("userID");
		if ("".equals(userId) || userId == null) {
			missingInput = true;
		} else {
			UserModel user = new UserModel();
			user.setId(Integer.parseInt(userId));
			task.setUser(user);
		}

		StatusModel status = new StatusModel();
		status.setId(StatusList.UNBEGUN.getValue());
		task.setStatus(status);

		if (missingInput) {
			return null;
		} else {
			return task;
		}
	}

	private Response goToEditTask(String taskID) {
		Response response = new Response();

		if (taskID != null && !"".equals(taskID)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang chỉnh sửa nhiệm vụ!");
			response.setData("/CRM-PROJECT/task-edit");
		} else {
			response.setStatus(400);
			response.setMessage("Không tìm thấy ID của nhiệm vụ muốn chỉnh sửa");
			response.setData(null);
		}

		return response;
	}

	private Response doPostOfEditTask(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "editTask":
			response = editTask(req);
			break;

		default:
			break;
		}

		return response;
	}

	private Response editTask(HttpServletRequest req) {
		Response response = new Response();
		TaskService taskService = new TaskService();

		String taskID = req.getParameter("id");
		if ("0".equals(taskID)) {
			response.setStatus(400);
			response.setMessage("Không tìm thấy dữ liệu nhiệm vụ!");
			response.setData(-2);

			return response;
		}

		TaskModel task = checkMissingInputEdit(req);
		if (task == null) {
			response.setStatus(400);
			response.setMessage("Dữ liệu chưa được nhập đầy đủ!");
			response.setData(-1);

			return response;
		}

		if (taskService.editTask(task)) {
			response.setStatus(200);
			response.setMessage("Chỉnh sửa nhiệm vụ thành công");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Chỉnh sửa nhiệm vụ thất bại");
			response.setData(0);
		}

		return response;
	}

	private TaskModel checkMissingInputEdit(HttpServletRequest req) {
		boolean missingInput = false;

		TaskModel task = new TaskModel();
		task.setId(Integer.parseInt(req.getParameter("id")));
		task.setName(req.getParameter("taskName"));
		task.setStart_date(req.getParameter("start-date"));
		task.setEnd_date(req.getParameter("end-date"));

		if ("".equals(task.getName()) || "".equals(task.getStart_date()) || "".equals(task.getEnd_date())) {
			missingInput = true;
		}

		String projectId = req.getParameter("projectID");
		if ("".equals(projectId) || projectId == null) {
			missingInput = true;
		} else {
			ProjectModel project = new ProjectModel();
			project.setId(Integer.parseInt(projectId));
			task.setProject(project);
		}

		String userId = req.getParameter("userID");
		if ("".equals(userId) || userId == null) {
			missingInput = true;
		} else {
			UserModel user = new UserModel();
			user.setId(Integer.parseInt(userId));
			task.setUser(user);
		}

		String statusId = req.getParameter("statusID");
		if ("".equals(userId) || userId == null) {
			missingInput = true;
		} else {
			StatusModel status = new StatusModel();
			status.setId(Integer.parseInt(statusId));
			task.setStatus(status);
		}
		if (missingInput) {
			return null;
		} else {
			return task;
		}
	}

	private Response deleteTask(int taskId) {
		Response response = new Response();
		TaskService taskService = new TaskService();

		if (taskService.deleteTask(taskId)) {
			response.setStatus(200);
			response.setMessage("Xóa nhiệm vụ thành công!");
			response.setData(true);
		} else {
			response.setStatus(400);
			response.setMessage("Xóa nhiệm vụ thất bại!");
			response.setData(false);
		}

		return response;
	}
}

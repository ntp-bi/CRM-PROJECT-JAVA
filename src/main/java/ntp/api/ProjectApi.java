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
import ntp.model.MemberTaskModel;
import ntp.model.ProjectModel;
import ntp.model.StatusList;
import ntp.model.TaskModel;
import ntp.model.UserModel;
import ntp.payload.Response;
import service.ProjectService;
import service.UserService;

@WebServlet(name = "ProjectApi", urlPatterns = { "/api/project", "/api/project-add", "/api/project-edit",
		"/api/project-detail" })
public class ProjectApi extends HttpServlet {
	UserModel user = new UserModel();
	String projectID = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Response> responseList = new ArrayList<Response>();
		String servletPath = req.getServletPath();

		Response response = new Response();

		switch (servletPath) {
		case "/api/project":
			responseList = doGetOfProject(req);
			break;
		case "/api/project-add":
			responseList = doGetOfAddProject(req);
			break;
		case "/api/project-edit":
			responseList = doGetOfEditProject(req);
			break;
		case "/api/project-detail":
			responseList = doGetOfDetailProject(req);
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

	private List<Response> doGetOfProject(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy toàn bộ thông tin project
		response = getAllProject();
		responseList.add(response);

		return responseList;
	}

	private Response getAllProject() {
		Response response = new Response();
		ProjectService projectService = new ProjectService();
		List<ProjectModel> listProject;

		if (user.getRole().getId() == AuthList.ADMIN.getValue()) {
			listProject = projectService.getAllProject();
		} else {
			listProject = projectService.getAllProjectByLeaderId(user.getId());
		}

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

	private List<Response> doGetOfAddProject(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy danh sách leader
		response = getAllLeader();
		responseList.add(response);

		return responseList;
	}

	private Response getAllLeader() {
		Response response = new Response();
		ProjectService projectService = new ProjectService();

		List<UserModel> listLeader = projectService.getAllLeader();
		if (listLeader.size() > 0) {
			response.setStatus(200);
			response.setMessage("Lấy thông tin quản lý thành công!");
			response.setData(listLeader);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy thông tin quản lý thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<Response> doGetOfEditProject(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin dự án
		response = getProjectById(projectID, false);
		responseList.add(response);

		// Lấy danh sách leader
		response = getAllLeader();
		responseList.add(response);

		// Quản lý việc chỉnh sửa người quản lý của dự án
		response = manageLeaderEdit();
		responseList.add(response);

		projectID = null;

		return responseList;
	}

	private Response getProjectById(String projectId, boolean changeDateFormat) {
		Response response = new Response();

		if (projectId != null && !"".equals(projectId)) {
			ProjectService projectService = new ProjectService();

			ProjectModel project = projectService.getProjectById(Integer.parseInt(projectId), changeDateFormat);
			response.setStatus(200);
			response.setMessage("Lấy thông tin dự án thành công!");
			response.setData(project);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy thông tin dự án thất bại!");
			response.setData(null);
		}

		return response;
	}

	private Response manageLeaderEdit() {
		Response response = new Response();

		if (user.getRole().getId() == AuthList.ADMIN.getValue()) {
			response.setStatus(200);
			response.setMessage("Có quyền thay đổi người quản lý của dự án!");
			response.setData(true);
		} else {
			response.setStatus(404);
			response.setMessage("Không có quyền thay đổi người quản lý của dự án!");
			response.setData(false);
		}

		return response;
	}

	private List<Response> doGetOfDetailProject(HttpServletRequest req) {
		List<Response> responseList = new ArrayList<Response>();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		Response response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy thông tin dự án
		response = getProjectById(projectID, true);
		responseList.add(response);

		// Lấy thông tin trạng thái công việc của dự án
		response = getTaskStatusByProjectId(projectID);
		responseList.add(response);

		// Lấy thông tin công việc của dự án
		response = getTaskListByProjectId(projectID);
		responseList.add(response);

		projectID = null;

		return responseList;
	}

	private Response getTaskStatusByProjectId(String projectID) {
		Response response = new Response();

		int[] listStatus = { 0, 0, 0 }; // 1-unbegun num, 2-doing, 3-finish

		if (projectID != null && !"".equals(projectID)) {
			ProjectService projectService = new ProjectService();
			List<Integer> listTaskStatus = projectService.getTaskStatusByProjectId(Integer.parseInt(projectID));

			if (listTaskStatus.size() > 0) {
				for (int i : listTaskStatus) {
					if (i == StatusList.UNBEGUN.getValue()) {
						listStatus[0]++;
					} else if (i == StatusList.DOING.getValue()) {
						listStatus[1]++;
					} else if (i == StatusList.FINISH.getValue()) {
						listStatus[2]++;
					}
				}
				listStatus[0] = Math.round(listStatus[0] * 100.0f / listTaskStatus.size());
				listStatus[1] = Math.round(listStatus[1] * 100.0f / listTaskStatus.size());
				listStatus[2] = Math.round(listStatus[2] * 100.0f / listTaskStatus.size());
			}

			response.setStatus(200);
			response.setMessage("Lấy danh sách thống kê công việc của dự án thành công!");
			response.setData(listStatus);
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách thống công việc của dự án thất bại!");
			response.setData(null);
		}

		return response;
	}

	private Response getTaskListByProjectId(String projectID) {
		Response response = new Response();

		if (projectID != null && !"".equals(projectID)) {
			ProjectService projectService = new ProjectService();
			List<TaskModel> listTask = projectService.getTaskListByProjectId(Integer.parseInt(projectID));

			response.setStatus(200);
			response.setMessage("Lấy danh sách công việc của dự án thành công!");
			response.setData(arrangeTaskByMember(listTask));
		} else {
			response.setStatus(404);
			response.setMessage("Lấy danh sách công việc của dự án thất bại!");
			response.setData(null);
		}

		return response;
	}

	private List<MemberTaskModel> arrangeTaskByMember(List<TaskModel> inputList) {
		List<MemberTaskModel> resultList = new ArrayList<MemberTaskModel>();

		if (inputList.size() > 0) {
			for (int i = 0; i < inputList.size(); i++) {
				boolean isDuplicate = false;

				if (i != 0) {
					for (int k = 0; k < resultList.size(); k++) {
						if (inputList.get(i).getUser().getId() == resultList.get(k).getUser().getId()) {
							resultList.get(k).getTaskList().add(inputList.get(i));
							isDuplicate = true;
							break;
						}
					}
				}

				if (!isDuplicate) {
					MemberTaskModel taskByMember = new MemberTaskModel();

					UserModel user = inputList.get(i).getUser();
					taskByMember.setUser(user);

					List<TaskModel> listTask = new ArrayList<TaskModel>();
					listTask.add(inputList.get(i));
					taskByMember.setTaskList(listTask);

					resultList.add(taskByMember);
				}
			}
		} else {
			return null;
		}

		return resultList;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response response = new Response();
		String servletPath = req.getServletPath();

		String function = req.getParameter("function");

		switch (servletPath) {
		case "/api/project":
			response = doPostOfProject(req, resp, function);
			break;
		case "/api/project-add":
			response = doPostOfAddProject(req, resp, function);
			break;
		case "/api/project-edit":
			response = doPostOfEditProject(req, resp, function);
			break;
		case "/api/project-detail":
			response = doPostOfDetailProject(req, resp, function);
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

	private Response doPostOfProject(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "goToAddProject":
			response = goToAddProject(req);
			break;
		case "goToEditProject":
			projectID = req.getParameter("projectID");
			response = goToEditProject(projectID);
			break;
		case "goToDetailProject":
			projectID = req.getParameter("projectID");
			response = goToDetailProject(projectID);
			break;
		case "deleteProject":
			int projectId = Integer.parseInt(req.getParameter("projectID"));
			response = deleteProject(projectId);
			break;
		default:
			break;
		}

		return response;
	}

	private Response goToAddProject(HttpServletRequest req) {
		Response response = new Response();

		response.setStatus(200);
		response.setMessage("Truy cập vào trang thêm dự án");
		response.setData("/CRM-PROJECT/project-add");

		return response;
	}

	private Response doPostOfAddProject(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "addProject":
			response = addProject(req);
			break;

		default:
			break;
		}

		return response;
	}

	private Response addProject(HttpServletRequest req) {
		Response response = new Response();

		ProjectModel project = new ProjectModel();
		project.setName(req.getParameter("name"));
		project.setStart_date(req.getParameter("start-date"));
		project.setEnd_date(req.getParameter("end-date"));

		String leaderId = req.getParameter("leaderId");

		UserModel user = new UserModel();
		user.setId(Integer.parseInt(leaderId));
		project.setLeader(user);

		if ("".equals(project.getName()) || "".equals(project.getStart_date()) || "".equals(project.getEnd_date())
				|| "".equals(leaderId) || leaderId == null) {
			response.setStatus(400);
			response.setMessage("Dữ liệu chưa được nhập đủ!");
			response.setData(-1);

			return response;
		}

		ProjectService projectService = new ProjectService();
		if (projectService.addProject(project)) {
			response.setStatus(200);
			response.setMessage("Thêm dự án thành công!");
			response.setData(1);
		} else {
			response.setStatus(404);
			response.setMessage("Thêm dự án thất bại!");
			response.setData(0);
		}

		return response;
	}

	private Response goToEditProject(String projectId) {
		Response response = new Response();

		if (projectId != null && !"".equals(projectId)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang chỉnh sửa dự án!");
			response.setData("/CRM-PROJECT/project-edit");
		} else {
			response.setStatus(404);
			response.setMessage("Không tìm thấy ID của dự án muốn chỉnh sửa!");
			response.setData(null);
		}

		return response;
	}

	private Response doPostOfEditProject(HttpServletRequest req, HttpServletResponse resp, String function) {
		Response response = new Response();

		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;
		case "editProject":
			response = editProject(req);
			break;
		default:
			break;
		}

		return response;
	}

	private Response editProject(HttpServletRequest req) {
		Response response = new Response();

		String projectId = req.getParameter("id");
		String leaderId = req.getParameter("leaderId");

		if ("0".equals(projectId)) {
			response.setStatus(400);
			response.setMessage("Không tìm thấy dữ liệu dự án!");
			response.setData(-2);

			return response;
		}

		ProjectModel project = new ProjectModel();
		project.setId(Integer.parseInt(req.getParameter("id")));
		project.setName(req.getParameter("name"));
		project.setStart_date(req.getParameter("start-date"));
		project.setEnd_date(req.getParameter("end-date"));

		UserModel user = new UserModel();
		user.setId(Integer.parseInt(leaderId));
		project.setLeader(user);

		if ("".equals(project.getName()) || "".equals(project.getStart_date()) || "".equals(project.getEnd_date())
				|| "".equals(leaderId) || leaderId == null) {
			response.setStatus(400);
			response.setMessage("Chưa nhập đủ dữ liệu!");
			response.setData(-1);

			return response;
		}

		ProjectService projectService = new ProjectService();

		if (projectService.editProject(project)) {
			response.setStatus(200);
			response.setMessage("Chỉnh sửa thông tin dự án thành công!");
			response.setData(1);
		} else {
			response.setStatus(400);
			response.setMessage("Chỉnh sửa thông tin dự án thất bại!");
			response.setData(0);
		}

		return response;
	}

	private Response goToDetailProject(String projectId) {
		Response response = new Response();

		if (projectId != null && !"".equals(projectId)) {
			response.setStatus(200);
			response.setMessage("Truy cập vào trang chi tiết dự án!");
			response.setData("/CRM-PROJECT/project-detail");
		} else {
			response.setStatus(404);
			response.setMessage("Không tìm thấy ID của dự án muốn xem chi tiết!");
			response.setData(null);
		}

		return response;
	}

	private Response doPostOfDetailProject(HttpServletRequest req, HttpServletResponse resp, String function) {
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

	private Response deleteProject(int projectId) {
		Response response = new Response();

		ProjectService projectService = new ProjectService();

		if (!projectService.checkExistingOfTaskByProjectId(projectId)) {
			if (projectService.deleteProject(projectId)) {
				response.setStatus(200);
				response.setMessage("Xóa dự án thành công!");
				response.setData(1);
			} else {
				response.setStatus(404);
				response.setMessage("Xóa dự án thất bại!");
				response.setData(0);
			}
		} else {
			response.setStatus(404);
			response.setMessage("Không thể xóa dự án này!");
			response.setData(-1);
		}

		return response;
	}
}

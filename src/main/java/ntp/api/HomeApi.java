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
import ntp.model.UserModel;
import ntp.payload.Response;
import service.HomeService;

@WebServlet(name = "HomeApi", urlPatterns = { "/api/home" })
public class HomeApi extends HttpServlet {
	UserModel user = new UserModel();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Response> responseList = new ArrayList<Response>();

		Response response = new Response();

		// Lấy thông tin user
		AuthenHandling authenHandling = new AuthenHandling();
		response = authenHandling.getUserInfo(req);
		responseList.add(response);

		user = (UserModel) response.getData();

		// Lấy status task by userID
		response = getTaskStatusByUserId();
		responseList.add(response);

		Gson gson = new Gson();
		String dataJson = gson.toJson(responseList);

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

		PrintWriter printWriter = resp.getWriter();
		printWriter.print(dataJson);
		printWriter.flush();
		printWriter.close();
	}

	private Response getTaskStatusByUserId() {
		Response response = new Response();

		int[] listStatus = { 0, 0, 0, 0 }; // 0-task qty sum, 1-unbegun qty, 2-doing qty, 3-finish qty

		if (user != null) {
			HomeService homeService = new HomeService();

			List<Integer> listTaskStatus = homeService.getTaskStatusByUserId(user.getId());
			if (listTaskStatus.size() > 0) {
				listStatus[0] = listTaskStatus.size();
				for (int i : listTaskStatus) {
					if (i == StatusList.UNBEGUN.getValue()) {
						listStatus[1]++;
					} else if (i == StatusList.DOING.getValue()) {
						listStatus[2]++;
					} else if (i == StatusList.FINISH.getValue()) {
						listStatus[3]++;
					}
				}

				response.setStatus(200);
				response.setMessage("Lấy danh sách thống kê công việc của người dùng thành công!");
				response.setData(listStatus);
			} else {
				response.setStatus(404);
				response.setMessage("Lấy danh sách thống kê công việc của người dùng thất bại!");
				response.setData(null);
			}
		}

		return response;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response response = new Response();

		String function = req.getParameter("function");
		switch (function) {
		case "logout":
			AuthenHandling authenHandling = new AuthenHandling();
			response = authenHandling.logOut(req, resp);
			break;

		default:
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
}

package service;

import java.util.List;

import ntp.model.TaskModel;
import ntp.model.UserModel;
import repository.TaskRepository;
import repository.UserRepository;

public class TaskService {
	TaskRepository taskRepository = new TaskRepository();
	UserRepository userRepository = new UserRepository();

	public List<TaskModel> getAllTask() {
		return taskRepository.getAllTask();
	}

	public List<TaskModel> getAllTaskByLeaderIdOfProject(int leaderId) {
		return taskRepository.getAllTaskByLeaderIdOfProject(leaderId);
	}

	public List<Integer> getTaskStatusByUserId(int userID) {
		return taskRepository.getTaskStatusByUserId(userID);
	}

	public TaskModel getTaskById(int taskId) {
		return taskRepository.getTaskById(taskId);
	}

	public boolean addTask(TaskModel newTask) {
		return taskRepository.addTask(newTask) > 0;
	}

	public boolean editTask(TaskModel task) {
		return taskRepository.editTask(task) > 0;
	}

	public boolean deleteTask(int taskId) {
		return taskRepository.deleteTask(taskId) > 0;
	}
	
	// USER
	public List<UserModel> getAllUserExceptAdmin() {
		return userRepository.getAllUserExceptAdmin();
	}
}

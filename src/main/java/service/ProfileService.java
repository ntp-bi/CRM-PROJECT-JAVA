package service;

import java.util.List;

import ntp.model.TaskModel;
import repository.TaskRepository;

public class ProfileService {

	TaskRepository taskRepository = new TaskRepository();

	public List<TaskModel> getTaskListByUserId(int userId) {
		return taskRepository.getTaskListByUserId(userId);
	}

	public TaskModel getTaskByIdInProfile(int taskID) {
		return taskRepository.getTaskByIdInProfile(taskID);
	}

	public boolean editStatusOfTask(int taskID, int statusID) {
		return taskRepository.editStatusOfTask(taskID, statusID) > 0;
	}
}

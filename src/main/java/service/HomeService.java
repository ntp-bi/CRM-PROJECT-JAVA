package service;

import java.util.List;

import ntp.model.UserModel;
import repository.TaskRepository;
import repository.UserRepository;

public class HomeService {
	UserRepository userRepository = new UserRepository();
	TaskRepository taskRepository = new TaskRepository();

	public UserModel getUserByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}

	public List<Integer> getTaskStatusByUserId(int userID) {
		return taskRepository.getTaskStatusByUserId(userID);
	}
}

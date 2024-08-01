package service;

import java.util.List;

import ntp.model.UserModel;
import repository.ProjectRepository;
import repository.TaskRepository;
import repository.UserRepository;

public class UserService {
	UserRepository userRepository = new UserRepository();
	TaskRepository taskRepository = new TaskRepository();
	ProjectRepository projectRepository = new ProjectRepository();

	public List<UserModel> getAllUser() {
		return userRepository.getAllUser();
	}

	public boolean addUser(UserModel newUser) {
		return userRepository.addUser(newUser) > 0;
	}

	public boolean deleteUser(int userID) {
		return userRepository.deleteUserById(userID) > 0;
	}

	public boolean editUser(UserModel user) {
		return userRepository.editUser(user) > 0;
	}

	public UserModel getUserById(int userID) {
		return userRepository.getUserById(userID);
	}

	public boolean checkExistingOfProjectByLeaderId(int leaderId) {
		return projectRepository.checkExistingOfProjectByLeaderId(leaderId) > 0;
	}
	
	public boolean checkExistingOfTaskByUserId(int userId) {
		return taskRepository.checkExistingOfTaskByUserId(userId) > 0;
	}
}

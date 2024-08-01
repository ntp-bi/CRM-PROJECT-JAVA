package service;

import java.util.List;

import ntp.model.ProjectModel;
import ntp.model.TaskModel;
import ntp.model.UserModel;
import repository.ProjectRepository;
import repository.TaskRepository;
import repository.UserRepository;

public class ProjectService {
	ProjectRepository projectRepository = new ProjectRepository();
	TaskRepository taskRepository = new TaskRepository();
	UserRepository userRepository = new UserRepository();

	public List<ProjectModel> getAllProject() {
		return projectRepository.getAllProject();
	}

	public List<ProjectModel> getAllProjectByLeaderId(int leaderId) {
		return projectRepository.getAllProjectByLeaderId(leaderId);
	}

	public ProjectModel getProjectById(int projectId, boolean changeDateFormat) {
		return projectRepository.getProjectById(projectId, changeDateFormat);
	}

	public List<Integer> getTaskStatusByProjectId(int projectId) {
		return projectRepository.getTaskStatusByProjectId(projectId);
	}

	public boolean addProject(ProjectModel newProject) {
		return projectRepository.addProject(newProject) > 0;
	}

	public boolean editProject(ProjectModel project) {
		return projectRepository.editProject(project) > 0;
	}

	public boolean deleteProject(int projectId) {
		return projectRepository.deleteProject(projectId) > 0;
	}
	
	// TASK	
	public List<TaskModel> getTaskListByProjectId(int projectId) {
		return taskRepository.getTaskListByProjectId(projectId);
	}

	public boolean checkExistingOfTaskByProjectId(int projectId) {
		return taskRepository.checkExistingOfTaskByProjectId(projectId) > 0;
	}
	
	// USER
	public List<UserModel> getAllLeader() {
		return userRepository.getAllLeader();
	}
}

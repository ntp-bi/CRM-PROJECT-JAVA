package service;

import java.util.List;

import ntp.model.StatusModel;
import repository.StatusRepository;

public class StatusService {
	StatusRepository statusRepository = new StatusRepository();

	public List<StatusModel> getAllStatus() {
		return statusRepository.getAllStatus();
	}
}

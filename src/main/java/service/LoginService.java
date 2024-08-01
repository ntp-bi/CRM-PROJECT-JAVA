package service;

import repository.UserRepository;

public class LoginService {
	UserRepository userRepository = new UserRepository();

	public boolean checkLogin(String email, String password) {
		int countUser = userRepository.checkExistingOfUserByEmailNPassword(email, password);
		return countUser > 0;
	}
}

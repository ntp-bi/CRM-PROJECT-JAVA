package service;

import java.util.List;

import ntp.model.RoleModel;
import repository.RoleRepository;
import repository.UserRepository;

public class RoleService {
	RoleRepository roleRepository = new RoleRepository();
	UserRepository userRepository = new UserRepository();

	public List<RoleModel> getAllRole() {
		return roleRepository.getAllRole();
	}

	public boolean addRole(RoleModel role) {
		return roleRepository.addRole(role) > 0;
	}

	public boolean delelteRole(int roleID) {
		return roleRepository.deleteRoleById(roleID) > 0;
	}

	public RoleModel getRoleById(int roleID) {
		return roleRepository.getRoleById(roleID);
	}

	public boolean updateRole(RoleModel role) {
		return roleRepository.updateRole(role) > 0;
	}
	
	// USER
	public boolean checkExistingOfUserByRoleId(int roleId) {
		return userRepository.checkExistingOfUserByRoleId(roleId) > 0;
	}
}

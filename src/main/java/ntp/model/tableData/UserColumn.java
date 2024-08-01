package ntp.model.tableData;

public enum UserColumn {
	ID("id"),
	EMAIL("email"),
	PASSWORD("password"),
	FULLNAME("fullname"),
	AVATAR("avatar"),
	ROLE_ID("role_id");
	
	
	private String value;

	private UserColumn(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}

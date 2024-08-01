package ntp.model.tableData;

public enum RoleColumn {
	ID("id"),
	NAME("name"),
	DESCRIPTION("description");
	
	private String value;

	private RoleColumn(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}	
}

package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ntp.config.DbConfig;
import ntp.model.StatusModel;
import ntp.model.tableData.StatusColumn;

public class StatusRepository {
	public List<StatusModel> getAllStatus() {
		List<StatusModel> list = new ArrayList<StatusModel>();

		Connection connection = DbConfig.getMySQLConnection();
		String query = "SELECT * FROM status AS s ORDER BY s.id";

		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				StatusModel status = new StatusModel();
				status.setId(resultSet.getInt(StatusColumn.ID.getValue()));
				status.setName(resultSet.getString(StatusColumn.NAME.getValue()));

				list.add(status);
			}
		} catch (Exception e) {
			System.out.println("Lỗi lấy dữ liệu từ DB | " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					System.out.println("Lỗi dữ liệu khi đóng DB | " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return list;
	}
}

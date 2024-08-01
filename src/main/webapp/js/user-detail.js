$(document).ready(function() {
	$.ajax({
		method: 'GET',
		url: `http://localhost:8080/CRM-PROJECT/api/user-detail`,
	}).done(function(data) {
		if (data[0].data != null) {
			$('#user-name-bar').html(data[0].data["fullname"])
			$('#user-avatar-bar').attr('src', 'plugins/images/users/' + data[0].data["avatar"])
		}

		if (data[1].data != null) {
			$('#member-name').html(data[1].data["fullname"])
			$('#member-email').html(data[1].data["email"])
			$('#member-avatar').attr('src', 'plugins/images/users/' + data[1].data["avatar"])
		} else {
			$('#page-title').html("Không tìm thấy dữ liệu thành viên")
		}
	})

	$('#logout').click(function() {
		$.ajax({
			method: 'POST',
			url: `http://localhost:8080/CRM-PROJECT/api/user-detail`,
			data: {
				'function': 'logout'
			}
		}).done(function(data) {
			if (data.data) {
				window.location.href = 'http://localhost:8080/CRM-PROJECT/login'
			}
		})
	})
})
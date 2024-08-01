$(document).ready(function() {
	$("#btn-login").click(function() {
		const email = $("#input-email").val()
		const password = $("#input-password").val()

		$.ajax({
			method: "POST",
			url: "http://localhost:8080/CRM-PROJECT/api/login",
			data: {
				"email": email,
				"password": password
			}
		}).done(function(data) {
			if (data.data) {
				window.location.href = "/CRM-PROJECT/home"
			} else {
				$("#text-announce").html('Email không tồn tại hoặc mật khẩu không đúng')
			}
		})
	})
})
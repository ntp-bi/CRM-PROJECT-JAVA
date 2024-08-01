$(document).ready(function() {
	$.ajax({
		method: "GET",
		url: "http://localhost:8080/CRM-PROJECT/api/project-add",
	}).done(function(data) {
		if (data[0].data != null) {
			$('#user-name-bar').html(data[0].data["fullname"])
			$('#user-avatar-bar').attr('src', 'plugins/images/users/' + data[0].data["avatar"])
		}

		if (data[1].data != null) {
			for (const i in data[1].data) {
				const html = `<option value="${data[1].data[i]["id"]}">${data[1].data[i]["fullname"]}</option>`
				$("#select-leader").append(html)
			}
		} else {
			$('#select-leader').append(`<option>N/A</option>`)
		}
	})

	$('#btn-add-project').click(function(e) {
		e.preventDefault()
		$.ajax({
			method: 'POST',
			url: "http://localhost:8080/CRM-PROJECT/api/project-add",
			data: {
				'function': 'addProject',
				'name': $('#input-name').val(),
				'leaderId': document.getElementById('select-leader').value,
				'start-date': $('#input-start-date').val(),
				'end-date': $('#input-end-date').val()
			}
		}).done(function(data) {
			switch (data.data) {
				case -1:
					alert(data.message)
					break
				case 1:
					alert(data.message)
					break
				case 0:
					alert(data.message)
					break
				default:
					break
			}
		})
	})

	$('#logout').click(function() {
		$.ajax({
			method: 'POST',
			url: "http://localhost:8080/CRM-PROJECT/api/project-add",
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
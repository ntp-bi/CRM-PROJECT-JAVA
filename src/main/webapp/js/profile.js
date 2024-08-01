$(document).ready(function() {
	$.ajax({
		method: 'GET',
		url: `http://localhost:8080/CRM-PROJECT/api/profile`,
	}).done(function(data) {
		if (data[0].data != null) {			
			$('#user-name-bar').html(data[0].data["fullname"])
			$('#user-avatar-bar').attr('src', 'plugins/images/users/' + data[0].data["avatar"])
			$('#user-name').html(data[0].data["fullname"])
			$('#user-email').html(data[0].data["email"])
			$('#user-avatar').attr('src', 'plugins/images/users/' + data[0].data["avatar"])
		}
		if (data[1].data != null) {
			$('#task-unbegun').html(data[1].data[0] + '%')
			$('#task-doing').html(data[1].data[1] + '%')
			$('#task-finish').html(data[1].data[2] + '%')
			$('#progress-bar-unbegun').attr('style', 'width: ' + data[1].data[0] + '%')
			$('#progress-bar-doing').attr('style', 'width: ' + data[1].data[1] + '%')
			$('#progress-bar-finish').attr('style', 'width: ' + data[1].data[2] + '%')
		}
		if (data[2].data != null) {			
			for (const i in data[2].data) {
				let stt = Number(Number(i) + 1)
				const html = `<tr>
                                    <td>${stt}</td>
                                    <td>${data[2].data[i]["name"]}</td>
                                    <td>${data[2].data[i]["project"]["name"]}</td>
                                    <td>${data[2].data[i]["start_date"]}</td>
                                    <td>${data[2].data[i]["end_date"]}</td>
                                    <td>${data[2].data[i]["status"]["name"]}</td>
                                    <td>
                                        <a class="btn btn-sm btn-primary btn-update" 
                                        id="${data[2].data[i]["id"]}"  >Cập nhật trạng thái</a>
                                    </td>
                               </tr>`				
				$('#task-list').append(html)
			}
		}
		$('#example').DataTable()
	})
		
	$('#logout').click(function() {
		$.ajax({
			method: 'POST',
			url: `http://localhost:8080/CRM-PROJECT/api/profile`,
			data: {
				'function': 'logout'
			}
		}).done(function(data) {
			if (data.data) {
				window.location.href = 'http://localhost:8080/CRM-PROJECT/login'
			}
		})
	})

	$('#task-list').on('click', '.btn-update', function() {
		$.ajax({
			method: 'POST',
			url: `http://localhost:8080/CRM-PROJECT/api/profile`,
			data: {
				'function': 'goToEditTask',
				'taskID': $(this).attr('id')
			}
		}).done(function(data) {
			if (data.data != null) {
				window.location.href = data.data;
			}
		})

	})
})
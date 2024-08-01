let roleData;
$(document).ready(function() {
	const thisList = $('#role-list')

	$.ajax({
		method: "GET",
		url: "http://localhost:8080/CRM-PROJECT/api/role"
	}).done(function(data) {
		if (data[0].data != null) {
			$('#user-name-bar').html(data[0].data["fullname"])
			$('#user-avatar-bar').attr('src', 'plugins/images/users/' + data[0].data["avatar"])
		}

		if (data[1].data != null) {
			roleData = data[1].data

			for (const i in roleData) {
				let stt = Number(Number(i) + 1)
				const html = `<tr>
				                  <td id="stt">${stt}</td>
				                  <td id="name-role">${roleData[i]["name"]}</td>
				                  <td>${roleData[i]["description"]}</td>
				                  <td>
				                      <a id="${roleData[i]["id"]}" href="#" class="btn btn-sm btn-primary btn-edit">Sửa</a>
				                      <a id="${roleData[i]["id"]}" href="#" class="btn btn-sm btn-danger btn-delete">Xóa</a>
				                  </td>
				              </tr>`
				$("#role-list").append(html)
			}
			$('#example').DataTable()
		} else {
			$('#example').DataTable()
		}
	})

	$("#add-role").click(function() {
		$.ajax({
			method: "POST",
			url: "http://localhost:8080/CRM-PROJECT/api/role",
			data: {
				'function': "goToAddRole"
			}
		}).done(function(data) {
			if (data.data != null) {
				window.location.href = data.data
			} else {
				alert('Bạn không có quyền truy cập trang này')
			}
		})
	})

	thisList.on('click', '.btn-edit', function() {
		$.ajax({
			method: "POST",
			url: "http://localhost:8080/CRM-PROJECT/api/role",
			data: {
				'function': "goToEditRole",
				'roleID': $(this).attr('id')
			}
		}).done(function(data) {
			if (data.data != null) {
				window.location.href = data.data;
			}
		})
	})

	thisList.on('click', '.btn-delete', function(e) {
		e.preventDefault()

		const This = $(this)
		const role = This.closest('tr').find("td[id='name-role']").html()

		let ok = confirm("Bạn có muốn xóa thành viên có tên quyền: " + role + " này không?")
		if (ok) {
			$.ajax({
				method: "POST",
				url: "http://localhost:8080/CRM-PROJECT/api/role",
				data: {
					'function': "deleteRole",
					'roleID': $(this).attr('id')
				}
			}).done(function(data) {
				if (data.data == 1) {
					let index = parseInt(This.closest('tr').find("td[id='stt']").html()) - 1
					roleData.splice(index, 1)
					$('#example').DataTable().clear().draw()
					Reload()
					alert(data.message)
				} else if (data.data == 0) {
					alert(data.message)
				} else if (data.data == -1) {
					alert('Đang có thành viên sử dụng quyền này. Vui lòng chuyển thành viên sang quyền khác hoặc xóa thành viên trước khi xóa quyền')
				}
			})
		}
	})

	$("#logout").click(function() {
		$.ajax({
			method: "POST",
			url: "http://localhost:8080/CRM-PROJECT/api/role",
			data: {
				'function': 'logout'
			}
		}).done(function(data) {
			if (data.data) {
				window.location.href = "http://localhost:8080/CRM-PROJECT/login"
			}
		})
	})
})

function Reload() {
	let stt = 0
	for (let i in roleData) {
		stt = Number(Number(stt) + 1)
		const html = `<tr>
                            <td id="stt">${stt}</td>
                            <td id="name-role">${roleData[i]["name"]}</td>
                            <td>${roleData[i]["description"]}</td>
                            <td>
                                <a id="${roleData[i]["id"]}" href="#" class="btn btn-sm btn-primary btn-edit">Sửa</a>
                                <a id="${roleData[i]["id"]}" href="#" class="btn btn-sm btn-danger btn-delete">Xóa</a>
                            </td>
                        </tr>`
		$('#example').DataTable().row.add($(html)).draw()
	}

}
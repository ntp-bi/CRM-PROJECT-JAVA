# DATABASE PROJECT CRM
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th7 02, 2024 lúc 02:41 PM
-- Phiên bản máy phục vụ: 10.4.28-MariaDB
-- Phiên bản PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `crm-project`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `projects`
--

CREATE TABLE `projects` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `start_date` varchar(50) NOT NULL,
  `end_date` varchar(50) NOT NULL,
  `leader_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `projects`
--

INSERT INTO `projects` (`id`, `name`, `start_date`, `end_date`, `leader_id`) VALUES
(1, 'HRM_220101', '2022-01-01', '2022-12-01', 2),
(2, 'CRM_220101', '2022-01-01', '2022-07-01', 4),
(3, 'Jira_220701', '2022-07-01', '2023-01-01', 4);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `roles`
--

INSERT INTO `roles` (`id`, `name`, `description`) VALUES
(1, 'Admin', 'Quản trị viên'),
(2, 'Leader', 'Quản lý dự án'),
(3, 'Member', 'Thành viên');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `status`
--

CREATE TABLE `status` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `status`
--

INSERT INTO `status` (`id`, `name`) VALUES
(1, 'Chưa bắt đầu'),
(2, 'Đang thực hiện'),
(3, 'Đã hoàn thành');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tasks`
--

CREATE TABLE `tasks` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `start_date` varchar(50) NOT NULL,
  `end_date` varchar(50) NOT NULL,
  `user_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `tasks`
--

INSERT INTO `tasks` (`id`, `name`, `start_date`, `end_date`, `user_id`, `project_id`, `status_id`) VALUES
(1, 'Soft_HRM_220101', '2022-01-01', '2022-03-01', 1, 1, 3),
(2, 'Sys_HRM_220101', '2022-03-01', '2022-04-01', 2, 1, 3),
(3, 'DB_HRM_220101', '2022-04-01', '2022-06-01', 3, 1, 3),
(4, 'BE_HRM_220101', '2022-06-01', '2022-08-01', 5, 1, 2),
(5, 'FE_HRM_220101', '2022-06-01', '2022-08-01', 6, 1, 2),
(6, 'UT_HRM_220101', '2022-08-01', '2022-10-01', 7, 1, 1),
(7, 'ST_HRM_220101', '2022-10-01', '2022-12-01', 2, 1, 1),
(8, 'Soft_CRM_220101', '2022-01-01', '2022-02-01', 1, 2, 3),
(9, 'Sys_CRM_220101', '2022-02-01', '2022-03-01', 4, 2, 3),
(10, 'DB_CRM_220101', '2022-03-01', '2022-04-01', 3, 2, 3),
(11, 'BE_CRM_220101', '2022-04-01', '2022-05-01', 5, 2, 3),
(12, 'FE_CRM_220101', '2022-04-01', '2022-05-01', 6, 2, 3),
(13, 'UT_CRM_220101', '2022-05-01', '2022-06-01', 7, 2, 3),
(14, 'ST_CRM_220101', '2022-06-01', '2022-07-01', 4, 2, 3),
(15, 'Soft_Jira_220701', '2022-07-01', '2022-08-01', 1, 3, 2),
(16, 'Sys_Jira_220701', '2022-08-01', '2022-09-01', 4, 3, 1),
(17, 'DB_Jira_220701', '2022-09-01', '2022-10-01', 3, 3, 1),
(18, 'BE_Jira_220701', '2022-10-01', '2022-11-01', 5, 3, 1),
(19, 'FE_Jira_220701', '2022-10-01', '2022-11-01', 6, 3, 1),
(20, 'UT_Jira_220701', '2022-11-01', '2022-12-01', 7, 3, 1),
(21, 'ST_Jira_220701', '2022-12-01', '2023-01-01', 4, 3, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(50) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `avatar` varchar(50) DEFAULT NULL,
  `role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `fullname`, `avatar`, `role_id`) VALUES
(1, 'nguyenvana@gmail.com', '123', 'Nguyễn Văn A', '1.jpg', 1),
(2, 'tranthic@gmail.com', '123', 'Trần Thị C', '2.jpg', 2),
(3, 'levand@gmail.com', '123', 'Lê Văn D', '3.jpg', 3),
(4, 'phamthib@gmail.com', '123', 'Phạm Thị B', '4.jpg', 2),
(5, 'hoangvane@gmail.com', '123', 'Hoàng Văn E', '5.jpg', 3),
(6, 'huynhthie@gmail.com', '123', 'Huỳnh Thị E', '6.jpg', 3),
(7, 'nguyenvang@gmail.com', '123', 'Nguyễn Văn G', '7.jpg', 3);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `projects`
--
ALTER TABLE `projects`
  ADD PRIMARY KEY (`id`),
  ADD KEY `leader_id` (`leader_id`);

--
-- Chỉ mục cho bảng `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `status`
--
ALTER TABLE `status`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `tasks`
--
ALTER TABLE `tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `project_id` (`project_id`),
  ADD KEY `status_id` (`status_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD KEY `role_id` (`role_id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `projects`
--
ALTER TABLE `projects`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `status`
--
ALTER TABLE `status`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `tasks`
--
ALTER TABLE `tasks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `projects`
--
ALTER TABLE `projects`
  ADD CONSTRAINT `projects_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `tasks`
--
ALTER TABLE `tasks`
  ADD CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `tasks_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  ADD CONSTRAINT `tasks_ibfk_3` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`);

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

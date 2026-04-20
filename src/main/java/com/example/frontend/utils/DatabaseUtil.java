package com.example.frontend.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.enums.Permissions;
import com.example.backend.repositories.CommentRepo;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;
import com.example.backend.repositories.UserRepo;
import com.example.backend.services.AuthService;
import com.example.backend.services.CommentService;
import com.example.backend.services.ProjectService;
import com.example.backend.services.TaskService;
import com.example.frontend.models.Comment;
import com.example.frontend.models.Project;
import com.example.frontend.models.Task;
import com.example.frontend.models.User;

public final class DatabaseUtil {
	private static final AuthService authService = AuthService.GetInstance();
	private static final ProjectService projectService = ProjectService.GetInstance();
	private static final TaskService taskService = TaskService.GetInstance();
	private static final CommentService commentService = CommentService.GetInstance();

	private static final UserRepo userRepo = new UserRepo(Database.GetInstance());
	private static final ProjectRepo projectRepo = new ProjectRepo(Database.GetInstance());
	private static final TaskRepo taskRepo = new TaskRepo(Database.GetInstance());
	private static final CommentRepo commentRepo = new CommentRepo(Database.GetInstance());

	private DatabaseUtil() {
	}

	public static User login(String identifier, String password) {
		if (!authService.login(identifier, password)) {
			return null;
		}

		if (authService.currentUser == null || authService.currentUser.isEmpty()) {
			return null;
		}

		return mapUser(authService.currentUser.get());
	}

	public static boolean register(String fullName, String email, String password) {
		String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
		if (normalizedEmail.isBlank() || userRepo.ExistsByEmail(normalizedEmail)) {
			return false;
		}

		String[] nameParts = fullName == null ? new String[0] : fullName.trim().split("\\s+");
		String firstName = nameParts.length > 0 ? nameParts[0] : "User";
		String lastName = nameParts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length)) : "";

		return authService.register(firstName, lastName, normalizedEmail, password);
	}

	public static List<Project> getCurrentUserProjects() {
		var backendUser = getBackendCurrentUser();
		if (backendUser.isEmpty()) {
			return List.of();
		}

		List<com.example.backend.models.Project> projects = projectRepo.GetAllForUser(backendUser.get().Id);
		List<Project> mapped = new ArrayList<>();
		for (var project : projects) {
			mapped.add(mapProject(project));
		}
		return mapped;
	}

	public static List<Task> getCurrentUserTasks() {
		List<Project> projects = getCurrentUserProjects();
		List<Task> mapped = new ArrayList<>();

		for (Project project : projects) {
			for (var backendTask : taskRepo.GetAllByProject((long) project.getId())) {
				mapped.add(mapTask(backendTask, project.getName()));
			}
		}

		return mapped;
	}

	public static List<User> getAllUsers() {
		List<User> mapped = new ArrayList<>();
		for (var user : userRepo.GetAll()) {
			mapped.add(mapUser(user));
		}
		return mapped;
	}

	public static Optional<Project> createProject(String title, String description) {
		var created = projectService.createProject(title, description);
		if (created.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(mapProject(created.get()));
	}

	public static boolean addMemberToProject(int projectId, int memberId) {
		return projectService.addMemberToProject((long) projectId, (long) memberId);
	}

	public static Optional<Task> createTask(int projectId, String title, String description, String priority, LocalDate deadline) {
		return createTask(projectId, title, description, priority, deadline, null);
	}

	public static Optional<Task> createTask(int projectId, String title, String description, String priority, LocalDate deadline, Integer assignedUserId) {
		long mappedPriority = mapPriority(priority);
		var created = taskService.createTask(
			(long) projectId,
			title,
			description,
			assignedUserId == null ? null : assignedUserId.longValue(),
			mappedPriority,
			deadline == null ? null : deadline.atStartOfDay()
		);

		if (created.isEmpty()) {
			return Optional.empty();
		}

		String projectName = projectRepo.Find((long) projectId).map(p -> p.title).orElse("Unknown Project");
		return Optional.of(mapTask(created.get(), projectName));
	}

	public static boolean updateTask(Task task) {
		if (task == null) {
			return false;
		}

		Long assignedUserId = task.getAssignedToId() > 0 ? (long) task.getAssignedToId() : null;
		return taskService.updateTask(
			(long) task.getId(),
			assignedUserId,
			mapPriority(task.getPriority()),
			task.getStatus(),
			task.getDescription(),
			task.getDeadline() == null ? null : task.getDeadline().atStartOfDay()
		);
	}

	public static User findUserById(int userId) {
		return userRepo.Find((long) userId).map(DatabaseUtil::mapUser).orElse(null);
	}

	public static User findUserByFullName(String fullName) {
		if (fullName == null || fullName.isBlank()) {
			return null;
		}
		return getAllUsers().stream()
			.filter(user -> fullName.equalsIgnoreCase(user.getFullName()))
			.findFirst()
			.orElse(null);
	}

	public static List<Comment> getCommentsForTask(int taskId) {
		List<Comment> mapped = new ArrayList<>();
		for (var comment : commentRepo.GetAllByTask((long) taskId)) {
			mapped.add(mapComment(comment));
		}
		return mapped;
	}

	public static Optional<Comment> addCommentToTask(int taskId, String text) {
		var created = commentService.addCommentToTask((long) taskId, text);
		if (created.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(mapComment(created.get()));
	}

	public static boolean isCurrentUserProjectManager() {
		var backendUser = getBackendCurrentUser();
		if (backendUser.isEmpty()) {
			return false;
		}
		return userRepo.HasPermission(backendUser.get().Id, Permissions.PROJECT_MANAGER)
			|| userRepo.HasPermission(backendUser.get().Id, Permissions.ADMIN);
	}

	private static Optional<com.example.backend.models.User> getBackendCurrentUser() {
		if (authService.currentUser == null || authService.currentUser.isEmpty()) {
			return Optional.empty();
		}
		return authService.currentUser;
	}

	private static User mapUser(com.example.backend.models.User backendUser) {
		String firstName = backendUser.fname == null ? "" : backendUser.fname.trim();
		String lastName = backendUser.lname == null ? "" : backendUser.lname.trim();
		String fullName = (firstName + " " + lastName).trim();
		String role = "Member";
		if (userRepo.HasPermission(backendUser.Id, Permissions.ADMIN)) {
			role = "Admin";
		} else if (userRepo.HasPermission(backendUser.Id, Permissions.PROJECT_MANAGER)) {
			role = "Project Manager";
		}

		String email = backendUser.email == null ? "" : backendUser.email;
		String username = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;

		User user = new User(username, email, backendUser.password, fullName.isBlank() ? username : fullName, role, "General");
		user.setId(Math.toIntExact(backendUser.Id));
		return user;
	}

	private static Project mapProject(com.example.backend.models.Project backendProject) {
		Project project = new Project();
		project.setId(Math.toIntExact(backendProject.Id));
		project.setName(backendProject.title);
		project.setDescription(backendProject.description);
		project.setOwnerId(Math.toIntExact(backendProject.manager_id));
		project.setStartDate(backendProject.createdDate == null ? LocalDate.now() : backendProject.createdDate.toLocalDate());
		project.setStatus("Active");

		int totalTasks = taskRepo.GetAllByProject(backendProject.Id).size();
		project.setTotalTasks(totalTasks);
		project.setCompletedTasks(0);
		return project;
	}

	private static Task mapTask(com.example.backend.models.Task backendTask, String projectName) {
		Task task = new Task();
		task.setId(Math.toIntExact(backendTask.Id));
		task.setName(backendTask.title);
		task.setDescription(backendTask.description);
		task.setProjectId(Math.toIntExact(backendTask.project_id));
		task.setProject(projectName);
		task.setPriority(mapPriority(backendTask.priority));
		task.setStatus(mapStatus(backendTask.status == null ? null : backendTask.status.toString()));
		task.setCreatedAt(backendTask.createdDate);
		task.setDeadline(backendTask.dueDate == null ? null : backendTask.dueDate.toLocalDate());
		if (backendTask.assignedUserId != null) {
			task.setAssignedToId(Math.toIntExact(backendTask.assignedUserId));
			User assignee = findUserById(task.getAssignedToId());
			task.setAssignedTo(assignee == null ? "Unassigned" : assignee.getFullName());
		} else {
			task.setAssignedTo("Unassigned");
		}
		return task;
	}

	public static ReportSummary buildReportSummary(LocalDate start, LocalDate end) {
		List<Task> tasks = getCurrentUserTasks();
		if (start != null || end != null) {
			tasks = tasks.stream().filter(task -> {
				LocalDate date = task.getDeadline();
				if (date == null) return true;
				if (start != null && date.isBefore(start)) return false;
				if (end != null && date.isAfter(end)) return false;
				return true;
			}).toList();
		}

		int total = tasks.size();
		int completed = (int) tasks.stream().filter(t -> "Done".equalsIgnoreCase(t.getStatus())).count();
		int pending = (int) tasks.stream().filter(t -> !"Done".equalsIgnoreCase(t.getStatus())).count();
		int overdue = (int) tasks.stream()
			.filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now()) && !"Done".equalsIgnoreCase(t.getStatus()))
			.count();

		int totalProjects = getCurrentUserProjects().size();
		int totalMembers = getAllUsers().size();
		return new ReportSummary(total, completed, pending, overdue, totalProjects, totalMembers);
	}

	public record ReportSummary(
		int totalTasks,
		int completedTasks,
		int pendingTasks,
		int overdueTasks,
		int totalProjects,
		int totalMembers
	) {}

	private static Comment mapComment(com.example.backend.models.Comment backendComment) {
		String userName = userRepo.Find(backendComment.userId)
			.map(user -> ((user.fname == null ? "" : user.fname) + " " + (user.lname == null ? "" : user.lname)).trim())
			.orElse("Unknown User");

		Comment comment = new Comment(
			Math.toIntExact(backendComment.taskId),
			Math.toIntExact(backendComment.userId),
			userName,
			backendComment.text
		);
		comment.setId(Math.toIntExact(backendComment.Id));
		comment.setCreatedAt(backendComment.createdDate);
		return comment;
	}

	private static long mapPriority(String priority) {
		if (priority == null) {
			return 2;
		}
		return switch (priority.trim().toLowerCase()) {
			case "high" -> 3;
			case "low" -> 1;
			default -> 2;
		};
	}

	private static String mapPriority(Long priority) {
		if (priority == null) {
			return "Medium";
		}
		if (priority >= 3) {
			return "High";
		}
		if (priority <= 1) {
			return "Low";
		}
		return "Medium";
	}

	private static String mapStatus(String status) {
		if (status == null) {
			return "To Do";
		}
		return switch (status.toUpperCase()) {
			case "COMPLETED" -> "Done";
			case "IN_PROGRESS" -> "In Progress";
			default -> "To Do";
		};
	}
}

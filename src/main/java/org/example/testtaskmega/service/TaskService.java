package org.example.testtaskmega.service;

import org.example.testtaskmega.entity.Task;
import org.example.testtaskmega.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private final CacheManager cacheManager;

//    public TaskService(CacheManager cacheManager) {
//        this.cacheManager = cacheManager;
//    }
    @Autowired
    public TaskService(CacheManager cacheManager, TaskRepository taskRepository) {
        this.cacheManager = cacheManager;
        this.taskRepository = taskRepository;
    }
    // Создание задачи
//    public Task createTask(Task task) {
//        return taskRepository.save(task);
//    }
// Создание задачи
    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {
        System.out.println("Evicting tasks cache...");
        Task savedTask = taskRepository.save(task);

        // Отправка email
        String subject = "New Task Created: " + savedTask.getTitle();
        String text = String.format(
                "A new task has been created:\n\nTitle: %s\nDescription: %s\nCompleted: %s",
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.isCompleted() ? "Yes" : "No"
        );
        emailService.sendEmail("kamilaabdilova14@gmail.com", subject, text);

        return savedTask;
    }
    // Получение всех задач
//    public List<Task> getAllTasks() {
//        return taskRepository.findAll();
//    }
// Получение всех задач с кэшированием
//    public List<Task> getAllTasks() {
//        Cache cache = cacheManager.getCache("tasks");
//        if (cache != null && cache.get("tasks") != null) {
//            System.out.println("Fetching tasks from cache...");
//            return (List<Task>) cache.get("tasks").get();
//        } else {
//            System.out.println("Fetching tasks from database...");
//            List<Task> tasks = taskRepository.findAll();
//            if (cache != null) {
//                cache.put("tasks", tasks);
//            }
//            return tasks;
//        }
//    }
    public List<Task> getAllTasks() {
        Cache cache = cacheManager.getCache("tasks");
        if (cache != null && cache.get("tasks") != null) {
            System.out.println("Fetching tasks from cache...");
            return (List<Task>) cache.get("tasks").get();
        } else {
            System.out.println("Fetching tasks from database...");
            List<Task> tasks = taskRepository.findAll();
            if (cache != null) {
                cache.put("tasks", tasks);
            }
            return tasks;
        }
    }

    // Получение задачи по ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Обновление задачи
    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setCompleted(updatedTask.isCompleted());
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    // Удаление задачи
    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("Task not found with id " + id);
        }
    }
}

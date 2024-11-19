package org.example.testtaskmega.service;

import org.example.testtaskmega.entity.Task;
import org.example.testtaskmega.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class TaskServiceTest {

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;
    @MockBean
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    @Mock
    private EmailService emailService;

    private Task task;

    @BeforeEach
    void setUp() {
        // Настройка заглушек
        when(cacheManager.getCache("tasks")).thenReturn(cache);
        when(cache.get(anyString())).thenReturn(null);
    }


    @Test
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task createdTask = taskService.createTask(new Task("Test Task", "Description of test task", false));

        assertNotNull(createdTask, "Created task should not be null");
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Description of test task", createdTask.getDescription());

        verify(taskRepository, times(1)).save(any(Task.class));
        verify(emailService, times(1)).sendEmail(
                eq("kamilaabdilova14@gmail.com"),
                contains("New Task Created"),
                anyString()
        );
    }

    @Test
    void testGetAllTasks() {
        List<Task> tasks = Arrays.asList(
                new Task("Task 1", "Description 1", false),
                new Task("Task 2", "Description 2", true)
        );

        // Настройка репозитория
        when(taskRepository.findAll()).thenReturn(tasks);

        // Выполнение теста
        List<Task> result = taskService.getAllTasks();

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
        verify(cache, times(1)).put(eq("tasks"), eq(tasks));
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> retrievedTask = taskService.getTaskById(1L);

        assertTrue(retrievedTask.isPresent());
        assertEquals("Test Task", retrievedTask.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }


    @Test
    void testUpdateTask() {
        Task updatedTask = new Task("Updated Task", "Updated description", true);
        updatedTask.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1L, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
    }



    @Test
    void testDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L));
        assertEquals("Task not found with id 1", exception.getMessage());
        verify(taskRepository, times(0)).deleteById(1L);
    }
}

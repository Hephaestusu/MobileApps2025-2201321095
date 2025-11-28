package com.example.taskhep

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeTaskDao : TaskDao {

    private val tasks = mutableListOf<Task>()
    private var autoId = 1

    override fun getAllTasks(): List<Task> {
        return tasks.toList()
    }

    override fun insert(task: Task) {
        val newTask =
            if (task.id == 0) {
                task.copy(id = autoId++)
            } else {
                task
            }
        tasks.add(newTask)
    }

    override fun update(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
        }
    }

    override fun delete(task: Task) {
        tasks.removeIf { it.id == task.id }
    }
}

class TaskRepositoryTest {

    @Test
    fun insertAndGetTasks() = runBlocking {
        val fakeDao = FakeTaskDao()
        val repository = TaskRepository(fakeDao)

        val task = Task(id = 0, title = "Test task", description = "Description")
        repository.insert(task)

        val allTasks = repository.getAllTasks()

        assertEquals(1, allTasks.size)
        assertEquals("Test task", allTasks[0].title)
        assertEquals("Description", allTasks[0].description)
    }

    @Test
    fun deleteTask_removesFromList() = runBlocking {
        val fakeDao = FakeTaskDao()
        val repository = TaskRepository(fakeDao)

        val task1 = Task(id = 0, title = "Task 1", description = "Desc 1")
        val task2 = Task(id = 0, title = "Task 2", description = "Desc 2")

        repository.insert(task1)
        repository.insert(task2)

        var allTasks = repository.getAllTasks()
        assertEquals(2, allTasks.size)

        val toDelete = allTasks.first { it.title == "Task 2" }
        repository.delete(toDelete)

        allTasks = repository.getAllTasks()
        assertEquals(1, allTasks.size)
        assertEquals("Task 1", allTasks[0].title)
    }
}

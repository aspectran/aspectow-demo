/*
 * Copyright (c) 2008-2024 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.aspectow.demo.todos;

import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Service class for managing To-Do items backed by H2 and MyBatis.
 *
 * <p>Created: 2025. 09. 25.</p>
 */
@Component
@Bean("todoService")
public class TodoService {

    private final TodoDao todoDao;

    @Autowired
    public TodoService(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    /**
     * Retrieves all To-Do items.
     * @return the list of todos
     */
    public List<Todo> getTodos() {
        return todoDao.selectTodos();
    }

    /**
     * Retrieves a To-Do item by ID.
     * @param id the ID of the todo
     * @return the todo item, or null if not found
     */
    public Todo getTodo(long id) {
        return todoDao.selectTodoById(id);
    }

    /**
     * Adds a new To-Do item with a title.
     * @param title the task description
     * @return the newly created todo
     */
    public Todo addTodo(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setCompleted(false);
        todoDao.insertTodo(todo);
        return todo;
    }

    /**
     * Adds a new To-Do item.
     * @param todo the todo item to add
     * @return the newly created todo
     */
    public Todo addTodo(@NonNull Todo todo) {
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        todoDao.insertTodo(todo);
        return todo;
    }

    /**
     * Updates the completion status of a To-Do item.
     * @param id the ID of the item to update
     * @param completed the new completion status
     * @return the updated todo, or null if not found
     */
    public Todo updateTodo(long id, boolean completed) {
        Todo todo = todoDao.selectTodoById(id);
        if (todo != null) {
            todo.setCompleted(completed);
            todoDao.updateTodo(todo);
        }
        return todo;
    }

    /**
     * Partially updates a To-Do item with new field values.
     * @param id the ID of the item to update
     * @param delta the todo item containing updated fields
     * @return the updated todo, or null if not found
     */
    public Todo patchTodo(long id, Todo delta) {
        Todo existing = todoDao.selectTodoById(id);
        if (existing == null) {
            return null;
        }
        if (delta.getTitle() != null) {
            existing.setTitle(delta.getTitle());
        }
        if (delta.getCompleted() != null) {
            existing.setCompleted(delta.getCompleted());
        }
        if (delta.getOrder() != null) {
            existing.setOrder(delta.getOrder());
        }
        todoDao.updateTodo(existing);
        return existing;
    }

    /**
     * Deletes a To-Do item by ID.
     * @param id the ID of the item to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTodo(long id) {
        return todoDao.deleteTodoById(id) > 0;
    }

    /**
     * Deletes all To-Do items.
     * @return true if deleted, false otherwise
     */
    public boolean deleteAllTodos() {
        return todoDao.deleteAllTodos() >= 0;
    }

}

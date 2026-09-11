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

import com.aspectran.core.component.bean.annotation.Action;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Redirect;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Activity for handling To-Do list operations in the web UI.
 *
 * <p>Created: 2025. 09. 25.</p>
 */
@Component
public class TodoActivity {

    private static final Logger logger = LoggerFactory.getLogger(TodoActivity.class);

    private final TodoService todoService;

    @Autowired
    public TodoActivity(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Shows the main to-do list page.
     * @return the list of todos to be displayed
     */
    @RequestToGet("/")
    @Dispatch("todos/list")
    @Action("todos")
    public List<Todo> showTodoListPage() {
        logger.info("Request to show the to-do list page.");
        return todoService.getTodos();
    }

    /**
     * Adds a new To-Do item.
     * @param title the task description from the request parameter
     */
    @RequestToPost("/")
    @Redirect("/")
    public void addTodo(String title, String task) {
        String description = (title != null && !title.isBlank()) ? title : task;
        if (description != null && !description.isBlank()) {
            todoService.addTodo(description);
            logger.info("Added new to-do: {}", description);
        }
    }

    /**
     * Deletes a To-Do item.
     * @param id the ID of the item to delete
     */
    @RequestToPost("/${id}/delete")
    @Redirect("/")
    public void deleteTodo(long id) {
        todoService.deleteTodo(id);
        logger.info("Deleted to-do item with id: {}", id);
    }

    /**
     * Updates the completion status of a To-Do item.
     * @param id the ID of the item to update
     * @param completed the completion status from the request parameter
     */
    @RequestToPost("/${id}/update")
    @Redirect("/")
    public void updateTodo(long id, boolean completed) {
        todoService.updateTodo(id, completed);
        logger.info("Updated completion status for to-do item with id: {}", id);
    }

}
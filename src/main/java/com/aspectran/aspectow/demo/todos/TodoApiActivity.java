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

import com.aspectran.core.activity.Translet;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.RequestToDelete;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPatch;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.core.component.bean.annotation.Transform;
import com.aspectran.core.context.rule.type.FormatType;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.web.adapter.WebRequestAdapter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST API controller conforming to the Todo-Backend specification.
 *
 * <p>Created: 2026. 09. 10.</p>
 */
@Component
public class TodoApiActivity {

    private static final Logger logger = LoggerFactory.getLogger(TodoApiActivity.class);

    private final TodoService todoService;

    @Autowired
    public TodoApiActivity(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Returns all To-Do items.
     * @param translet the translet
     * @return the list of todos
     */
    @RequestToGet("/api")
    @Transform(FormatType.JSON)
    public List<Todo> getTodos(Translet translet) {
        List<Todo> todos = todoService.getTodos();
        for (Todo todo : todos) {
            todo.setUrl(buildTodoUrl(translet, todo.getId()));
        }
        return todos;
    }

    /**
     * Creates a new To-Do item.
     * @param translet the translet
     * @return the created todo
     */
    @RequestToPost("/api")
    @Transform(FormatType.JSON)
    public Todo createTodo(@NonNull Translet translet, @NonNull Parameters parameters) {
        Todo todo = parseTodoFromBody(translet, parameters);
        if (todo.getTitle() == null) {
            todo.setTitle("");
        }
        todoService.addTodo(todo);
        todo.setUrl(buildTodoUrl(translet, todo.getId()));
        logger.info("Created todo: id={}, title={}", todo.getId(), todo.getTitle());
        return todo;
    }

    /**
     * Deletes all To-Do items.
     */
    @RequestToDelete("/api")
    @Transform(FormatType.JSON)
    public void deleteAllTodos() {
        todoService.deleteAllTodos();
        logger.info("Deleted all todos");
    }

    /**
     * Returns a single To-Do item by ID.
     * @param id the todo ID
     * @param translet the translet
     * @return the todo item, or null if not found
     */
    @RequestToGet("/api/${id}")
    @Transform(FormatType.JSON)
    public Todo getTodo(long id, Translet translet) {
        Todo todo = todoService.getTodo(id);
        if (todo != null) {
            todo.setUrl(buildTodoUrl(translet, todo.getId()));
        } else {
            translet.getResponseAdapter().setStatus(404);
        }
        return todo;
    }

    /**
     * Partially updates a To-Do item.
     * @param id the todo ID
     * @param translet the translet
     * @return the updated todo, or null if not found
     */
    @RequestToPatch("/api/${id}")
    @Transform(FormatType.JSON)
    public Todo patchTodo(long id, @NonNull Translet translet, @NonNull Parameters parameters) {
        Todo delta = parseTodoFromBody(translet, parameters);
        Todo updated = todoService.patchTodo(id, delta);
        if (updated != null) {
            updated.setUrl(buildTodoUrl(translet, updated.getId()));
            logger.info("Patched todo: id={}", id);
        } else {
            translet.getResponseAdapter().setStatus(404);
        }
        return updated;
    }

    /**
     * Deletes a single To-Do item by ID.
     * @param id the todo ID
     */
    @RequestToDelete("/api/${id}")
    @Transform(FormatType.JSON)
    public void deleteTodo(long id) {
        todoService.deleteTodo(id);
        logger.info("Deleted todo: id={}", id);
    }

    private String buildTodoUrl(Translet translet, Long id) {
        if (id == null) {
            return null;
        }
        RequestAdapter requestAdapter = translet.getRequestAdapter();
        String scheme = "http";
        String host = requestAdapter.getHeader("Host");
        String contextPath = "";

        if (requestAdapter instanceof WebRequestAdapter webRequestAdapter) {
            scheme = webRequestAdapter.getScheme();
            contextPath = webRequestAdapter.getContextPath();
            if (host == null) {
                String serverName = webRequestAdapter.getServerName();
                int port = webRequestAdapter.getServerPort();
                host = serverName + (port == 80 || port == 443 ? "" : ":" + port);
            }
        }
        if (host == null) {
            host = "localhost:8080";
        }
        if (contextPath == null) {
            contextPath = "";
        }
        return scheme + "://" + host + contextPath + "/api/" + id;
    }

    private @NonNull Todo parseTodoFromBody(Translet translet, Parameters parameters) {
        Todo todo = new Todo();
        try {
            todo.setTitle(parameters.getString("title"));
            todo.setCompleted(parameters.getBoolean("completed"));
            todo.setOrder(parameters.getInt("order"));
        } catch (Exception e) {
            String body = translet.getRequestAdapter().getBody();
            logger.warn("Failed to parse JSON request body: {}", body, e);
        }
        // Fallback to request parameters
        if (todo.getTitle() == null) {
            String title = translet.getParameter("title");
            if (title != null) {
                todo.setTitle(title);
            }
        }
        if (todo.getCompleted() == null) {
            String completed = translet.getParameter("completed");
            if (completed != null) {
                todo.setCompleted(Boolean.parseBoolean(completed));
            }
        }
        if (todo.getOrder() == null) {
            String order = translet.getParameter("order");
            if (order != null) {
                try {
                    todo.setOrder(Integer.parseInt(order));
                } catch (NumberFormatException ignored) {
                    // ignore
                }
            }
        }
        return todo;
    }

}

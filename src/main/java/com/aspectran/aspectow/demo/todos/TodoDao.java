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
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.mybatis.SqlMapperAccess;
import com.aspectran.mybatis.SqlMapperProvider;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Data Access Object interface for To-Do items.
 *
 * <p>Created: 2026. 09. 10.</p>
 */
@Mapper
public interface TodoDao {

    List<Todo> selectTodos();

    Todo selectTodoById(long id);

    int insertTodo(Todo todo);

    int updateTodo(Todo todo);

    int deleteTodoById(long id);

    int deleteAllTodos();

    @Component
    class Dao extends SqlMapperAccess<TodoDao> implements TodoDao {

        @Autowired
        public Dao(SqlMapperProvider sqlMapperProvider) {
            super(sqlMapperProvider);
        }

        @Override
        public List<Todo> selectTodos() {
            return mapper().selectTodos();
        }

        @Override
        public Todo selectTodoById(long id) {
            return mapper().selectTodoById(id);
        }

        @Override
        public int insertTodo(Todo todo) {
            return mapper().insertTodo(todo);
        }

        @Override
        public int updateTodo(Todo todo) {
            return mapper().updateTodo(todo);
        }

        @Override
        public int deleteTodoById(long id) {
            return mapper().deleteTodoById(id);
        }

        @Override
        public int deleteAllTodos() {
            return mapper().deleteAllTodos();
        }

    }

}

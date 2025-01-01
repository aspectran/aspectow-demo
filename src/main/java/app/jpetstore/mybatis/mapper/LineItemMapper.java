/*
 * Copyright (c) 2018-2025 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.jpetstore.mybatis.mapper;

import app.jpetstore.order.domain.LineItem;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.mybatis.SqlMapperAgent;
import com.aspectran.mybatis.SqlMapperDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * The Interface LineItemMapper.
 *
 * @author Juho Jeong
 */
@Mapper
public interface LineItemMapper {

    List<LineItem> getLineItemsByOrderId(int orderId);

    void insertLineItem(LineItem lineItem);

    @Component
    class Dao extends SqlMapperDao<LineItemMapper> implements LineItemMapper {

        @Autowired
        public Dao(SqlMapperAgent mapperAgent) {
            super(mapperAgent, LineItemMapper.class);
        }

        @Override
        public List<LineItem> getLineItemsByOrderId(int orderId) {
            return simple().getLineItemsByOrderId(orderId);
        }

        @Override
        public void insertLineItem(LineItem lineItem) {
            simple().insertLineItem(lineItem);
        }

    }

}

/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.jpetstore.common.mybatis.mapper;

import app.jpetstore.common.mybatis.SqlMapper;
import app.jpetstore.order.domain.Item;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * The Interface ItemMapper.
 *
 * @author Juho Jeong
 */
@Mapper
public interface ItemMapper {

    void updateInventoryQuantity(Map<String, Object> param);

    int getInventoryQuantity(String itemId);

    List<Item> getItemListByProduct(String productId);

    Item getItem(String itemId);

    @Component
    class Dao implements ItemMapper {

        private final SqlMapper sqlMapper;

        @Autowired
        public Dao(SqlMapper sqlMapper) {
            this.sqlMapper = sqlMapper;
        }

        @Override
        public void updateInventoryQuantity(Map<String, Object> params) {
            sqlMapper.simple(ItemMapper.class).updateInventoryQuantity(params);
        }

        @Override
        public int getInventoryQuantity(String itemId) {
            return sqlMapper.simple(ItemMapper.class).getInventoryQuantity(itemId);
        }

        @Override
        public List<Item> getItemListByProduct(String productId) {
            return sqlMapper.simple(ItemMapper.class).getItemListByProduct(productId);
        }

        @Override
        public Item getItem(String itemId) {
            return sqlMapper.simple(ItemMapper.class).getItem(itemId);
        }

    }

}

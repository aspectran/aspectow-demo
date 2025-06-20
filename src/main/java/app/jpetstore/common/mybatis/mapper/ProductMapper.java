/*
 * Copyright (c) 2018-present The Aspectran Project
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
package app.jpetstore.common.mybatis.mapper;

import app.jpetstore.catalog.domain.Product;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.mybatis.SqlMapperAccess;
import com.aspectran.mybatis.SqlMapperProvider;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * The Interface ProductMapper.
 *
 * @author Juho Jeong
 */
@Mapper
public interface ProductMapper {

    List<Product> getProductListByCategory(String categoryId);

    Product getProduct(String productId);

    List<Product> searchProductList(String keywords);

    @Component
    class Dao extends SqlMapperAccess<ProductMapper> implements ProductMapper {

        @Autowired
        public Dao(SqlMapperProvider sqlMapperProvider) {
            super(sqlMapperProvider, ProductMapper.class);
        }
        @Override
        public List<Product> getProductListByCategory(String categoryId) {
            return simple().getProductListByCategory(categoryId);
        }

        @Override
        public Product getProduct(String productId) {
            return simple().getProduct(productId);
        }

        @Override
        public List<Product> searchProductList(String keywords) {
            return simple().searchProductList(keywords);
        }

    }

}

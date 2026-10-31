<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="aspectran" uri="aspectran.tags" %>
<%--

       Copyright 2010-2016 the original author or authors.

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

--%>
<%@ include file="../common/IncludeTop.jsp"%>

<div class="d-flex justify-content-between align-items-center mb-3 bg-body-secondary px-3 py-2 rounded">
    <h3>${product.name}</h3>
    <a class="btn btn-outline-secondary btn-sm" href="<aspectran:url value="/categories/${product.categoryId}"/>"><i class="bi bi-arrow-left me-1"></i>Return to ${product.categoryId}</a>
</div>

<table class="table table-striped">
    <thead>
    <tr>
        <th>Item ID</th>
        <th>Product ID</th>
        <th>Description</th>
        <th>List Price</th>
        <th>&nbsp;</th>
    </tr>
    </thead>
    <tbody class="table-group-divider">
    <c:forEach var="item" items="${itemList}">
        <tr>
            <td>
                <a href="<aspectran:url value="/products/${item.product.productId}/items/${item.itemId}"/>">${item.itemId}</a>
            </td>
            <td>${item.product.productId}</td>
            <td>
                ${item.attribute1} ${item.attribute2} ${item.attribute3}
                ${item.attribute4} ${item.attribute5} ${product.name}
            </td>
            <td><fmt:formatNumber value="${item.listPrice}" pattern="$#,##0.00"/></td>
            <td class="text-end">
                <a class="btn btn-primary btn-sm" href="<aspectran:url value="/cart/addItemToCart?itemId=${item.itemId}"/>">Add to Cart</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="../common/IncludeBottom.jsp"%>

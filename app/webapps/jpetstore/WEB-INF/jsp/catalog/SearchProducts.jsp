<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://aspectran.com/tags" prefix="aspectran" %>
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

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Search Results for "${param.keyword}"</h3>
    <a class="btn btn-secondary btn-sm" href="<aspectran:url value="/"/>">Return to Main Menu</a>
</div>

<table class="table table-striped">
    <colgroup>
        <col style="width: 20%"/>
        <col style="width: 30%"/>
        <col/>
    </colgroup>
    <tbody class="table-group-divider">
    <tr>
        <th>Product ID</th>
        <th>Name</th>
        <th>Description</th>
    </tr>
    <c:forEach var="product" items="${productList}">
        <tr>
            <td>
                <strong><a href="<aspectran:url value="/products/${product.productId}"/>">${product.productId}</a></strong>
            </td>
            <td>${product.name}</td>
            <td style="text-align: left">
                <a href="<aspectran:url value="/products/${product.productId}"/>">${product.description}</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<%@ include file="../common/IncludeBottom.jsp"%>

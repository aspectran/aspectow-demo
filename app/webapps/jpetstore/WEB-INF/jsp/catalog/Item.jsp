<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    <h3>${product.name}</h3>
    <a class="btn btn-secondary btn-sm" href="<aspectran:url value="/products/${not empty product ? product.productId : productId}"/>">Return to ${not empty product ? product.productId : productId}</a>
</div>

<div class="row">
    <div class="col-lg-4 text-center align-content-center">
        <img src="<aspectran:url value="${product.image}"/>" class="img-fluid"/>
    </div>
    <div class="col-lg-8">
        <p>${product.description}</p>
        <table class="table table-bordered table-striped">
            <tr>
                <td><strong>${item.itemId}</strong></td>
            </tr>
            <tr>
                <td><strong><small> ${item.attribute1}
                ${item.attribute2} ${item.attribute3}
                ${item.attribute4} ${item.attribute5}
                ${product.name} </small></strong></td>
            </tr>
            <tr>
                <td>
                    <c:if test="${item.quantity le 0}">Back ordered.</c:if>
                    <c:if test="${item.quantity gt 0}">${item.quantity} in stock.</c:if>
                </td>
            </tr>
            <tr>
                <td><fmt:formatNumber value="${item.listPrice}" pattern="$#,##0.00"/></td>
            </tr>
        </table>
    </div>
</div>

<div class="text-center">
    <a class="btn btn-primary" href="<aspectran:url value="/cart/addItemToCart?itemId=${item.itemId}"/>">Add to Cart</a>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

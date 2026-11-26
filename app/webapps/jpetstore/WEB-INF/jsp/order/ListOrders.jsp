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

<div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-3 bg-body-secondary px-3 py-2 rounded">
    <div class="d-flex align-items-center gap-2">
        <h3 class="mb-0 text-nowrap">My Orders</h3>
        <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle fw-semibold text-nowrap">
            <i class="bi bi-basket3-fill me-1"></i><fmt:formatNumber value="${pageInfo.totalElements}"/>
        </span>
    </div>
    <div class="d-flex flex-wrap align-items-center gap-2">
        <div class="input-group input-group-sm w-auto">
            <span class="input-group-text"><i class="bi bi-calendar3"></i></span>
            <input type="date" id="startDate" class="form-control" value="${param.startDate}" aria-label="Start Date" title="Start Date">
            <span class="input-group-text">~</span>
            <input type="date" id="endDate" class="form-control" value="${param.endDate}" aria-label="End Date" title="End Date">
            <button class="btn btn-outline-secondary" type="button" onclick="performSearch()" title="Search">
                <i class="bi bi-search"></i>
            </button>
            <c:if test="${not empty param.startDate or not empty param.endDate}">
                <button class="btn btn-outline-secondary" type="button" onclick="clearSearch()" title="Clear filter">
                    <i class="bi bi-x-lg"></i>
                </button>
            </c:if>
        </div>
    </div>
</div>

<table class="table table-striped">
    <thead>
    <tr>
        <th>Order ID</th>
        <th>Date</th>
        <th>Total Price</th>
        <th>Courier</th>
        <th>Status</th>
    </tr>
    </thead>
    <tbody class="table-group-divider">
    <c:forEach var="order" items="${orderList}">
        <tr>
            <td>
                <a href="<aspectran:url value="/order/viewOrder?orderId=${order.orderId}"/>">${order.orderId}</a>
            </td>
            <td><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatNumber value="${order.totalPrice}" pattern="$#,##0.00"/></td>
            <td>${order.courier}</td>
            <td>${order.status}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty orderList}">
        <tr>
            <td colspan="5" class="text-center">
                You have placed no orders.
            </td>
        </tr>
    </c:if>
    </tbody>
</table>

<%@ include file="../common/IncludePagination.jsp"%>

<script>
    function performSearch() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        if (startDate && endDate && startDate > endDate) {
            alert('The start date cannot be later than the end date.');
            return;
        }
        const urlParams = new URLSearchParams(window.location.search);
        if (startDate) {
            urlParams.set('startDate', startDate);
        } else {
            urlParams.delete('startDate');
        }
        if (endDate) {
            urlParams.set('endDate', endDate);
        } else {
            urlParams.delete('endDate');
        }
        urlParams.set('page', '1');
        window.location.search = urlParams.toString();
    }

    function clearSearch() {
        const urlParams = new URLSearchParams(window.location.search);
        urlParams.delete('startDate');
        urlParams.delete('endDate');
        urlParams.set('page', '1');
        window.location.search = urlParams.toString();
    }

    $(function() {
        $('#startDate, #endDate').on('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });
    });
</script>

<%@ include file="../common/IncludeBottom.jsp"%>

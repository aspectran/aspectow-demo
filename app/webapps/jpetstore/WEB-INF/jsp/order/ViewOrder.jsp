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
<%@ include file="../common/IncludeTop.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <c:if test="${param.submitted eq 'true'}">
            <div class="alert alert-info">
                <p class="mb-0">Thank you, your order has been submitted.</p>
            </div>
        </c:if>

        <div class="card shadow-sm">
            <div class="card-header py-3">
                <h2 class="h4 mb-0">
                    <i class="bi bi-receipt me-2" aria-hidden="true"></i>
                    <span>Order #${order.orderId}</span>
                </h2>
            </div>
            <div class="card-body p-4">
                <table class="table table-striped">
                    <colgroup>
                        <col style="width: 25%"/>
                        <col/>
                    </colgroup>
                    <tbody class="table-group-divider">
                    <tr>
                        <td>Order No.</td>
                        <td>#${order.orderId}</td>
                    </tr>
                    <tr>
                        <td>Order Date</td>
                        <td>
                            <fmt:formatDate value="${order.orderDate}" pattern="yyyy/MM/dd hh:mm:ss"/>
                        </td>
                    </tr>
                    </tbody>
                </table>

                <h3 class="h5 mt-4 mb-2">Payment Details</h3>
                <table class="table table-striped">
                    <colgroup>
                        <col style="width: 25%"/>
                        <col/>
                    </colgroup>
                    <tbody class="table-group-divider">
                    <tr>
                        <td>Card Type:</td>
                        <td><c:out value="${order.cardType}"/></td>
                    </tr>
                    <tr>
                        <td>Card Number:</td>
                        <td>
                            <c:out value="${order.creditCard}"/>
                            * Fake number!
                        </td>
                    </tr>
                    <tr>
                        <td>Expiry Date (MM/YYYY):</td>
                        <td><c:out value="${order.expiryDate}"/></td>
                    </tr>
                    </tbody>
                </table>

                <h3 class="h5 mt-4 mb-2">Billing Address</h3>
                <table class="table table-striped">
                    <colgroup>
                        <col style="width: 25%"/>
                        <col/>
                    </colgroup>
                    <tbody class="table-group-divider">
                    <tr>
                        <td>First name:</td>
                        <td><c:out value="${order.billToFirstName}"/></td>
                    </tr>
                    <tr>
                        <td>Last name:</td>
                        <td><c:out value="${order.billToLastName}"/></td>
                    </tr>
                    <tr>
                        <td>Address 1:</td>
                        <td><c:out value="${order.billAddress1}"/></td>
                    </tr>
                    <tr>
                        <td>Address 2:</td>
                        <td><c:out value="${order.billAddress2}"/></td>
                    </tr>
                    <tr>
                        <td>City:</td>
                        <td><c:out value="${order.billCity}"/></td>
                    </tr>
                    <tr>
                        <td>State:</td>
                        <td><c:out value="${order.billState}"/></td>
                    </tr>
                    <tr>
                        <td>Zip:</td>
                        <td><c:out value="${order.billZip}"/></td>
                    </tr>
                    <tr>
                        <td>Country:</td>
                        <td><c:out value="${order.billCountry}"/></td>
                    </tr>
                    </tbody>
                </table>

                <h3 class="h5 mt-4 mb-2">Shipping Address</h3>
                <table class="table table-striped">
                    <colgroup>
                        <col style="width: 25%"/>
                        <col/>
                    </colgroup>
                    <tbody class="table-group-divider">
                    <tr>
                        <td>First name:</td>
                        <td><c:out value="${order.shipToFirstName}"/></td>
                    </tr>
                    <tr>
                        <td>Last name:</td>
                        <td><c:out value="${order.shipToLastName}"/></td>
                    </tr>
                    <tr>
                        <td>Address 1:</td>
                        <td><c:out value="${order.shipAddress1}"/></td>
                    </tr>
                    <tr>
                        <td>Address 2:</td>
                        <td><c:out value="${order.shipAddress2}"/></td>
                    </tr>
                    <tr>
                        <td>City:</td>
                        <td><c:out value="${order.shipCity}"/></td>
                    </tr>
                    <tr>
                        <td>State:</td>
                        <td><c:out value="${order.shipState}"/></td>
                    </tr>
                    <tr>
                        <td>Zip:</td>
                        <td><c:out value="${order.shipZip}"/></td>
                    </tr>
                    <tr>
                        <td>Country:</td>
                        <td><c:out value="${order.shipCountry}"/></td>
                    </tr>
                    <tr>
                        <td>Courier:</td>
                        <td><c:out value="${order.courier}"/></td>
                    </tr>
                    <tr>
                        <td>Status:</td>
                        <td><c:out value="${order.status}"/></td>
                    </tr>
                    </tbody>
                </table>

                <h3 class="h5 mt-4 mb-2">Order Items</h3>
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>Item ID</th>
                        <th>Description</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Total Cost</th>
                    </tr>
                    </thead>
                    <tbody class="table-group-divider">
                    <c:forEach var="lineItem" items="${order.lineItems}">
                        <tr>
                            <td align="center">
                                <a href="<aspectran:url value="/products/${lineItem.item.product.productId}/items/${lineItem.item.itemId}"/>">${lineItem.item.itemId}</a>
                            </td>
                            <td>
                                <c:if test="${not empty lineItem.item}">
                                    ${lineItem.item.attribute1}
                                    ${lineItem.item.attribute2}
                                    ${lineItem.item.attribute3}
                                    ${lineItem.item.attribute4}
                                    ${lineItem.item.attribute5}
                                    ${lineItem.item.product.name}
                                </c:if>
                                <c:if test="${empty lineItem.item}">
                                    <i>{description unavailable}</i>
                                </c:if>
                            </td>
                            <td align="center">${lineItem.quantity}</td>
                            <td align="center"><fmt:formatNumber value="${lineItem.unitPrice}" pattern="$#,##0.00"/></td>
                            <td align="center"><fmt:formatNumber value="${lineItem.total}" pattern="$#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot class="table-group-divider">
                    <tr>
                        <th colspan="3"></th>
                        <th>Total</th>
                        <th>
                            <fmt:formatNumber value="${order.totalPrice}" pattern="$#,##0.00"/>
                        </th>
                    </tr>
                    </tfoot>
                </table>

                <div class="text-center mt-4">
                    <c:if test="${param.submitted eq 'true'}">
                        <a href="<aspectran:url value="/order/listOrders"/>" class="btn btn-secondary">My Orders</a>
                    </c:if>
                    <c:if test="${param.submitted ne 'true'}">
                        <button type="button" class="btn btn-danger"
                                onclick="location.href='<aspectran:url value="/order/deleteOrder/${order.orderId}"/>';">Delete Order</button>
                        <button type="button" class="btn btn-secondary" onclick="history.back();">Back</button>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/IncludeBottom.jsp" %>

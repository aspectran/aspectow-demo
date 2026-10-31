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

<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-header py-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
                <h2 class="h4 mb-0">
                    <i class="bi bi-tag me-2" aria-hidden="true"></i>
                    <span>${product.name}</span>
                </h2>
                <a class="btn btn-outline-secondary btn-sm" href="<aspectran:url value="/products/${not empty product ? product.productId : productId}"/>">
                    <i class="bi bi-arrow-left me-1"></i>Return to ${not empty product ? product.productId : productId}
                </a>
            </div>
            <div class="card-body p-4">
                <div class="row align-items-center">
                    <div class="col-md-5 text-center mb-3 mb-md-0">
                        <c:if test="${not empty product.image}">
                            <img src="<aspectran:url value="${product.image}"/>" alt="${product.name}" class="img-fluid rounded border bg-white p-2 shadow-sm" style="max-height: 220px; object-fit: contain;"/>
                        </c:if>
                    </div>
                    <div class="col-md-7 ps-md-4">
                        <c:if test="${not empty product.description}">
                            <p class="text-secondary mb-3">${product.description}</p>
                        </c:if>
                        <table class="table table-striped">
                            <colgroup>
                                <col style="width: 35%"/>
                                <col/>
                            </colgroup>
                            <tbody class="table-group-divider">
                            <tr>
                                <td>Item ID</td>
                                <td><strong>${item.itemId}</strong></td>
                            </tr>
                            <tr>
                                <td>Description</td>
                                <td>
                                    ${item.attribute1} ${item.attribute2} ${item.attribute3}
                                    ${item.attribute4} ${item.attribute5} ${product.name}
                                </td>
                            </tr>
                            <tr>
                                <td>Availability</td>
                                <td>
                                    <c:if test="${item.quantity le 0}">
                                        <span class="badge text-bg-warning text-dark"><i class="bi bi-exclamation-circle me-1"></i>Back ordered</span>
                                    </c:if>
                                    <c:if test="${item.quantity gt 0}">
                                        <span class="badge text-bg-success"><i class="bi bi-check-circle me-1"></i>${item.quantity} in stock</span>
                                    </c:if>
                                </td>
                            </tr>
                            <tr>
                                <td>Price</td>
                                <td>
                                    <span class="fs-5 fw-bold text-primary">
                                        <fmt:formatNumber value="${item.listPrice}" pattern="$#,##0.00"/>
                                    </span>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="text-center mt-4">
                    <a class="btn btn-primary" href="<aspectran:url value="/cart/addItemToCart?itemId=${item.itemId}"/>">
                        <i class="bi bi-cart-plus me-1"></i>Add to Cart
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

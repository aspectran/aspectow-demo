<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

<style>
    body.plate.console .page-content .product-grid {
        --bs-gutter-x: 1rem !important;
        --bs-gutter-y: 1rem !important;
    }
    @media (min-width: 768px) {
        body.plate.console .page-content .product-grid {
            --bs-gutter-x: 1.5rem !important;
            --bs-gutter-y: 1.5rem !important;
        }
    }
    .product-card {
        transition: transform 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
    }
    .product-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
    }
</style>

<div class="d-flex justify-content-between align-items-center mb-4 bg-body-secondary px-3 py-2 rounded">
    <h3 class="mb-0 d-flex align-items-center">
        <c:if test="${not empty category.image}">
            <img src="<aspectran:url value="${category.image}"/>" alt="${category.name}" class="me-2" style="height: 28px; width: auto;"/>
        </c:if>
        <span>${category.name}</span>
    </h3>
    <span class="badge text-bg-secondary">${productList.size()} product<c:if test="${productList.size() ne 1}">s</c:if></span>
</div>

<div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 product-grid">
    <c:forEach var="product" items="${productList}">
        <div class="col">
            <div class="card h-100 border shadow-sm product-card position-relative">
                <div class="bg-white rounded-top p-3 text-center d-flex align-items-center justify-content-center" style="height: 160px;">
                    <c:if test="${not empty product.image}">
                        <img src="<aspectran:url value="${product.image}"/>" alt="${product.name}" class="img-fluid" style="max-height: 130px; max-width: 100%; object-fit: contain;"/>
                    </c:if>
                    <c:if test="${empty product.image}">
                        <i class="bi bi-box-seam text-secondary fs-1"></i>
                    </c:if>
                </div>
                <div class="card-body d-flex flex-column p-3">
                    <div class="d-flex justify-content-between align-items-baseline mb-2">
                        <h3 class="h6 card-title mb-0 fw-bold">
                            <a href="<aspectran:url value="/products/${product.productId}"/>" class="text-decoration-none text-reset stretched-link">
                                ${product.name}
                            </a>
                        </h3>
                        <span class="badge text-bg-light border ms-1">${product.productId}</span>
                    </div>
                    <c:if test="${not empty product.description}">
                        <p class="card-text text-secondary small flex-grow-1 mb-3">
                            ${product.description}
                        </p>
                    </c:if>
                    <div class="mt-auto pt-2">
                        <span class="btn btn-outline-primary btn-sm w-100">View Items</span>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty productList}">
        <div class="col-12 text-center py-5 text-secondary">
            <i class="bi bi-inbox fs-1 d-block mb-2"></i>
            There are no products in this category.
        </div>
    </c:if>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

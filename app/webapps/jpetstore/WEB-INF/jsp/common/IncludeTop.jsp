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
<div class="container p-3 shadow-sm" style="background-color: #353b3e;">
    <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-xl-between gap-2 gap-xl-3">
        <!-- Logo & Navigation -->
        <div class="d-flex align-items-center justify-content-center flex-wrap gap-2 gap-xl-3 text-center">
            <a href="<aspectran:url value="/"/>" class="d-flex align-items-center text-white text-decoration-none">
                <img src="<aspectran:url value="/images/logo-topbar.gif"/>" alt="JPetStore">
            </a>

            <!-- Expanded menu for large screens (XL and up) -->
            <ul class="nav nav-pills d-none d-xl-flex align-items-center gap-1">
                <c:forEach var="entry" items="${staticCodes.categories}">
                    <li class="nav-item">
                        <a class="nav-link text-white px-2 py-1" href="<aspectran:url value="/categories/${entry.key}"/>">${entry.value}</a>
                    </li>
                </c:forEach>
                <li class="nav-item">
                    <aspectran:profile expression="prod">
                        <a class="nav-link text-warning px-2 py-1" href="https://public.aspectran.com/monitoring/#jpetstore" target="_blank" title="Real-time logs, metrics, and traffic monitoring">
                            <i class="bi bi-speedometer2 me-1"></i>Monitoring
                        </a>
                    </aspectran:profile>
                    <aspectran:profile expression="!prod">
                        <a class="nav-link text-warning px-2 py-1" href="<aspectran:url value="/../monitoring/#jpetstore"/>" target="_blank" title="Real-time logs, metrics, and traffic monitoring">
                            <i class="bi bi-speedometer2 me-1"></i>Monitoring
                        </a>
                    </aspectran:profile>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 px-2 py-1" href="<aspectran:url value="/help.html"/>" title="Help">
                        <i class="bi bi-question-circle fs-5 align-middle"></i>
                    </a>
                </li>
            </ul>

            <!-- Dropdown menu for smaller screens (< XL) -->
            <div class="nav d-xl-none align-items-center justify-content-center gap-2">
                <div class="nav-item dropdown">
                    <a class="btn btn-sm btn-outline-light dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Categories
                    </a>
                    <ul class="dropdown-menu shadow">
                        <c:forEach var="entry" items="${staticCodes.categories}">
                            <li><a class="dropdown-item" href="<aspectran:url value="/categories/${entry.key}"/>">${entry.value}</a></li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="nav-item">
                    <aspectran:profile expression="prod">
                        <a class="nav-link text-warning px-2 py-1" href="https://public.aspectran.com/monitoring/#jpetstore" target="_blank" title="Real-time logs, metrics, and traffic monitoring">
                            <i class="bi bi-speedometer2"></i><span class="d-none d-sm-inline ms-1">Monitoring</span>
                        </a>
                    </aspectran:profile>
                    <aspectran:profile expression="!prod">
                        <a class="nav-link text-warning px-2 py-1" href="<aspectran:url value="/../monitoring/#jpetstore"/>" target="_blank" title="Real-time logs, metrics, and traffic monitoring">
                            <i class="bi bi-speedometer2"></i><span class="d-none d-sm-inline ms-1">Monitoring</span>
                        </a>
                    </aspectran:profile>
                </div>
                <div class="nav-item">
                    <a class="nav-link text-white-50 px-2 py-1" href="<aspectran:url value="/help.html"/>" title="Help">
                        <i class="bi bi-question-circle"></i>
                    </a>
                </div>
            </div>
        </div>

        <!-- Right Side: Cart, User Actions, and Search -->
        <div class="d-flex align-items-center justify-content-center flex-wrap gap-2">
            <!-- Cart & Auth Actions -->
            <div class="d-flex align-items-center justify-content-center gap-2">
                <a href="<aspectran:url value="/cart/viewCart"/>" class="btn btn-success d-inline-flex align-items-center" title="Shopping Cart">
                    <i class="bi bi-cart4 me-1"></i>
                    <span class="badge text-bg-light text-success"><aspectran:eval expression="user?.cart?.numberOfItems ?: 0"/></span>
                </a>
                <c:if test="${not user.authenticated}">
                    <a href="<aspectran:url value="/account/signonForm"/>" class="btn btn-outline-light">Sign In</a>
                    <a href="<aspectran:url value="/account/newAccountForm"/>" class="btn btn-secondary">Sign Up</a>
                </c:if>
                <c:if test="${user.authenticated}">
                    <div class="dropdown d-inline-block">
                        <button class="btn btn-secondary dropdown-toggle d-inline-flex align-items-center" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false" title="My Account">
                            <i class="bi bi-person-circle me-1"></i>
                            <span>${not empty user.account ? user.account.firstName : 'Account'}</span>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="dropdownMenuButton">
                            <c:if test="${not empty user.account}">
                                <li class="dropdown-header">Signed in as <strong>${user.account.username}</strong></li>
                                <li><hr class="dropdown-divider"/></li>
                            </c:if>
                            <li>
                                <a class="dropdown-item" href="<aspectran:url value="/order/listOrders"/>">
                                    <i class="bi bi-receipt me-2"></i>My Orders
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="<aspectran:url value="/account/editAccountForm"/>">
                                    <i class="bi bi-person-gear me-2"></i>My Account
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"/></li>
                            <li>
                                <a class="dropdown-item text-warning" href="<aspectran:url value="/account/signoff"/>">
                                    <i class="bi bi-box-arrow-right me-2"></i>Sign Out
                                </a>
                            </li>
                        </ul>
                    </div>
                </c:if>
            </div>

            <!-- Search Form -->
            <form class="d-flex justify-content-center m-0" role="search" action="<aspectran:url value="/catalog/searchProducts"/>">
                <div class="input-group flex-nowrap" style="min-width: 100px; max-width: 160px;">
                    <input type="search" class="form-control border-secondary-subtle" name="keyword" placeholder="Search..." aria-label="Search" aria-describedby="jpetstore-search-btn">
                    <button class="btn btn-outline-light border-secondary-subtle" type="submit" id="jpetstore-search-btn" title="Search">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="container py-4 px-3 px-lg-4">

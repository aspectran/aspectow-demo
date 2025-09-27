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
<div class="container p-3" style="background-color: #353b3e;">
    <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
        <a href="<aspectran:url value="/"/>" class="d-flex align-items-center mb-2 mb-lg-0 text-white text-decoration-none">
            <img src="<aspectran:url value="/images/logo-topbar.gif"/>" alt="JPetStore">
        </a>
        <div class="col-12 col-md-auto me-lg-auto d-flex justify-content-center">
            <!-- Expanded menu for large screens and up -->
            <ul class="nav d-none d-xl-flex">
                <c:forEach var="entry" items="${staticCodes.categories}">
                    <li><a class="nav-link px-2 text-white" href="<aspectran:url value="/categories/${entry.key}"/>">${entry.value}</a></li>
                </c:forEach>
                <li>
                    <aspectran:profile expression="prod">
                        <a class="nav-link px-2 text-white" href="https://public.aspectran.com/monitoring/#jpetstore" target="_blank" title="View logs with AppMon">AppMon</a>
                    </aspectran:profile>
                    <aspectran:profile expression="!prod">
                        <a class="nav-link px-2 text-warning" href="<aspectran:url value="/../monitoring/#jpetstore"/>" target="_blank" title="View logs with AppMon">AppMon</a>
                    </aspectran:profile>
                </li>
                <li><a class="nav-link px-2 text-white" href="<aspectran:url value="/help.html"/>">?</a></li>
            </ul>
            <!-- Dropdown menu for screens smaller than large -->
            <div class="nav d-xl-none">
                <div class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle px-2 text-white" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Categories
                    </a>
                    <ul class="dropdown-menu">
                        <c:forEach var="entry" items="${staticCodes.categories}">
                            <li><a class="dropdown-item" href="<aspectran:url value="/categories/${entry.key}"/>">${entry.value}</a></li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="nav-item">
                <aspectran:profile expression="prod">
                    <a class="nav-link text-white" href="https://public.aspectran.com/monitoring/#jpetstore" target="_blank" title="View logs with AppMon">AppMon</a>
                </aspectran:profile>
                <aspectran:profile expression="!prod">
                    <a class="nav-link text-white" href="<aspectran:url value="/../monitoring/#jpetstore"/>" target="_blank" title="View logs with AppMon">AppMon</a>
                </aspectran:profile>
                </div>
                <div class="nav-item">
                    <a class="nav-link text-white" href="<aspectran:url value="/help.html"/>">?</a>
                </div>
            </div>
        </div>
        <div class="me-lg-3 mb-lg-0">
            <a href="<aspectran:url value="/cart/viewCart"/>" class="btn btn-success">
                <i class="bi bi-cart4"></i> ${user.cart.numberOfItems}
            </a>
            <c:if test="${not user.authenticated}">
                <a href="<aspectran:url value="/account/signonForm"/>" class="btn btn-outline-light me-2">Sign In</a>
                <a href="<aspectran:url value="/account/newAccountForm"/>" class="btn btn-warning">Sign Up</a>
            </c:if>
            <c:if test="${user.authenticated}">
                <a href="<aspectran:url value="/order/listOrders"/>" class="btn btn-secondary">My Orders</a>
                <div class="dropdown d-inline-block">
                    <a class="btn btn-primary dropdown-toggle" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false" title="My Account">
                        <i class="bi bi-person-circle"></i>
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                        <li><a class="dropdown-item" href="<aspectran:url value="/account/editAccountForm"/>">My Account</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="<aspectran:url value="/account/signoff"/>">Sign Out</a></li>
                    </ul>
                </div>
            </c:if>
        </div>
        <form class="col-12 col-md-6 col-lg-2 mt-3 mt-md-0" role="search" action="<aspectran:url value="/catalog/searchProducts"/>">
            <div class="input-group">
                <input type="search" class="form-control" name="keyword" placeholder="Search" aria-label="Search" aria-describedby="jpetstore-search-btn">
                <button class="btn btn-outline-primary text-white" type="button" id="jpetstore-search-btn"><i class="bi bi-search"></i></button>
            </div>
        </form>
    </div>
</div>
<div class="container py-4 px-3 px-lg-4">
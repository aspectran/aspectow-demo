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

<c:if test="${param.created eq 'true'}">
	<div id="MessageBar">
		<p>Your account has been created. Please try login.</p>
	</div>
</c:if>

<div id="Signon" class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Please enter your username and password.</h5>
                <form method="post" action="<aspectran:url value="/account/signon"/>">
                    <input type="hidden" name="referer" value="${param.referer}"/>
                    <div class="mb-3">
                        <label for="username" class="form-label">Username:</label>
                        <input type="text" id="username" name="username" value="j2ee" class="form-control"/>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password:</label>
                        <input type="password" id="password" name="password" value="j2ee" class="form-control"/>
                    </div>
                    <div class="text-center">
                        <button type="submit" class="btn btn-primary">Login</button>
                    </div>
                    <c:if test="${param.retry eq 'true'}">
                        <div class="alert alert-danger mt-3">
                            Invalid username or password.  Signon failed.
                        </div>
                    </c:if>
                </form>
            </div>
        </div>
        <div class="card mt-3">
            <div class="card-body text-center">
                Need a username and password?
                <a href="<aspectran:url value="/account/newAccountForm"/>">Register Now!</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

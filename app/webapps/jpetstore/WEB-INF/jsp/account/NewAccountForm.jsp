<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
            <div class="card-header py-3">
                <h2 class="h4 mb-0">
                    <i class="bi bi-person-plus me-2" aria-hidden="true"></i>
                    <span>New Account</span>
                </h2>
            </div>
            <div class="card-body p-4">
                <form method="post" action="<aspectran:url value="/account/newAccount"/>">
                    <h3 class="h5 mb-2">User Information</h3>
                    <table class="table table-striped">
                        <colgroup>
                            <col style="width: 25%"/>
                            <col/>
                        </colgroup>
                        <tbody class="table-group-divider">
                        <tr>
                            <td>User ID:</td>
                            <td>
                                <input type="text" name="username" value="${account.username}" class="form-control"/>
                                <span class="text-danger">${errors.username}</span>
                                <span class="text-danger">${errors.usernameDuplicated}</span>
                            </td>
                        </tr>
                        <tr>
                            <td>New password:</td>
                            <td>
                                <input type="password" name="password" value="${account.password}" class="form-control"/>
                                <span class="text-danger">${errors.password}</span>
                            </td>
                        </tr>
                        <tr>
                            <td>Confirm password:</td>
                            <td>
                                <input type="password" name="repeatedPassword" value="${account.repeatedPassword}" class="form-control"/>
                                <span class="text-danger">${errors.repeatedPassword}</span>
                            </td>
                        </tr>
                        </tbody>
                    </table>

                    <%@ include file="IncludeAccountFields.jsp"%>

                    <div class="text-center mt-4">
                        <button type="submit" class="btn btn-success">Save Account Information</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

<div class="row justify-content-center">
    <div class="col-lg-8">
        <c:if test="${param.updated eq 'true'}">
            <div class="alert alert-info">
                <p>Your account has been updated.</p>
            </div>
        </c:if>
        <form method="post" action="<aspectran:url value="/account/editAccount"/>">
            <h3>User Information</h3>
            <table class="table table-striped">
                <colgroup>
                    <col style="width: 25%"/>
                    <col/>
                </colgroup>
                <tbody class="table-group-divider">
                <tr>
                    <td>User ID:</td>
                    <td>${account.username}</td>
                </tr>
                <tr>
                    <td>New password:</td>
                    <td>
                        <input type="password" name="password" value="${account.password}" autocomplete="off" class="form-control"/>
                        <span class="text-danger">${errors.password}</span>
                    </td>
                </tr>
                <tr>
                    <td>Confirm password:</td>
                    <td>
                        <input type="password" name="repeatedPassword" value="${account.repeatedPassword}" autocomplete="off" class="form-control"/>
                        <span class="text-danger">${errors.repeatedPassword}</span>
                    </td>
                </tr>
                </tbody>
            </table>

            <%@ include file="IncludeAccountFields.jsp" %>

            <div class="text-center">
                <button type="submit" class="btn btn-success">Save Account Information</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

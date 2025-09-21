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

<c:if test="${not empty user.account}">
    <div class="text-end pb-2">
       <aspectran:message code='welcome' arguments="${user.account.firstName}" htmlEscape="true" javaScriptEscape="true"/>
    </div>
</c:if>

<div id="Main" class="row">
	<div id="Sidebar" class="col-lg-3 col-md-4">
		<div id="SidebarContent">
			<h4><a href="<aspectran:url value="/categories/FISH"/>">Fish</a></h4>
			<p>Saltwater, Freshwater</p>
			<h4><a href="<aspectran:url value="/categories/DOGS"/>">Dogs</a></h4>
			<p>Various Breeds</p>
			<h4><a href="<aspectran:url value="/categories/CATS"/>">Cats</a></h4>
			<p>Various Breeds, Exotic Varieties</p>
			<h4><a href="<aspectran:url value="/categories/REPTILES"/>">Reptiles</a></h4>
			<p>Lizards, Turtles, Snakes</p>
			<h4><a href="<aspectran:url value="/categories/BIRDS"/>">Birds</a></h4>
			<p>Exotic Varieties</p>
		</div>
	</div>
	<div id="MainImage" class="col-lg-6 col-md-8">
		<div id="MainImageContent" class="text-center p-3">
		  <map name="estoremap">
			<area alt="Birds" coords="72,2,280,250"
				href="<aspectran:url value="/categories/BIRDS"/>" shape="RECT"/>
			<area alt="Fish" coords="2,180,72,250"
				href="<aspectran:url value="/categories/FISH"/>" shape="RECT"/>
			<area alt="Dogs" coords="60,250,130,320"
				href="<aspectran:url value="/categories/DOGS"/>" shape="RECT"/>
			<area alt="Reptiles" coords="140,270,210,340"
				href="<aspectran:url value="/categories/REPTILES"/>" shape="RECT"/>
			<area alt="Cats" coords="225,240,295,310"
				href="<aspectran:url value="/categories/CATS"/>" shape="RECT"/>
			<area alt="Birds" coords="280,180,350,250"
				href="<aspectran:url value="/categories/BIRDS"/>" shape="RECT"/>
		  </map>
		  <img height="355" src="<aspectran:url value="/images/splash.gif"/>" align="middle" usemap="#estoremap" width="350" class="img-fluid"/>
		</div>
	</div>
	<div id="RightSidebar" class="col-lg-3 col-md-12">
		<div id="MyList">
			<c:if test="${not empty user.account}">
				<c:if test="${!empty user.account.listOption}">
					<%@ include file="../cart/IncludeMyList.jsp"%>
				</c:if>
			</c:if>
			<c:if test="${empty user.account}">
				<div class="card">
					<div class="card-body">
						<h5 class="card-title"><a href="https://petclinic.aspectran.com/" title="Visit a pet clinic with excellent non-profit veterinarians.">PetClinic Demo</a></h5>
						<a href="https://petclinic.aspectran.com/" title="Visit a pet clinic with excellent non-profit veterinarians."><img src="https://petclinic.aspectran.com/images/pets.png" class="img-fluid"/></a>
						<p class="card-text">There's a new pet clinic with excellent non-profit veterinarians. Of course, it's just for demonstration purposes.</p>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>

<%@ include file="../common/IncludeBottom.jsp"%>

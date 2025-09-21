<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://aspectran.com/tags" prefix="aspectran" %>
<div class="row g-3 pb-3">
  <div class="col-12 pt-3">
      Explore powerful Aspectran features firsthand through examples of web applications, RESTful API creation, and integration with various technologies.
  </div>
  <div class="col-12">
    <div class="row g-3 pt-3">
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:url value="/jpetstore/"/>" title="JPetStore is a full-stack sample web application built on top of MyBatis 3, Aspectran 8."><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/jpetstore.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:url value="/jpetstore/"/>">JPetStore Demo</a></p>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:url value="/petclinic/"/>" title="PetClinic is a full-stack sample web application built on top Aspectran 8."><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/petclinic.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:url value="/petclinic/"/>">PetClinic Demo</a></p>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:url value="/demo/"/>" title="Aspectran Examples"><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/demo.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:url value="/demo/"/>">Aspectran Examples</a></p>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:url value="/monitoring/"/>" title="View logs with AppMon"><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/appmon.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:url value="/monitoring/"/>">Aspectow AppMon</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
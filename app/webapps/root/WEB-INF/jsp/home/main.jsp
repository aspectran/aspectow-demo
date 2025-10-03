<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://aspectran.com/tags" prefix="aspectran" %>
<div class="row g-3 pb-3">
  <div class="col-12 pt-3">
    <h1 class="display-5">Aspectow Sample Applications</h1>
    <p class="lead"><strong>Aspectow</strong> is an <strong>enterprise WAS</strong> product, optimized and stabilized for specific purposes based on the powerful open-source project <strong>Aspectran</strong>. It particularly focuses on solving problems that developers face in Microservice Architecture (MSA) environments.</p>
    <p>The sample applications below feature a practical enterprise stack composed of industry-leading libraries. Check out the powerful features and stability of Aspectow for yourself through each demo.</p>
  </div>
  <div class="col-12">
    <h2 class="mt-3">Demo List</h2>
    <div class="row g-3 pt-3">
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:profile expression="prod">https://jpetstore.aspectran.com/</aspectran:profile><aspectran:profile expression="!prod"><aspectran:url value="/jpetstore/"/></aspectran:profile>"
               title="JPetStore is a full-stack sample web application built on top of MyBatis 3, Aspectran 9."><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/jpetstore.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:profile expression="prod">https://jpetstore.aspectran.com/</aspectran:profile><aspectran:profile expression="!prod"><aspectran:url value="/jpetstore/"/></aspectran:profile>">JPetStore Demo</a></p>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:profile expression="prod">https://petclinic.aspectran.com/</aspectran:profile><aspectran:profile expression="!prod"><aspectran:url value="/petclinic/"/></aspectran:profile>"
               title="PetClinic is a full-stack sample web application built on top Aspectran 9."><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/petclinic.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:profile expression="prod">https://petclinic.aspectran.com/</aspectran:profile><aspectran:profile expression="!prod"><aspectran:url value="/petclinic/"/></aspectran:profile>">PetClinic Demo</a></p>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6 col-lg-3 text-center">
        <div class="card">
          <div class="card-body">
            <a href="<aspectran:profile expression="prod">https://demo.aspectran.com/</aspectran:profile><aspectran:profile expression="!prod"><aspectran:url value="/demo/"/></aspectran:profile>"
               title="Aspectran Examples"><img src="<aspectran:token type='bean' expression='cdnAssets^url'/>/img/demo/demo.png" class="img-fluid"/></a>
            <p class="card-text mt-2"><a href="<aspectran:profile expression="prod">https://demo.aspectran.com/</aspectran:profile><aspectran:profile expression="!prod"><aspectran:url value="/demo/"/></aspectran:profile>">Aspectran Examples</a></p>
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
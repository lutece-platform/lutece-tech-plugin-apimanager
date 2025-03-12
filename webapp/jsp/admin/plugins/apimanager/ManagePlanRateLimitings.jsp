<jsp:useBean id="manageplansPlanRateLimiting" scope="session" class="fr.paris.lutece.plugins.apimanager.web.PlanRateLimitingJspBean" />
<% String strContent = manageplansPlanRateLimiting.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

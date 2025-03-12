<jsp:useBean id="manageplansPlanOauthConfiguration" scope="session" class="fr.paris.lutece.plugins.apimanager.web.PlanOauthConfigurationJspBean" />
<% String strContent = manageplansPlanOauthConfiguration.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

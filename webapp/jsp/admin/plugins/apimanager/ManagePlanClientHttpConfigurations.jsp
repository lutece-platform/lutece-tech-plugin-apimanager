<jsp:useBean id="manageplansPlanClientHttpConfiguration" scope="session" class="fr.paris.lutece.plugins.apimanager.web.PlanClientHttpConfigurationJspBean" />
<% String strContent = manageplansPlanClientHttpConfiguration.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

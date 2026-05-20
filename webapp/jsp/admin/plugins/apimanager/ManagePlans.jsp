<jsp:useBean id="manageplansPlan" scope="session" class="fr.paris.lutece.plugins.apimanager.web.PlanJspBean" />
<% String strContent = manageplansPlan.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<link href="css/admin/plugins/apimanager/apimanager.css" rel="stylesheet">

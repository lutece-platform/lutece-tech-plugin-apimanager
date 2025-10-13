<jsp:useBean id="manageapisInstance" scope="session" class="fr.paris.lutece.plugins.apimanager.web.InstanceJspBean" />
<% String strContent = manageapisInstance.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<link href="css/admin/plugins/apimanager/apimanager.css" rel="stylesheet">
<script src="javascript/menu/menu.js"></script>

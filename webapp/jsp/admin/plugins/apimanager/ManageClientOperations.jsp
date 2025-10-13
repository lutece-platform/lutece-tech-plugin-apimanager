<jsp:useBean id="manageClientOperation" scope="session" class="fr.paris.lutece.plugins.apimanager.web.OperationClientJspBean" />
<% String strContent = manageClientOperation.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<link href="css/admin/plugins/apimanager/apimanager.css" rel="stylesheet">
<script src="javascript/menu/menu.js"></script>

<jsp:useBean id="manageOperation" scope="session" class="fr.paris.lutece.plugins.apimanager.web.OperationJspBean" />
<% String strContent = manageOperation.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<link href="css/admin/plugins/apimanager/apimanager.css" rel="stylesheet">

<jsp:useBean id="manageplansResource" scope="session" class="fr.paris.lutece.plugins.apimanager.web.ResourceJspBean" />
<% String strContent = manageplansResource.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<link href="css/admin/plugins/apimanager/apimanager.css" rel="stylesheet">

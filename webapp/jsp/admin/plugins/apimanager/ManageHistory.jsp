<jsp:useBean id="managehistoryHistory" scope="session" class="fr.paris.lutece.plugins.apimanager.web.HistoryJspBean" />
<% String strContent = managehistoryHistory.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

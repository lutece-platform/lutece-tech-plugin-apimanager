package fr.paris.lutece.plugins.apimanager.web;

import fr.paris.lutece.plugins.apimanager.business.history.History;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryHome;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.AbstractPaginator;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller(controllerJsp = "ManageHistory.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = HistoryJspBean.RIGHT_MANAGEHISTORY )
public class HistoryJspBean extends AbstractJspBean <String, History> {

    // Rights
    public static final String RIGHT_MANAGEHISTORY = "APIMANAGER_HISTORY_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_MANAGE_HISTORY = "/admin/plugins/apimanager/manage_history.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_HISTORY = "apimanager.manage_history.pageTitle";

    // Markers
    private static final String MARK_HISTORY_LIST = "history_list";

    private static final String JSP_MANAGE_HISTORY = "jsp/admin/plugins/apimanager/ManageHistory.jsp";

    // Views
    private static final String VIEW_MANAGE_HISTORY = "manageHistory";

    // Session variable to store working values
    private List<String> _listIdHistory;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View(value = VIEW_MANAGE_HISTORY, defaultView = true )
    public String getManageClients( HttpServletRequest request)
    {
        // new search only if in pagination mode
        if (request.getParameter(AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
            // reload the filter criteria and search
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest(request);
            _listIdHistory = HistoryHome.getIdHistorysList(_mapFilterCriteria, "date" , SORT_ATTRIBUTES_DESC);

            //set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator();
        }

        Map<String, Object> model = getPaginatedListModel(request, MARK_HISTORY_LIST, _listIdHistory, JSP_MANAGE_HISTORY);

        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_HISTORY, TEMPLATE_MANAGE_HISTORY, model);

    }

    @Override
    List<History> getItemsFromIds(final List<String> listIds) {
        List<History> listHistory = HistoryHome.getHistorysListByIds(listIds);

        // keep original order
        return listHistory.stream()
                         .sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid())))
                         .collect(Collectors.toList());
    }
}

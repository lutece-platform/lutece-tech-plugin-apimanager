/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.apimanager.web;

import fr.paris.lutece.plugins.apimanager.business.api.Api;
import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.history.History;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.resource.ResourceHome;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.service.*;
import fr.paris.lutece.plugins.apimanager.service.generator.IConfigGeneratorService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEOPERATIONS;

/**
 * This class provides the user interface to manage Subscription features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageHistoryOperations.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEOPERATIONS)
public class OperationHistoryJspBean extends AbstractJspBean<String, History>
{

    // Templates
    private static final String TEMPLATE_HISTORY_OPERATIONS = "/admin/plugins/apimanager/operation/history_operations.html";
    // Parameters
    private static final String PARAMETER_VIEW_FROM_CLIENT = "view_from_client";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_HISTORY_OPERATION = "apimanager.manage_operations.history.pageTitle";

    // Markers
    private static final String MARK_HISTORY_LIST = "history_list";
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_VIEW_FROM_CLIENT = "view_from_client";

    private static final String JSP_MANAGE_HISTORY = "jsp/admin/plugins/apimanager/HistoryOperations.jsp";
    private static final String VIEW_HISTORY_CLIENT_OPERATIONS = "historyOperations";

    // Session variable to store working values
    private List<History> _historyList;
    private List<String> _listIdResources;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>( );
    private String _optionOrderBy;

    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean( IConfigGeneratorService.BEAN_NAME );

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_HISTORY_CLIENT_OPERATIONS, defaultView = true )
    public String getHistoryOperations( HttpServletRequest request )
    {

        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null )
        {
            _optionOrderBy = request.getParameter( PARAMETER_SEARCH_ORDER_BY );
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
            final HashMap<String, String> criterias = new HashMap<>( _mapFilterCriteria );
            if ( !_mapFilterCriteria.containsKey( FILTER_DISPLAY_ARCHIVED ) )
            {
                criterias.put( FILTER_ARCHIVED, Boolean.FALSE.toString( ) );
            }
            _listIdResources = ResourceService.getInstance().getIdEntitiesList( criterias );

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator( );
        }

        _historyList = HistoryService.getInstance().getEntitiesListByIds(HistoryService.getInstance().getIdEntitiesList());

        Map<String, Object> model = getPaginatedListModel( request, MARK_HISTORY_LIST, _historyList.stream().map(History::getUuid).collect(Collectors.toList()), JSP_MANAGE_HISTORY );

        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs
        model.put( MARK_SHOW_GENERATE_BUTTON, ( _configGeneratorService != null ) );
        model.put( MARK_ENVIRONMENT_LIST, environmentList );
        model.put( MARK_VIEW_FROM_CLIENT, Boolean.parseBoolean( Optional.ofNullable( request.getParameter( PARAMETER_VIEW_FROM_CLIENT ) ).orElse( "false" ) ) );

        return getPage( PROPERTY_PAGE_HISTORY_OPERATION, TEMPLATE_HISTORY_OPERATIONS, model );

    }


    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<History> getItemsFromIds(List<String> listIds) {
        // keep original order
        return _historyList.stream().sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid()))).collect(Collectors.toList());
    }

    @Override
    protected HistoryService getService( )
    {
        return HistoryService.getInstance( );
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
    }

    /**
     * reset the _listIdSubscriptions list
     */
    public void resetListId( )
    {
        _listIdResources = new ArrayList<>( );
    }


}

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
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.meecrogate.Meecrogate;
import fr.paris.lutece.plugins.apimanager.business.meecrogate.MeecrogateHome;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
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

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEMEECROGATES;
import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEOPERATIONS;

/**
 * This class provides the user interface to manage Subscription features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageMeecrogates.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEMEECROGATES)
public class MeecrogateJspBean extends AbstractJspBean<String, Meecrogate>
{

    // Templates
    private static final String TEMPLATE_MANAGE_MEECROGATE_GATEWAYS = "/admin/plugins/apimanager/meecrogate/manage_meecrogate_gateways.html";
    private static final String TEMPLATE_MANAGE_MEECROGATE_ID_SERVERS = "/admin/plugins/apimanager/meecrogate/manage_meecrogate_idservers.html";
    // Parameters
    private static final String PARAMETER_ID_OPERATION = "uuid";
    private static final String PARAMETER_VIEW_FROM_CLIENT = "view_from_client";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_GATEWAYS = "apimanager.manage_meecrogate_gateways.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_IDSERVERS = "apimanager.manage_meecrogate_id_servers.pageTitle";

    // Markers
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_MEECROGATE_GATEWAY_LIST = "gateway_list";
    private static final String MARK_MEECROGATE_ID_SERVER_LIST = "id_server_list";
    private static final String MARK_VIEW_FROM_CLIENT = "view_from_client";
    private static final String MARK_INSTANCE = "instance";

    private static final String JSP_MANAGE_MEECROGATES = "jsp/admin/plugins/apimanager/ManageMeecrogates.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_OPERATION = "apimanager.message.confirmRemoveSubscription";

    // Views
    private static final String VIEW_MANAGE_GATEWAYS = "manageGateways";
    private static final String VIEW_MANAGE_IDSERVERS = "manageIDServers";

    // Actions
    private static final String ACTION_CREATE_MEECROGATE = "createMeecrogate";
    private static final String ACTION_REMOVE_MEECROGATE = "removeMeecrogate";
    private static final String ACTION_CONFIRM_REMOVE_MEECROGATE = "confirmRemoveMeecrogate";
    private static final String ACTION_GENERATE_API_MANAGER = "generateApiManager";

    // Infos
    private static final String INFO_OPERATION_CREATED = "apimanager.info.subscription.created";
    private static final String INFO_MEECROGATE_REMOVED = "apimanager.info.meecrogate.removed";

    // Session variable to store working values
    private Meecrogate _meecrogate;
    private List<Meecrogate> _meecrogateList;
    private List<String> _listIdMeecrogates;
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
    @View( value = VIEW_MANAGE_GATEWAYS, defaultView = true )
    public String getManageGateways( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_OPERATION );

        _meecrogate = MeecrogateHome.findByPrimaryKey( uuid ).orElse(null);

        _meecrogate = (_meecrogate != null) ? _meecrogate : new Meecrogate();

        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null )
        {
            _optionOrderBy = request.getParameter( PARAMETER_SEARCH_ORDER_BY );
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
            _mapFilterCriteria.put("type","GATEWAY");

            final HashMap<String, String> criterias = new HashMap<>( _mapFilterCriteria );
            if ( !_mapFilterCriteria.containsKey( FILTER_DISPLAY_ARCHIVED ) )
            {
                criterias.put( FILTER_ARCHIVED, Boolean.FALSE.toString( ) );
            }
            _listIdMeecrogates = MeecrogateService.getInstance().getIdEntitiesList( criterias );

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator( );
        }


        _meecrogateList = MeecrogateService.getInstance().getEntitiesListByIds(_listIdMeecrogates);

        Map<String, Object> model = getPaginatedListModel( request, MARK_MEECROGATE_GATEWAY_LIST, _meecrogateList.stream().map(Meecrogate::getUuid).collect(Collectors.toList()), JSP_MANAGE_MEECROGATES );

        model.put( MARK_INSTANCE, _meecrogate );
        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs
        model.put( MARK_SHOW_GENERATE_BUTTON, ( _configGeneratorService != null ) );
        model.put( MARK_ENVIRONMENT_LIST, environmentList );
        model.put( MARK_VIEW_FROM_CLIENT, Boolean.parseBoolean( Optional.ofNullable( request.getParameter( PARAMETER_VIEW_FROM_CLIENT ) ).orElse( "false" ) ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_MEECROGATE ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_GATEWAYS, TEMPLATE_MANAGE_MEECROGATE_GATEWAYS, model );

    }



    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_IDSERVERS )
    public String getManageIDServers( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_OPERATION );

        _meecrogate = MeecrogateHome.findByPrimaryKey( uuid ).orElse(null);

        _meecrogate = (_meecrogate != null) ? _meecrogate : new Meecrogate();

        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null )
        {
            _optionOrderBy = request.getParameter( PARAMETER_SEARCH_ORDER_BY );
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
            _mapFilterCriteria.put("type","ID");
            final HashMap<String, String> criterias = new HashMap<>( _mapFilterCriteria );
            if ( !_mapFilterCriteria.containsKey( FILTER_DISPLAY_ARCHIVED ) )
            {
                criterias.put( FILTER_ARCHIVED, Boolean.FALSE.toString( ) );
            }
            _listIdMeecrogates = MeecrogateService.getInstance().getIdEntitiesList( criterias );

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator( );
        }


        _meecrogateList = MeecrogateService.getInstance().getEntitiesListByIds(_listIdMeecrogates);

        Map<String, Object> model = getPaginatedListModel( request, MARK_MEECROGATE_ID_SERVER_LIST, _meecrogateList.stream().map(Meecrogate::getUuid).collect(Collectors.toList()), JSP_MANAGE_MEECROGATES );

        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs
        model.put( MARK_INSTANCE, _meecrogate );
        model.put( MARK_SHOW_GENERATE_BUTTON, ( _configGeneratorService != null ) );
        model.put( MARK_ENVIRONMENT_LIST, environmentList );
        model.put( MARK_VIEW_FROM_CLIENT, Boolean.parseBoolean( Optional.ofNullable( request.getParameter( PARAMETER_VIEW_FROM_CLIENT ) ).orElse( "false" ) ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_MEECROGATE ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_IDSERVERS, TEMPLATE_MANAGE_MEECROGATE_ID_SERVERS, model );

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

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Meecrogate> getItemsFromIds(List<String> listIds) {
        // keep original order
        return _meecrogateList.stream().sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid()))).collect(Collectors.toList());
    }



    @Override
    protected MeecrogateService getService( )
    {
        return MeecrogateService.getInstance( );
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
        _listIdMeecrogates = new ArrayList<>( );
    }


    /**
     * Process the data capture form of a new subscription
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_MEECROGATE )
    public String doCreateSubscription( HttpServletRequest request ) throws AccessDeniedException
    {
        _meecrogate = (_meecrogate != null) ? _meecrogate : new Meecrogate();

        populate(_meecrogate, request, getLocale());

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_MEECROGATE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        if(_meecrogate.getUuid() != null && !_meecrogate.getUuid().isEmpty() ){
            getService().update(_meecrogate, getUser().getEmail());
        }else{
            getService().create(_meecrogate, getUser().getEmail());
        }

        return redirect( request, "ManageMeecrogates.jsp?infoMsg=" + INFO_OPERATION_CREATED+((_meecrogate.getType()!=null && _meecrogate.getType().equals("ID"))?("&view="+VIEW_MANAGE_IDSERVERS):"") );
    }

    /**
     * Manages the removal form of a subscription whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_MEECROGATE )
    public String getConfirmRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_OPERATION );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MEECROGATE ) );
        url.addParameter( PARAMETER_ID_OPERATION, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_OPERATION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a subscription
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage subscriptions
     */
    @Action( ACTION_REMOVE_MEECROGATE )
    public String doRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_OPERATION );

        _meecrogate = MeecrogateHome.findByPrimaryKey( uuid ).orElse(null);

        getService( ).delete( uuid, getUser( ).getEmail( ) );

        addInfo( INFO_MEECROGATE_REMOVED, getLocale( ) );
        resetListId( );

        return redirect( request, "ManageMeecrogates.jsp?infoMsg=" + INFO_MEECROGATE_REMOVED+((_meecrogate.getType()!=null && _meecrogate.getType().equals("ID"))?("&view="+VIEW_MANAGE_IDSERVERS):"")  );
    }

}

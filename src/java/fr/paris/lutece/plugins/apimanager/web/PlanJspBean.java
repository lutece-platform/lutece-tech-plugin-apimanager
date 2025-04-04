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
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanClientHttpConfiguration;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHeaderMatching;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanOauthConfiguration;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanRateLimiting;
import fr.paris.lutece.plugins.apimanager.service.AbstractService;
import fr.paris.lutece.plugins.apimanager.service.PlanService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEAPIS;

/**
 * This class provides the user interface to manage Plan features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManagePlans.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEAPIS )
public class PlanJspBean extends AbstractJspBean<String, Plan>
{

    // Templates
    private static final String TEMPLATE_MANAGE_PLANS = "/admin/plugins/apimanager/manage_plans.html";
    private static final String TEMPLATE_CREATE_PLAN = "/admin/plugins/apimanager/create_plan.html";
    private static final String TEMPLATE_MODIFY_PLAN = "/admin/plugins/apimanager/modify_plan.html";

    // Parameters
    private static final String PARAMETER_ID_PLAN = "uuid";
    private static final String PARAMETER_ID_API = "uuid_api";
    private static final String PARAMETER_SUBSCRIPTION_MODE = "subscriptionMode";
    private static final String PARAMETER_RATE_LIMITING_PREFIX = "rate_limiting_";
    private static final String PARAMETER_CLIENT_HTTP_PREFIX = "client_http_";
    private static final String PARAMETER_HEADER_MATCHING_PREFIX = "header_matching_";
    private static final String PARAMETER_OAUTH_CONFIGURATION_PREFIX = "oauth_configuration_";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PLANS = "apimanager.manage_plans.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PLAN = "apimanager.modify_plan.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PLAN = "apimanager.create_plan.pageTitle";

    // Markers
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_PLAN = "plan";

    private static final String JSP_MANAGE_PLANS = "jsp/admin/plugins/apimanager/ManagePlans.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_PLAN = "apimanager.message.confirmRemovePlan";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.plan.attribute.";

    // Views
    private static final String VIEW_MANAGE_PLANS = "managePlans";
    private static final String VIEW_CREATE_PLAN = "createPlan";
    private static final String VIEW_MODIFY_PLAN = "modifyPlan";

    // Actions
    private static final String ACTION_CREATE_PLAN = "createPlan";
    private static final String ACTION_MODIFY_PLAN = "modifyPlan";
    private static final String ACTION_REMOVE_PLAN = "removePlan";
    private static final String ACTION_CONFIRM_REMOVE_PLAN = "confirmRemovePlan";

    // Infos
    private static final String INFO_PLAN_CREATED = "apimanager.info.plan.created";
    private static final String INFO_PLAN_UPDATED = "apimanager.info.plan.updated";
    private static final String INFO_PLAN_REMOVED = "apimanager.info.plan.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Plan _plan;
    private List<String> _listIdPlans;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>( );
    private String _optionOrderBy;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_PLANS, defaultView = true )
    public String getManagePlans( HttpServletRequest request )
    {
        _plan = null;

        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null )
        {
            // if sorting request : new search with the existing filter criteria, ordered
            // example of order by parameter : orderby=name
            if ( StringUtils.isNotBlank( (String) request.getParameter( PARAMETER_SEARCH_ORDER_BY ) ) )
            {

                String strOrderByColumn = (String) request.getParameter( PARAMETER_SEARCH_ORDER_BY );
                String strSortMode = getSortMode( );

                _listIdPlans = getService( ).getIdEntitiesList( _mapFilterCriteria, strOrderByColumn, strSortMode );

            }
            else
            {
                // reload the filter criteria and search
                _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
                _listIdPlans = getService( ).getIdEntitiesList( _mapFilterCriteria );
            }

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_PLAN_LIST, _listIdPlans, JSP_MANAGE_PLANS );

        final String subscriptionMode = request.getParameter( PARAMETER_SUBSCRIPTION_MODE );
        if ( subscriptionMode != null )
        {
            model.put( PARAMETER_SUBSCRIPTION_MODE, Boolean.parseBoolean( subscriptionMode ) );
        }

        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_PLANS, TEMPLATE_MANAGE_PLANS, model );

    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Plan> getItemsFromIds( List<String> listIds )
    {
        List<Plan> listPlan = getService( ).getEntitiesListByIds( listIds );

        // keep original order
        return listPlan.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid( ) ) ) ).collect( Collectors.toList( ) );
    }

    @Override
    protected PlanService getService( )
    {
        return PlanService.getInstance( );
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
    }

    /**
     * reset the _listIdPlans list
     */
    public void resetListId( )
    {
        _listIdPlans = new ArrayList<>( );
    }

    /**
     * Returns the form to create a plan
     *
     * @param request
     *            The Http request
     * @return the html code of the plan form
     */
    @View( VIEW_CREATE_PLAN )
    public String getCreatePlan( HttpServletRequest request )
    {
        _plan = ( _plan != null ) ? _plan : new Plan( );
        final Api api = new Api( );
        api.setUuid( request.getParameter( PARAMETER_ID_API ) );
        _plan.setApi( api );

        Map<String, Object> model = getModel( );
        model.put( MARK_PLAN, _plan );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_PLAN ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_PLAN, TEMPLATE_CREATE_PLAN, model );
    }

    /**
     * Process the data capture form of a new plan
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_PLAN )
    public String doCreatePlan( HttpServletRequest request ) throws AccessDeniedException
    {
        populateAll( request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_PLAN ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _plan, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_PLAN );
        }

        getService( ).create( _plan, getUser( ).getEmail( ) );
        resetListId( );

        return redirect( request, "ManageApis.jsp?infoMsg=" + INFO_PLAN_CREATED );
    }

    /**
     * Manages the removal form of a plan whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_PLAN )
    public String getConfirmRemovePlan( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_PLAN );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PLAN ) );
        url.addParameter( PARAMETER_ID_PLAN, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PLAN, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a plan
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage plans
     */
    @Action( ACTION_REMOVE_PLAN )
    public String doRemovePlan( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_PLAN );

        getService( ).delete( uuid, getUser( ).getEmail( ) );
        resetListId( );

        return redirect( request, "ManageApis.jsp?infoMsg=" + INFO_PLAN_REMOVED );
    }

    /**
     * Returns the form to update info about a plan
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_PLAN )
    public String getModifyPlan( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_PLAN );
        if ( uuid == null )
        {
            return redirect( request, VIEW_MANAGE_PLANS );
        }
        if ( _plan == null || !uuid.equals( _plan.getUuid( ) ) )
        {
            Optional<Plan> optPlan = PlanHome.findByPrimaryKey( uuid );
            _plan = optPlan.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_PLAN, _plan );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_PLAN ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_PLAN, TEMPLATE_MODIFY_PLAN, model );
    }

    /**
     * Process the change form of a plan
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_PLAN )
    public String doModifyPlan( HttpServletRequest request ) throws AccessDeniedException
    {
        populateAll( request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_PLAN ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _plan, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_PLAN, Map.of( PARAMETER_ID_PLAN, _plan.getUuid( ) ) );
        }

        getService( ).update( _plan, getUser( ).getEmail( ) );

        resetListId( );

        return redirect( request, "ManageApis.jsp?infoMsg=" + INFO_PLAN_UPDATED );
    }

    private void populateAll( final HttpServletRequest request, final Locale locale )
    {
        populate( _plan, request, locale );

        // RATE LIMITING
        final PlanRateLimiting planRateLimiting = new PlanRateLimiting( );
        final Map<String, String [ ]> rateLimitingParams = request.getParameterMap( ).entrySet( ).stream( )
                .filter( entry -> entry.getKey( ).startsWith( PARAMETER_RATE_LIMITING_PREFIX ) )
                .collect( Collectors.toMap( entry -> entry.getKey( ).replace( PARAMETER_RATE_LIMITING_PREFIX, "" ), Map.Entry::getValue ) );
        final MultipartHttpServletRequest rateLimitingRequest = new MultipartHttpServletRequest( request, Map.of( ), rateLimitingParams );
        populate( planRateLimiting, rateLimitingRequest, locale );
        _plan.setRateLimiting( planRateLimiting );

        // HTTP CONFIGURATION
        final PlanClientHttpConfiguration planClientHttpConfiguration = new PlanClientHttpConfiguration( );
        final Map<String, String [ ]> clientHttpParams = request.getParameterMap( ).entrySet( ).stream( )
                .filter( entry -> entry.getKey( ).startsWith( PARAMETER_CLIENT_HTTP_PREFIX ) )
                .collect( Collectors.toMap( entry -> entry.getKey( ).replace( PARAMETER_CLIENT_HTTP_PREFIX, "" ), Map.Entry::getValue ) );
        final MultipartHttpServletRequest clientHttpRequest = new MultipartHttpServletRequest( request, Map.of( ), clientHttpParams );
        populate( planClientHttpConfiguration, clientHttpRequest, locale );
        _plan.setClientHttpConfiguration( planClientHttpConfiguration );

        // HEADER MATCHINGS
        final List<Integer> headerMatchingIndexes = request.getParameterMap( ).keySet( ).stream( )
                .filter( key -> key.startsWith( PARAMETER_HEADER_MATCHING_PREFIX ) ).map( key -> key.replace( PARAMETER_HEADER_MATCHING_PREFIX, "" ) )
                .map( key -> Integer.parseInt( key.substring( 0, key.indexOf( '_' ) ) ) ).distinct( ).collect( Collectors.toList( ) );
        for ( final int index : headerMatchingIndexes )
        {
            final String prefix = PARAMETER_HEADER_MATCHING_PREFIX + index + "_";
            final Map<String, String [ ]> headerMatchingParams = request.getParameterMap( ).entrySet( ).stream( )
                    .filter( entry -> entry.getKey( ).startsWith( prefix ) )
                    .collect( Collectors.toMap( entry -> entry.getKey( ).replace( prefix, "" ), Map.Entry::getValue ) );
            final PlanHeaderMatching planHeaderMatching = new PlanHeaderMatching( );
            final MultipartHttpServletRequest headerMatchingRequest = new MultipartHttpServletRequest( request, Map.of( ), headerMatchingParams );
            populate( planHeaderMatching, headerMatchingRequest, locale );
            _plan.getHeaderMatchings( ).add( planHeaderMatching );
        }

        // OAUTH CONFIGURATION
        final PlanOauthConfiguration planOauthConfiguration = new PlanOauthConfiguration( );
        final Map<String, String [ ]> oauthConfigurationParams = request.getParameterMap( ).entrySet( ).stream( )
                .filter( entry -> entry.getKey( ).startsWith( PARAMETER_OAUTH_CONFIGURATION_PREFIX ) )
                .collect( Collectors.toMap( entry -> entry.getKey( ).replace( PARAMETER_OAUTH_CONFIGURATION_PREFIX, "" ), Map.Entry::getValue ) );
        final MultipartHttpServletRequest oauthConfigurationRequest = new MultipartHttpServletRequest( request, Map.of( ), oauthConfigurationParams );
        populate( planOauthConfiguration, oauthConfigurationRequest, locale );
        _plan.setOauthConfiguration( planOauthConfiguration );
    }
}

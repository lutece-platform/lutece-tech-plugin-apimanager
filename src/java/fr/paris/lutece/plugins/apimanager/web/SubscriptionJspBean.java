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
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.service.SubscriptionService;
import fr.paris.lutece.plugins.apimanager.service.generator.IConfigGeneratorService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGESUBSCRIPTIONS;

/**
 * This class provides the user interface to manage Subscription features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageSubscriptions.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGESUBSCRIPTIONS )
public class SubscriptionJspBean extends AbstractJspBean<String, Subscription>
{

    // Templates
    private static final String TEMPLATE_MANAGE_SUBSCRIPTIONS = "/admin/plugins/apimanager/manage_subscriptions.html";
    private static final String TEMPLATE_CREATE_SUBSCRIPTION = "/admin/plugins/apimanager/create_subscription.html";

    // Parameters
    private static final String PARAMETER_ID_SUBSCRIPTION = "uuid";
    private static final String PARAMETER_ID_CLIENT = "uuid_client";
    private static final String PARAMETER_ID_PLAN = "uuid_plan";
    private static final String PARAMETER_VIEW_FROM_CLIENT = "view_from_client";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIPTIONS = "apimanager.manage_subscriptions.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_SUBSCRIPTION = "apimanager.create_subscription.pageTitle";

    // Markers
    private static final String MARK_SUBSCRIPTION_LIST = "subscription_list";
    private static final String MARK_SUBSCRIPTION = "subscription";
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_VIEW_FROM_CLIENT = "view_from_client";

    private static final String JSP_MANAGE_SUBSCRIPTIONS = "jsp/admin/plugins/apimanager/ManageSubscriptions.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_SUBSCRIPTION = "apimanager.message.confirmRemoveSubscription";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.subscription.attribute.";

    // Views
    private static final String VIEW_MANAGE_SUBSCRIPTIONS = "manageSubscriptions";
    private static final String VIEW_CREATE_SUBSCRIPTION = "createSubscription";

    // Actions
    private static final String ACTION_CREATE_SUBSCRIPTION = "createSubscription";
    private static final String ACTION_REMOVE_SUBSCRIPTION = "removeSubscription";
    private static final String ACTION_CONFIRM_REMOVE_SUBSCRIPTION = "confirmRemoveSubscription";

    // Infos
    private static final String INFO_SUBSCRIPTION_CREATED = "apimanager.info.subscription.created";
    private static final String INFO_SUBSCRIPTION_REMOVED = "apimanager.info.subscription.removed";

    // Session variable to store working values
    private Subscription _subscription;
    private List<String> _listIdSubscriptions;
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
    @View( value = VIEW_MANAGE_SUBSCRIPTIONS, defaultView = true )
    public String getManageSubscriptions( HttpServletRequest request )
    {
        _subscription = null;

        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null )
        {
            _optionOrderBy = request.getParameter( PARAMETER_SEARCH_ORDER_BY );
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
            _listIdSubscriptions = getService( ).getIdEntitiesList( _mapFilterCriteria );

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_SUBSCRIPTION_LIST, _listIdSubscriptions, JSP_MANAGE_SUBSCRIPTIONS );

        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs
        model.put( MARK_SHOW_GENERATE_BUTTON, ( _configGeneratorService != null ) );
        model.put( MARK_ENVIRONMENT_LIST, AppPropertiesService.getProperty( "apimanager.instance.environment.values" ).split( "," ) );
        model.put( MARK_VIEW_FROM_CLIENT, Boolean.parseBoolean( Optional.ofNullable( request.getParameter( PARAMETER_VIEW_FROM_CLIENT ) ).orElse( "false" ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIPTIONS, TEMPLATE_MANAGE_SUBSCRIPTIONS, model );

    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Subscription> getItemsFromIds( List<String> listIds )
    {
        List<Subscription> listSubscription = getService( ).getEntitiesListByIds( listIds );
        Comparator<Subscription> comparator = Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid( ) ) );
        if ( StringUtils.isBlank( _optionOrderBy ) )
        {
            // keep original order
            return listSubscription.stream( ).sorted( comparator ).collect( Collectors.toList( ) );
        }

        if ( "plan".equals( _optionOrderBy ) )
        {
            comparator = Comparator.comparing( Subscription::getPlan, Comparator.comparing( Plan::getName ) );
        }
        if ( "environment".equals( _optionOrderBy ) )
        {
            comparator = Comparator.comparing( Subscription::getEnvironnement );
        }
        if ( "api".equals( _optionOrderBy ) )
        {
            comparator = Comparator.comparing( sub -> sub.getPlan( ).getApi( ), Comparator.comparing( Api::getName ) );
        }
        if ( "client".equals( _optionOrderBy ) )
        {
            comparator = Comparator.comparing( Subscription::getClient, Comparator.comparing( Client::getName ) );
        }

        if ( getSortMode( ).equals( SORT_ATTRIBUTES_ASC ) )
        {
            return listSubscription.stream( ).sorted( comparator ).collect( Collectors.toList( ) );
        }
        return listSubscription.stream( ).sorted( comparator.reversed( ) ).collect( Collectors.toList( ) );
    }

    @Override
    protected SubscriptionService getService( )
    {
        return SubscriptionService.getInstance( );
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
        _listIdSubscriptions = new ArrayList<>( );
    }

    /**
     * Returns the form to create a subscription
     *
     * @param request
     *            The Http request
     * @return the html code of the subscription form
     */
    @View( VIEW_CREATE_SUBSCRIPTION )
    public String getCreateSubscription( HttpServletRequest request )
    {
        _subscription = ( _subscription != null ) ? _subscription : new Subscription( );
        final Client client = new Client( );
        client.setUuid( request.getParameter( PARAMETER_ID_CLIENT ) );
        _subscription.setClient( client );
        _subscription.setPlan( new Plan( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_SUBSCRIPTION, _subscription );
        model.put( MARK_ENVIRONMENT_LIST, AppPropertiesService.getProperty( "apimanager.instance.environment.values" ).split( "," ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_SUBSCRIPTION ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_SUBSCRIPTION, TEMPLATE_CREATE_SUBSCRIPTION, model );
    }

    /**
     * Process the data capture form of a new subscription
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_SUBSCRIPTION )
    public String doCreateSubscription( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _subscription, request, getLocale( ) );
        _subscription.getPlan( ).setUuid( request.getParameter( PARAMETER_ID_PLAN ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_SUBSCRIPTION ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _subscription, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_SUBSCRIPTION );
        }

        getService( ).create( _subscription, getUser( ).getEmail( ) );

        resetListId( );

        return redirect( request, "ManageClients.jsp?infoMsg=" + INFO_SUBSCRIPTION_CREATED );
    }

    /**
     * Manages the removal form of a subscription whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_SUBSCRIPTION )
    public String getConfirmRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_SUBSCRIPTION );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_SUBSCRIPTION ) );
        url.addParameter( PARAMETER_ID_SUBSCRIPTION, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUBSCRIPTION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a subscription
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage subscriptions
     */
    @Action( ACTION_REMOVE_SUBSCRIPTION )
    public String doRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_SUBSCRIPTION );

        getService( ).delete( uuid, getUser( ).getEmail( ) );

        addInfo( INFO_SUBSCRIPTION_REMOVED, getLocale( ) );
        resetListId( );

        return redirect( request, "ManageClients.jsp?infoMsg=" + INFO_SUBSCRIPTION_REMOVED );
    }
}

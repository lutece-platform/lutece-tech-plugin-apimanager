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

import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionHome;
import fr.paris.lutece.plugins.apimanager.service.SubscriptionService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEAPIS;

/**
 * This class provides the user interface to manage Subscription features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageSubscriptions.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEAPIS )
public class SubscriptionJspBean extends AbstractJspBean <String, Subscription>
{

    // Templates
    private static final String TEMPLATE_MANAGE_SUBSCRIPTIONS = "/admin/plugins/apimanager/manage_subscriptions.html";
    private static final String TEMPLATE_CREATE_SUBSCRIPTION = "/admin/plugins/apimanager/create_subscription.html";
    private static final String TEMPLATE_MODIFY_SUBSCRIPTION = "/admin/plugins/apimanager/modify_subscription.html";

    // Parameters
    private static final String PARAMETER_ID_SUBSCRIPTION = "uuid";
    private static final String PARAMETER_ID_CLIENT = "uuid_client";
    private static final String PARAMETER_ID_PLAN = "uuid_plan";


    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIPTIONS = "apimanager.manage_subscriptions.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_SUBSCRIPTION = "apimanager.modify_subscription.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_SUBSCRIPTION = "apimanager.create_subscription.pageTitle";

    // Markers
    private static final String MARK_SUBSCRIPTION_LIST = "subscription_list";
    private static final String MARK_SUBSCRIPTION = "subscription";

    private static final String JSP_MANAGE_SUBSCRIPTIONS = "jsp/admin/plugins/apimanager/ManageSubscriptions.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_SUBSCRIPTION = "apimanager.message.confirmRemoveSubscription";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.subscription.attribute.";

    // Views
    private static final String VIEW_MANAGE_SUBSCRIPTIONS = "manageSubscriptions";
    private static final String VIEW_CREATE_SUBSCRIPTION = "createSubscription";
    private static final String VIEW_MODIFY_SUBSCRIPTION = "modifySubscription";

    // Actions
    private static final String ACTION_CREATE_SUBSCRIPTION = "createSubscription";
    private static final String ACTION_MODIFY_SUBSCRIPTION = "modifySubscription";
    private static final String ACTION_REMOVE_SUBSCRIPTION = "removeSubscription";
    private static final String ACTION_CONFIRM_REMOVE_SUBSCRIPTION = "confirmRemoveSubscription";

    // Infos
    private static final String INFO_SUBSCRIPTION_CREATED = "apimanager.info.subscription.created";
    private static final String INFO_SUBSCRIPTION_UPDATED = "apimanager.info.subscription.updated";
    private static final String INFO_SUBSCRIPTION_REMOVED = "apimanager.info.subscription.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Subscription _subscription;
    private List<String> _listIdSubscriptions;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_SUBSCRIPTIONS, defaultView = true )
    public String getManageSubscriptions( HttpServletRequest request )
    {
        _subscription = null;
        
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
        	// if sorting request : new search with the existing filter criteria, ordered 
        	// example of order by parameter : orderby=name
        	if ( StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) )
        	{
        		
        		String strOrderByColumn =  (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY);
        		String strSortMode = getSortMode(); 
        		
        		_listIdSubscriptions = SubscriptionService.getInstance().getIdEntitiesList(_mapFilterCriteria, strOrderByColumn, strSortMode);
               	
	       	}
	       	else
	       	{
	       		// reload the filter criteria and search
	       		_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
	       		_listIdSubscriptions = SubscriptionService.getInstance().getIdEntitiesList( _mapFilterCriteria );
	       	}
        	
        	//set CurrentPageIndex of Paginator to null in aim of displays the first page of results
        	resetCurrentPageIndexOfPaginator();
        }
       	
       	Map<String, Object> model = getPaginatedListModel( request, MARK_SUBSCRIPTION_LIST, _listIdSubscriptions, JSP_MANAGE_SUBSCRIPTIONS );
             
        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs
                     
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIPTIONS, TEMPLATE_MANAGE_SUBSCRIPTIONS, model );

    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Subscription> getItemsFromIds( List<String> listIds )
	{
		List<Subscription> listSubscription = SubscriptionService.getInstance().getEntitiesListByIds( listIds );
		
		// keep original order
        return listSubscription.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
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
     * @param request The Http request
     * @return the html code of the subscription form
     */
    @View( VIEW_CREATE_SUBSCRIPTION )
    public String getCreateSubscription( HttpServletRequest request )
    {
        _subscription = ( _subscription != null ) ? _subscription : new Subscription(  );
        final Client client = new Client();
        client.setUuid( request.getParameter( PARAMETER_ID_CLIENT ) );
        _subscription.setClient( client );
        _subscription.setPlan( new Plan() );

        Map<String, Object> model = getModel(  );
        model.put( MARK_SUBSCRIPTION, _subscription );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_SUBSCRIPTION ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_SUBSCRIPTION, TEMPLATE_CREATE_SUBSCRIPTION, model );
    }

    /**
     * Process the data capture form of a new subscription
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_SUBSCRIPTION )
    public String doCreateSubscription( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _subscription, request, getLocale());
        _subscription.getPlan().setUuid( request.getParameter( PARAMETER_ID_PLAN ) );


        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_SUBSCRIPTION ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _subscription, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_SUBSCRIPTION );
        }

        SubscriptionService.getInstance().create( _subscription, getUser().getEmail() );

        resetListId( );

        return redirect(request, "ManageClients.jsp?infoMsg=" + INFO_SUBSCRIPTION_CREATED);
    }

    /**
     * Manages the removal form of a subscription whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_SUBSCRIPTION )
    public String getConfirmRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_SUBSCRIPTION );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_SUBSCRIPTION ) );
        url.addParameter( PARAMETER_ID_SUBSCRIPTION, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUBSCRIPTION, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a subscription
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage subscriptions
     */
    @Action( ACTION_REMOVE_SUBSCRIPTION )
    public String doRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_SUBSCRIPTION );


        SubscriptionService.getInstance().delete( uuid, getUser().getEmail() );

        addInfo( INFO_SUBSCRIPTION_REMOVED, getLocale(  ) );
        resetListId( );

        return redirect(request, "ManageClients.jsp?infoMsg=" + INFO_SUBSCRIPTION_REMOVED);
    }

    /**
     * Returns the form to update info about a subscription
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_SUBSCRIPTION )
    public String getModifySubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_SUBSCRIPTION );
        if(uuid == null){
            return redirectView( request, VIEW_MANAGE_SUBSCRIPTIONS );
        }
        if ( _subscription == null || !uuid.equals( _subscription.getUuid( ) ) )
        {
            Optional<Subscription> optSubscription = SubscriptionHome.findByPrimaryKey(uuid);
            _subscription = optSubscription.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_SUBSCRIPTION, _subscription );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_SUBSCRIPTION ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_SUBSCRIPTION, TEMPLATE_MODIFY_SUBSCRIPTION, model );
    }

    /**
     * Process the change form of a subscription
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_SUBSCRIPTION )
    public String doModifySubscription( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _subscription, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_SUBSCRIPTION ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _subscription, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_SUBSCRIPTION, Map.of(PARAMETER_ID_SUBSCRIPTION, _subscription.getUuid( ) ));
        }

        SubscriptionService.getInstance().update( _subscription, getUser().getEmail() );

        resetListId( );

        return redirect(request, "ManageClients.jsp?infoMsg=" + INFO_SUBSCRIPTION_UPDATED);
    }
}

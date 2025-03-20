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

import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.service.ClientService;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


import fr.paris.lutece.plugins.apimanager.business.client.Client;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGECLIENTS;

/**
 * This class provides the user interface to manage Client features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageClients.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGECLIENTS )
public class ClientJspBean extends AbstractJspBean <String, Client>
{
    // Templates
    private static final String TEMPLATE_MANAGE_CLIENTS = "/admin/plugins/apimanager/manage_clients.html";
    private static final String TEMPLATE_CREATE_CLIENT = "/admin/plugins/apimanager/create_client.html";
    private static final String TEMPLATE_MODIFY_CLIENT = "/admin/plugins/apimanager/modify_client.html";

    // Parameters
    private static final String PARAMETER_ID_CLIENT = "uuid";
    private static final String PARAMETER_SELECTED_TAGS = "selected_tags";
    private static final String PARAMETER_INFO_MSG = "infoMsg";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CLIENTS = "apimanager.manage_clients.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CLIENT = "apimanager.modify_client.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CLIENT = "apimanager.create_client.pageTitle";

    // Markers
    private static final String MARK_CLIENT_LIST = "client_list";
    private static final String MARK_CLIENT = "client";

    private static final String JSP_MANAGE_CLIENTS = "jsp/admin/plugins/apimanager/ManageClients.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CLIENT = "apimanager.message.confirmRemoveClient";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.client.attribute.";

    // Views
    private static final String VIEW_MANAGE_CLIENTS = "manageClients";
    private static final String VIEW_CREATE_CLIENT = "createClient";
    private static final String VIEW_MODIFY_CLIENT = "modifyClient";

    // Actions
    private static final String ACTION_CREATE_CLIENT = "createClient";
    private static final String ACTION_MODIFY_CLIENT = "modifyClient";
    private static final String ACTION_REMOVE_CLIENT = "removeClient";
    private static final String ACTION_CONFIRM_REMOVE_CLIENT = "confirmRemoveClient";

    // Infos
    private static final String INFO_CLIENT_CREATED = "apimanager.info.client.created";
    private static final String INFO_CLIENT_UPDATED = "apimanager.info.client.updated";
    private static final String INFO_CLIENT_REMOVED = "apimanager.info.client.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Client _client;
    private List<String> _listIdClients;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CLIENTS, defaultView = true )
    public String getManageClients( HttpServletRequest request )
    {
        final String infoMsg = request.getParameter(PARAMETER_INFO_MSG);
        if(infoMsg != null) {
            addInfo(infoMsg, getLocale());
            return redirectView( request, VIEW_MANAGE_CLIENTS );
        }
        _client = null;
        
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
        	// if sorting request : new search with the existing filter criteria, ordered 
        	// example of order by parameter : orderby=name
        	if ( StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) )
        	{
        		
        		String strOrderByColumn =  (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY);
        		String strSortMode = getSortMode(); 
        		
        		_listIdClients = ClientService.getInstance().getIdEntitiesList(_mapFilterCriteria, strOrderByColumn, strSortMode);
               	
	       	}
	       	else
	       	{
	       		// reload the filter criteria and search
	       		_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
	       		_listIdClients = ClientService.getInstance().getIdEntitiesList( _mapFilterCriteria );
	       	}
        	
        	//set CurrentPageIndex of Paginator to null in aim of displays the first page of results
        	resetCurrentPageIndexOfPaginator();
        }
       	
       	Map<String, Object> model = getPaginatedListModel( request, MARK_CLIENT_LIST, _listIdClients, JSP_MANAGE_CLIENTS );
             
        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs
                     
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CLIENTS, TEMPLATE_MANAGE_CLIENTS, model );

    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Client> getItemsFromIds( List<String> listIds )
	{
		List<Client> listClient = ClientService.getInstance().getEntitiesListByIds( listIds );
		
		// keep original order
        return listClient.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdClients list
    */
    public void resetListId( )
    {
    	_listIdClients = new ArrayList<>( );
    }

    /**
     * Returns the form to create a client
     *
     * @param request The Http request
     * @return the html code of the client form
     */
    @View( VIEW_CREATE_CLIENT )
    public String getCreateClient( HttpServletRequest request )
    {
        _client = ( _client != null ) ? _client : new Client(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_CLIENT, _client );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_CLIENT ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CLIENT, TEMPLATE_CREATE_CLIENT, model );
    }

    /**
     * Process the data capture form of a new client
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_CLIENT )
    public String doCreateClient( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _client, request, getLocale( ) );
        _client.setTags(Arrays.stream(Optional.ofNullable( request.getParameterValues(PARAMETER_SELECTED_TAGS)).orElse( new String[0])).collect(Collectors.toList()));

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_CLIENT ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _client, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CLIENT );
        }

        ClientService.getInstance().create( _client, getUser().getEmail() );
        addInfo( INFO_CLIENT_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CLIENTS );
    }

    /**
     * Manages the removal form of a client whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CLIENT )
    public String getConfirmRemoveClient( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_CLIENT );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CLIENT ) );
        url.addParameter( PARAMETER_ID_CLIENT, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CLIENT, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a client
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage clients
     */
    @Action( ACTION_REMOVE_CLIENT )
    public String doRemoveClient( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_CLIENT );


        ClientService.getInstance().delete( uuid, getUser().getEmail() );
        addInfo( INFO_CLIENT_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CLIENTS );
    }

    /**
     * Returns the form to update info about a client
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CLIENT )
    public String getModifyClient( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_CLIENT );
        if(uuid==null){
            return redirectView( request, VIEW_MANAGE_CLIENTS );
        }

        if ( _client == null || !uuid.equals( _client.getUuid( ) ) )
        {
            Optional<Client> optClient = ClientHome.findByPrimaryKey(uuid);
            _client = optClient.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_CLIENT, _client );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CLIENT ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CLIENT, TEMPLATE_MODIFY_CLIENT, model );
    }

    /**
     * Process the change form of a client
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_CLIENT )
    public String doModifyClient( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _client, request, getLocale( ) );
        _client.setTags(Arrays.stream(Optional.ofNullable( request.getParameterValues(PARAMETER_SELECTED_TAGS)).orElse( new String[0])).collect(Collectors.toList()));

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_CLIENT ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _client, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CLIENT, Map.of(PARAMETER_ID_CLIENT, _client.getUuid( )) );
        }

        ClientService.getInstance().update( _client, getUser().getEmail() );
        addInfo( INFO_CLIENT_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CLIENTS );
    }
}

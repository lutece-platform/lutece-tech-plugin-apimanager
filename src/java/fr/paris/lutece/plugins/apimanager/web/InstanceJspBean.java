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


import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;

/**
 * This class provides the user interface to manage Instance features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageInstances.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = "APIMANAGER_API_MANAGEMENT" )
public class InstanceJspBean extends AbstractJspBean <String, Instance>
{

	// Rights
	public static final String RIGHT_MANAGEAPIS = "APIMANAGER_API_MANAGEMENT";
		
    // Templates
    private static final String TEMPLATE_MANAGE_INSTANCES = "/admin/plugins/apimanager/manage_instances.html";
    private static final String TEMPLATE_CREATE_INSTANCE = "/admin/plugins/apimanager/create_instance.html";
    private static final String TEMPLATE_MODIFY_INSTANCE = "/admin/plugins/apimanager/modify_instance.html";

    // Parameters
    private static final String PARAMETER_ID_INSTANCE = "uuid";
    private static final String PARAMETER_ID_API = "uuid_api";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_INSTANCES = "apimanager.manage_instances.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_INSTANCE = "apimanager.modify_instance.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_INSTANCE = "apimanager.create_instance.pageTitle";

    // Markers
    private static final String MARK_INSTANCE_LIST = "instance_list";
    private static final String MARK_INSTANCE = "instance";

    private static final String JSP_MANAGE_INSTANCES = "jsp/admin/plugins/apimanager/ManageInstances.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_INSTANCE = "apimanager.message.confirmRemoveInstance";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.instance.attribute.";

    // Views
    private static final String VIEW_MANAGE_INSTANCES = "manageInstances";
    private static final String VIEW_CREATE_INSTANCE = "createInstance";
    private static final String VIEW_MODIFY_INSTANCE = "modifyInstance";

    // Actions
    private static final String ACTION_CREATE_INSTANCE = "createInstance";
    private static final String ACTION_MODIFY_INSTANCE = "modifyInstance";
    private static final String ACTION_REMOVE_INSTANCE = "removeInstance";
    private static final String ACTION_CONFIRM_REMOVE_INSTANCE = "confirmRemoveInstance";

    // Infos
    private static final String INFO_INSTANCE_CREATED = "apimanager.info.instance.created";
    private static final String INFO_INSTANCE_UPDATED = "apimanager.info.instance.updated";
    private static final String INFO_INSTANCE_REMOVED = "apimanager.info.instance.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Instance _instance;
    private List<String> _listIdInstances;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_INSTANCES, defaultView = true )
    public String getManageInstances( HttpServletRequest request )
    {
        _instance = null;
        
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
        	// if sorting request : new search with the existing filter criteria, ordered 
        	// example of order by parameter : orderby=name
        	if ( StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) )
        	{
        		
        		String strOrderByColumn =  (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY);
        		String strSortMode = getSortMode(); 
        		
        		_listIdInstances = InstanceHome.getIdInstancesList( _mapFilterCriteria, strOrderByColumn, strSortMode );
               	
	       	}
	       	else
	       	{
	       		// reload the filter criteria and search
	       		_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
	       		_listIdInstances = InstanceHome.getIdInstancesList( _mapFilterCriteria, null ,null);
	       	}
        	
        	//set CurrentPageIndex of Paginator to null in aim of displays the first page of results
        	resetCurrentPageIndexOfPaginator();
        }
       	
       	Map<String, Object> model = getPaginatedListModel( request, MARK_INSTANCE_LIST, _listIdInstances, JSP_MANAGE_INSTANCES );
             
        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs
                     
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_INSTANCES, TEMPLATE_MANAGE_INSTANCES, model );

    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Instance> getItemsFromIds( List<String> listIds )
	{
		List<Instance> listInstance = InstanceHome.getInstancesListByIds( listIds );
		
		// keep original order
        return listInstance.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdInstances list
    */
    public void resetListId( )
    {
    	_listIdInstances = new ArrayList<>( );
    }

    /**
     * Returns the form to create a instance
     *
     * @param request The Http request
     * @return the html code of the instance form
     */
    @View( VIEW_CREATE_INSTANCE )
    public String getCreateInstance( HttpServletRequest request )
    {
        _instance = ( _instance != null ) ? _instance : new Instance(  );
        final Api api = new Api();
        api.setUuid( request.getParameter( PARAMETER_ID_API ) );
        _instance.setApi( api );

        Map<String, Object> model = getModel(  );
        model.put( MARK_INSTANCE, _instance );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_INSTANCE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_INSTANCE, TEMPLATE_CREATE_INSTANCE, model );
    }

    /**
     * Process the data capture form of a new instance
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_INSTANCE )
    public String doCreateInstance( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _instance, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_INSTANCE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _instance, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_INSTANCE );
        }

        InstanceHome.create( _instance );
        addInfo( INFO_INSTANCE_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_INSTANCES );
    }

    /**
     * Manages the removal form of a instance whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_INSTANCE )
    public String getConfirmRemoveInstance( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_INSTANCE );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_INSTANCE ) );
        url.addParameter( PARAMETER_ID_INSTANCE, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_INSTANCE, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a instance
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage instances
     */
    @Action( ACTION_REMOVE_INSTANCE )
    public String doRemoveInstance( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_INSTANCE );
        
        
        InstanceHome.remove( uuid );
        addInfo( INFO_INSTANCE_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_INSTANCES );
    }

    /**
     * Returns the form to update info about a instance
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_INSTANCE )
    public String getModifyInstance( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_INSTANCE );
        if(uuid == null) {
            return redirect( request, VIEW_MANAGE_INSTANCES );
        }
        if ( _instance == null || !uuid.equals( _instance.getUuid( ) ) )
        {
            Optional<Instance> optInstance = InstanceHome.findByPrimaryKey( uuid );
            _instance = optInstance.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_INSTANCE, _instance );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_INSTANCE ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_INSTANCE, TEMPLATE_MODIFY_INSTANCE, model );
    }

    /**
     * Process the change form of a instance
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_INSTANCE )
    public String doModifyInstance( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _instance, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_INSTANCE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _instance, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_INSTANCE, Map.of(PARAMETER_ID_INSTANCE, _instance.getUuid( )) );
        }

        InstanceHome.update( _instance );
        addInfo( INFO_INSTANCE_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_INSTANCES );
    }
}

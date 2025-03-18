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

import fr.paris.lutece.plugins.apimanager.business.history.HistoryHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.resource.ResourceVerbEnum;
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


import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.resource.ResourceHome;

import static fr.paris.lutece.plugins.apimanager.right.Constants.RIGHT_MANAGEAPIS;

/**
 * This class provides the user interface to manage Resource features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageResources.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEAPIS )
public class ResourceJspBean extends AbstractJspBean <String, Resource>
{

    // Templates
    private static final String TEMPLATE_MANAGE_RESOURCES = "/admin/plugins/apimanager/manage_resources.html";
    private static final String TEMPLATE_CREATE_RESOURCE = "/admin/plugins/apimanager/create_resource.html";
    private static final String TEMPLATE_MODIFY_RESOURCE = "/admin/plugins/apimanager/modify_resource.html";

    // Parameters
    private static final String PARAMETER_ID_RESOURCE = "uuid";
    private static final String PARAMETER_ID_PLAN = "uuid_plan";
    private static final String PARAMETER_VERB_NAME = "verb_name";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_RESOURCES = "apimanager.manage_resources.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_RESOURCE = "apimanager.modify_resource.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_RESOURCE = "apimanager.create_resource.pageTitle";

    // Markers
    private static final String MARK_RESOURCE_LIST = "resource_list";
    private static final String MARK_RESOURCE = "resource";
    private static final String MARK_VERB_LIST = "verb_list";

    private static final String JSP_MANAGE_RESOURCES = "jsp/admin/plugins/apimanager/ManageResources.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_RESOURCE = "apimanager.message.confirmRemoveResource";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.resource.attribute.";

    // Views
    private static final String VIEW_MANAGE_RESOURCES = "manageResources";
    private static final String VIEW_CREATE_RESOURCE = "createResource";
    private static final String VIEW_MODIFY_RESOURCE = "modifyResource";

    // Actions
    private static final String ACTION_CREATE_RESOURCE = "createResource";
    private static final String ACTION_MODIFY_RESOURCE = "modifyResource";
    private static final String ACTION_REMOVE_RESOURCE = "removeResource";
    private static final String ACTION_CONFIRM_REMOVE_RESOURCE = "confirmRemoveResource";

    // Infos
    private static final String INFO_RESOURCE_CREATED = "apimanager.info.resource.created";
    private static final String INFO_RESOURCE_UPDATED = "apimanager.info.resource.updated";
    private static final String INFO_RESOURCE_REMOVED = "apimanager.info.resource.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Resource _resource;
    private List<String> _listIdResources;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_RESOURCES, defaultView = true )
    public String getManageResources( HttpServletRequest request )
    {
        _resource = null;
        
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
        	// if sorting request : new search with the existing filter criteria, ordered 
        	// example of order by parameter : orderby=name
        	if ( StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) )
        	{
        		
        		String strOrderByColumn =  (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY);
        		String strSortMode = getSortMode(); 
        		
        		_listIdResources = ResourceHome.getIdResourcesList( _mapFilterCriteria, strOrderByColumn, strSortMode );
               	
	       	}
	       	else
	       	{
	       		// reload the filter criteria and search
	       		_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
	       		_listIdResources = ResourceHome.getIdResourcesList( _mapFilterCriteria, null ,null);
	       	}
        	
        	//set CurrentPageIndex of Paginator to null in aim of displays the first page of results
        	resetCurrentPageIndexOfPaginator();
        }
       	
       	Map<String, Object> model = getPaginatedListModel( request, MARK_RESOURCE_LIST, _listIdResources, JSP_MANAGE_RESOURCES );
             
        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs
                     
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_RESOURCES, TEMPLATE_MANAGE_RESOURCES, model );

    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Resource> getItemsFromIds( List<String> listIds )
	{
		List<Resource> listResource = ResourceHome.getResourcesListByIds( listIds );
		
		// keep original order
        return listResource.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdResources list
    */
    public void resetListId( )
    {
    	_listIdResources = new ArrayList<>( );
    }

    /**
     * Returns the form to create a resource
     *
     * @param request The Http request
     * @return the html code of the resource form
     */
    @View( VIEW_CREATE_RESOURCE )
    public String getCreateResource( HttpServletRequest request )
    {
        _resource = ( _resource != null ) ? _resource : new Resource(  );
        final Plan plan = new Plan();
        plan.setUuid( request.getParameter( PARAMETER_ID_PLAN ) );
        _resource.setPlan( plan );

        Map<String, Object> model = getModel(  );
        model.put( MARK_RESOURCE, _resource );
        model.put(MARK_VERB_LIST, ResourceVerbEnum.values());
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_RESOURCE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_RESOURCE, TEMPLATE_CREATE_RESOURCE, model );
    }

    /**
     * Process the data capture form of a new resource
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_RESOURCE )
    public String doCreateResource( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _resource, request, getLocale( ) );
        _resource.setVerb(ResourceVerbEnum.valueOf( request.getParameter( PARAMETER_VERB_NAME ) ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_RESOURCE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _resource, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_RESOURCE );
        }

        ResourceHome.create( _resource );
        HistoryHome.create(buildNewHistory(_resource.getUuid(), HistoryTypeEnum.CREATE));

        resetListId( );

        return redirect(request, "ManageApis.jsp?infoMsg=" + INFO_RESOURCE_CREATED);
    }

    /**
     * Manages the removal form of a resource whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_RESOURCE )
    public String getConfirmRemoveResource( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_RESOURCE );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_RESOURCE ) );
        url.addParameter( PARAMETER_ID_RESOURCE, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_RESOURCE, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a resource
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage resources
     */
    @Action( ACTION_REMOVE_RESOURCE )
    public String doRemoveResource( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_RESOURCE );
        
        
        ResourceHome.remove( uuid );
        HistoryHome.create(buildNewHistory(uuid, HistoryTypeEnum.DELETE));

        resetListId( );

        return redirect(request, "ManageApis.jsp?infoMsg=" + INFO_RESOURCE_REMOVED);
    }

    /**
     * Returns the form to update info about a resource
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_RESOURCE )
    public String getModifyResource( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_RESOURCE );
        if(uuid==null){
            return redirect(request, VIEW_MANAGE_RESOURCES);
        }
        if ( _resource == null || !uuid.equals( _resource.getUuid( ) ) )
        {
            Optional<Resource> optResource = ResourceHome.findByPrimaryKey( uuid );
            _resource = optResource.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_RESOURCE, _resource );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_RESOURCE ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_RESOURCE, TEMPLATE_MODIFY_RESOURCE, model );
    }

    /**
     * Process the change form of a resource
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_RESOURCE )
    public String doModifyResource( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _resource, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_RESOURCE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _resource, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_RESOURCE, Map.of(PARAMETER_ID_RESOURCE, _resource.getUuid( )) );
        }

        ResourceHome.update( _resource );
        HistoryHome.create(buildNewHistory(_resource.getUuid(), HistoryTypeEnum.UPDATE));

        resetListId( );

        return redirect(request, "ManageApis.jsp?infoMsg=" + INFO_RESOURCE_UPDATED);
    }
}

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.paris.lutece.plugins.apimanager.business.api.Api;
import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionHome;
import fr.paris.lutece.plugins.apimanager.service.ApiService;
import fr.paris.lutece.plugins.apimanager.service.InstanceService;
import fr.paris.lutece.plugins.apimanager.service.PlanService;
import fr.paris.lutece.plugins.apimanager.service.ResourceService;
import fr.paris.lutece.plugins.apimanager.service.SubscriptionService;
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
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEAPIS;

/**
 * This class provides the user interface to manage Api features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageApis.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEAPIS )
public class ApiJspBean extends AbstractJspBean<String, Api>
{
    // Templates
    private static final String TEMPLATE_MANAGE_APIS = "/admin/plugins/apimanager/manage_apis.html";
    private static final String TEMPLATE_CREATE_API = "/admin/plugins/apimanager/create_api.html";
    private static final String TEMPLATE_MODIFY_API = "/admin/plugins/apimanager/modify_api.html";

    // Parameters
    private static final String PARAMETER_ID_API = "uuid";
    private static final String PARAMETER_OPENAPI = "openapi";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_PATH = "path";
    private static final String PARAMETER_SUBSCRIPTION_MODE = "subscriptionMode";
    private static final String PARAMETER_LINK_MODE = "linkMode";
    private static final String PARAMETER_SELECTED_TAGS = "selected_tags";
    private static final String PARAMETER_INFO_MSG = "infoMsg";
    private static final String PARAMETER_ID_INSTANCE = "uuid_instance";
    private static final String PARAMETER_SHOW_INSTANCES = "showInstances";
    private static final String PARAMETER_DELETE_LINK = "deleteLink";
    private static final String PARAMETER_ARCHIVED = "archived";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APIS = "apimanager.manage_apis.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_API = "apimanager.modify_api.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_API = "apimanager.create_api.pageTitle";

    // Markers
    private static final String MARK_API_LIST = "api_list";
    private static final String MARK_API = "api";
    private static final String MARK_PLAN_TEMPLATE_NAMES = "plan_template_names";

    private static final String JSP_MANAGE_APIS = "jsp/admin/plugins/apimanager/ManageApis.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_ARCHIVE_API = "apimanager.message.confirmArchiveApi";
    private static final String MESSAGE_CONFIRM_REMOVE_LINK = "apimanager.message.confirmRemoveLink";
    private static final String TEMPLATE_NAME_PROP = "apimanager.plan.template.{i}.template.name";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.api.attribute.";

    // Views
    private static final String VIEW_MANAGE_APIS = "manageApis";
    private static final String VIEW_CREATE_API = "createApi";
    private static final String VIEW_MODIFY_API = "modifyApi";
    private static final String VIEW_LINK_API = "linkApi";

    // Actions
    private static final String ACTION_CREATE_API = "createApi";
    private static final String ACTION_MODIFY_API = "modifyApi";
    private static final String ACTION_ARCHIVE_API = "archiveApi";
    private static final String ACTION_REMOVE_LINK = "removeLink";
    private static final String ACTION_CONFIRM_ARCHIVE_API = "confirmArchiveApi";
    private static final String ACTION_CONFIRM_REMOVE_LINK = "confirmRemoveLink";
    private static final String ACTION_DOWNLOAD_OPENAPI = "downloadOpenapi";
    private static final String ACTION_LINK_INSTANCE = "linkInstance";

    // Infos
    private static final String INFO_API_CREATED = "apimanager.info.api.created";
    private static final String INFO_API_UPDATED = "apimanager.info.api.updated";
    private static final String INFO_API_ARCHIVED = "apimanager.info.api.archived";
    private static final String INFO_INSTANCE_LINKED = "apimanager.info.api.instanceLinked";
    private static final String INFO_LINK_REMOVED = "apimanager.info.api.linkRemoved";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Api _api;
    private List<String> _listIdApis;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>( );
    private String _optionOrderBy;

    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean( IConfigGeneratorService.BEAN_NAME );
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper( ).enable( SerializationFeature.INDENT_OUTPUT );

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_APIS, defaultView = true )
    public String getManageApis( HttpServletRequest request )
    {
        final String infoMsg = request.getParameter( PARAMETER_INFO_MSG );
        if ( infoMsg != null )
        {
            addInfo( infoMsg, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_APIS );
        }

        _api = null;
        final Map<String, Object> model = new HashMap<>( );
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null )
        {
            // if sorting request : new search with the existing filter criteria, ordered
            // example of order by parameter : orderby=name
            if ( StringUtils.isNotBlank( (String) request.getParameter( PARAMETER_SEARCH_ORDER_BY ) ) )
            {

                String strOrderByColumn = (String) request.getParameter( PARAMETER_SEARCH_ORDER_BY );
                String strSortMode = getSortMode( );

                _listIdApis = getService( ).getIdEntitiesList( _mapFilterCriteria, strOrderByColumn, strSortMode );

            }
            else
            {
                // reload the filter criteria and search
                _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
                final HashMap<String, String> criterias = new HashMap<>( _mapFilterCriteria );
                if ( !_mapFilterCriteria.containsKey( FILTER_DISPLAY_ARCHIVED ) )
                {
                    criterias.put( FILTER_ARCHIVED, Boolean.FALSE.toString( ) );
                }
                if ( criterias.containsKey( PARAMETER_ID_INSTANCE ) )
                {
                    final String uuidInstance = criterias.get( PARAMETER_ID_INSTANCE );
                    _listIdApis = getService( ).getIdApisListLinkedToInstanceUuid( uuidInstance );
                    model.put( PARAMETER_ID_INSTANCE, uuidInstance );
                    model.put( PARAMETER_SHOW_INSTANCES, false );
                    model.put( PARAMETER_DELETE_LINK, true );
                }
                else
                {
                    _listIdApis = getService( ).getIdEntitiesList( criterias );
                }
            }

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator( );
        }

        model.putAll( getPaginatedListModel( request, MARK_API_LIST, _listIdApis, JSP_MANAGE_APIS ) );

        final String subscriptionMode = request.getParameter( PARAMETER_SUBSCRIPTION_MODE );
        if ( subscriptionMode != null )
        {
            model.put( PARAMETER_SUBSCRIPTION_MODE, Boolean.parseBoolean( subscriptionMode ) );
        }
        addPlanTemplateNamesToModel( model );

        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APIS, TEMPLATE_MANAGE_APIS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Api> getItemsFromIds( List<String> listIds )
    {
        final List<Api> listApi = getService( ).getEntitiesListByIds( listIds );

        // keep original order
        return listApi.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getUuid( ) ) ) ).collect( Collectors.toList( ) );
    }

    @Override
    protected ApiService getService( )
    {
        return ApiService.getInstance( );
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
    }

    /**
     * reset the _listIdApis list
     */
    public void resetListId( )
    {
        _listIdApis = new ArrayList<>( );
    }

    /**
     * Returns the form to create a api
     *
     * @param request
     *            The Http request
     * @return the html code of the api form
     */
    @View( VIEW_CREATE_API )
    public String getCreateApi( HttpServletRequest request )
    {
        _api = ( _api != null ) ? _api : new Api( );

        Map<String, Object> model = getModel( );
        model.put( MARK_API, _api );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_API ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_API, TEMPLATE_CREATE_API, model );
    }

    /**
     * Process the data capture form of a new api
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_API )
    public String doCreateApi( HttpServletRequest request ) throws AccessDeniedException
    {
        try
        {
            populateApi( _api, request, getLocale( ) );
        }
        catch( JsonProcessingException e )
        {
            this.addError( "Error while parsing the openapi file. Please select a valid JSON file." );
            return redirectView( request, VIEW_CREATE_API );
        }
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_API ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _api, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_API );
        }

        getService( ).create( _api, getUser( ).getEmail( ) );
        addInfo( INFO_API_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_APIS );
    }

    /**
     * Manages the removal form of a api whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_ARCHIVE_API )
    public String getConfirmArchiveApi( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_API );
        UrlItem url = new UrlItem( getActionUrl( ACTION_ARCHIVE_API ) );
        url.addParameter( PARAMETER_ID_API, uuid );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_ARCHIVE_API, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a api
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage apis
     */
    @Action( ACTION_ARCHIVE_API )
    public String doArchiveApi( HttpServletRequest request )
    {
        final String apiUuid = request.getParameter( PARAMETER_ID_API );
        final Api api = ApiHome.findByPrimaryKey( apiUuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

        // DELETE PUBLISHED CONFIGS AND ARCHIVE SUBSCRIPTIONS
        // get all plans linked to this API, if any
        PlanService.getInstance( ).getIdEntitiesList( Map.of( "uuid_api", apiUuid ) ).forEach( planUuid -> {
            // for each plan, get the subscriptions, if any
            SubscriptionService.getInstance( ).getIdEntitiesList( Map.of( "uuid_plan", planUuid ) ).forEach( subscriptionUuid -> {
                SubscriptionHome.findByPrimaryKey( subscriptionUuid ).ifPresent( subscription -> {
                    // for each subscription, send a delete request, and archive the subscription
                    _configGeneratorService.deleteApiManager( subscription.getClient( ), subscription.getPlan( ),
                            ResourceService.getInstance( ).getResourcesByPlanUuid( planUuid ),
                            InstanceService.getInstance( ).getEntitiesListByIds( InstanceService.getInstance( ).getIdInstancesListLinkedToApiUuid( apiUuid ) ),
                            subscription.getEnvironnement( ), getUser( ).getEmail( ) );
                    SubscriptionService.getInstance( ).archive( subscriptionUuid, getUser( ).getEmail( ) );
                } );
            } );
        } );

        getService( ).archive( apiUuid, getUser( ).getEmail( ) );
        addInfo( INFO_API_ARCHIVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_APIS );
    }

    /**
     * Manages the removal form of an instance link to an API whose identifiers is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_LINK )
    public String getConfirmRemoveLink( HttpServletRequest request )
    {
        final String instanceUuid = request.getParameter( PARAMETER_ID_INSTANCE );
        final String apiUuid = request.getParameter( PARAMETER_ID_API );
        final UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_LINK ) );
        url.addParameter( PARAMETER_ID_INSTANCE, instanceUuid );
        url.addParameter( PARAMETER_ID_API, apiUuid );

        final String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_LINK, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of an instance link to an API
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage instances
     */
    @Action( ACTION_REMOVE_LINK )
    public String doRemoveLink( HttpServletRequest request )
    {
        final String instanceUuid = request.getParameter( PARAMETER_ID_INSTANCE );
        final String apiUuid = request.getParameter( PARAMETER_ID_API );
        final Instance instance = InstanceHome.findByPrimaryKey( instanceUuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

        InstanceService.getInstance( ).deleteLinkApi( instance, apiUuid, getUser( ).getEmail( ) );

        addInfo( INFO_LINK_REMOVED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_APIS );
    }

    /**
     * Returns the form to update info about a api
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_API )
    public String getModifyApi( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_API );
        if ( uuid == null )
        {
            return redirectView( request, VIEW_MANAGE_APIS );
        }
        if ( _api == null || !uuid.equals( _api.getUuid( ) ) )
        {
            Optional<Api> optApi = ApiHome.findByPrimaryKey( uuid );
            _api = optApi.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_API, _api );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_API ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_API, TEMPLATE_MODIFY_API, model );
    }

    /**
     * Process the change form of a api
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_API )
    public String doModifyApi( MultipartHttpServletRequest request ) throws AccessDeniedException
    {
        try
        {
            populateApi( _api, request, getLocale( ) );
        }
        catch( JsonProcessingException e )
        {
            this.addError( "Error while parsing the openapi file. Please select a valid JSON file." );
            return redirect( request, VIEW_MODIFY_API, Map.of( PARAMETER_ID_API, _api.getUuid( ) ) );
        }

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_API ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _api, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_API, Map.of( PARAMETER_ID_API, _api.getUuid( ) ) );
        }

        getService( ).update( _api, getUser( ).getEmail( ) );
        addInfo( INFO_API_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_APIS );
    }

    @Action( ACTION_DOWNLOAD_OPENAPI )
    public void doDownloadOpenapi( HttpServletRequest request ) throws JsonProcessingException
    {
        final String uuid = request.getParameter( PARAMETER_ID_API );
        if ( uuid == null )
        {
            redirectView( request, VIEW_MANAGE_APIS );
        }

        final Api api = ApiHome.findByPrimaryKey( uuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        this.download( JSON_MAPPER.writeValueAsBytes( api.getOpenapi( ) ), api.getName( ).replace( " ", "-" ) + "_openapi.json", "application/json" );
    }

    @View( VIEW_LINK_API )
    public String getLinkApi( HttpServletRequest request )
    {
        final String instanceUuid = request.getParameter( PARAMETER_ID_INSTANCE );
        _listIdApis = getService( ).getIdApisListNotLinkedToInstanceUuid( instanceUuid );
        final Map<String, Object> model = getPaginatedListModel( request, MARK_API_LIST, _listIdApis, JSP_MANAGE_APIS );

        final String linkMode = request.getParameter( PARAMETER_LINK_MODE );
        if ( linkMode != null )
        {
            model.put( PARAMETER_LINK_MODE, Boolean.parseBoolean( linkMode ) );
        }
        model.put( PARAMETER_ID_INSTANCE, instanceUuid );
        addSearchParameters( model, _mapFilterCriteria ); // allow the persistence of search values in inputs search bar inputs

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APIS, TEMPLATE_MANAGE_APIS, model );
    }

    @Action( ACTION_LINK_INSTANCE )
    public String doLinkApi( HttpServletRequest request )
    {
        final String instanceUuid = request.getParameter( PARAMETER_ID_INSTANCE );
        final String apiUuid = request.getParameter( PARAMETER_ID_API );
        if ( StringUtils.isAnyBlank( instanceUuid, apiUuid ) )
        {
            return redirectView( request, VIEW_MANAGE_APIS );
        }
        final Instance instance = InstanceHome.findByPrimaryKey( instanceUuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        final Api api = ApiHome.findByPrimaryKey( apiUuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

        getService( ).linkInstance( api, instanceUuid, getUser( ).getEmail( ) );

        addInfo( INFO_INSTANCE_LINKED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_APIS );
    }

    protected void populateApi( Object bean, HttpServletRequest request, Locale locale ) throws JsonProcessingException
    {
        super.populate( bean, request, locale );

        if ( request instanceof MultipartHttpServletRequest )
        {
            final FileItem openapiFile = ( (MultipartHttpServletRequest) request ).getFile( PARAMETER_OPENAPI );
            if ( openapiFile != null && openapiFile.getSize( ) > 0 )
            {
                _api.setOpenapi( JSON_MAPPER.readValue( openapiFile.getString( ), new TypeReference<Map<String, Object>>( )
                {
                } ) );
            }
        }
        _api.setTags( Arrays.stream( Optional.ofNullable( request.getParameterValues( PARAMETER_SELECTED_TAGS ) ).orElse( new String [ 0] ) )
                .collect( Collectors.toList( ) ) );
    }

    private void addPlanTemplateNamesToModel( final Map<String, Object> model )
    {
        final List<String> templateNameList = new ArrayList<>( );
        for ( int i = 0;; i++ )
        {
            final String templateName = AppPropertiesService.getProperty( TEMPLATE_NAME_PROP.replace( "{i}", String.valueOf( i ) ), null );
            if ( templateName == null )
            {
                break;
            }
            templateNameList.add( templateName );
        }
        model.put( MARK_PLAN_TEMPLATE_NAMES, templateNameList );
    }
}

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
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanOauthConfiguration;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.service.PlanService;
import fr.paris.lutece.plugins.apimanager.service.ResourceService;
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
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEPLANS;

/**
 * This class provides the user interface to manage Plan features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManagePlans.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEPLANS )
public class PlanJspBean extends AbstractJspBean<String, Plan>
{

    // Templates
    private static final String TEMPLATE_MANAGE_PLANS = "/admin/plugins/apimanager/plan/manage_plans.html";
    private static final String TEMPLATE_CREATE_PLAN = "/admin/plugins/apimanager/plan/create_plan.html";
    private static final String TEMPLATE_MODIFY_PLAN = "/admin/plugins/apimanager/plan/modify_plan.html";

    // Parameters
    private static final String PARAMETER_ID_PLAN = "uuid";
    private static final String PARAMETER_ID_API = "uuid_api";
    private static final String PARAMETER_SUBSCRIPTION_MODE = "subscriptionMode";
    private static final String PARAMETER_OAUTH_CONFIGURATION_PREFIX = "oauth_configuration_";
    private static final String PARAMETER_TEMPLATE_NAME = "template_name";
    private static final String PARAMETER_VERSION = "version";
    private static final String PARAMETER_API_ARCHIVED = "api_archived";
    private static final String PARAMETER_ENVIRONMENT_PREFIX = "environment_";
    private static final String PARAMETER_SELECTED_TAGS = "selected_tags";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PLANS = "apimanager.manage_plans.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PLAN = "apimanager.modify_plan.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PLAN = "apimanager.create_plan.pageTitle";

    // Markers
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_PLAN = "plan";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_TAG_LIST = "tag_list";
    private static final String MARK_SELECTED_TAG_LIST = "selected_tag_list";
    private static final String MARK_SELECTED_ENVIRONMENT_UUID = "selected_environment_uuid";

    private static final String MARK_HEADER_MATCHING_TYPE_LIST = "header_matching_type_list";

    private static final String MARK_RATE_LIMITING_TEMPLATE_MAP = "rate_limiting_template_map";
    private static final String MARK_CLIENT_HTTP_TEMPLATE_MAP = "client_http_template_map";

    private static final String JSP_MANAGE_PLANS = "jsp/admin/plugins/apimanager/ManagePlans.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_PLAN = "apimanager.message.confirmRemovePlan";
    private static final String HEADER_MATCHING_TYPE_VALUES = "apimanager.plan.headermatching.type.values";

    private static final String TEMPLATE_PREFIX = "apimanager.plan.template.";
    private static final String RATE_LIMITING_TEMPLATE_PREFIX = "apimanager.plan.ratelimiting.template.";
    private static final String CLIENT_HTTP_TEMPLATE_PREFIX = "apimanager.plan.clienthttp.template.";

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
    private static final String ACTION_NEW_VERSION = "newVersion";

    // Infos
    private static final String INFO_PLAN_CREATED = "apimanager.info.plan.created";
    private static final String INFO_PLAN_UPDATED = "apimanager.info.plan.updated";
    private static final String INFO_PLAN_REMOVED = "apimanager.info.plan.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ERROR_TEMPLATE_LOADING = "Template loading failed";

    // Session variable to store working values
    private Plan _plan;
    private List<String> _listIdPlans;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>( );
    private String _optionOrderBy;

    private final Map<String, String> clientHttpTemplateMap = new HashMap<>( );
    private final Map<String, String> rateLimitingTemplateMap = new HashMap<>( );

    private final List<String> headerMatchingTypeList = Arrays.asList( AppPropertiesService.getProperty( HEADER_MATCHING_TYPE_VALUES ).split( "," ) );

    private final Map<String, Plan> planTemplates = new HashMap<>( );
    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean( IConfigGeneratorService.BEAN_NAME );

    @Override
    public void init( HttpServletRequest request, String strRight ) throws AccessDeniedException
    {
        super.init( request, strRight );
        try
        {
            loadTemplates( );
        }
        catch( final Exception e )
        {
            this.addError( ERROR_TEMPLATE_LOADING );
        }
    }

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

        // new search only if in pagination mode
        if ( request.getParameter( PARAMETER_SELECTED_TAGS ) != null ){
            String selectedStringTags = request.getParameter(PARAMETER_SELECTED_TAGS);
            List<String> selectedTags =  new ArrayList<>( );
            if(selectedStringTags != null && !selectedStringTags.isEmpty() && selectedStringTags.contains(",")){
                selectedTags.addAll( Arrays.asList(selectedStringTags.split(",")));
            }else{
                selectedTags.add(selectedStringTags);
            }

            _listIdPlans = getService( ).getPlansByTags(selectedTags);

            model.put(MARK_SELECTED_TAG_LIST, selectedTags);
        }

        Map<String, Object> planModel = getPaginatedListModel(request, MARK_PLAN_LIST, _listIdPlans, JSP_MANAGE_PLANS);
        List<Plan> planList = ((List<Plan>) planModel.get(MARK_PLAN_LIST));
        String selectedEnvironementUuid = _mapFilterCriteria.get("uuid_environement");
        if(selectedEnvironementUuid != null){
            Environement environement = EnvironementHome.findByPrimaryKey(selectedEnvironementUuid).orElse(null);
            if(environement != null){
                planModel.put(MARK_PLAN_LIST, planList.stream().filter(plan -> plan.getAvailableEnvironments().contains(environement.getName()) ).collect(Collectors.toList()));            }
            model.put(MARK_SELECTED_ENVIRONMENT_UUID,selectedEnvironementUuid);
        }

        model.putAll( planModel );

        ArrayList<String> tags = new ArrayList<String>();
        for(Plan planValue : ((List<Plan>)planModel.get(MARK_PLAN_LIST))){
            tags.addAll(planValue.getTags());
        }

        model.put( MARK_ENVIRONMENT_LIST, environmentList );
        model.put( MARK_TAG_LIST, tags.stream().distinct().collect( Collectors.toList( ) ) );

        model.putAll(initPlanCreation(request));

        final String subscriptionMode = request.getParameter( PARAMETER_SUBSCRIPTION_MODE );
        if ( subscriptionMode != null )
        {
            model.put( PARAMETER_SUBSCRIPTION_MODE, Boolean.parseBoolean( subscriptionMode ) );
        }
        if ( request.getParameterMap( ).containsKey( PARAMETER_API_ARCHIVED ) )
        {
            model.put( PARAMETER_API_ARCHIVED, true );
        }


        model.put(MARK_SELECTED_ENVIRONMENT_UUID,_mapFilterCriteria.get("uuid_environement"));
        //exlude some filters from the returned list
        for(String exclusion: getExcludedSearchParameters()){
            _mapFilterCriteria.remove(exclusion);
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
        Map<String, Object> model = initPlanCreation(request);
        return getPage( PROPERTY_PAGE_TITLE_CREATE_PLAN, TEMPLATE_CREATE_PLAN, model );
    }

    private Map<String, Object> initPlanCreation(HttpServletRequest request ){
        _plan = new Plan( );
        final String templateName = request.getParameter( PARAMETER_TEMPLATE_NAME );
        if ( StringUtils.isNotBlank( templateName ) )
        {
            try
            {
                _plan = (Plan) BeanUtilsBean.getInstance( ).cloneBean( planTemplates.get( templateName ) );
            }
            catch( final Exception e )
            {
                this.addError( ERROR_TEMPLATE_LOADING );
            }
        }
        if ( _plan.getOauthConfiguration( ) == null )
        {
            _plan.setOauthConfiguration( new PlanOauthConfiguration( ) );
        }
        final Api api = new Api( );
        api.setUuid( request.getParameter( PARAMETER_ID_API ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_PLAN, _plan );
        model.put( MARK_ENVIRONMENT_LIST, environmentList );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_PLAN ) );
        addValuesAndDefaultsToModel( model );
        return model;
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
        _plan = new Plan( );
        populateAll( request, getLocale( ) );
        _plan.setStatus( PlanStatusEnum.PUBLISHED );

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

        return redirect( request, "ManagePlans.jsp?infoMsg=" + INFO_PLAN_CREATED );
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
        final String uuid = request.getParameter( PARAMETER_ID_PLAN );
        _plan = PlanHome.findByPrimaryKey( uuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

        // DELETE PUBLISHED CONFIG
   /*  SubscriptionService.getInstance( ).getIdEntitiesList( Map.of( "uuid_plan", uuid ) ).forEach( subscriptionUuid -> {
            // Delete the subscriptions to this plan, if any
            SubscriptionHome.findByPrimaryKey( subscriptionUuid ).ifPresent( subscription -> {
                // Send delete request and delete subscription
                _configGeneratorService.deleteApiManager( subscription.getClient( ), _plan, ResourceService.getInstance( ).getResourcesByPlanUuid( uuid ),
                        InstanceService.getInstance( )
                                .getEntitiesListByIds( InstanceService.getInstance( ).getIdInstancesListLinkedToResourceUuid( _plan.getApi( ).getUuid( ) ) ),
                        subscription.getEnvironnement( ), getUser( ).getEmail( ) );
                SubscriptionService.getInstance( ).delete( subscriptionUuid, getUser( ).getEmail( ) );
            } );
        } );*/

        getService( ).delete( uuid, getUser( ).getEmail( ) );
        resetListId( );

        return redirect( request, "ManagePlans.jsp?infoMsg=" + INFO_PLAN_REMOVED );
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
        model.put( MARK_ENVIRONMENT_LIST, environmentList );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_PLAN ) );
        addValuesAndDefaultsToModel( model );

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

        return redirect( request, "ManagePlans.jsp?infoMsg=" + INFO_PLAN_UPDATED );
    }

    /**
     * Clone a plan, and put a new version
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_NEW_VERSION )
    public String doNewVersion( HttpServletRequest request ) throws AccessDeniedException
    {
        final String uuid = request.getParameter( PARAMETER_ID_PLAN );
        final String newVersion = request.getParameter( PARAMETER_VERSION );
        if ( uuid == null || newVersion == null )
        {
            return redirect( request, "ManagePlans.jsp" );
        }
        final Plan planToClone = PlanHome.findByPrimaryKey( uuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        if ( planToClone.getVersion( ).equals( newVersion ) )
        {
            return redirect( request, "ManagePlans.jsp" );
        }

        try
        {
            _plan = (Plan) BeanUtilsBean.getInstance( ).cloneBean( planToClone );
        }
        catch( final Exception e )
        {
            throw new AppException( "Error while cloning plan.", e );
        }
        _plan.setVersion( newVersion );
        _plan.setStatus( PlanStatusEnum.DRAFT );
        _plan.setUuid( null );

        getService( ).create( _plan, getUser( ).getEmail( ) );

        final List<Resource> resourceList = new ArrayList<>( );
        try
        {
            for ( final Resource resourceToClone : ResourceService.getInstance( ).getResourcesByPlanUuid( uuid ) )
            {
                resourceList.add( (Resource) BeanUtilsBean.getInstance( ).cloneBean( resourceToClone ) );
            }
        }
        catch( final Exception e )
        {
            throw new AppException( "Error while cloning resources.", e );
        }

        resourceList.forEach( resource -> {
            resource.setUuid( null );
            resource.setPlan( _plan );
            ResourceService.getInstance( ).create( resource, getUser( ).getEmail( ) );
        } );

        resetListId( );

        return redirect( request, "ManagePlans.jsp?reload=true&infoMsg=" + INFO_PLAN_CREATED );
    }

    private void populateAll( final HttpServletRequest request, final Locale locale )
    {
        populate( _plan, request, locale );

        // ENVIRONNEMENTS
        _plan.getAvailableEnvironments( ).clear( );
        request.getParameterMap( ).keySet( ).stream( ).filter( key -> key.startsWith( PARAMETER_ENVIRONMENT_PREFIX ) )
                .map( key -> key.replace( PARAMETER_ENVIRONMENT_PREFIX, "" ) ).forEach( env -> _plan.getAvailableEnvironments( ).add( env ) );

        // OAUTH CONFIGURATION
        final PlanOauthConfiguration planOauthConfiguration = new PlanOauthConfiguration( );
        final Map<String, String [ ]> oauthConfigurationParams = request.getParameterMap( ).entrySet( ).stream( )
                .filter( entry -> entry.getKey( ).startsWith( PARAMETER_OAUTH_CONFIGURATION_PREFIX ) )
                .collect( Collectors.toMap( entry -> entry.getKey( ).replace( PARAMETER_OAUTH_CONFIGURATION_PREFIX, "" ), Map.Entry::getValue ) );
        final MultipartHttpServletRequest oauthConfigurationRequest = new MultipartHttpServletRequest( request, Map.of( ), oauthConfigurationParams );
        populate( planOauthConfiguration, oauthConfigurationRequest, locale );
        _plan.setOauthConfiguration( planOauthConfiguration );
    }

    private void addValuesAndDefaultsToModel( final Map<String, Object> model )
    {
        model.put( MARK_HEADER_MATCHING_TYPE_LIST, headerMatchingTypeList );
        model.put( MARK_RATE_LIMITING_TEMPLATE_MAP, rateLimitingTemplateMap );
        model.put( MARK_CLIENT_HTTP_TEMPLATE_MAP, clientHttpTemplateMap );
    }

    private void loadTemplates( ) throws InvocationTargetException, IllegalAccessException
    {
        if ( planTemplates.isEmpty( ) )
        {
            for ( int i = 0;; i++ )
            {
                final String prefix = TEMPLATE_PREFIX + i + ".";
                if ( AppPropertiesService.getKeys( prefix ).isEmpty( ) )
                {
                    break;
                }
                final String templateName = AppPropertiesService.getProperty( prefix + "template.name" );

                final Plan planTemplate = new Plan( );
                final String planPrefix = prefix + "plan.";
                final Map<String, Object> planProperties = new HashMap<>( );
                AppPropertiesService.getKeys( planPrefix )
                        .forEach( key -> planProperties.put( key.replace( planPrefix, "" ), AppPropertiesService.getProperty( key ) ) );
                BeanUtilsBean.getInstance( ).populate( planTemplate, planProperties );

                planTemplates.put( templateName, planTemplate );
            }
        }
        if ( rateLimitingTemplateMap.isEmpty( ) )
        {
            for ( int i = 0;; i++ )
            {
                final String prefix = RATE_LIMITING_TEMPLATE_PREFIX + i + ".";
                if ( AppPropertiesService.getKeys( prefix ).isEmpty( ) )
                {
                    break;
                }
                final String templateName = AppPropertiesService.getProperty( prefix + "name" );
                final String templateDesc = AppPropertiesService.getProperty( prefix + "description" );
                rateLimitingTemplateMap.put( templateName, templateDesc );
            }
        }
        if ( clientHttpTemplateMap.isEmpty( ) )
        {
            for ( int i = 0;; i++ )
            {
                final String prefix = CLIENT_HTTP_TEMPLATE_PREFIX + i + ".";
                if ( AppPropertiesService.getKeys( prefix ).isEmpty( ) )
                {
                    break;
                }
                final String templateName = AppPropertiesService.getProperty( prefix + "name" );
                final String templateDesc = AppPropertiesService.getProperty( prefix + "description" );
                clientHttpTemplateMap.put( templateName, templateDesc );
            }
        }
    }
}

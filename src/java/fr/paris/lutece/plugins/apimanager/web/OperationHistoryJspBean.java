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
    private static final String TEMPLATE_MANAGE_API_OPERATIONS = "/admin/plugins/apimanager/operation/manage_api_operations.html";
    private static final String TEMPLATE_MANAGE_CLIENT_OPERATIONS = "/admin/plugins/apimanager/operation/manage_client_operations.html";
    private static final String TEMPLATE_HISTORY_OPERATIONS = "/admin/plugins/apimanager/operation/history_operations.html";
    // Parameters
    private static final String PARAMETER_ID_OPERATION = "uuid";
    private static final String PARAMETER_ID_CLIENT = "uuid_client";
    private static final String PARAMETER_ID_PLAN = "uuid_plan";
    private static final String PARAMETER_VIEW_FROM_CLIENT = "view_from_client";
    private static final String PARAMETER_ENVIRONNEMENT = "environnement";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_UUID_OPERATION = "uuid_api";
    private static final String PARAMETER_UUID_APPLICATION = "uuid_application";
    private static final String PARAMETER_UUID_ENVIRONEMENT = "uuid_environement";
    private static final String PARAMETER_UUID_API = "uuid_api";
    private static final String PARAMETER_UUID_PLAN = "uuid_plan";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_HISTORY_OPERATION = "apimanager.manage_operations.history.pageTitle";

    // Markers
    private static final String MARK_OPERATION_LIST = "subscription_list";
    private static final String MARK_CLIENT_LIST = "client_list";
    private static final String MARK_HISTORY_LIST = "history_list";
    private static final String MARK_OPERATION = "subscription";
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_API_LIST = "api_list";
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_VIEW_FROM_CLIENT = "view_from_client";

    private static final String JSP_MANAGE_OPERATIONS = "jsp/admin/plugins/apimanager/ManageOperations.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_OPERATION = "apimanager.message.confirmRemoveSubscription";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.subscription.attribute.";

    // Views
    private static final String VIEW_MANAGE_CLIENT_OPERATIONS = "manageClientOperations";
    private static final String VIEW_HISTORY_CLIENT_OPERATIONS = "historyOperations";
    private static final String VIEW_CREATE_OPERATION = "createOperation";

    // Actions
    private static final String ACTION_CREATE_OPERATION = "createOperation";
    private static final String ACTION_REMOVE_OPERATION = "removeOperation";
    private static final String ACTION_CONFIRM_REMOVE_OPERATION = "confirmRemoveOperation";
    private static final String ACTION_GENERATE_API_MANAGER = "generateApiManager";

    // Infos
    private static final String INFO_OPERATION_CREATED = "apimanager.info.subscription.created";
    private static final String INFO_OPERATION_REMOVED = "apimanager.info.subscription.removed";
    private static final String INFO_API_MANAGER_GENERATED = "apimanager.info.subscription.api.manager.published";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ERROR_API_MANAGER_GENERATION = "Error publishing API manager";

    // Session variable to store working values
    private Subscription _subscription;
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

        Map<String, Object> model = getPaginatedListModel( request, MARK_HISTORY_LIST, _historyList.stream().map(History::getUuid).collect(Collectors.toList()), JSP_MANAGE_OPERATIONS );

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

    /**
     * Returns the form to create a subscription
     *
     * @param request
     *            The Http request
     * @return the html code of the subscription form
     */
    @View( VIEW_CREATE_OPERATION )
    public String getCreateSubscription( HttpServletRequest request )
    {
        _subscription = ( _subscription != null ) ? _subscription : new Subscription( );
        _subscription.setClient( new Client( ) );
        _subscription.setResource( new Resource( ) );

        String clientUuid = request.getParameter(PARAMETER_UUID_APPLICATION);
        String environementUuid = request.getParameter(PARAMETER_UUID_ENVIRONEMENT);
        String apiUuid = request.getParameter(PARAMETER_UUID_API);
        String planUuid = request.getParameter(PARAMETER_UUID_PLAN);
        Map<String, Object> model = getModel( );
        model.put(PARAMETER_UUID_APPLICATION,clientUuid );
        model.put(PARAMETER_UUID_ENVIRONEMENT,environementUuid );
        model.put(PARAMETER_UUID_API,apiUuid );
        model.put(PARAMETER_UUID_PLAN,planUuid );
        model.put( MARK_OPERATION, _subscription );
        List<Environement> environements = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());
        model.put( MARK_ENVIRONMENT_LIST, environements );
        model.put(MARK_CLIENT_LIST, ClientService.getInstance().getEntitiesListByIds(ClientService.getInstance().getIdEntitiesList()));

        if(clientUuid!=null && environementUuid!=null){
            List<Resource> resources = ResourceService.getInstance().getEntitiesListByIds(ResourceService.getInstance().getIdEntitiesList());
            List<Resource> environementResources = resources.stream()
                    .filter(resource -> resource.getEnvironement().getUuid().equals( environementUuid ) ).collect(Collectors.toList());
            Collection<Resource> uniqueByApi = environementResources
                    .stream()
                    .filter(resource -> resource.getApi() != null)
                    .collect(Collectors.toMap(usr -> Set.of(usr.getApi().getUuid()), Function.identity(), (usr1, usr2) -> usr1))
                    .values();
            model.put( MARK_API_LIST, uniqueByApi.stream().map(resource -> resource.getApi()).collect(Collectors.toList()) );
            if(apiUuid!=null){
                List<Resource> environementAndApiResources = resources.stream()
                        .filter(resource -> resource.getApi() != null)
                        .filter(resource -> resource.getEnvironement().getUuid().equals( environementUuid ) && resource.getApi().getUuid().equals(apiUuid) ).collect(Collectors.toList());
                Collection<Resource> uniqueByPlan = environementAndApiResources
                        .stream()
                        .collect(Collectors.toMap(usr -> Set.of(usr.getPlan().getUuid()), Function.identity(), (usr1, usr2) -> usr1))
                        .values();
                model.put( MARK_PLAN_LIST, uniqueByPlan.stream().map(resource -> resource.getPlan()).collect(Collectors.toList()) );
            }
        }


        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_OPERATION ) );

        return getPage( PROPERTY_PAGE_HISTORY_OPERATION, TEMPLATE_MANAGE_CLIENT_OPERATIONS, model );
    }

    /**
     * Process the data capture form of a new subscription
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_OPERATION )
    public String doCreateSubscription( HttpServletRequest request ) throws AccessDeniedException
    {
        Map<String, String[]> test = request.getParameterMap();
        String clientUuid = request.getParameter(PARAMETER_UUID_APPLICATION);
        String environementUuid = request.getParameter(PARAMETER_UUID_ENVIRONEMENT);
        String apiUuid = request.getParameter(PARAMETER_UUID_API);
        String planUuid = request.getParameter(PARAMETER_UUID_PLAN);

        _subscription = ( _subscription != null ) ? _subscription : new Subscription( );
        _subscription.setClient( new Client( ) );
        _subscription.getClient().setUuid(clientUuid);
        _subscription.setEnvironement( new Environement( ) );
        _subscription.getEnvironement().setUuid(environementUuid);
        _subscription.setPlan( new Plan( ) );
        _subscription.getPlan().setUuid(planUuid);

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_OPERATION ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( clientUuid == null || environementUuid == null || apiUuid == null || planUuid == null )
        {
            return redirectView( request, VIEW_CREATE_OPERATION );
        }


        resetListId( );

        return redirect( request, "ManageSubscriptions.jsp?infoMsg=" + INFO_OPERATION_CREATED );
    }

    /**
     * Manages the removal form of a subscription whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_OPERATION )
    public String getConfirmRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_OPERATION );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_OPERATION ) );
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
    @Action( ACTION_REMOVE_OPERATION )
    public String doRemoveSubscription( HttpServletRequest request )
    {
        String uuid = request.getParameter( PARAMETER_ID_OPERATION );
        addInfo( INFO_OPERATION_REMOVED, getLocale( ) );
        resetListId( );

        return redirect( request, "ManageClients.jsp?infoMsg=" + INFO_OPERATION_REMOVED );
    }

    @Action( ACTION_GENERATE_API_MANAGER )
    public String doGenerateApiManager( final HttpServletRequest request )
    {
        final String apiUuid = request.getParameter( PARAMETER_UUID_OPERATION );
        if ( apiUuid == null )
        {
            addError( ERROR_RESOURCE_NOT_FOUND );
            return redirectView( request, VIEW_MANAGE_CLIENT_OPERATIONS );
        }
        final Api api = ApiHome.findByPrimaryKey( apiUuid ).orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

        final String env = request.getParameter( PARAMETER_ENVIRONNEMENT );
        final String comment = request.getParameter( PARAMETER_COMMENT );

        try
        {

            List<Resource> resources = ResourceService.getInstance().getEntitiesListByIds(ResourceService.getInstance().getIdEntitiesList());
            List<Resource> apiResources = resources.stream()
                    .filter(resource -> resource.getApi() != null)
                    .filter(resource -> resource.getApi().getUuid().equals( apiUuid ) ).collect(Collectors.toList());
            List<Subscription> resourceSubscription =new ArrayList<>();
            for(Resource apiResource : apiResources){
                resourceSubscription.addAll(SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByResource(apiResource.getUuid())));
            }

            Map<String,Map<String, Map<String, List<Subscription>>>> multipleFieldsMap = resourceSubscription.stream()
                    .collect(
                            Collectors.groupingBy(o -> o.getClient().getUuid(),
                                    Collectors.groupingBy(o -> o.getResource().getEnvironement().getUuid(),
                                            (Collectors.groupingBy(o -> o.getResource().getPlan().getUuid())))));

            for(Map.Entry<String, Map<String, Map<String, List<Subscription>>>> clientSubscriptionByEnvironementAndPlan : multipleFieldsMap.entrySet()){
                String clientUuid = clientSubscriptionByEnvironementAndPlan.getKey();
                Map<String, Map<String, List<Subscription>>> clientEnvironements = clientSubscriptionByEnvironementAndPlan.getValue();
                for(Map.Entry<String, Map<String, List<Subscription>>> environementSubscription :  clientEnvironements.entrySet()){
                    String environemenbtUuid = environementSubscription.getKey();
                    Map<String, List<Subscription>> clientPlans = environementSubscription.getValue();
                    for(Map.Entry<String, List<Subscription>> planSubscription :  clientPlans.entrySet()) {
                        String planUuid = planSubscription.getKey();
                        List<Subscription> subscriptions = planSubscription.getValue();


                        for(Subscription sub : subscriptions){
                            List<String> instanceIds = InstanceHome.getIdInstancesListLinkedToResourceUuid(sub.getResource().getUuid());
                            sub.getResource().setInstances(InstanceHome.getInstancesListByIds(instanceIds));

                        }

                        _configGeneratorService.generateSubscriptions(
                                subscriptions, comment, getUser( ).getEmail( ) );

                    }
                }


            }

            getService( ).addNewHistory( api.getUuid( ), HistoryTypeEnum.GENERATE, getUser( ).getEmail( ) );
            api.setStatus( PlanStatusEnum.PUBLISHED.name() );
            ApiService.getInstance( ).update( api, getUser( ).getEmail( ) );
        }
        catch( final AppException e )
        {
            addError( ERROR_API_MANAGER_GENERATION );
            addError( e.getMessage( ) );
            return redirectView( request, VIEW_MANAGE_CLIENT_OPERATIONS );
        }

        addInfo( INFO_API_MANAGER_GENERATED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CLIENT_OPERATIONS );
    }

}

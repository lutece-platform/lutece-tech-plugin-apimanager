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
import fr.paris.lutece.plugins.apimanager.business.api.ApiStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.service.*;
import fr.paris.lutece.plugins.apimanager.service.generator.IConfigGeneratorService;
import fr.paris.lutece.plugins.apimanager.web.rest.dto.MeecrogateAckResponse;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEOPERATIONS;

/**
 * This class provides the user interface to manage Subscription features ( manage, create, modify, remove )
 */
@Controller(controllerJsp = "ManageOperations.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEOPERATIONS)
public class OperationJspBean extends AbstractJspBean<String, Api> {

    // Templates
    private static final String TEMPLATE_MANAGE_API_OPERATIONS = "/admin/plugins/apimanager/operation/manage_api_operations.html";
    // Parameters
    private static final String PARAMETER_ID_OPERATION = "uuid";
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
    private static final String PROPERTY_PAGE_API_OPERATIONS = "apimanager.manage_operations.pageTitle";

    // Markers
    private static final String MARK_OPERATION_LIST = "subscription_list";
    private static final String MARK_CLIENT_LIST = "client_list";
    private static final String MARK_OPERATION = "subscription";
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_API_LIST = "api_list";
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_VIEW_FROM_CLIENT = "view_from_client";
    private static final String MARK_SELECTED_ENVIRONMENT_UUID = "selected_environment_uuid";

    private static final String JSP_MANAGE_OPERATIONS = "jsp/admin/plugins/apimanager/ManageOperations.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_OPERATION = "apimanager.message.confirmRemoveSubscription";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.subscription.attribute.";

    // Views
    private static final String VIEW_MANAGE_OPERATIONS = "manageOperations";
    private static final String VIEW_CREATE_OPERATION = "createOperation";

    // Actions
    private static final String ACTION_CREATE_OPERATION = "createOperation";
    private static final String ACTION_REMOVE_OPERATION = "removeOperation";
    private static final String ACTION_CONFIRM_REMOVE_OPERATION = "confirmRemoveOperation";
    private static final String ACTION_GENERATE_API_MANAGER = "generateApiManager";
    private static final String ACTION_UPDATE_API_MANAGER = "updateApiManager";

    // Infos
    private static final String INFO_OPERATION_CREATED = "apimanager.info.subscription.created";
    private static final String INFO_OPERATION_REMOVED = "apimanager.info.subscription.removed";
    private static final String INFO_API_MANAGER_DELETED = "apimanager.info.subscription.api.manager.unpublished";
    private static final String INFO_API_MANAGER_GENERATED = "apimanager.info.subscription.api.manager.published";
    private static final String INFO_API_MANAGER_UPDATED = "apimanager.info.subscription.api.manager.updated";


    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ERROR_API_MANAGER_GENERATION = "Error publishing API manager";

    // Session variable to store working values
    private Subscription _subscription;
    private List<Api> _apiList;
    private List<String> _listIdApis;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;

    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean(IConfigGeneratorService.BEAN_NAME);

    /**
     * Build the Manage View
     *
     * @param request The HTTP request
     * @return The page
     */
    @View(value = VIEW_MANAGE_OPERATIONS, defaultView = true)
    public String getManageOperations(HttpServletRequest request) {
        _subscription = null;

        // new search only if in pagination mode
        if (request.getParameter(AbstractPaginator.PARAMETER_PAGE_INDEX) == null) {
            _optionOrderBy = request.getParameter(PARAMETER_SEARCH_ORDER_BY);
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest(request);
            final HashMap<String, String> criterias = new HashMap<>(_mapFilterCriteria);
            _listIdApis = ApiService.getInstance().getIdEntitiesList(criterias).stream().filter(s ->
                    !SubscriptionService.getInstance().getIdSubscriptionsByApi(s).isEmpty()
            ).collect(Collectors.toList());
            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator();
        } else {
            final HashMap<String, String> criterias = new HashMap<>(_mapFilterCriteria);
            criterias.put(FILTER_ARCHIVED, Boolean.FALSE.toString());
            _listIdApis = ApiService.getInstance().getIdEntitiesList(criterias)
                    .stream().filter(s -> !SubscriptionService.getInstance().getIdSubscriptionsByApi(s).isEmpty()
                    ).collect(Collectors.toList());
        }

        _apiList = ApiService.getInstance().getEntitiesListByIds(_listIdApis);

        //remove archived apis
        _apiList = _apiList.stream().filter(api -> !api.getArchived()).collect(Collectors.toList());

        for (Api api : _apiList) {
            Map<String, Client> subscribers = new HashMap<>();
            Map<String, Environement> subscribedEnvs = new HashMap<>();
            List<Resource> resources = ResourceService.getInstance().getResourcesByApiUuid(api.getUuid());
            api.setEnvironementList(
                    resources.stream().filter(resource -> resource.getEnvironement() != null && resource.getEnvironement().getUuid() != null)
                            .map(Resource::getEnvironement)
                            .filter(distinctByKey(Environement::getUuid))
                            .collect(Collectors.toList())
            );
            api.setPlantList(
                    resources.stream().filter(resource -> resource.getPlan() != null && resource.getPlan().getUuid() != null)
                            .map(Resource::getPlan)
                            .filter(distinctByKey(Plan::getUuid))
                            .collect(Collectors.toList())
            );
            api.setResourceList(resources);
            for (Resource resource : resources) {
                List<Subscription> subscriptions = SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByResource(resource.getUuid()));

                for (Subscription subscription : subscriptions) {
                    if (subscription.getClient() != null && subscription.getClient().getUuid() != null) {
                        Client currentClient = ClientHome.findByPrimaryKey(subscription.getClient().getUuid()).orElse(null);
                        if(currentClient != null){
                            // we add the subscriber add one subscription to a resource per environement
                            // in order to have the api/environement info in the frontend
                            if (!subscribers.containsKey(currentClient.getUuid())) {
                                currentClient.getSubscriptionList().add(subscription);
                                subscribers.put(subscription.getClient().getUuid(), currentClient);
                            }else{
                                if(subscribers.get(subscription.getClient().getUuid()).getSubscriptionList().stream()
                                        .noneMatch(subscription1 -> subscription1.getEnvironement().getUuid().equals(subscription.getEnvironement().getUuid()))){
                                    subscribers.get(subscription.getClient().getUuid()).getSubscriptionList().add(subscription);
                                }
                            }
                        }

                    }
                    if(subscription.getEnvironement() != null && subscription.getEnvironement().getUuid() != null) {
                        Environement environement  = EnvironementHome.findByPrimaryKey(subscription.getEnvironement().getUuid()).orElse(null);
                        if(environement!=null && !subscribedEnvs.containsKey(environement.getUuid())) {
                            subscribedEnvs.put(subscription.getEnvironement().getUuid(), environement);
                        }
                    }
                }
                //check if there is a desynchronize with api definition
                if(api.getStatus().equals(ApiStatusEnum.PUBLISHED.name())){
                    if(subscriptions.stream().anyMatch(subscription -> subscription.getStatus()!=null && subscription.getStatus().equals(ApiStatusEnum.NEW.name()))){
                        api.setStatus(ApiStatusEnum.DESYNCHRONIZED.name());
                    }
                }
            }
            api.setSubscriberList(new ArrayList<>(subscribers.values()));
            api.setSubscribedEnvironementList(new ArrayList<>(subscribedEnvs.values()));
        }

        String selectedEnvironementUuid = _mapFilterCriteria.get("uuid_environement");
        if (selectedEnvironementUuid != null) {
            _apiList = _apiList.stream().filter(api -> api.getResourceList().stream().anyMatch(resource -> resource.getEnvironement().getUuid().equals(selectedEnvironementUuid))).collect(Collectors.toList());
        }

        Map<String, Object> model = getPaginatedListModel(request, MARK_API_LIST, _apiList.stream().map(Api::getUuid).collect(Collectors.toList()), JSP_MANAGE_OPERATIONS);

        model.put(MARK_SELECTED_ENVIRONMENT_UUID, _mapFilterCriteria.get("uuid_environement"));
        //exlude some filters from the returned list
        for (String exclusion : getExcludedSearchParameters()) {
            _mapFilterCriteria.remove(exclusion);
        }
        addSearchParameters(model, _mapFilterCriteria); // allow the persistence of search values in inputs search bar inputs
        model.put(MARK_SHOW_GENERATE_BUTTON, (_configGeneratorService != null));
        model.put(MARK_ENVIRONMENT_LIST, environmentList);
        model.put(MARK_VIEW_FROM_CLIENT, Boolean.parseBoolean(Optional.ofNullable(request.getParameter(PARAMETER_VIEW_FROM_CLIENT)).orElse("false")));

        return getPage(PROPERTY_PAGE_API_OPERATIONS, TEMPLATE_MANAGE_API_OPERATIONS, model);

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
    List<Api> getItemsFromIds(List<String> listIds) {
        // keep original order
        return _apiList.stream().sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid()))).collect(Collectors.toList());
    }


    @Override
    protected ApiService getService() {
        return ApiService.getInstance();
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage() {
        return AppPropertiesService.getPropertyInt(PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50);
    }

    /**
     * reset the _listIdSubscriptions list
     */
    public void resetListId() {
        _listIdApis = new ArrayList<>();
    }

    /**
     * Returns the form to create a subscription
     *
     * @param request The Http request
     * @return the html code of the subscription form
     */
    @View(VIEW_CREATE_OPERATION)
    public String getCreateSubscription(HttpServletRequest request) {
        _subscription = (_subscription != null) ? _subscription : new Subscription();
        _subscription.setClient(new Client());
        _subscription.setResource(new Resource());

        String clientUuid = request.getParameter(PARAMETER_UUID_APPLICATION);
        String environementUuid = request.getParameter(PARAMETER_UUID_ENVIRONEMENT);
        String apiUuid = request.getParameter(PARAMETER_UUID_API);
        String planUuid = request.getParameter(PARAMETER_UUID_PLAN);
        Map<String, Object> model = getModel();
        model.put(PARAMETER_UUID_APPLICATION, clientUuid);
        model.put(PARAMETER_UUID_ENVIRONEMENT, environementUuid);
        model.put(PARAMETER_UUID_API, apiUuid);
        model.put(PARAMETER_UUID_PLAN, planUuid);
        model.put(MARK_OPERATION, _subscription);
        List<Environement> environements = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());
        model.put(MARK_ENVIRONMENT_LIST, environements);
        model.put(MARK_CLIENT_LIST, ClientService.getInstance().getEntitiesListByIds(ClientService.getInstance().getIdEntitiesList()));

        if (clientUuid != null && environementUuid != null) {
            List<Resource> resources = ResourceService.getInstance().getEntitiesListByIds(ResourceService.getInstance().getIdEntitiesList());
            List<Resource> environementResources = resources.stream()
                    .filter(resource -> resource.getEnvironement().getUuid().equals(environementUuid)).collect(Collectors.toList());
            Collection<Resource> uniqueByApi = environementResources
                    .stream()
                    .filter(resource -> resource.getApi() != null)
                    .collect(Collectors.toMap(usr -> Set.of(usr.getApi().getUuid()), Function.identity(), (usr1, usr2) -> usr1))
                    .values();
            model.put(MARK_API_LIST, uniqueByApi.stream().map(resource -> resource.getApi()).collect(Collectors.toList()));
            if (apiUuid != null) {
                List<Resource> environementAndApiResources = resources.stream()
                        .filter(resource -> resource.getApi() != null)
                        .filter(resource -> resource.getEnvironement().getUuid().equals(environementUuid) && resource.getApi().getUuid().equals(apiUuid)).collect(Collectors.toList());
                Collection<Resource> uniqueByPlan = environementAndApiResources
                        .stream()
                        .collect(Collectors.toMap(usr -> Set.of(usr.getPlan().getUuid()), Function.identity(), (usr1, usr2) -> usr1))
                        .values();
                model.put(MARK_PLAN_LIST, uniqueByPlan.stream().map(resource -> resource.getPlan()).collect(Collectors.toList()));
            }
        }


        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_CREATE_OPERATION));

        return getPage(PROPERTY_PAGE_API_OPERATIONS, TEMPLATE_MANAGE_API_OPERATIONS, model);
    }

    /**
     * Process the data capture form of a new subscription
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_CREATE_OPERATION)
    public String doCreateSubscription(HttpServletRequest request) throws AccessDeniedException {
        Map<String, String[]> test = request.getParameterMap();
        String clientUuid = request.getParameter(PARAMETER_UUID_APPLICATION);
        String environementUuid = request.getParameter(PARAMETER_UUID_ENVIRONEMENT);
        String apiUuid = request.getParameter(PARAMETER_UUID_API);
        String planUuid = request.getParameter(PARAMETER_UUID_PLAN);

        _subscription = (_subscription != null) ? _subscription : new Subscription();
        _subscription.setClient(new Client());
        _subscription.getClient().setUuid(clientUuid);
        _subscription.setEnvironement(new Environement());
        _subscription.getEnvironement().setUuid(environementUuid);
        _subscription.setPlan(new Plan());
        _subscription.getPlan().setUuid(planUuid);

        if (!SecurityTokenService.getInstance().validate(request, ACTION_CREATE_OPERATION)) {
            throw new AccessDeniedException("Invalid security token");
        }

        // Check constraints
        if (clientUuid == null || environementUuid == null || apiUuid == null || planUuid == null) {
            return redirectView(request, VIEW_CREATE_OPERATION);
        }


        resetListId();

        return redirect(request, "ManageSubscriptions.jsp?infoMsg=" + INFO_OPERATION_CREATED);
    }

    /**
     * Manages the removal form of a subscription whose identifier is in the http request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action(ACTION_CONFIRM_REMOVE_OPERATION)
    public String getConfirmRemoveSubscription(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_OPERATION);
        UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_OPERATION));
        url.addParameter(PARAMETER_ID_OPERATION, uuid);

        String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_OPERATION, url.getUrl(), AdminMessage.TYPE_CONFIRMATION);

        return redirect(request, strMessageUrl);
    }

    /**
     * Handles the removal form of a subscription
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage subscriptions
     */
    @Action(ACTION_REMOVE_OPERATION)
    public String doRemoveSubscription(HttpServletRequest request) {
        final String requestApiUuid = request.getParameter(PARAMETER_UUID_OPERATION);
        final List<String> apiUuids = new ArrayList<>();
        if (requestApiUuid == null) {
            addError(ERROR_RESOURCE_NOT_FOUND);
            return redirectView(request, VIEW_MANAGE_OPERATIONS);
        } else {
            if (requestApiUuid.contains(",")) {
                apiUuids.addAll(List.of(requestApiUuid.split(",")));
            } else {
                apiUuids.add(requestApiUuid);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
                    getService().unpublish(apiUuids, getUser().getEmail(), _configGeneratorService);
                });
        executor.shutdown();

        addInfo(INFO_API_MANAGER_DELETED, getLocale());
        return redirect(request, "ManageOperations.jsp?infoMsg=" + INFO_OPERATION_REMOVED);
    }

    @Action(ACTION_GENERATE_API_MANAGER)
    public String doGenerateApiManager(final HttpServletRequest request) {
        final String requestApiUuid = request.getParameter(PARAMETER_UUID_OPERATION);
        final List<String> apiUuids = new ArrayList<>();
        if (requestApiUuid == null) {
            addError(ERROR_RESOURCE_NOT_FOUND);
            return redirectView(request, VIEW_MANAGE_OPERATIONS);
        } else {
            if (requestApiUuid.contains(",")) {
                apiUuids.addAll(List.of(requestApiUuid.split(",")));
            } else {
                apiUuids.add(requestApiUuid);
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
                    getService().publish(apiUuids, getUser().getEmail(), _configGeneratorService);
                });
        executor.shutdown();

        addInfo(INFO_API_MANAGER_GENERATED, getLocale());
        return redirectView(request, VIEW_MANAGE_OPERATIONS);
    }



    @Action(ACTION_UPDATE_API_MANAGER)
    public String doUpdateApiManager(final HttpServletRequest request) {
        final String requestApiUuid = request.getParameter(PARAMETER_UUID_OPERATION);
        final List<String> apiUuids = new ArrayList<>();
        if (requestApiUuid == null) {
            addError(ERROR_RESOURCE_NOT_FOUND);
            return redirectView(request, VIEW_MANAGE_OPERATIONS);
        } else {
            if (requestApiUuid.contains(",")) {
                apiUuids.addAll(List.of(requestApiUuid.split(",")));
            } else {
                apiUuids.add(requestApiUuid);
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            getService().unpublish(apiUuids, getUser().getEmail(), _configGeneratorService);
            getService().publish(apiUuids, getUser().getEmail(), _configGeneratorService);
        });
        executor.shutdown();

        addInfo(INFO_API_MANAGER_UPDATED, getLocale());
        return redirectView(request, VIEW_MANAGE_OPERATIONS);
    }

}

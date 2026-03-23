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

import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.api.ApiStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
import fr.paris.lutece.plugins.apimanager.business.client.ClientStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.resource.ResourceHome;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.service.*;
import fr.paris.lutece.plugins.apimanager.service.generator.IConfigGeneratorService;
import fr.paris.lutece.plugins.apimanager.web.rest.dto.MeecrogateAckResponse;
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
@Controller(controllerJsp = "ManageClientOperations.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEOPERATIONS)
public class OperationClientJspBean extends AbstractJspBean<String, Client> {

    // Templates
    private static final String TEMPLATE_MANAGE_CLIENT_OPERATIONS = "/admin/plugins/apimanager/operation/manage_client_operations.html";
    // Parameters
    private static final String PARAMETER_ID_OPERATION = "uuid";
    private static final String PARAMETER_VIEW_FROM_CLIENT = "view_from_client";
    private static final String PARAMETER_ENVIRONNEMENT = "environnement";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_UUID_APPLICATION = "uuid_application";
    private static final String PARAMETER_UUID_ENVIRONEMENT = "uuid_environement";
    private static final String PARAMETER_UUID_API = "uuid_api";
    private static final String PARAMETER_UUID_PLAN = "uuid_plan";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_CLIENT_OPERATION = "apimanager.manage_operations.clients.pageTitle";

    // Markers
    private static final String MARK_CLIENT_LIST = "client_list";
    private static final String MARK_OPERATION = "subscription";
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_API_LIST = "api_list";
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_VIEW_FROM_CLIENT = "view_from_client";

    private static final String JSP_MANAGE_OPERATIONS = "jsp/admin/plugins/apimanager/ManageOperations.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_OPERATION = "apimanager.message.confirmRemoveSubscription";

    // Views
    private static final String VIEW_MANAGE_CLIENT_OPERATIONS = "manageClientOperations";
    private static final String VIEW_CREATE_OPERATION = "createOperation";

    // Actions
    private static final String ACTION_CREATE_OPERATION = "createOperation";
    private static final String ACTION_REMOVE_OPERATION = "removeOperation";
    private static final String ACTION_CONFIRM_REMOVE_OPERATION = "confirmRemoveOperation";
    private static final String ACTION_GENERATE_CLIENT = "generateClient";

    // Infos
    private static final String INFO_OPERATION_REMOVED = "apimanager.info.subscription.client.manager.unpublished";
    private static final String INFO_API_MANAGER_GENERATED = "apimanager.info.subscription.client.manager.published";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ERROR_CLIENT_GENERATION = "Error publishing Client";

    // Session variable to store working values
    private Subscription _subscription;
    private List<Client> _clientList;
    private List<String> _listIdResources;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;

    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean(IConfigGeneratorService.BEAN_NAME);

    /**
     * Build the Manage View
     *
     * @param request The HTTP request
     * @return The page
     */
    @View(value = VIEW_MANAGE_CLIENT_OPERATIONS, defaultView = true)
    public String getManageClientOperations(HttpServletRequest request) {

        // new search only if in pagination mode
        if (request.getParameter(AbstractPaginator.PARAMETER_PAGE_INDEX) == null) {
            _optionOrderBy = request.getParameter(PARAMETER_SEARCH_ORDER_BY);
            _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest(request);
            final HashMap<String, String> criterias = new HashMap<>(_mapFilterCriteria);
            if (!_mapFilterCriteria.containsKey(FILTER_DISPLAY_ARCHIVED)) {
                criterias.put(FILTER_ARCHIVED, Boolean.FALSE.toString());
            }
            _listIdResources = ResourceService.getInstance().getIdEntitiesList(criterias);

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator();
        }


        _clientList = ClientService.getInstance().getEntitiesListByIds(ClientService.getInstance().getIdEntitiesList());

        for (Client client : _clientList) {
            List<Subscription> subscriptionByApi = new ArrayList<>();
            List<Subscription> currentSubscriptions = SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByClient(client.getUuid()));
            Map<String, List<Subscription>> apiSubscriptions = currentSubscriptions.stream().filter(subscription -> subscription.getApi() != null).collect(Collectors.groupingBy(p -> p.getApi().getUuid()));
            for (String subscriptionApi : apiSubscriptions.keySet()) {
                if(apiSubscriptions.get(subscriptionApi) != null && !apiSubscriptions.get(subscriptionApi).isEmpty()){
                    apiSubscriptions.get(subscriptionApi).get(0).setApi(ApiHome.findByPrimaryKey(subscriptionApi).orElse(apiSubscriptions.get(subscriptionApi).get(0).getApi()));
                    subscriptionByApi.add(apiSubscriptions.get(subscriptionApi).get(0));
                }
            }
            client.setSubscriptionList(currentSubscriptions);
        }

        Map<String, Object> model = getPaginatedListModel(request, MARK_CLIENT_LIST, _clientList.stream().map(Client::getUuid).collect(Collectors.toList()), JSP_MANAGE_OPERATIONS);

        addSearchParameters(model, _mapFilterCriteria); // allow the persistence of search values in inputs search bar inputs
        model.put(MARK_SHOW_GENERATE_BUTTON, (_configGeneratorService != null));
        model.put(MARK_ENVIRONMENT_LIST, environmentList);
        model.put(MARK_VIEW_FROM_CLIENT, Boolean.parseBoolean(Optional.ofNullable(request.getParameter(PARAMETER_VIEW_FROM_CLIENT)).orElse("false")));

        return getPage(PROPERTY_PAGE_CLIENT_OPERATION, TEMPLATE_MANAGE_CLIENT_OPERATIONS, model);

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
    List<Client> getItemsFromIds(List<String> listIds) {
        // keep original order
        return _clientList.stream().sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid()))).collect(Collectors.toList());
    }


    @Override
    protected ClientService getService() {
        return ClientService.getInstance();
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage() {
        return AppPropertiesService.getPropertyInt(PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50);
    }

    /**
     * reset the _listIdSubscriptions list
     */
    public void resetListId() {
        _listIdResources = new ArrayList<>();
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

        return getPage(PROPERTY_PAGE_CLIENT_OPERATION, TEMPLATE_MANAGE_CLIENT_OPERATIONS, model);
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
        String uuid = request.getParameter(PARAMETER_ID_OPERATION);

        try {
            final Client client = ClientHome.findByPrimaryKey(uuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));

            List<Subscription> subscriptions = new ArrayList<>();
            subscriptions.addAll(SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByClient(client.getUuid())));

            List<Environement> availableEnvs = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());

            getService().addNewHistory(client.getUuid(), HistoryTypeEnum.UNPUBLISH, getUser().getEmail(), "CLIENT " + client.getName());
            client.setStatus(ClientStatusEnum.UNPUBLISHING.name());
            ClientService.getInstance().update(client, getUser().getEmail());


            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.submit(() -> {
                try {
                    for (Environement envir : availableEnvs) {
                        _configGeneratorService.deleteOauth2Client(client,
                                envir, getUser().getEmail());

                        ClientService.getInstance().updateStatus(uuid, ClientStatusEnum.UNPUBLISHED.name(), getUser().getEmail());

                        /*MeecrogateAckResponse ackResponse = MeecrogateGatewayService.getInstance().getStatus(envir.getName());
                        if(ackResponse!=null && ackResponse.getDeployOauth2Status()!=null && ackResponse.getDeployOauth2Status().equals("updated")){
                            ClientService.getInstance().updateStatus(uuid, ClientStatusEnum.UNPUBLISHED.name(), getUser().getEmail());
                        }else{
                            ClientService.getInstance().updateStatus(uuid, ClientStatusEnum.DEPLOY_ERROR.name(), getUser().getEmail());
                        }*/
                    }
                } catch (Exception e) {
                    ClientService.getInstance().updateStatus(uuid, ClientStatusEnum.UNPUBLISH_ERROR.name(), getUser().getEmail());
                }
            });
            executor.shutdown();

        } catch (final AppException e) {
            addError(ERROR_CLIENT_GENERATION);
            addError(e.getMessage());
            return redirectView(request, VIEW_MANAGE_CLIENT_OPERATIONS);
        }

        addInfo(INFO_OPERATION_REMOVED, getLocale());
        resetListId();

        return redirect(request, "ManageClientOperations.jsp?infoMsg=" + INFO_OPERATION_REMOVED);
    }

    @Action(ACTION_GENERATE_CLIENT)
    public String doGenerateClient(final HttpServletRequest request) {
        final String clientUuid = request.getParameter(PARAMETER_ID_OPERATION);
        if (clientUuid == null) {
            addError(ERROR_RESOURCE_NOT_FOUND);
            return redirectView(request, VIEW_MANAGE_CLIENT_OPERATIONS);
        }
        final Client client = ClientHome.findByPrimaryKey(clientUuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));

        final String comment = request.getParameter(PARAMETER_COMMENT);

        try {

            List<Subscription> subscriptions = new ArrayList<>();
            subscriptions.addAll(SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByClient(client.getUuid())));

            List<Environement> availableEnvs = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());

            getService().addNewHistory(client.getUuid(), HistoryTypeEnum.PUBLISH, getUser().getEmail(), "CLIENT " + client.getName());
            client.setStatus(ClientStatusEnum.PUBLISHING.name());
            ClientService.getInstance().update(client, getUser().getEmail());

            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.submit(() -> {
                try {
                    for (Environement envir : availableEnvs) {
                        _configGeneratorService.generateOauth2Client(client,
                                envir, comment, getUser().getEmail());

                        ClientService.getInstance().updateStatus(clientUuid, ClientStatusEnum.PUBLISHED.name(), getUser().getEmail());

                        /*MeecrogateAckResponse ackResponse = MeecrogateGatewayService.getInstance().getStatus(envir.getName());
                        if(ackResponse!=null && ackResponse.getDeployOauth2Status()!=null && ackResponse.getDeployOauth2Status().equals("updated")){
                            ClientService.getInstance().updateStatus(clientUuid, ClientStatusEnum.PUBLISHED.name(), getUser().getEmail());
                        }else{
                            ClientService.getInstance().updateStatus(clientUuid, ClientStatusEnum.DEPLOY_ERROR.name(), getUser().getEmail());
                        }*/
                    }
                } catch (Exception e) {
                    ClientService.getInstance().updateStatus(clientUuid, ClientStatusEnum.PUBLISH_ERROR.name(), getUser().getEmail());
                }
            });
            executor.shutdown();

        } catch (final AppException e) {
            addError(ERROR_CLIENT_GENERATION);
            addError(e.getMessage());
            return redirectView(request, VIEW_MANAGE_CLIENT_OPERATIONS);
        }

        addInfo(INFO_API_MANAGER_GENERATED, getLocale());
        return redirectView(request, VIEW_MANAGE_CLIENT_OPERATIONS);
    }

}

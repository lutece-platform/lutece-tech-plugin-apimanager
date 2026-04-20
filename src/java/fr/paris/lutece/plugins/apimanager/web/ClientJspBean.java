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
import fr.paris.lutece.plugins.apimanager.business.client.*;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionHome;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionStatusEnum;
import fr.paris.lutece.plugins.apimanager.service.ApiService;
import fr.paris.lutece.plugins.apimanager.service.ClientService;
import fr.paris.lutece.plugins.apimanager.service.EnvironementService;
import fr.paris.lutece.plugins.apimanager.service.PlanService;
import fr.paris.lutece.plugins.apimanager.service.ResourceService;
import fr.paris.lutece.plugins.apimanager.service.SubscriptionService;
import fr.paris.lutece.plugins.apimanager.service.generator.IConfigGeneratorService;
import fr.paris.lutece.plugins.apimanager.service.utils.PasswordUtils;
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
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGECLIENTS;

/**
 * This class provides the user interface to manage Client features ( manage, create, modify, remove )
 */
@Controller(controllerJsp = "ManageClients.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGECLIENTS)
public class ClientJspBean extends AbstractJspBean<String, Client> {
    // Templates
    private static final String TEMPLATE_MANAGE_CLIENTS = "/admin/plugins/apimanager/client/manage_clients.html";
    private static final String TEMPLATE_CREATE_CLIENT = "/admin/plugins/apimanager/client/create_client.html";
    private static final String TEMPLATE_MODIFY_CLIENT = "/admin/plugins/apimanager/client/modify_client.html";
    private static final String TEMPLATE_GENERATE_NEW_SECRETS = "/admin/plugins/apimanager/client/generate_new_client_secrets.html";

    // Parameters
    private static final String PARAMETER_ID_CLIENT = "uuid";
    private static final String PARAMETER_SELECTED_TAGS = "selected_tags";
    private static final String PARAMETER_CREATE_USECASE = "create_usecase";
    private static final String PARAMETER_INFO_MSG = "infoMsg";
    private static final String PARAMETER_ENVIRONNEMENT = "environnement";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_RELOAD = "reload";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CLIENTS = "apimanager.manage_clients.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CLIENT = "apimanager.modify_client.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CLIENT = "apimanager.create_client.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_GENERATE_NEW_SECRETS = "apimanager.generate_new_secrets.title";

    // Markers
    private static final String MARK_CLIENT_LIST = "client_list";
    private static final String MARK_CLIENT = "client";
    private static final String MARK_CURRENT_SUBSCRIPTION_ROW = "current_subscription_row";
    private static final String MARK_PREFIX_API_SUBSCRIPTION_ROW = "api_selection_";
    private static final String MARK_PREFIX_SUBSCRIPTION_ROW = "subscription_uuid_";
    private static final String MARK_PREFIX_ENVIRONEMENT_SUBSCRIPTION_ROW = "environement_selection_";
    private static final String MARK_PREFIX_PLAN_SUBSCRIPTION_ROW = "plan_selection_";
    private static final String MARK_SUBSCRIPTION_LIST = "subscription_list";
    private static final String MARK_API_LIST = "api_list";
    private static final String MARK_SELECTED_API = "selected_api";
    private static final String MARK_SELECTED_ENVIRONEMENT = "selected_environement";
    private static final String MARK_SELECTED_PLAN = "selected_plan";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_SHOW_GENERATE_BUTTON = "show_generate_button";
    private static final String MARK_TAG_LIST = "tag_list";
    private static final String MARK_SELECTED_TAG_LIST = "selected_tag_list";

    private static final String JSP_MANAGE_CLIENTS = "jsp/admin/plugins/apimanager/ManageClients.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_ARCHIVE_CLIENT = "apimanager.message.confirmArchiveClient";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.client.attribute.";

    // Views
    private static final String VIEW_MANAGE_CLIENTS = "manageClients";
    private static final String VIEW_CREATE_CLIENT = "createClient";
    private static final String VIEW_MODIFY_CLIENT = "modifyClient";
    private static final String VIEW_GENERATE_NEW_SECRETS = "generateNewSecrets";

    // Actions
    private static final String ACTION_CREATE_CLIENT = "createClient";
    private static final String ACTION_MODIFY_CLIENT = "modifyClient";
    private static final String ACTION_ARCHIVE_CLIENT = "archiveClient";
    private static final String ACTION_CONFIRM_ARCHIVE_CLIENT = "confirmArchiveClient";
    private static final String ACTION_GENERATE_OAUTH2 = "generateOauth2";
    private static final String ACTION_GENERATE_NEW_SECRETS = "generateNewSecrets";

    // Infos
    private static final String INFO_CLIENT_CREATED = "apimanager.info.client.created";
    private static final String INFO_CLIENT_UPDATED = "apimanager.info.client.updated";
    private static final String INFO_CLIENT_ARCHIVED = "apimanager.info.client.archived";
    private static final String INFO_CLIENT_OAUTH2_GENERATED = "apimanager.info.client.oauth2.published";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ERROR_CLIENT_OAUTH2_GENERATION = "Error publishing OAuth2 Client";
    private static final String ERROR_HASHING_SECRETS = "Error wihle hashing secrets";

    // Session variable to store working values
    private Client _client;
    private List<Subscription> _subscriptions;
    private List<String> _listIdClients;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;

    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean(IConfigGeneratorService.BEAN_NAME);

    /**
     * Build the Manage View
     *
     * @param request The HTTP request
     * @return The page
     */
    @View(value = VIEW_MANAGE_CLIENTS, defaultView = true)
    public String getManageClients(HttpServletRequest request) {
        final String infoMsg = request.getParameter(PARAMETER_INFO_MSG);
        final Map<String, Object> model = new HashMap<>( );
        if (infoMsg != null) {
            addInfo(infoMsg, getLocale());
            return redirectView(request, VIEW_MANAGE_CLIENTS);
        }
        if ("true".equals(request.getParameter(PARAMETER_RELOAD))) {
            return getPage(PROPERTY_PAGE_TITLE_MANAGE_CLIENTS, TEMPLATE_MANAGE_CLIENTS, Map.of());
        }
        _client = null;

        // new search only if in pagination mode
        if (request.getParameter(AbstractPaginator.PARAMETER_PAGE_INDEX) == null) {
            // if sorting request : new search with the existing filter criteria, ordered
            // example of order by parameter : orderby=name
            if (StringUtils.isNotBlank((String) request.getParameter(PARAMETER_SEARCH_ORDER_BY))) {

                String strOrderByColumn = (String) request.getParameter(PARAMETER_SEARCH_ORDER_BY);
                String strSortMode = getSortMode();

                _listIdClients = getService().getIdEntitiesList(_mapFilterCriteria, strOrderByColumn, strSortMode);

            } else {
                // reload the filter criteria and search
                _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest(request);
                final HashMap<String, String> criterias = new HashMap<>(_mapFilterCriteria);
                if (!_mapFilterCriteria.containsKey(FILTER_DISPLAY_ARCHIVED)) {
                    // DEFAULT : display only non-archived clients - we copy the map to add the criteria so that the "archived" filter doesn't show up on the
                    // page
                    criterias.put(FILTER_ARCHIVED, Boolean.FALSE.toString());
                }
                _listIdClients = getService().getIdEntitiesList(criterias);
            }

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator();
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

            _listIdClients = getService( ).getClientsByTags(selectedTags);
            model.put(MARK_SELECTED_TAG_LIST, selectedTags);
        }


        model.putAll(getPaginatedListModel(request, MARK_CLIENT_LIST, _listIdClients, JSP_MANAGE_CLIENTS));

        model.put(MARK_TAG_LIST, getService().getAvailableTags(_listIdClients));

        addSearchParameters(model, _mapFilterCriteria); // allow the persistence of search values in inputs search bar inputs
        model.put(MARK_SHOW_GENERATE_BUTTON, (_configGeneratorService != null));

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_CLIENTS, TEMPLATE_MANAGE_CLIENTS, model);

    }

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Client> getItemsFromIds(List<String> listIds) {
        final List<Client> listClient = getService().getEntitiesListByIds(listIds);
        // keep original order
        return listClient.stream().sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid()))).collect(Collectors.toList());
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
     * reset the _listIdClients list
     */
    public void resetListId() {
        _listIdClients = new ArrayList<>();
    }

    /**
     * Returns the form to create a client
     *
     * @param request The Http request
     * @return the html code of the client form
     */
    @View(VIEW_CREATE_CLIENT)
    public String getCreateClient(HttpServletRequest request) {

        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, Object> model = getModel();

        model.putAll(parameters);
        _client = (_client != null) ? _client : new Client();
        populate(_client, request, getLocale());

        _subscriptions = (_subscriptions != null) ? _subscriptions : new ArrayList<Subscription>();
        populateSubscriptions(request);


        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);
        if (usecase != null && usecase.equals("add_subscription"))
            _subscriptions.add(new Subscription());

        Integer subscriptionIndex = null;

        if (request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW) != null && !request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW).isEmpty())
            subscriptionIndex = Integer.parseInt(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));

        populateRow(request,subscriptionIndex,model);

        model.put(MARK_CLIENT, _client);
        model.put(MARK_SUBSCRIPTION_LIST, _subscriptions);
        if (subscriptionIndex == null || usecase.equals("delete_subscription")) {
            model.put(MARK_CURRENT_SUBSCRIPTION_ROW, !_subscriptions.isEmpty() ? _subscriptions.size() - 1 : 0);
        } else {
            model.put(MARK_CURRENT_SUBSCRIPTION_ROW, subscriptionIndex);
        }
        model.put(MARK_API_LIST, ApiService.getInstance().getEntitiesListByIds(ApiService.getInstance().getIdEntitiesList()));
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_CREATE_CLIENT));

        return getPage(PROPERTY_PAGE_TITLE_CREATE_CLIENT, TEMPLATE_CREATE_CLIENT, model);
    }

    private void populateRow(HttpServletRequest request,Integer subscriptionIndex,Map<String, Object> model ){
        if (request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW) != null && !request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW).isEmpty())
            subscriptionIndex = Integer.parseInt(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));

        if (subscriptionIndex != null) {
            String selectedApiUuid = request.getParameter(MARK_PREFIX_API_SUBSCRIPTION_ROW + subscriptionIndex);
            String selectedEnvironementUuid = request.getParameter(MARK_PREFIX_ENVIRONEMENT_SUBSCRIPTION_ROW + subscriptionIndex);
            String selectedPlanUuid = request.getParameter(MARK_PREFIX_PLAN_SUBSCRIPTION_ROW + subscriptionIndex);

            // verifie s'il y a deja des souscription dans les parameters
            // sinon on creer un souscription vide
            //si use case ajouter resource
            // on verifie que la resource en cours est completement configuré et on augmente l'indice qu'on peut valider avec l'indice du front

            if (selectedApiUuid != null && !selectedApiUuid.isEmpty()) {
                //recuper l'api
                model.put(MARK_SELECTED_API, ApiService.getInstance().getEntitiesListByIds(Arrays.asList(selectedApiUuid)).stream().findFirst().orElse(null));
                List<Resource> selectedResources = ResourceService.getInstance().getResourcesByApiUuid(selectedApiUuid);
                List<String> availableEnvironementUuids = selectedResources.stream().map(selectedResource -> selectedResource.getEnvironement().getUuid()).distinct().collect(Collectors.toList());

                model.put(MARK_ENVIRONMENT_LIST, EnvironementService.getInstance().getEntitiesListByIds(availableEnvironementUuids));
                if (selectedEnvironementUuid != null && !selectedEnvironementUuid.isEmpty()) {
                    //recuper env et plan
                    model.put(MARK_SELECTED_ENVIRONEMENT, EnvironementService.getInstance().getEntitiesListByIds(Arrays.asList(selectedEnvironementUuid)).stream().findFirst().orElse(null));
                    List<String> availablePLANUuids = selectedResources.stream().map(selectedResource -> selectedResource.getPlan().getUuid()).distinct().collect(Collectors.toList());

                    model.put(MARK_PLAN_LIST, PlanService.getInstance().getEntitiesListByIds(availablePLANUuids));
                    if (selectedPlanUuid != null && !selectedPlanUuid.isEmpty()) {
                        model.put(MARK_SELECTED_PLAN, PlanService.getInstance().getEntitiesListByIds(Arrays.asList(selectedPlanUuid)).stream().findFirst().orElse(null));
                    }
                }
            }
        }

        // We don't hash the new secrets yet, so that we can display them to the user
        _client.setSecretList(EnvironementHome.getEnvironementsList().stream().map(env -> {
            final ClientSecret clientSecret = new ClientSecret();
            clientSecret.setSecret(PasswordUtils.generateSecurePassword());
            clientSecret.setEnvironnement(env);
            return clientSecret;
        }).collect(Collectors.toList()));
    }

    private void populateSubscriptions(HttpServletRequest request) {
        this._subscriptions = new ArrayList<>();

        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);
        final Map<String, String[]> subscriptionApis;
        final Map<String, String[]> subscriptionUuids;
        final Map<String, String[]> subscriptionEnvironements;
        final Map<String, String[]> subscriptionPlans;

        subscriptionUuids = request.getParameterMap().entrySet().stream().filter(stringEntry -> stringEntry.getKey().startsWith(MARK_PREFIX_SUBSCRIPTION_ROW))
                .collect(Collectors.toMap(entry -> entry.getKey().replace(MARK_PREFIX_SUBSCRIPTION_ROW, ""), Map.Entry::getValue));

        if (request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW) != null && !request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW).isEmpty()) {
            subscriptionApis = request.getParameterMap().entrySet().stream().filter(stringEntry -> stringEntry.getKey().startsWith(MARK_PREFIX_API_SUBSCRIPTION_ROW))
                    .collect(Collectors.toMap(entry -> entry.getKey().replace(MARK_PREFIX_API_SUBSCRIPTION_ROW, ""), Map.Entry::getValue));

            subscriptionEnvironements = request.getParameterMap().entrySet().stream().filter(stringEntry -> stringEntry.getKey().startsWith(MARK_PREFIX_ENVIRONEMENT_SUBSCRIPTION_ROW))
                    .collect(Collectors.toMap(entry -> entry.getKey().replace(MARK_PREFIX_ENVIRONEMENT_SUBSCRIPTION_ROW, ""), Map.Entry::getValue));

            subscriptionPlans = request.getParameterMap().entrySet().stream().filter(stringEntry -> stringEntry.getKey().startsWith(MARK_PREFIX_PLAN_SUBSCRIPTION_ROW))
                    .collect(Collectors.toMap(entry -> entry.getKey().replace(MARK_PREFIX_PLAN_SUBSCRIPTION_ROW, ""), Map.Entry::getValue));

            if (usecase != null && usecase.equals("delete_subscription")) {
                subscriptionUuids.remove(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));
                subscriptionApis.remove(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));
                subscriptionEnvironements.remove(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));
                subscriptionPlans.remove(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));
            }


            Subscription currentSubscription = null;
            for (String apiIndex : subscriptionApis.keySet()) {
                currentSubscription = new Subscription();
                if(subscriptionUuids.get(apiIndex) != null && subscriptionUuids.get(apiIndex).length >0)
                    currentSubscription.setUuid(subscriptionUuids.get(apiIndex)[0]);
                currentSubscription.setApi(ApiService.getInstance().getEntitiesListByIds(List.of(subscriptionApis.get(apiIndex))).stream().findFirst().orElse(null));
                if (subscriptionEnvironements.get(apiIndex) != null) {
                    currentSubscription.setEnvironement(EnvironementService.getInstance().getEntitiesListByIds(List.of(subscriptionEnvironements.get(apiIndex))).stream().findFirst().orElse(null));
                }
                if (subscriptionPlans.get(apiIndex) != null) {
                    currentSubscription.setPlan(PlanService.getInstance().getEntitiesListByIds(List.of(subscriptionPlans.get(apiIndex))).stream().findFirst().orElse(null));
                }
                this._subscriptions.add(currentSubscription);
            }
        }
    }

    /**
     * Process the data capture form of a new client
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_CREATE_CLIENT)
    public String doCreateClient(HttpServletRequest request) throws AccessDeniedException {
        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);

        if (usecase != null && usecase.equals("create")) {
            populate(_client, request, getLocale());
            populateSubscriptions(request);

            _client.setTags(Arrays.stream(Optional.ofNullable(request.getParameterValues(PARAMETER_SELECTED_TAGS)).orElse(new String[0]))
                    .collect(Collectors.toList()));
            _client.setSubscriptionList(_subscriptions);

            if (!SecurityTokenService.getInstance().validate(request, ACTION_CREATE_CLIENT)) {
                throw new AccessDeniedException("Invalid security token");
            }

            // Check constraints
            if (!validateBean(_client, VALIDATION_ATTRIBUTES_PREFIX)) {
                return getCreateClient(request);
            }

            _client.setStatus(ClientStatusEnum.NEW.name());
            getService().create(_client, getUser().getEmail());

            if(!_client.getSubscriptionList().isEmpty()){
                for (final Subscription subscription : _subscriptions) {
                    subscription.setClient(_client);
                    List<Resource> resources = ResourceService.getInstance().getResourcesByAPIUiidPlanUuidEnvironementUUID(subscription.getApi().getUuid(), subscription.getPlan().getUuid(), subscription.getEnvironement().getUuid());
                    for(Resource resource : resources){
                        subscription.setResource(resource);
                        subscription.setStatus(SubscriptionStatusEnum.NEW.name());
                        SubscriptionService.getInstance().create(subscription,getUser().getEmail());
                    }
                }
            }

            addInfo(INFO_CLIENT_CREATED, getLocale());
            resetListId();

            return generateSecrets(request);
        } else {
            return getCreateClient(request);
        }
    }

    /**
     * Manages the removal form of a client whose identifier is in the http request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action(ACTION_CONFIRM_ARCHIVE_CLIENT)
    public String getConfirmArchiveClient(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_CLIENT);
        UrlItem url = new UrlItem(getActionUrl(ACTION_ARCHIVE_CLIENT));
        url.addParameter(PARAMETER_ID_CLIENT, uuid);

        String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_ARCHIVE_CLIENT, url.getUrl(), AdminMessage.TYPE_CONFIRMATION);

        return redirect(request, strMessageUrl);
    }

    /**
     * Handles the removal form of a client
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage clients
     */
    @Action(ACTION_ARCHIVE_CLIENT)
    public String doArchiveClient(HttpServletRequest request) {
        final String clientUuid = request.getParameter(PARAMETER_ID_CLIENT);
        final Client client = ClientHome.findByPrimaryKey(clientUuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));

        // DELETE PUBLISHED CONFIG
        // delete oauth2 client for all env
        //environmentList.forEach(env -> _configGeneratorService.deleteOauth2Client(client, env.getUuid(), getUser().getEmail()));
        // get all client subscriptions
        SubscriptionService.getInstance().getIdEntitiesList(Map.of("uuid_client", clientUuid)).forEach(subscriptionUuid -> {
            SubscriptionHome.findByPrimaryKey(subscriptionUuid).ifPresent(subscription -> {
                // for each subscription, send a delete request and archive the subscription
               /* _configGeneratorService.deleteSubscription(client, subscription.getResource().getPlan(),
                        ResourceService.getInstance().getResourcesByPlanUuid(subscription.getResource().getPlan().getUuid()),
                        InstanceService.getInstance().getEntitiesListByIds(
                                InstanceService.getInstance().getIdInstancesListLinkedToResourceUuid(subscription.getResource().getApi().getUuid())),
                        subscription.getEnvironement().getUuid(), getUser().getEmail());*/
                if (!subscription.getArchived()) {
                    SubscriptionService.getInstance().archive(subscriptionUuid, getUser().getEmail());
                }
            });
        });

        getService().archive(clientUuid, getUser().getEmail());
        addInfo(INFO_CLIENT_ARCHIVED, getLocale());
        resetListId();

        return redirectView(request, VIEW_MANAGE_CLIENTS);
    }

    /**
     * Returns the form to update info about a client
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View(VIEW_MODIFY_CLIENT)
    public String getModifyClient(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_CLIENT);
        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);

        Map<String, String[]> parameters = request.getParameterMap();

        if (uuid == null) {
            return redirectView(request, VIEW_MANAGE_CLIENTS);
        }

        if (_client == null || !uuid.equals(_client.getUuid())) {
            Optional<Client> optClient = ClientHome.findByPrimaryKey(uuid);
            _client = optClient.orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        }

        _subscriptions = new ArrayList<>();
        populateSubscriptions(request);

        if(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW) == null || request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW).isEmpty()){
            List<String> idSubscriptionsByClient = SubscriptionService.getInstance().getIdSubscriptionsByClient(uuid);
            List<Subscription> entitiesListByIds = SubscriptionService.getInstance().getEntitiesListByIds(idSubscriptionsByClient);
            _subscriptions.addAll(entitiesListByIds);
            _subscriptions = new ArrayList<>(_subscriptions
                    .stream()
                    .collect(Collectors.toMap(usr -> Set.of(usr.getResource().getApi().getUuid(), usr.getEnvironement().getUuid(), usr.getClient().getUuid(), usr.getResource().getPlan().getUuid()), Function.identity(), (usr1, usr2) -> usr1))
                    .values());
        }

        if (usecase != null && usecase.equals("add_subscription"))
            _subscriptions.add(new Subscription());


        Map<String, Object> model = getModel();

        model.putAll(parameters);


        Integer subscriptionIndex = null;

        if (request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW) != null && !request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW).isEmpty())
            subscriptionIndex = Integer.parseInt(request.getParameter(MARK_CURRENT_SUBSCRIPTION_ROW));

        populateRow(request,subscriptionIndex,model);

        model.put(MARK_CLIENT, _client);
        model.put(MARK_SUBSCRIPTION_LIST, _subscriptions);
        if (subscriptionIndex == null || usecase.equals("delete_subscription")) {
            model.put(MARK_CURRENT_SUBSCRIPTION_ROW, !_subscriptions.isEmpty() ? _subscriptions.size() - 1 : 0);

            if(!_subscriptions.isEmpty()){
                Subscription selectedSubscription = _subscriptions.get(_subscriptions.size() - 1);
                if(selectedSubscription != null){
                    Api selectedSubscriptionApi = selectedSubscription.getApi();
                    Api selectedApi = ApiService.getInstance().getEntitiesListByIds(Arrays.asList(selectedSubscriptionApi.getUuid())).stream().findFirst().orElse(null);
                    model.put(MARK_SELECTED_API, selectedApi);
                    List<Resource> selectedResources = ResourceService.getInstance().getResourcesByApiUuid(selectedApi.getUuid());
                    List<String> availableEnvironementUuids = selectedResources.stream().map(selectedResource -> selectedResource.getEnvironement().getUuid()).distinct().collect(Collectors.toList());

                    List<String> availablePLANUuids = selectedResources.stream().map(selectedResource -> selectedResource.getPlan().getUuid()).distinct().collect(Collectors.toList());

                    model.put(MARK_PLAN_LIST, PlanService.getInstance().getEntitiesListByIds(availablePLANUuids));

                    model.put(MARK_ENVIRONMENT_LIST, EnvironementService.getInstance().getEntitiesListByIds(availableEnvironementUuids));
                    model.put(MARK_SELECTED_ENVIRONEMENT, _subscriptions.get(_subscriptions.size() - 1).getEnvironement());
                    model.put(MARK_SELECTED_PLAN, _subscriptions.get(_subscriptions.size() - 1).getPlan());
                }
            }

        } else {
            model.put(MARK_CURRENT_SUBSCRIPTION_ROW, subscriptionIndex);
        }
        model.put(MARK_API_LIST, ApiService.getInstance().getEntitiesListByIds(ApiService.getInstance().getIdEntitiesList()));
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_MODIFY_CLIENT));

        return getPage(PROPERTY_PAGE_TITLE_MODIFY_CLIENT, TEMPLATE_MODIFY_CLIENT, model);
    }

    /**
     * Process the change form of a client
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_MODIFY_CLIENT)
    public String doModifyClient(HttpServletRequest request) throws AccessDeniedException {
        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);

        if (usecase != null && usecase.equals("create")) {
            populate(_client, request, getLocale());
            populateSubscriptions(request);

            _client.setTags(Arrays.stream(Optional.ofNullable(request.getParameterValues(PARAMETER_SELECTED_TAGS)).orElse(new String[0]))
                    .collect(Collectors.toList()));
            _client.setSubscriptionList(_subscriptions);

            if (!SecurityTokenService.getInstance().validate(request, ACTION_MODIFY_CLIENT)) {
                throw new AccessDeniedException("Invalid security token");
            }

            // Check constraints
            if (!validateBean(_client, VALIDATION_ATTRIBUTES_PREFIX)) {
                return getModifyClient(request);
            }


            getService().update(_client, getUser().getEmail());

            // check the current subscriptions
            List<String> currentClientSubscriptionUuids = SubscriptionService.getInstance().getIdSubscriptionsByClient(_client.getUuid());
            List<Subscription> existingClientSubscription  = SubscriptionService.getInstance().getEntitiesListByIds(currentClientSubscriptionUuids);
            // récupération des resources déjà souscrites
            if(!_client.getSubscriptionList().isEmpty()){
                for (final Subscription subscription : _subscriptions) {
                    subscription.setClient(_client);
                    List<Resource> resourceToProcess = ResourceService.getInstance().getResourcesByAPIUiidPlanUuidEnvironementUUID(subscription.getApi().getUuid(), subscription.getPlan().getUuid(), subscription.getEnvironement().getUuid());
                    resourceToProcess.stream()
                            .filter(resource -> existingClientSubscription.stream()
                                    .noneMatch(sub ->
                                            Objects.equals(sub.getApi().getUuid(), resource.getApi().getUuid())
                                                    && Objects.equals(sub.getEnvironement().getUuid(), resource.getEnvironement().getUuid())
                                                    && Objects.equals(sub.getPlan().getUuid(), resource.getPlan().getUuid())
                                                    && Objects.equals(sub.getResource().getUuid(), resource.getUuid())
                                    )
                            )
                            .forEach(resource -> {
                                subscription.setResource(resource);
                                subscription.setStatus(SubscriptionStatusEnum.NEW.name());
                                SubscriptionService.getInstance().create( subscription, getUser( ).getEmail( ) );
                            });
                }

                // check if some subscriptions have been deleted
                currentClientSubscriptionUuids = SubscriptionService.getInstance().getIdSubscriptionsByClient(_client.getUuid());
                List<String> requestedAndCreatedUuidSubscriptions = _subscriptions.stream()
                        .map(subscription -> ResourceService.getInstance().getResourcesByAPIUiidPlanUuidEnvironementUUID(subscription.getApi().getUuid(), subscription.getPlan().getUuid(), subscription.getEnvironement().getUuid()))
                        .flatMap(Collection::stream)
                        .map(resource -> SubscriptionService.getInstance().getIdSubscriptionsByResourceAndEnvironementAndClient(resource.getUuid(), resource.getEnvironement().getUuid(), _client.getUuid()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                currentClientSubscriptionUuids.removeAll(requestedAndCreatedUuidSubscriptions);
                for(String subscriptionUuid : currentClientSubscriptionUuids){
                    SubscriptionService.getInstance().delete(subscriptionUuid,getUser().getEmail());
                }

            }else{
                for(String subscriptionUuid : currentClientSubscriptionUuids){
                    SubscriptionService.getInstance().delete(subscriptionUuid,getUser().getEmail());
                }
            }

            addInfo(INFO_CLIENT_UPDATED, getLocale());
            resetListId();

            return redirectView(request, VIEW_MANAGE_CLIENTS);
        } else {
            return getModifyClient(request);
        }
    }

    @Action(ACTION_GENERATE_OAUTH2)
    public String doGenerateOauth2(final HttpServletRequest request) {
        final String uuid = request.getParameter(PARAMETER_ID_CLIENT);
        if (uuid == null) {
            return redirectView(request, VIEW_MANAGE_CLIENTS);
        }
        final String env = request.getParameter(PARAMETER_ENVIRONNEMENT);
        _client = ClientService.getInstance().getClientById(uuid, Optional.of(env)).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        try {
            getService().addNewHistory(_client.getUuid(), HistoryTypeEnum.GENERATE, getUser().getEmail(), "CLIENT " + _client.getName());
        } catch (final AppException e) {
            addError(ERROR_CLIENT_OAUTH2_GENERATION);
            addError(e.getMessage());
            return redirectView(request, VIEW_MANAGE_CLIENTS);
        }
        addInfo(INFO_CLIENT_OAUTH2_GENERATED, getLocale());
        return redirectView(request, VIEW_MANAGE_CLIENTS);
    }

    private String generateSecrets(final HttpServletRequest request){

        // We don't hash the new secrets yet, so that we can display them to the user
        _client.setSecretList(EnvironementHome.getEnvironementsList().stream().map(env -> {
            final ClientSecret clientSecret = new ClientSecret();
            clientSecret.setSecret(PasswordUtils.generateSecurePassword());
            clientSecret.setEnvironnement(env);
            return clientSecret;
        }).collect(Collectors.toList()));

        final Map<String, Object> model = getModel();
        model.put(MARK_CLIENT, _client);
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_GENERATE_NEW_SECRETS));

        return getPage(PROPERTY_PAGE_TITLE_GENERATE_NEW_SECRETS, TEMPLATE_GENERATE_NEW_SECRETS, model);
    }

    @View(VIEW_GENERATE_NEW_SECRETS)
    public String getGenerateNewSecrets(final HttpServletRequest request) {
        final String uuid = request.getParameter(PARAMETER_ID_CLIENT);
        if (uuid == null) {
            return redirectView(request, VIEW_MANAGE_CLIENTS);
        }

        if (_client == null || !uuid.equals(_client.getUuid())) {
            final Optional<Client> optClient = ClientHome.findByPrimaryKey(uuid);
            _client = optClient.orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        }

        return generateSecrets(request);
    }

    @Action(ACTION_GENERATE_NEW_SECRETS)
    public String doGenerateNewSecrets(final HttpServletRequest request) throws AccessDeniedException {
        if (!SecurityTokenService.getInstance().validate(request, ACTION_GENERATE_NEW_SECRETS)) {
            throw new AccessDeniedException("Invalid security token");
        }

        // Hashing secrets
        try {
            for (final ClientSecret clientSecret : _client.getSecretList()) {
                clientSecret.setSecret(PasswordUtils.hashPassword(clientSecret.getSecret()));
            }
        } catch (final Exception e) {
            this.addError(ERROR_HASHING_SECRETS);
            this.addError(e.getMessage());
            return redirectView(request, VIEW_CREATE_CLIENT);
        }

        ClientSecretHome.removeByClientId(_client.getUuid());
        _client.getSecretList().forEach(secret -> {
            secret.setClient(_client);
            ClientSecretHome.create(secret);
        });
        ClientService.getInstance().addNewHistory(_client.getUuid(), HistoryTypeEnum.UPDATE, getUser().getEmail(), "CLIENT " + _client.getName());

        addInfo(INFO_CLIENT_UPDATED, getLocale());
        resetListId();

        return redirectView(request, VIEW_MANAGE_CLIENTS);
    }
}

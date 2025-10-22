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
import fr.paris.lutece.plugins.apimanager.business.api.ApiStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanOauthConfiguration;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.*;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionHome;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.paris.lutece.plugins.apimanager.web.right.Constants.RIGHT_MANAGEAPIS;

/**
 * This class provides the user interface to manage Api features ( manage, create, modify, remove )
 */
@Controller(controllerJsp = "ManageApis.jsp", controllerPath = "jsp/admin/plugins/apimanager/", right = RIGHT_MANAGEAPIS)
public class ApiJspBean extends AbstractJspBean<String, Api> {
    // Templates
    private static final String TEMPLATE_MANAGE_APIS = "/admin/plugins/apimanager/api/manage_apis.html";
    private static final String TEMPLATE_DISPLAY_API = "/admin/plugins/apimanager/api/display_api.html";
    private static final String TEMPLATE_CREATE_API_STEP_1 = "/admin/plugins/apimanager/api/create/step1.html";
    private static final String TEMPLATE_CREATE_API_STEP_2 = "/admin/plugins/apimanager/api/create/step2.html";
    private static final String TEMPLATE_CREATE_API_STEP_3 = "/admin/plugins/apimanager/api/create/step3.html";

    // Parameters
    private static final String PARAMETER_ID_API = "uuid";
    private static final String PARAMETER_OPENAPI = "openapi";
    private static final String PARAMETER_SELECTED_ENVIRONMENT = "environement";
    private static final String PARAMETER_PREFIX_SELECTED_PLAN = "plan-environement-";
    private static final String PARAMETER_SUBSCRIPTION_MODE = "subscriptionMode";
    private static final String PARAMETER_LINK_MODE = "linkMode";
    private static final String PARAMETER_SELECTED_TAGS = "selected_tags";
    private static final String PARAMETER_INFO_MSG = "infoMsg";
    private static final String PARAMETER_ID_INSTANCE = "uuid_instance";
    private static final String PARAMETER_SHOW_INSTANCES = "showInstances";
    private static final String PARAMETER_DELETE_LINK = "deleteLink";
    private static final String PARAMETER_ACTIVE_TAB = "activeTab";
    private static final String PARAMETER_RELOAD = "reload";
    private static final String PARAMETER_ENVIRONEMENT_PREFIX = "environement-";
    private static final String PARAMETER_RESOURCE_ROW = "-resource-row-";
    private static final String PARAMETER_PLAN = "-plan-";
    private static final String PARAMETER_PLAN_RESOURCES = "-resources";
    private static final String PARAMETER_RESOURCE_HEADER_MATCHING = "header-matching-";
    private static final String PARAMETER_SUBSCRIPTIONS_ROW = "subscription-row-";
    private static final String PARAMETER_VERB_NAME = "verb_name";
    private static final String PARAMETER_UUID_INSTANCES = "uuid_instances";
    private static final String PARAMETER_CREATE_USECASE = "create_usecase";
    private static final String PARAMETER_CURRENT_ENVIRONMENT_TAB = "current_environement_tab";
    private static final String PARAMETER_CURRENT_ENVIRONMENT_SOURCE = "current_source_environement";
    private static final String PARAMETER_CURRENT_PLAN_TAB = "current_plan_tab";
    private static final String PARAMETER_CURRENT_RESOURCE = "current_resource";
    private static final String PARAMETER_CURRENT_RESOURCE_HEADER_MATCHING = "current_header_matching";

    // Filters
    private static final String FILTER_DISPLAY_ARCHIVED = "display_archived";
    private static final String FILTER_ARCHIVED = "archived";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APIS = "apimanager.manage_apis.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_API = "apimanager.create_api.pageTitle";

    // Markers
    private static final String MARK_API_LIST = "api_list";
    private static final String MARK_ENVIRONMENT_LIST = "environment_list";
    private static final String MARK_STATUS_LIST = "status_list";
    private static final String MARK_INSTANCE_LIST = "instance_list";
    private static final String MARK_PLAN_LIST = "plan_list";
    private static final String MARK_TAG_LIST = "tag_list";
    private static final String MARK_VERB_LIST = "verb_list";
    private static final String MARK_REWRITE_URL_TYPE_LIST = "rewrite_url_type_list";
    private static final String MARK_MATCHER_TYPE_LIST = "matcher_type_list";
    private static final String MARK_API = "api";
    private static final String MARK_RESOURCE = "resource";
    private static final String MARK_PLAN_TEMPLATE_NAMES = "plan_template_names";
    private static final String MARK_SELECTED_TAG_LIST = "selected_tag_list";
    private static final String MARK_SELECTED_ENVIRONMENT_UUID = "selected_environment_uuid";
    private static final String MARK_SELECTED_STATUS = "selected_status";
    private static final String MARK_HEADER_MATCHING_TYPE_LIST = "header_matching_type_list";
    private static final String JSP_MANAGE_APIS = "jsp/admin/plugins/apimanager/ManageApis.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_ARCHIVE_API = "apimanager.message.confirmArchiveApi";
    private static final String MESSAGE_CONFIRM_REMOVE_LINK = "apimanager.message.confirmRemoveLink";
    private static final String TEMPLATE_NAME_PROP = "apimanager.plan.template.{i}.template.name";
    private static final String HEADER_MATCHING_TYPE_VALUES = "apimanager.plan.headermatching.type.values";


    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "apimanager.model.entity.api.attribute.";

    // Views
    private static final String VIEW_MANAGE_APIS = "manageApis";
    private static final String VIEW_CREATE_API = "createApi";
    private static final String VIEW_CREATE_API_STEP_2 = "step2";
    private static final String VIEW_CREATE_API_STEP_3 = "step3";
    private static final String VIEW_MODIFY_API = "modifyApi";
    private static final String VIEW_NEW_API_VERSION = "newApiVersion";
    private static final String VIEW_DISPLAY_API = "display";
    private static final String VIEW_LINK_API = "linkApi";

    // Actions
    private static final String ACTION_CREATE_API = "createApi";
    private static final String ACTION_CREATE_API_STEP_1 = "step1";
    private static final String ACTION_CREATE_API_STEP_2 = "step2";
    private static final String ACTION_CREATE_API_STEP_3 = "step3";
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
    private List<String> _listResources;
    private HashMap<String, String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    private final List<String> headerMatchingTypeList = Arrays.asList(AppPropertiesService.getProperty(HEADER_MATCHING_TYPE_VALUES).split(","));

    private final IConfigGeneratorService _configGeneratorService = SpringContextService.getBean(IConfigGeneratorService.BEAN_NAME);
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private Resource _resource;
    private List<Resource> _resources;

    // Property enums
    private static final List<String> matcherTypeList = Arrays
            .asList(AppPropertiesService.getProperty("apimanager.plan.resource.matcher.type.values").split(","));

    private List<Environement> environements;
    private List<Instance> instances;

    /**
     * Build the Manage View
     *
     * @param request The HTTP request
     * @return The page
     */
    @View(value = VIEW_MANAGE_APIS, defaultView = true)
    public String getManageApis(HttpServletRequest request) {
        final String infoMsg = request.getParameter(PARAMETER_INFO_MSG);
        if (infoMsg != null) {
            addInfo(infoMsg, getLocale());
            if ("true".equals(request.getParameter(PARAMETER_RELOAD))) {
                return getPage(PROPERTY_PAGE_TITLE_MANAGE_APIS, TEMPLATE_MANAGE_APIS, Map.of());
            }
            return redirectView(request, VIEW_MANAGE_APIS);
        }

        _api = null;
        final Map<String, Object> model = new HashMap<>();
        // new search only if in pagination mode
        if (request.getParameter(AbstractPaginator.PARAMETER_PAGE_INDEX) == null) {
            // if sorting request : new search with the existing filter criteria, ordered
            // example of order by parameter : orderby=name
            if (StringUtils.isNotBlank((String) request.getParameter(PARAMETER_SEARCH_ORDER_BY))) {

                String strOrderByColumn = (String) request.getParameter(PARAMETER_SEARCH_ORDER_BY);
                String strSortMode = getSortMode();

                _listIdApis = getService().getIdEntitiesList(_mapFilterCriteria, strOrderByColumn, strSortMode);


            } else {
                // reload the filter criteria and search
                _mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest(request);
                final HashMap<String, String> criterias = new HashMap<>(_mapFilterCriteria);
                if (criterias.containsKey(PARAMETER_ID_INSTANCE)) {
                    final String uuidInstance = criterias.get(PARAMETER_ID_INSTANCE);
                    _listIdApis = getService().getIdApisListLinkedToInstanceUuid(uuidInstance);
                    model.put(PARAMETER_ID_INSTANCE, uuidInstance);
                    model.put(PARAMETER_SHOW_INSTANCES, false);
                    model.put(PARAMETER_DELETE_LINK, true);
                } else {
                    _listIdApis = getService().getIdEntitiesList(criterias);
                }
            }

            // set CurrentPageIndex of Paginator to null in aim of displays the first page of results
            resetCurrentPageIndexOfPaginator();
        }


        // new search only if in pagination mode
        if (request.getParameter(PARAMETER_SELECTED_TAGS) != null) {
            String selectedStringTags = request.getParameter(PARAMETER_SELECTED_TAGS);
            List<String> selectedTags = new ArrayList<>();
            if (selectedStringTags != null && !selectedStringTags.isEmpty() && selectedStringTags.contains(",")) {
                selectedTags.addAll(Arrays.asList(selectedStringTags.split(",")));
            } else {
                selectedTags.add(selectedStringTags);
            }

            _listIdApis = getService().getApisByTags(selectedTags);
            model.put(MARK_SELECTED_TAG_LIST, selectedTags);
        }


        String selectedEnvironementUuid = _mapFilterCriteria.get("uuid_environement");
        if (selectedEnvironementUuid != null) {
            _listIdApis = ResourceService.getInstance().getDistinctApiUuidsByEnv(selectedEnvironementUuid);
        }


        Map<String, Object> apiModel = getPaginatedListModel(request, MARK_API_LIST, _listIdApis, JSP_MANAGE_APIS);
        model.put(MARK_SELECTED_ENVIRONMENT_UUID, selectedEnvironementUuid);
        model.putAll(apiModel);

        ArrayList<String> tags = new ArrayList<String>();
        for (Api apiValue : ((List<Api>) apiModel.get(MARK_API_LIST))) {
            tags.addAll(apiValue.getTags());
            List<String> envUuidList = ResourceService.getInstance().getEnvForApiUuid(apiValue.getUuid());
            if(envUuidList != null && !envUuidList.isEmpty()){
                apiValue.setEnvironementList(EnvironementService.getInstance().getEntitiesListByIds(envUuidList));
            }
        }

        model.put(MARK_TAG_LIST, tags.stream().distinct().collect(Collectors.toList()));

        environements = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());
        instances = new ArrayList<>();
        for (Environement envir : environements) {
            List<Instance> envInstances = InstanceService.getInstance().getEntitiesListByIds(InstanceService.getInstance().getIdInstancesListLinkedToEnvironementUuid(envir.getUuid()));
            instances.addAll(envInstances);
            envir.setInstances(envInstances);
        }

        List<Plan> plans = PlanService.getInstance().getEntitiesListByIds(PlanService.getInstance().getIdEntitiesList());

        model.put(MARK_ENVIRONMENT_LIST, environements);
        model.put(MARK_STATUS_LIST, getService().getDistinctStatus());
        model.put(MARK_INSTANCE_LIST, instances);
        model.put(MARK_PLAN_LIST, plans);

        final String subscriptionMode = request.getParameter(PARAMETER_SUBSCRIPTION_MODE);
        if (subscriptionMode != null) {
            model.put(PARAMETER_SUBSCRIPTION_MODE, Boolean.parseBoolean(subscriptionMode));
        }
        addPlanTemplateNamesToModel(model);


        model.put(MARK_SELECTED_ENVIRONMENT_UUID, _mapFilterCriteria.get("uuid_environement"));
        model.put(MARK_SELECTED_STATUS, _mapFilterCriteria.get("status"));

        //exlude some filters from the returned list
        for (String exclusion : getExcludedSearchParameters()) {
            _mapFilterCriteria.remove(exclusion);
        }

        addSearchParameters(model, _mapFilterCriteria); // allow the persistence of search values in inputs search bar inputs

        _api = (_api != null) ? _api : new Api();
        _resource = (_resource != null) ? _resource : new Resource();
        _resources = (_resources != null) ? _resources : new ArrayList<Resource>();
        _resource.setRewriteUrl(new ResourceRewriteUrl());

        model.put(MARK_API, _api);
        model.put(MARK_RESOURCE, _resource);
        model.put(MARK_VERB_LIST, ResourceVerbEnum.values());
        model.put(MARK_REWRITE_URL_TYPE_LIST, ResourceRewriteUrlTypeEnum.values());
        model.put(MARK_MATCHER_TYPE_LIST, matcherTypeList);
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_CREATE_API));
        model.put(PARAMETER_ACTIVE_TAB, 1);

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_APIS, TEMPLATE_MANAGE_APIS, model);
    }

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Api> getItemsFromIds(List<String> listIds) {
        final List<Api> listApi = getService().getEntitiesListByIds(listIds);

        // keep original order
        return listApi.stream().sorted(Comparator.comparingInt(notif -> listIds.indexOf(notif.getUuid()))).collect(Collectors.toList());
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
     * reset the _listIdApis list
     */
    public void resetListId() {
        _listIdApis = new ArrayList<>();
    }

    /**
     * Returns the form to create a api
     *
     * @param request The Http request
     * @return the html code of the api form
     */
    @View(VIEW_CREATE_API)
    public String getCreateApi(HttpServletRequest request) {
        _api = (_api != null) ? _api : new Api();
        _resources = (_resources != null) ? _resources : new ArrayList<Resource>();

        Map<String, Object> model = getModel();
        model.put(MARK_API, _api);
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_CREATE_API));

        return getPage(PROPERTY_PAGE_TITLE_CREATE_API, TEMPLATE_CREATE_API_STEP_1, model);
    }


    /**
     * Process the data capture form of a new api
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_CREATE_API)
    public String doCreateApi(HttpServletRequest request) throws AccessDeniedException {

        if (!SecurityTokenService.getInstance().validate(request, ACTION_ARCHIVE_API)) {
            throw new AccessDeniedException("Invalid security token");
        }

        // Check constraints
        if (!validateBean(_api, VALIDATION_ATTRIBUTES_PREFIX)) {
            return redirectView(request, VIEW_CREATE_API);
        }

        _api.setStatus("NEW");
        getService().create(_api, getUser().getEmail());
        addInfo(INFO_API_CREATED, getLocale());
        resetListId();

        return redirectView(request, VIEW_MANAGE_APIS);
    }

    /**
     * Process the data capture form of a new api
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_CREATE_API_STEP_1)
    public String doCreateApiStep1(HttpServletRequest request) throws AccessDeniedException {
        _api = (_api != null) ? _api : new Api();
        super.populate(_api, request, getLocale());

        try {
            populateApi(_api, request, getLocale());
        } catch (JsonProcessingException e) {
            this.addError("Error while parsing the openapi file. Please select a valid JSON file.");
            return redirect(request, VIEW_MANAGE_APIS);
        }

        if (!SecurityTokenService.getInstance().validate(request, ACTION_CREATE_API)) {
            throw new AccessDeniedException("Invalid security token");
        }

        // Check constraints
        if (!validateBean(_api, VALIDATION_ATTRIBUTES_PREFIX)) {
            return redirectView(request, VIEW_MANAGE_APIS);
        }

        return getCreateApiStep2(request);
    }

    private void reloadLists() {
        if (this.environements == null) {
            this.environements = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());
        }

        if (this.instances == null) {
            instances = new ArrayList<>();
            for (Environement envir : environements) {
                List<Instance> envInstances = InstanceService.getInstance().getEntitiesListByIds(InstanceService.getInstance().getIdInstancesListLinkedToEnvironementUuid(envir.getUuid()));
                instances.addAll(envInstances);
                envir.setInstances(envInstances);
            }
        }
    }

    /**
     * Returns the form to create a api
     *
     * @param request The Http request
     * @return the html code of the api form
     */
    @View(VIEW_CREATE_API_STEP_2)
    public String getCreateApiStep2(HttpServletRequest request) {
        _api = (_api != null) ? _api : new Api();
        if (_api.getEnvironementList() == null) {
            _api.setEnvironementList(new ArrayList<>());
        }
        this.reloadLists();


        String resourceIndex = request.getParameter(PARAMETER_CURRENT_RESOURCE);
        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);
        if (usecase != null && usecase.equals("add_environment")) {
            String selectedEnvironementUuid = request.getParameter(PARAMETER_SELECTED_ENVIRONMENT);
            Environement currentEnvironement = EnvironementHome.findByPrimaryKey(selectedEnvironementUuid).orElse(null);
            if (currentEnvironement != null) {
                currentEnvironement.setResourceList(new ArrayList<>());
                if (_api.getEnvironementList() == null) {
                    _api.setEnvironementList(new ArrayList<>());
                }

                if (currentEnvironement.getPlanList() == null)
                    currentEnvironement.setPlanList(new ArrayList<>());

                for (Resource res : currentEnvironement.getResourceList()) {
                    if (res.getRewriteUrl() == null)
                        res.setRewriteUrl(new ResourceRewriteUrl());
                }
                _api.getEnvironementList().add(currentEnvironement);
            }
        }

        if (usecase != null && usecase.equals("clone_environment")) {
            String sourceEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_SOURCE);
            String selectedEnvironementUuid = request.getParameter(PARAMETER_SELECTED_ENVIRONMENT);
            Environement sourceEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(sourceEnvironementUuid)).findFirst().orElse(null);
            Environement currentEnvironement = EnvironementHome.findByPrimaryKey(selectedEnvironementUuid).orElse(null);
            if (currentEnvironement != null) {
                currentEnvironement.setResourceList(new ArrayList<>());
                if (sourceEnvironement != null) {
                    for (Resource resource : sourceEnvironement.getResourceList()) {
                        currentEnvironement.getResourceList().add(new Resource(resource));
                    }
                }

                if (_api.getEnvironementList() == null) {
                    _api.setEnvironementList(new ArrayList<>());
                }

                if (currentEnvironement.getPlanList() == null)
                    currentEnvironement.setPlanList(new ArrayList<>());

                for (Resource res : currentEnvironement.getResourceList()) {
                    if (res.getRewriteUrl() == null)
                        res.setRewriteUrl(new ResourceRewriteUrl());
                }
                _api.getEnvironementList().add(currentEnvironement);
            }
        }

        if (usecase != null && usecase.equals("add_resource")) {
            String selectedEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);
            Environement currentEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(selectedEnvironementUuid)).findFirst().orElse(null);

            if (currentEnvironement != null) {
                if (currentEnvironement.getResourceList() == null)
                    currentEnvironement.setResourceList(new ArrayList<>());
                Resource blankResource = new Resource();
                blankResource.setRewriteUrl(new ResourceRewriteUrl());
                blankResource.setHeaderMatchings(new ArrayList<>());
                currentEnvironement.getResourceList().add(blankResource);
            }
            resourceIndex = Integer.toString(currentEnvironement.getResourceList().size() - 1);

        }

        if (usecase != null && usecase.equals("delete_resource")) {
            String selectedEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);
            Environement currentEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(selectedEnvironementUuid)).findFirst().orElse(null);

            if (resourceIndex != null && currentEnvironement != null) {
                currentEnvironement.getResourceList().remove(Integer.parseInt(resourceIndex));
            }
        }

        if (usecase != null && usecase.equals("delete_environment")) {
            String selectedEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);
            _api.getEnvironementList().removeIf(environement -> environement.getUuid().equals(selectedEnvironementUuid));
        }

        if (usecase != null && usecase.equals("add_header_matching")) {
            String selectedEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);
            Environement currentEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(selectedEnvironementUuid)).findFirst().orElse(null);
            if (resourceIndex != null && currentEnvironement != null) {
                Resource selectedResource = currentEnvironement.getResourceList().get(Integer.parseInt(resourceIndex));
                if (selectedResource.getHeaderMatchings() == null)
                    selectedResource.setHeaderMatchings(new ArrayList<>());
                selectedResource.getHeaderMatchings().add(new ResourceHeaderMatching());
            }
        }
        String indexHeaderMatching = request.getParameter(PARAMETER_CURRENT_RESOURCE_HEADER_MATCHING);
        if (usecase != null && usecase.equals("delete_header_matching")) {
            String selectedEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);
            Environement currentEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(selectedEnvironementUuid)).findFirst().orElse(null);

            if (resourceIndex != null && currentEnvironement != null) {
                Resource selectedResource = currentEnvironement.getResourceList().get(Integer.parseInt(resourceIndex));
                selectedResource.getHeaderMatchings().remove(Integer.parseInt(indexHeaderMatching));
            }
        }

        this.environements.removeIf(environement -> _api.getEnvironementList().stream().anyMatch(environement1 -> environement1.getUuid().equals(environement.getUuid())));
        Map<String, Object> model = getModel();

        String currentTab = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);

        if (currentTab == null) {
            if (!_api.getEnvironementList().isEmpty()) {
                model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, _api.getEnvironementList().get(0).getUuid());
            } else {
                model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, "");
            }
        } else {
            Environement envToSelect = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(currentTab)).findFirst().orElse(null);
            if (envToSelect != null) {
                model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, currentTab);
            } else {
                if (!_api.getEnvironementList().isEmpty()) {
                    model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, _api.getEnvironementList().get(0).getUuid());
                } else {
                    model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, "");
                }
            }
        }
        model.put(MARK_API, _api);
        model.put(MARK_ENVIRONMENT_LIST, environements);

        model.put(MARK_HEADER_MATCHING_TYPE_LIST, headerMatchingTypeList);
        model.put(MARK_VERB_LIST, ResourceVerbEnum.values());
        model.put(MARK_REWRITE_URL_TYPE_LIST, ResourceRewriteUrlTypeEnum.values());
        model.put(MARK_MATCHER_TYPE_LIST, matcherTypeList);
        model.put(PARAMETER_ACTIVE_TAB, 1);
        model.put(PARAMETER_CURRENT_RESOURCE, resourceIndex != null ? resourceIndex : 0);
        model.put(PARAMETER_CURRENT_ENVIRONMENT_SOURCE, 0);
        model.put(PARAMETER_CURRENT_RESOURCE_HEADER_MATCHING, 0);
        model.put(MARK_INSTANCE_LIST, instances);

        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_CREATE_API));

        return getPage(PROPERTY_PAGE_TITLE_CREATE_API, TEMPLATE_CREATE_API_STEP_2, model);
    }


    /**
     * Process the data capture form of a new api
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_CREATE_API_STEP_2)
    public String doCreateApiStep2(HttpServletRequest request) throws AccessDeniedException {
        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);


        if (usecase != null && usecase.isEmpty()) {
        try {
            populateEnvironement(_api, request, getLocale());
        } catch (JsonProcessingException e) {
            this.addError("Error while parsing the openapi file. Please select a valid JSON file.");
            return redirect(request, VIEW_CREATE_API);
        }

            _api = (_api != null) ? _api : new Api();

            if (!SecurityTokenService.getInstance().validate(request, ACTION_CREATE_API)) {
                throw new AccessDeniedException("Invalid security token");
            }

            // Check constraints
            if (!validateBean(_api, VALIDATION_ATTRIBUTES_PREFIX)) {
                return redirectView(request, VIEW_CREATE_API);
            }

            return getCreateApiStep3(request);
        }
        return getCreateApiStep2(request);
    }

    /**
     * Returns the form to create a api
     *
     * @param request The Http request
     * @return the html code of the api form
     */
    @View(VIEW_CREATE_API_STEP_3)
    public String getCreateApiStep3(HttpServletRequest request) {
        _api = (_api != null) ? _api : new Api();
        if (_api.getEnvironementList() == null) {
            _api.setEnvironementList(new ArrayList<>());
        }

        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);
        String selectedEnvironementUuid = request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB);
        String selectedPlanIndex = request.getParameter(PARAMETER_CURRENT_PLAN_TAB);
        String selectedPlanUuid = request.getParameter(PARAMETER_PREFIX_SELECTED_PLAN + selectedEnvironementUuid);
        if (usecase != null && usecase.equals("add_plan")) {
            Environement currentEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(selectedEnvironementUuid)).findFirst().orElse(null);
            Plan currentPlan = PlanHome.findByPrimaryKey(selectedPlanUuid).orElse(null);
            if (currentEnvironement != null && currentPlan != null) {
                currentEnvironement.getPlanList().add(currentPlan);
            }
        }
        if (usecase != null && usecase.equals("delete_plan")) {
            Environement currentEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getUuid().equals(selectedEnvironementUuid)).findFirst().orElse(null);

            if (selectedPlanIndex != null && currentEnvironement != null) {
                currentEnvironement.getPlanList().remove(Integer.parseInt(selectedPlanIndex));
            }
        }


        Map<String, Object> model = getModel();
        model.put(MARK_API, _api);
        model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB) != null ? request.getParameter(PARAMETER_CURRENT_ENVIRONMENT_TAB) : 0);

        List<Plan> plans = PlanService.getInstance().getEntitiesListByIds(PlanService.getInstance().getIdEntitiesList());
        model.put(MARK_PLAN_LIST, plans);
        String defaultIndex = "0";
        Environement defaultEnvironement = _api.getEnvironementList().stream().filter(environement -> environement.getPlanList() != null && !environement.getPlanList().isEmpty()).findFirst().orElse(null);
        if (defaultEnvironement != null) {
            String defaultuuid = defaultEnvironement.getPlanList().get(0).getUuid();
            if (defaultuuid != null)
                defaultIndex = defaultuuid;
        }
        model.put(PARAMETER_CURRENT_PLAN_TAB, selectedPlanUuid != null ? selectedPlanUuid : defaultIndex);

        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_CREATE_API));

        return getPage(PROPERTY_PAGE_TITLE_CREATE_API, TEMPLATE_CREATE_API_STEP_3, model);
    }

    /**
     * Process the data capture form of a new api
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_CREATE_API_STEP_3)
    public String doCreateApiStep3(HttpServletRequest request) throws AccessDeniedException {
        String usecase = request.getParameter(PARAMETER_CREATE_USECASE);

        Map<String, String[]> params = request.getParameterMap();
        if (usecase != null && usecase.isEmpty()) {
            _api = (_api != null) ? _api : new Api();

            try {
                populatePlan(_api, request, getLocale());
            } catch (JsonProcessingException e) {
                this.addError("Error while parsing the openapi file. Please select a valid JSON file.");
                return redirect(request, VIEW_CREATE_API);
            }

            if (!SecurityTokenService.getInstance().validate(request, ACTION_CREATE_API)) {
                throw new AccessDeniedException("Invalid security token");
            }

            // Check constraints
            if (!validateBean(_api, VALIDATION_ATTRIBUTES_PREFIX)) {
                return redirectView(request, VIEW_CREATE_API);
            }

            // if no environement configured then it's a draft otherwiser it's a new api
            _api.setStatus(
                    _api.getEnvironementList() !=null &&
                            _api.getEnvironementList() .size() > 0 ? ApiStatusEnum.NEW.name(): ApiStatusEnum.DRAFT.name());

            if (_api.getUuid() != null && !_api.getUuid().isEmpty()) {
                addInfo(INFO_API_UPDATED, getLocale());
                getService().update(_api, getUser().getEmail());
            } else {
                addInfo(INFO_API_CREATED, getLocale());
                getService().create(_api, getUser().getEmail());
                // clone subscriptions in case of new version
                if (_api.getEnvironementList() != null) {
                    for (Environement environement : _api.getEnvironementList()) {
                        for (Resource resource : environement.getResourceList()) {
                            for (Subscription subscription : resource.getSubscriptionList()) {
                                subscription.setApi(_api);
                                subscription.setResource(resource);
                                subscription.setEnvironement(resource.getEnvironement());
                                subscription.setPlan(resource.getPlan());
                                SubscriptionService.getInstance().create(subscription, getUser().getEmail());
                            }
                        }
                    }
                }
            }
            resetListId();

            return redirectView(request, VIEW_MANAGE_APIS);
        }
        return getCreateApiStep3(request);
    }

    /**
     * Manages the removal form of a api whose identifier is in the http request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action(ACTION_CONFIRM_ARCHIVE_API)
    public String getConfirmArchiveApi(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_API);
        UrlItem url = new UrlItem(getActionUrl(ACTION_ARCHIVE_API));
        url.addParameter(PARAMETER_ID_API, uuid);

        String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_ARCHIVE_API, url.getUrl(), AdminMessage.TYPE_CONFIRMATION);

        return redirect(request, strMessageUrl);
    }

    /**
     * Handles the removal form of a api
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage apis
     */
    @Action(ACTION_ARCHIVE_API)
    public String doArchiveApi(HttpServletRequest request) {
        final String apiUuid = request.getParameter(PARAMETER_ID_API);
        final Api api = ApiHome.findByPrimaryKey(apiUuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));


        getService().archive(apiUuid, getUser().getEmail());
        addInfo(INFO_API_ARCHIVED, getLocale());
        resetListId();

        return redirectView(request, VIEW_MANAGE_APIS);
    }

    /**
     * Manages the removal form of an instance link to an API whose identifiers is in the http request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action(ACTION_CONFIRM_REMOVE_LINK)
    public String getConfirmRemoveLink(HttpServletRequest request) {
        final String instanceUuid = request.getParameter(PARAMETER_ID_INSTANCE);
        final String apiUuid = request.getParameter(PARAMETER_ID_API);
        final UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_LINK));
        url.addParameter(PARAMETER_ID_INSTANCE, instanceUuid);
        url.addParameter(PARAMETER_ID_API, apiUuid);

        final String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_LINK, url.getUrl(), AdminMessage.TYPE_CONFIRMATION);

        return redirect(request, strMessageUrl);
    }

    /**
     * Handles the removal form of an instance link to an API
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage instances
     */
    @Action(ACTION_REMOVE_LINK)
    public String doRemoveLink(HttpServletRequest request) {
        final String instanceUuid = request.getParameter(PARAMETER_ID_INSTANCE);
        final String apiUuid = request.getParameter(PARAMETER_ID_API);
        final Instance instance = InstanceHome.findByPrimaryKey(instanceUuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));

        //InstanceService.getInstance( ).deleteLinkResource( instance, resourceUuid, getUser( ).getEmail( ) );

        addInfo(INFO_LINK_REMOVED, getLocale());
        return redirectView(request, VIEW_MANAGE_APIS);
    }

    /**
     * Returns the form to update info about a api
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View(VIEW_MODIFY_API)
    public String getModifyApi(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_API);
        if (uuid == null) {
            return redirectView(request, VIEW_MANAGE_APIS);
        }
        if (_api == null || !uuid.equals(_api.getUuid())) {
            Optional<Api> optApi = ApiHome.findByPrimaryKey(uuid);
            _api = optApi.orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        }
        loadApi();

        Map<String, Object> model = getModel();
        model.put(MARK_API, _api);
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_MODIFY_API));

        return getCreateApi(request);
    }


    /**
     * Returns the form to update info about a api
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View(VIEW_NEW_API_VERSION)
    public String getNewApiVersion(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_API);
        if (uuid == null) {
            return redirectView(request, VIEW_MANAGE_APIS);
        }
        if (_api == null || !uuid.equals(_api.getUuid())) {
            Optional<Api> optApi = ApiHome.findByPrimaryKey(uuid);
            _api = optApi.orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        }
        _api.setVersion(generateNewVersionNumber(_api.getVersion()));
        loadApi();

        if (_api.getEnvironementList() != null && !_api.getEnvironementList().isEmpty()) {
            for (Environement env : _api.getEnvironementList()) {
                if (env.getResourceList() != null && !env.getResourceList().isEmpty()) {
                    for (Resource resource : env.getResourceList()) {
                        List<Subscription> subscriptions = SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByResource(resource.getUuid()));
                        subscriptions.forEach(subscription -> subscription.setUuid(null));
                        resource.setUuid(null);
                        resource.setSubscriptionList(subscriptions);
                    }
                }
            }
        }

        _api.setUuid(null);
        Map<String, Object> model = getModel();
        model.put(MARK_API, _api);
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_MODIFY_API));

        return getCreateApi(request);
    }

    /**
     * Returns the form to update info about a api
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View(VIEW_DISPLAY_API)
    public String getDisplayApi(HttpServletRequest request) {
        String uuid = request.getParameter(PARAMETER_ID_API);
        if (uuid == null) {
            return redirectView(request, VIEW_MANAGE_APIS);
        }
        if (_api == null || !uuid.equals(_api.getUuid())) {
            Optional<Api> optApi = ApiHome.findByPrimaryKey(uuid);
            _api = optApi.orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        }
        loadApi();


        instances = new ArrayList<>();
        environements = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());
        for (Environement envir : environements) {
            List<Instance> envInstances = InstanceService.getInstance().getEntitiesListByIds(InstanceService.getInstance().getIdInstancesListLinkedToEnvironementUuid(envir.getUuid()));
            instances.addAll(envInstances);
            envir.setInstances(envInstances);
        }


        Map<String, Object> model = getModel();
        model.put(MARK_API, _api);
        model.put(MARK_ENVIRONMENT_LIST, environements);
        model.put(MARK_INSTANCE_LIST, instances);
        model.put(PARAMETER_CURRENT_ENVIRONMENT_TAB, "");
        model.put(PARAMETER_ACTIVE_TAB, 1);
        model.put(PARAMETER_CURRENT_RESOURCE, 0);
        model.put(PARAMETER_CURRENT_ENVIRONMENT_SOURCE, 0);
        model.put(PARAMETER_CURRENT_RESOURCE_HEADER_MATCHING, 0);
        model.put(MARK_VERB_LIST, ResourceVerbEnum.values());
        model.put(MARK_REWRITE_URL_TYPE_LIST, ResourceRewriteUrlTypeEnum.values());
        model.put(MARK_MATCHER_TYPE_LIST, matcherTypeList);
        model.put(SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance().getToken(request, ACTION_MODIFY_API));

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_APIS, TEMPLATE_DISPLAY_API, model);
    }

    private String generateNewVersionNumber(String version) {
        if (isNumeric(version)) {
            if (version.contains(".")) {
                String[] numbers = version.split("\\.");
                if (numbers.length == 2) {
                    String decimal = numbers[1];
                    int decimalInt = Integer.parseInt(decimal);
                    if (decimalInt < 9) {
                        // new minor
                        return numbers[0] + "." + (decimalInt + 1);
                    } else {
                        // new major
                        return String.valueOf(Integer.parseInt(numbers[0]) + 1) + .0;
                    }
                }
            }

        }
        return version + "-1";
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    /**
     * Process the change form of a api
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action(ACTION_MODIFY_API)
    public String doModifyApi(MultipartHttpServletRequest request) throws AccessDeniedException {
        try {
            populateApi(_api, request, getLocale());
        } catch (JsonProcessingException e) {
            this.addError("Error while parsing the openapi file. Please select a valid JSON file.");
            return redirect(request, VIEW_MODIFY_API, Map.of(PARAMETER_ID_API, _api.getUuid()));
        }

        if (!SecurityTokenService.getInstance().validate(request, ACTION_MODIFY_API)) {
            throw new AccessDeniedException("Invalid security token");
        }

        // Check constraints
        if (!validateBean(_api, VALIDATION_ATTRIBUTES_PREFIX)) {
            return redirect(request, VIEW_MODIFY_API, Map.of(PARAMETER_ID_API, _api.getUuid()));
        }

        getService().update(_api, getUser().getEmail());
        addInfo(INFO_API_UPDATED, getLocale());
        resetListId();

        return redirectView(request, VIEW_MANAGE_APIS);
    }

    @Action(ACTION_DOWNLOAD_OPENAPI)
    public void doDownloadOpenapi(HttpServletRequest request) throws JsonProcessingException {
        final String uuid = request.getParameter(PARAMETER_ID_API);
        if (uuid == null) {
            redirectView(request, VIEW_MANAGE_APIS);
        }

        final Api api = ApiHome.findByPrimaryKey(uuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        this.download(JSON_MAPPER.writeValueAsBytes(api.getOpenapi()), api.getName().replace(" ", "-") + "_openapi.json", "application/json");
    }

    @View(VIEW_LINK_API)
    public String getLinkApi(HttpServletRequest request) {
        final String instanceUuid = request.getParameter(PARAMETER_ID_INSTANCE);
        _listIdApis = getService().getIdApisListNotLinkedToInstanceUuid(instanceUuid);
        final Map<String, Object> model = getPaginatedListModel(request, MARK_API_LIST, _listIdApis, JSP_MANAGE_APIS);

        final String linkMode = request.getParameter(PARAMETER_LINK_MODE);
        if (linkMode != null) {
            model.put(PARAMETER_LINK_MODE, Boolean.parseBoolean(linkMode));
        }
        model.put(PARAMETER_ID_INSTANCE, instanceUuid);
        addSearchParameters(model, _mapFilterCriteria); // allow the persistence of search values in inputs search bar inputs

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_APIS, TEMPLATE_MANAGE_APIS, model);
    }

    @Action(ACTION_LINK_INSTANCE)
    public String doLinkApi(HttpServletRequest request) {
        final String instanceUuid = request.getParameter(PARAMETER_ID_INSTANCE);
        final String apiUuid = request.getParameter(PARAMETER_ID_API);
        if (StringUtils.isAnyBlank(instanceUuid, apiUuid)) {
            return redirectView(request, VIEW_MANAGE_APIS);
        }
        final Instance instance = InstanceHome.findByPrimaryKey(instanceUuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));
        final Api api = ApiHome.findByPrimaryKey(apiUuid).orElseThrow(() -> new AppException(ERROR_RESOURCE_NOT_FOUND));

        getService().linkInstance(api, instanceUuid, getUser().getEmail());

        addInfo(INFO_INSTANCE_LINKED, getLocale());

        return redirectView(request, VIEW_MANAGE_APIS);
    }

    protected void loadApi() {
        if (_api != null && !_api.getUuid().isEmpty()) {

            List<Resource> resources = ResourceService.getInstance().getResourcesByApiUuid(_api.getUuid());
            Map<String, List<Resource>> resourcesByEnvironements =
                    resources.stream().collect(Collectors.groupingBy(w -> w.getEnvironement().getUuid()));
            for (String envuuid : resourcesByEnvironements.keySet()) {
                Environement resourceEnv = EnvironementHome.findByPrimaryKey(envuuid).orElse(null);
                if (resourceEnv != null) {
                    resourceEnv.setResourceList(resourcesByEnvironements.get(envuuid));
                    List<Plan> planList = PlanService.getInstance().getEntitiesListByIds(resourcesByEnvironements.get(envuuid).stream().filter(resource -> resource.getPlan() != null && resource.getPlan().getUuid() != null)
                            .map(resource -> resource.getPlan().getUuid()).collect(Collectors.toList()));
                    resourceEnv.setPlanList(planList != null ? planList : new ArrayList<>());
                    for (Resource resource : resourceEnv.getResourceList()) {
                        if (resource.getRewriteUrl() == null) {
                            resource.setRewriteUrl(new ResourceRewriteUrl());
                        }
                        if (resource.getHeaderMatchings() == null) {
                            resource.setHeaderMatchings(new ArrayList<>());
                        }
                        resource.setInstances(InstanceService.getInstance().getEntitiesListByIds(InstanceService.getInstance().getIdInstancesListLinkedToResourceUuid(resource.getUuid())));
                    }
                    if (_api.getEnvironementList() == null) {
                        _api.setEnvironementList(new ArrayList<>());
                    }
                    if (!_api.getEnvironementList().stream().anyMatch(environement -> environement.getUuid().equals(resourceEnv.getUuid()))) {
                        _api.getEnvironementList().add(resourceEnv);
                    }
                }
            }

            Api currentApi = ApiHome.findByPrimaryKey(_api.getUuid()).orElse(null);
            _api.setOpenapi(currentApi.getOpenapi());
            _api.setTags(currentApi.getTags());

        }


    }

    protected void populateApi(Object bean, HttpServletRequest request, Locale locale) throws JsonProcessingException {
        super.populate(bean, request, locale);

        Map<String, String[]> map = request.getParameterMap();
        if (request instanceof MultipartHttpServletRequest) {
            final FileItem openapiFile = ((MultipartHttpServletRequest) request).getFile(PARAMETER_OPENAPI);
            if (openapiFile != null && openapiFile.getSize() > 0) {
                _api.setOpenapi(JSON_MAPPER.readValue(openapiFile.getString(), new TypeReference<Map<String, Object>>() {
                }));
            }
        }
        _api.setTags(Arrays.stream(Optional.ofNullable(request.getParameterValues(PARAMETER_SELECTED_TAGS)).orElse(new String[0]))
                .collect(Collectors.toList()));

        if (map.get("active") != null) {
            _api.setActive(true);
        }

        if (map.get("inMaintenance") != null) {
            _api.setInMaintenance(true);
        }

    }


    protected void populateEnvironement(Object bean, HttpServletRequest request, Locale locale) throws JsonProcessingException {

        _resources = new ArrayList<Resource>();
        //environement

        final List<String> configuredEnvironements = request.getParameterMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(PARAMETER_ENVIRONEMENT_PREFIX))
                .map(envEntry -> {
                    String key = envEntry.getKey().replace(PARAMETER_ENVIRONEMENT_PREFIX, "");
                    return key.substring(0, key.indexOf(PARAMETER_RESOURCE_ROW));
                }).distinct()
                .collect(Collectors.toList());


        environements = EnvironementService.getInstance().getEntitiesListByIds(EnvironementService.getInstance().getIdEntitiesList());


        for (String environementUuid : configuredEnvironements) {

            // Resources
            final List<Integer> resourceIndexes = request.getParameterMap().keySet().stream()
                    .filter(key -> key.startsWith(PARAMETER_ENVIRONEMENT_PREFIX + environementUuid + PARAMETER_RESOURCE_ROW))
                    .map(key -> key.replace(PARAMETER_ENVIRONEMENT_PREFIX + environementUuid + PARAMETER_RESOURCE_ROW, ""))
                    .map(key -> Integer.parseInt(key.substring(0, key.indexOf('-')))).distinct().collect(Collectors.toList());

            for (final int index : resourceIndexes) {
                Resource currentResource = new Resource();
                final String prefix = PARAMETER_ENVIRONEMENT_PREFIX + environementUuid + PARAMETER_RESOURCE_ROW + index + "-";
                final Map<String, String[]> resourceParams = request.getParameterMap().entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith(prefix))
                        .collect(Collectors.toMap(entry -> entry.getKey().replace(prefix, ""), Map.Entry::getValue));

                final MultipartHttpServletRequest resourceRequest = new MultipartHttpServletRequest(request, Map.of(), resourceParams);
                populate(currentResource, resourceRequest, locale);
                populateRewriteResourceUrl(currentResource, resourceParams);
                populateResourceHeaderMatching(currentResource, resourceParams, environementUuid);
                populateSubscriptions(currentResource, resourceParams);


                String verbName = resourceRequest.getParameter(PARAMETER_VERB_NAME);
                if (verbName != null)
                    currentResource.setVerb(ResourceVerbEnum.valueOf(resourceRequest.getParameter(PARAMETER_VERB_NAME)));

                //instances
                if (resourceRequest.getParameterValues(PARAMETER_UUID_INSTANCES) != null) {
                    List<String> uuid_instance = List.of(resourceRequest.getParameterValues(PARAMETER_UUID_INSTANCES));
                    currentResource.setInstances(InstanceService.getInstance().getEntitiesListByIds(uuid_instance));
                } else {
                    currentResource.setInstances(new ArrayList<>());
                }

                _resources.add(currentResource);
            }

            Environement apiEnvironement = _api.getEnvironementList().stream().filter(environement1 -> environement1.getUuid().equals(environementUuid)).findFirst().orElse(null);

            if (apiEnvironement != null)
                apiEnvironement.setResourceList(_resources);
        }

    }

    private void populateSubscriptions(Resource currentResource, Map<String, String[]> resourceParams) {

        currentResource.setSubscriptionList(new ArrayList<>());


        // Subscriptions
        final List<Integer> subscriptionsIndexes = resourceParams.keySet().stream()
                .filter(key -> key.startsWith(PARAMETER_SUBSCRIPTIONS_ROW))
                .map(key -> key.replace(PARAMETER_SUBSCRIPTIONS_ROW, ""))
                .map(key -> Integer.parseInt(key.substring(0, key.indexOf('-')))).distinct().collect(Collectors.toList());


        for (final int subscriptionIndex : subscriptionsIndexes) {

            final String Subscriptionprefix = PARAMETER_SUBSCRIPTIONS_ROW + subscriptionIndex + "-";
            final Map<String, String[]> subscriptionParams = resourceParams.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(Subscriptionprefix))
                    .collect(Collectors.toMap(entry -> entry.getKey().replace(Subscriptionprefix, ""), Map.Entry::getValue));
            String[] archived = subscriptionParams.get("archived");
            String[] uuid_client = subscriptionParams.get("uuid_client");
            String[] trace_enabled = subscriptionParams.get("trace_enabled");

            if (!subscriptionParams.isEmpty()) {
                Subscription subscription = new Subscription();


                if (trace_enabled != null && trace_enabled.length > 0) {
                    subscription.setTraceEnabled(Boolean.parseBoolean(trace_enabled[0]));
                }
                if (uuid_client != null && uuid_client.length > 0) {
                    subscription.setClient(ClientService.getInstance().getClientById(uuid_client[0], null).orElse(null));
                }
                if (archived != null && archived.length > 0) {
                    subscription.setArchived(Boolean.parseBoolean(archived[0]));
                }

                currentResource.getSubscriptionList().add(subscription);

            }
        }
    }

    private void populateResourceHeaderMatching(Resource currentResource, Map<String, String[]> resourceParams, String environementUuid) {
        final List<Integer> resourceHeadearMatchingIndexes = resourceParams.keySet().stream()
                .filter(key -> key.startsWith(PARAMETER_RESOURCE_HEADER_MATCHING))
                .map(key -> key.replace(PARAMETER_RESOURCE_HEADER_MATCHING, ""))
                .map(key -> Integer.parseInt(key.substring(0, key.indexOf('-')))).distinct().collect(Collectors.toList());

        currentResource.setEnvironement(environements.stream().filter(s -> s.getUuid().equals(environementUuid)).findFirst().orElse(null));

        for (final int headerMatchingIndex : resourceHeadearMatchingIndexes) {
            final String headerMatchingprefix = PARAMETER_RESOURCE_HEADER_MATCHING + headerMatchingIndex + "-";
            final Map<String, String[]> headerMatchingParams = resourceParams.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(headerMatchingprefix))
                    .collect(Collectors.toMap(entry -> entry.getKey().replace(headerMatchingprefix, ""), Map.Entry::getValue));

            String[] types = headerMatchingParams.get("type");
            String[] values = headerMatchingParams.get("value");
            String[] names = headerMatchingParams.get("name");
            String[] uuids = headerMatchingParams.get("uuid");

            if (!headerMatchingParams.isEmpty()) {
                ResourceHeaderMatching resourceHeaderMatching = new ResourceHeaderMatching();
                if (types != null && types.length > 0) {
                    resourceHeaderMatching.setType(types[0]);
                }
                if (values != null && values.length > 0) {
                    resourceHeaderMatching.setValue(values[0]);
                }
                if (names != null && names.length > 0) {
                    resourceHeaderMatching.setName(names[0]);
                }
                if (uuids != null && uuids.length > 0) {
                    String headerUuid = uuids[0];
                    if (!headerUuid.isEmpty()) {
                        resourceHeaderMatching.setUuid(headerUuid);
                    }
                }

                if (currentResource.getHeaderMatchings() == null) {
                    currentResource.setHeaderMatchings(new ArrayList<>());
                }
                currentResource.getHeaderMatchings().add(resourceHeaderMatching);
            }

        }
    }

    private void populateRewriteResourceUrl(Resource currentResource, Map<String, String[]> resourceParams) {
        final Map<String, String[]> rewriteUrlParams = resourceParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("rewrite_url_"))
                .collect(Collectors.toMap(entry -> entry.getKey().replace("rewrite_url_", ""), Map.Entry::getValue));
        if (!rewriteUrlParams.isEmpty()) {
            ResourceRewriteUrl currentRewriteResourceUrl = new ResourceRewriteUrl();
            String[] targets = rewriteUrlParams.get("target");
            String[] values = rewriteUrlParams.get("value");
            String[] types = rewriteUrlParams.get("type_name");
            String[] uuids = rewriteUrlParams.get("uuid");
            if (targets != null && targets.length > 0) {
                currentRewriteResourceUrl.setTarget(targets[0]);
            }
            if (values != null && values.length > 0) {
                currentRewriteResourceUrl.setValue(values[0]);
            }
            if (types != null && types.length > 0) {
                currentRewriteResourceUrl.setType(ResourceRewriteUrlTypeEnum.valueOf(types[0]));
            }
            if (uuids != null && uuids.length > 0) {
                String rewriteUuid = uuids[0];
                if (!rewriteUuid.isEmpty()) {
                    currentRewriteResourceUrl.setUuid(uuids[0]);
                }
            }
            currentResource.setRewriteUrl(currentRewriteResourceUrl);
        }
    }


    protected void populatePlan(Object bean, HttpServletRequest request, Locale locale) throws JsonProcessingException {


        List<Plan> plans = PlanService.getInstance().getEntitiesListByIds(PlanService.getInstance().getIdEntitiesList());

        for (Environement environement : _api.getEnvironementList()) {

            for (Resource resource : environement.getResourceList()) {
                //get plans
                final String plan_prefix = PARAMETER_ENVIRONEMENT_PREFIX + environement.getUuid() + PARAMETER_PLAN;
                final Map<String, String[]> envPlanResources = request.getParameterMap().entrySet().stream().filter(stringEntry -> stringEntry.getKey().startsWith(plan_prefix))
                        .collect(Collectors.toMap(entry -> entry.getKey().replace(plan_prefix, ""), Map.Entry::getValue));
                ;

                for (String envPlanResource : envPlanResources.keySet()) {

                    String[] planResources = envPlanResources.get(envPlanResource);
                    if (planResources != null && planResources.length > 0) {
                        if (Arrays.stream(planResources).anyMatch(s -> s.equals(resource.getVerb().name() + "|" + resource.getPath()))) {
                            Plan currentPlan = plans.stream().filter(plan -> plan.getUuid().replace(" ", "").equals(envPlanResource.replace(PARAMETER_PLAN_RESOURCES, ""))).findFirst().orElse(null);
                            if (currentPlan != null) {
                                if (resource.getPlan() == null) {
                                    resource.setPlan(currentPlan);
                                } else {
                                    Resource currentResource = new Resource(resource);
                                    currentResource.setPlan(currentPlan);
                                    environement.getResourceList().add(currentResource);
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    private void addPlanTemplateNamesToModel(final Map<String, Object> model) {
        final List<String> templateNameList = new ArrayList<>();
        for (int i = 0; ; i++) {
            final String templateName = AppPropertiesService.getProperty(TEMPLATE_NAME_PROP.replace("{i}", String.valueOf(i)), null);
            if (templateName == null) {
                break;
            }
            templateNameList.add(templateName);
        }
        model.put(MARK_PLAN_TEMPLATE_NAMES, templateNameList);
    }
}

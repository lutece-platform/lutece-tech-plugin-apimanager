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
package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.api.Api;
import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.api.ApiStatusEnum;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.resource.ResourceHome;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.service.generator.IConfigGeneratorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiService extends AbstractService<Api>
{

    private static ApiService _instance;


    private ApiService( )
    {
    }

    public static ApiService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new ApiService( );
        }
        return _instance;
    }

    @Override
    public void create( final Api entity, final String user )
    {
        final String uuid = ApiHome.create( entity ).getUuid( );
        entity.setUuid(uuid);
        for (Environement env : entity.getEnvironementList()){
            for(Resource resource : env.getResourceList()){
                resource.setApi(entity);
                ResourceService.getInstance().create(resource, user);
                if( resource.getInstances() != null){
                    for(Instance instance : resource.getInstances()){
                        ResourceHome.linkInstance(resource, instance.getUuid());
                    }
                }
            }
        }

        this.addNewHistory( uuid, HistoryTypeEnum.CREATE, user, "API " + entity.getName( ) );
    }


    public void updateStatus( final String apiUuid, final String status, final String user )
    {
        Api api = ApiHome.findByPrimaryKey(apiUuid).orElse(null);
        ApiHome.updateStatus(apiUuid,status);
        this.addNewHistory( apiUuid, HistoryTypeEnum.UPDATE, user, (api!=null?"API "+api.getName():apiUuid) );
    }

    @Override
    public void update( final Api entity, final String user )
    {
        ApiHome.update( entity );
        if(entity.getEnvironementList() != null){
            for (Environement env : entity.getEnvironementList()){
                // retrieve current resource list to find deleted ones
                List<Resource> currentResources = ResourceService.getInstance().getResourcesByAPIUiidPlanUuidEnvironementUUID(entity.getUuid(), null, env.getUuid());
                // check if some subscriptions have been deleted
                // compare the new edition with the current stored resource list
                List<String> currentResourcesUuids = currentResources.stream().map(Resource::getUuid).collect(Collectors.toList());
                currentResourcesUuids.removeAll(env.getResourceList().stream().map(Resource::getUuid).collect(Collectors.toList()));
                for(String ressourceUuid : currentResourcesUuids){
                    ResourceService.getInstance().delete(ressourceUuid,user);
                }

                for(Resource resource : env.getResourceList()){
                    resource.setApi(entity);
                    if(resource.getUuid() != null && !resource.getUuid().isEmpty()){
                        ResourceService.getInstance().update(resource, user);
                    }else{
                        ResourceService.getInstance().create(resource, user);
                    }
                    //clean previous link
                    ResourceHome.removeInstanceLinks(resource);
                    if( resource.getInstances() != null){
                        for(Instance instance : resource.getInstances()){
                            ResourceHome.linkInstance(resource, instance.getUuid());
                        }
                    }
                }
            }
        }

        this.addNewHistory( entity.getUuid( ), HistoryTypeEnum.UPDATE, user, "API " + entity.getName( ) );
    }

    /**
     * The delete function is not available for API. This method performs the archive action. You shouldn't use this method.
     * 
     * @param uuid
     *            the api uuid
     * @param user
     *            the user
     * @see ApiService#archive(String, String)
     */
    @Deprecated
    @Override
    public void delete( final String uuid, final String user )
    {
        Api api = ApiHome.findByPrimaryKey(uuid).orElse(null);
        this.archive( uuid, user );
        this.addNewHistory( uuid, HistoryTypeEnum.ARCHIVE, user, "API " + (api != null ? api.getName( ):uuid) );
    }

    @Override
    public List<String> getIdEntitiesList( final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy )
    {
        return ApiHome.getIdApisList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Api> getEntitiesListByIds( final List<String> listIds )
    {
        return ApiHome.getApisListByIds( listIds );
    }

    /**
     * Load the uuid of all APIs that are NOT linked to the specified instance UUID, and returns them as a list
     * 
     * @param instanceUuid
     *            the instance uuid
     * @return the list which contains the uuid of all the APIs
     */
    public List<String> getIdApisListNotLinkedToInstanceUuid( final String instanceUuid )
    {
        return ApiHome.getIdApisListNotLinkedToInstanceUuid( instanceUuid );
    }

    /**
     * Load the uuid of all APIs that are linked to the specified instance UUID, and returns them as a list
     * 
     * @param instanceUuid
     *            the instance uuid
     * @return the list which contains the uuid of all the APIs
     */
    public List<String> getIdApisListLinkedToInstanceUuid( final String instanceUuid )
    {
        return ApiHome.getIdApisListLinkedToInstanceUuid( instanceUuid );
    }


    /**
     * Link the specified API to the specified instance UUID.
     * 
     * @param api
     *            the API
     * @param instanceUuid
     *            the instance UUID
     * @param user
     *            the user
     */
    public void linkInstance( final Api api, final String instanceUuid, final String user )
    {
        ApiHome.linkInstance( api, instanceUuid );
        Instance instance = InstanceHome.findByPrimaryKey(instanceUuid).orElse(null);
        this.addNewHistory( api.getUuid( ), HistoryTypeEnum.UPDATE, user, "API " + api.getName( ) );
        this.addNewHistory( instanceUuid, HistoryTypeEnum.UPDATE, user,"INSTANCE " + (instance != null?instance.getName() : instanceUuid) );
    }

    /**
     * archives an API
     * 
     * @param uuid
     *            the api uuid
     * @param user
     *            the user
     */
    public void archive( final String uuid, final String user )
    {
        ApiHome.findByPrimaryKey( uuid ).ifPresent( api -> {
            api.setArchived( true );
            ApiHome.update( api );
            this.addNewHistory( uuid, HistoryTypeEnum.ARCHIVE, user, "API " + api.getName( ) );
        } );

    }


    /**
     * unpublish APIs
     *
     * @param apiUuids
     *            the apis uuids
     * @param email
     *            the user email
     */
    public void unpublish(final List<String> apiUuids, String email, IConfigGeneratorService _configGeneratorService)
    {


            for (String apiUuid : apiUuids) {
                final Api api = ApiHome.findByPrimaryKey(apiUuid).orElse(null);

                if (api != null && api.getStatus().equals(ApiStatusEnum.PUBLISHED.name())) {

                    final String comment = "Suppression des souscriptions vers l'api "+ api.getName();

                    List<Resource> apiResources = ResourceService.getInstance().getResourcesByApiUuid(apiUuid);
                    List<Subscription> resourceSubscription = new ArrayList<>();
                    for (Resource apiResource : apiResources) {
                        resourceSubscription.addAll(SubscriptionService.getInstance().getEntitiesListByIds(SubscriptionService.getInstance().getIdSubscriptionsByResource(apiResource.getUuid())));
                    }

                    Map<String, Map<String, Map<String, List<Subscription>>>> multipleFieldsMap = resourceSubscription.stream()
                            .collect(
                                    Collectors.groupingBy(o -> o.getClient().getUuid(),
                                            Collectors.groupingBy(o -> o.getResource().getEnvironement().getUuid(),
                                                    (Collectors.groupingBy(o -> o.getResource().getPlan().getUuid())))));

                    addNewHistory(api.getUuid(), HistoryTypeEnum.UNPUBLISH, email, "API " + api.getName());
                    api.setStatus(ApiStatusEnum.UNPUBLISHING.name());
                    ApiService.getInstance().update(api, email);

                    try {
                        for (Map.Entry<String, Map<String, Map<String, List<Subscription>>>> clientSubscriptionByEnvironementAndPlan : multipleFieldsMap.entrySet()) {
                            Map<String, Map<String, List<Subscription>>> clientEnvironements = clientSubscriptionByEnvironementAndPlan.getValue();
                            for (Map.Entry<String, Map<String, List<Subscription>>> environementSubscription : clientEnvironements.entrySet()) {
                                Map<String, List<Subscription>> clientPlans = environementSubscription.getValue();
                                for (Map.Entry<String, List<Subscription>> planSubscription : clientPlans.entrySet()) {
                                    String environementName = null;
                                    List<Subscription> subscriptions = planSubscription.getValue();
                                    for (Subscription sub : subscriptions) {
                                        environementName = sub.getEnvironement().getName();
                                        List<String> instanceIds = InstanceHome.getIdInstancesListLinkedToResourceUuid(sub.getResource().getUuid());
                                        sub.getResource().setInstances(InstanceHome.getInstancesListByIds(instanceIds));
                                    }
                                    _configGeneratorService.deleteSubscriptions(
                                            subscriptions, comment, email);

                                    ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.UNPUBLISHED.name(), email);
                                       /* MeecrogateAckResponse ackResponse = MeecrogateGatewayService.getInstance().getStatus(environementName);
                                        if (ackResponse!=null && ackResponse.getDeployGatewayStatus() != null && ackResponse.getDeployGatewayStatus().equals("updated")) {
                                            ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.UNPUBLISHED.name(), getUser().getEmail());
                                        } else {
                                            ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.UNDEPLOY_ERROR.name(), getUser().getEmail());
                                        }*/
                                }
                            }
                        }
                    } catch (Exception e) {
                        ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.UNPUBLISH_ERROR.name(), email);
                    }
                }
            }


    }


    /**
     * publish an API
     *
     * @param apiUuids
     *            the apis uuids
     * @param email
     *            the user email
     */
    public void publish(List<String> apiUuids, String email, IConfigGeneratorService _configGeneratorService)
    {

            for (String apiUuid : apiUuids) {
                final Api api = ApiHome.findByPrimaryKey(apiUuid).orElse(null);

                if (api!=null && !api.getStatus().equals(ApiStatusEnum.PUBLISHED.name())) {

                    final String comment = "Création des souscriptions vers l'api "+ api.getName();

                    List<Resource> apiResources = ResourceService.getInstance().getResourcesByApiUuid(apiUuid);
                    List<Subscription> resourceSubscription = new ArrayList<>();
                    for (Resource apiResource : apiResources) {
                        List<String> idSubscriptionsByResource = SubscriptionService.getInstance().getIdSubscriptionsByResource(apiResource.getUuid());
                        List<Subscription> entitiesListByIds = SubscriptionService.getInstance().getEntitiesListByIds(idSubscriptionsByResource);
                        resourceSubscription.addAll(entitiesListByIds);
                    }

                    Map<String, Map<String, Map<String, List<Subscription>>>> multipleFieldsMap = resourceSubscription.stream()
                            .collect(
                                    Collectors.groupingBy(o -> o.getClient().getUuid(),
                                            Collectors.groupingBy(o -> o.getResource().getEnvironement().getUuid(),
                                                    (Collectors.groupingBy(o -> o.getResource().getPlan().getUuid())))));


                    if(!multipleFieldsMap.isEmpty()){
                        addNewHistory(api.getUuid(), HistoryTypeEnum.PUBLISH, email, "API " + api.getName());
                        api.setStatus(ApiStatusEnum.PUBLISHING.name());
                        ApiService.getInstance().update(api, email);
                    }

                    try {
                        for (Map.Entry<String, Map<String, Map<String, List<Subscription>>>> clientSubscriptionByEnvironementAndPlan : multipleFieldsMap.entrySet()) {
                            Map<String, Map<String, List<Subscription>>> clientEnvironements = clientSubscriptionByEnvironementAndPlan.getValue();
                            for (Map.Entry<String, Map<String, List<Subscription>>> environementSubscription : clientEnvironements.entrySet()) {
                                Map<String, List<Subscription>> clientPlans = environementSubscription.getValue();
                                for (Map.Entry<String, List<Subscription>> planSubscription : clientPlans.entrySet()) {
                                    List<Subscription> subscriptions = planSubscription.getValue();
                                    for (Subscription sub : subscriptions) {
                                        List<String> instanceIds = InstanceHome.getIdInstancesListLinkedToResourceUuid(sub.getResource().getUuid());
                                        sub.getResource().setInstances(InstanceHome.getInstancesListByIds(instanceIds));
                                    }

                                    _configGeneratorService.generateSubscriptions(subscriptions, comment, email);
                                    ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.PUBLISHED.name(), email);
                                    /*
                                    if(environementName != null) {
                                        _configGeneratorService.generateSubscriptions(
                                                subscriptions, comment, getUser().getEmail());
                                        MeecrogateAckResponse ackResponse = MeecrogateGatewayService.getInstance().getStatus(environementName);
                                        if (ackResponse!=null && ackResponse.getDeployGatewayStatus() != null && ackResponse.getDeployGatewayStatus().equals("updated")) {
                                            ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.PUBLISHED.name(), getUser().getEmail());
                                        } else {
                                            ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.DEPLOY_ERROR.name(), getUser().getEmail());
                                        }
                                    }else{
                                        ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.DEPLOY_ERROR.name(), getUser().getEmail());
                                    }*/
                                }
                            }
                        }
                    } catch (Exception e) {
                        ApiService.getInstance().updateStatus(apiUuid, ApiStatusEnum.PUBLISH_ERROR.name(), email);
                    }

                }
            }


    }


    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getApisByTags(List<String> tags )
    {
        return ApiHome.getuuidsByTags( tags );
    }

    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getApisByPath(String path )
    {
        return ApiHome.getuuidsByPath( path );
    }

    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getApisByPathAndVersion(String path, String version )
    {
        return ApiHome.getuuidsByPathAndVersion( path, version );
    }


    public List<String> getDistinctStatus() {
        return ApiHome.getDistinctStatus( );
    }
}

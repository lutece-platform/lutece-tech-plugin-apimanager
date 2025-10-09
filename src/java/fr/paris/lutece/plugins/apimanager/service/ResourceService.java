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

import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourceService extends AbstractService<Resource> {

    private static ResourceService _instance;

    private ResourceService() {
    }

    public static ResourceService getInstance() {
        if (_instance == null) {
            _instance = new ResourceService();
        }
        return _instance;
    }

    @Override
    public void create(final Resource entity, final String user) {
        if(entity.getRewriteUrl() != null){
            ResourceRewriteUrlHome.create(entity.getRewriteUrl());
        }
        final String uuid = ResourceHome.create(entity).getUuid();

        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user, "");
    }

    @Override
    public void update(final Resource entity, final String user) {
        if(entity.getRewriteUrl() != null){
            if(entity.getRewriteUrl().getUuid() != null){
                ResourceRewriteUrlHome.update(entity.getRewriteUrl());
            }else{
                ResourceRewriteUrlHome.create(entity.getRewriteUrl());
            }
        }
        ResourceHome.update(entity);
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user, "RESOURCE "+ entity.getName());
    }

    @Override
    public void delete(final String uuid, final String user) {
        ResourceHome.findByPrimaryKey(uuid).ifPresent(resource -> {
            ResourceHome.remove(uuid);

            Optional.ofNullable(resource.getRewriteUrl()).ifPresent(ru -> ResourceRewriteUrlHome.remove(ru.getUuid()));


            // Delete header matching
            ResourceHeaderMatchingHome.getIdResourceHeaderMatchingsList(Map.of("uuid_resource", uuid), null, null).forEach(ResourceHeaderMatchingHome::remove);

            this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user, "RESOURCE "+ resource.getName());
        });
    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return ResourceHome.getIdResourcesList(mapFilterCriteria, columnToOrder, orderBy);
    }

    @Override
    public List<Resource> getEntitiesListByIds(final List<String> listIds) {
        return ResourceHome.getResourcesListByIds(listIds);
    }

    /**
     * Get Resources by plan UUID
     *
     * @param planUuid the plan UUID
     * @return list of Resource
     */
    public List<Resource> getResourcesByPlanUuid(final String planUuid) {
        final List<String> resourceIds = this.getIdEntitiesList(Map.of("uuid_plan", planUuid));
        if (resourceIds.isEmpty()) {
            return List.of();
        }
        return this.getEntitiesListByIds(resourceIds);
    }

    /**
     * Get Resources by plan UUID
     *
     * @param envUuid the plan UUID
     * @return list of Resource
     */
    public List<Resource> getResourcesByEnvironementUuid(final String envUuid) {
        final List<String> resourceIds = this.getIdEntitiesList(Map.of("uuid_environement", envUuid));
        if (resourceIds.isEmpty()) {
            return List.of();
        }
        return this.getEntitiesListByIds(resourceIds);
    }

    /**
     * Get Resources by plan UUID
     *
     * @param planUuid the plan UUID
     * @return list of Resource
     */
    public List<Resource> getResourcesByAPIUiidPlanUuidEnvironementUUID(final String apiUuid, final String planUuid, final String environementUuid) {
        Map<String, String> args = new HashMap<>();
        if (planUuid != null)
            args.put("uuid_plan", planUuid);
        if (apiUuid != null)
            args.put("uuid_api", apiUuid);
        if (environementUuid != null)
            args.put("uuid_environement", environementUuid);
        final List<String> resourceIds = this.getIdEntitiesList(args);

        if (resourceIds.isEmpty()) {
            return List.of();
        }
        return this.getEntitiesListByIds(resourceIds);
    }

    /**
     * Get Resources by plan UUID
     *
     * @param apiUuid the plan UUID
     * @return list of Resource
     */
    public List<Resource> getResourcesByApiUuid(final String apiUuid) {
        final List<String> resourceIds = this.getIdEntitiesList(Map.of("uuid_api", apiUuid));
        if (resourceIds.isEmpty()) {
            return List.of();
        }
        return this.getEntitiesListByIds(resourceIds);
    }
}

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
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;

import java.util.List;
import java.util.Map;

public class InstanceService extends AbstractService<Instance>
{

    private static InstanceService _instance = new InstanceService( );

    private InstanceService( )
    {
    }

    public static InstanceService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new InstanceService( );
        }
        return _instance;
    }

    @Override
    public void create( final Instance entity, final String user )
    {
        final String uuid = InstanceHome.create( entity ).getUuid( );
        this.addNewHistory( uuid, HistoryTypeEnum.CREATE, user );
    }

    @Override
    public void update( final Instance entity, final String user )
    {
        InstanceHome.update( entity );
        this.addNewHistory( entity.getUuid( ), HistoryTypeEnum.UPDATE, user );
    }

    @Override
    public void delete( final String uuid, final String user )
    {
        InstanceHome.remove( uuid );
        this.addNewHistory( uuid, HistoryTypeEnum.DELETE, user );
    }

    @Override
    public List<String> getIdEntitiesList( final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy )
    {
        return InstanceHome.getIdInstancesList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Instance> getEntitiesListByIds( final List<String> listIds )
    {
        return InstanceHome.getInstancesListByIds( listIds );
    }

    /**
     * Load the uuid of all instances that are NOT linked to the specified API UUID, and returns them as a list
     * 
     * @param apiUuid
     *            the API uuid
     * @return the list which contains the uuid of all the instances
     */
    public List<String> getIdInstancesListNotLinkedToApiUuid( final String apiUuid )
    {
        return InstanceHome.getIdInstancesListNotLinkedToApiUuid( apiUuid );
    }
    /**
     * Load the uuid of all instances that are linked to the specified API UUID, and returns them as a list
     *
     * @param apiUuid
     *            the API uuid
     * @return the list which contains the uuid of all the instances
     */
    public List<String> getIdInstancesListLinkedToResourceUuid( final String apiUuid )
    {
        return InstanceHome.getIdInstancesListLinkedToResourceUuid( apiUuid );
    }

    /**
     * Load the uuid of all instances that are linked to the specified API UUID, and returns them as a list
     * 
     * @param envUuid
     *            the API uuid
     * @return the list which contains the uuid of all the instances
     */
    public List<String> getIdInstancesListLinkedToEnvironementUuid( final String envUuid )
    {
        return InstanceHome.getIdInstancesListLinkedToEnvironementUuid( envUuid );
    }

    /**
     * Link the specified instance to the specified Resource UUID.
     * 
     * @param instance
     *            the instance
     * @param resourceUuid
     *            the API UUID
     * @param user
     *            the user
     */
    public void linkResource( final Instance instance, final String resourceUuid, final String user )
    {
        InstanceHome.linkResource( instance, resourceUuid );
        this.addNewHistory( instance.getUuid( ), HistoryTypeEnum.UPDATE, user );
        this.addNewHistory( resourceUuid, HistoryTypeEnum.UPDATE, user );
    }

    /**
     * Deletes the link between the specified instance and the specified API UUID.
     * 
     * @param instance
     *            the instance
     * @param resourceUuid
     *            the Resource UUID
     * @param user
     *            the user
     */
    public void deleteLinkResource( final Instance instance, final String resourceUuid, final String user )
    {
        InstanceHome.deleteLinkResource( instance, resourceUuid );
        this.addNewHistory( instance.getUuid( ), HistoryTypeEnum.UPDATE, user );
        this.addNewHistory( resourceUuid, HistoryTypeEnum.UPDATE, user );
    }


    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getAvailableTags(List<String> listIds )
    {
        return InstanceHome.getAvailableTags( listIds );
    }


    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getInstancesByTags(List<String> tags )
    {
        return InstanceHome.getuuidsByTags( tags );
    }
}

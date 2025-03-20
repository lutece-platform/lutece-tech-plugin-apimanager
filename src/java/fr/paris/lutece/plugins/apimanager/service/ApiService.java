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
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;

import java.util.List;
import java.util.Map;

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
        this.addNewHistory( uuid, HistoryTypeEnum.CREATE, user );
    }

    @Override
    public void update( final Api entity, final String user )
    {
        ApiHome.update( entity );
        this.addNewHistory( entity.getUuid( ), HistoryTypeEnum.UPDATE, user );
    }

    @Override
    public void delete( final String uuid, final String user )
    {
        ApiHome.findByPrimaryKey( uuid ).ifPresent( api -> {
            // Delete API plans
            PlanService.getInstance( ).getIdEntitiesList( Map.of( "uuid_api", uuid ) ).forEach( planId -> PlanService.getInstance( ).delete( planId, user ) );

            // Delete API instances
            InstanceService.getInstance( ).getIdEntitiesList( Map.of( "uuid_api", uuid ) )
                    .forEach( instanceId -> InstanceService.getInstance( ).delete( instanceId, user ) );

            // Delete API
            ApiHome.remove( uuid );
            this.addNewHistory( uuid, HistoryTypeEnum.DELETE, user );
        } );
        ApiHome.remove( uuid );
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
}

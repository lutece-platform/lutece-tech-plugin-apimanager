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
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanOauthConfigurationHome;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlanService extends AbstractService<Plan>
{

    private static PlanService _instance = new PlanService( );

    private PlanService( )
    {
    }

    public static PlanService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new PlanService( );
        }
        return _instance;
    }

    @Override
    public void create( final Plan entity, final String user )
    {
        Optional.ofNullable( entity.getOauthConfiguration( ) ).ifPresent( PlanOauthConfigurationHome::create );

        final String uuid = PlanHome.create( entity ).getUuid( );

        this.addNewHistory( uuid, HistoryTypeEnum.CREATE, user );
    }

    @Override
    public void update( final Plan entity, final String user )
    {
        Optional.ofNullable( entity.getOauthConfiguration( ) ).ifPresent( PlanOauthConfigurationHome::update );

        PlanHome.update( entity );
        this.addNewHistory( entity.getUuid( ), HistoryTypeEnum.UPDATE, user );
    }

    @Override
    public void delete( final String uuid, final String user )
    {
        PlanHome.findByPrimaryKey( uuid ).ifPresent( plan -> {
            // Delete resources
            ResourceService.getInstance( ).getIdEntitiesList( Map.of( "uuid_plan", uuid ) )
                    .forEach( resourceId -> ResourceService.getInstance( ).delete( resourceId, user ) );


            // Delete plan
            PlanHome.remove( uuid );

            // Delete plan config objects
            Optional.ofNullable( plan.getOauthConfiguration( ) ).ifPresent( oac -> PlanOauthConfigurationHome.remove( oac.getUuid( ) ) );

            this.addNewHistory( uuid, HistoryTypeEnum.DELETE, user );
        } );

    }

    @Override
    public List<String> getIdEntitiesList( final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy )
    {
        return PlanHome.getIdPlansList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Plan> getEntitiesListByIds( final List<String> listIds )
    {
        return PlanHome.getPlansListByIds( listIds );
    }


    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getPlansByTags(List<String> tags )
    {
        return PlanHome.getuuidsByTags( tags );
    }

}

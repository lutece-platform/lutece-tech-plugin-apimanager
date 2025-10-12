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
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionHome;

import java.util.List;
import java.util.Map;

public class SubscriptionService extends AbstractService<Subscription>
{

    private static SubscriptionService _instance;

    private SubscriptionService( )
    {
    }

    public static SubscriptionService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new SubscriptionService( );
        }
        return _instance;
    }

    @Override
    public void create( final Subscription entity, final String user )
    {
        final String uuid = SubscriptionHome.create( entity ).getUuid( );
        this.addNewHistory( uuid, HistoryTypeEnum.CREATE, user,
                (entity.getApi() !=null && entity.getClient()!=null? "SOUSCRIPTION "+ (entity.getApi() != null? entity.getApi().getName(): "") + " par " +(entity.getClient() != null? entity.getClient().getName(): ""):"") );
    }

    @Override
    public void update( final Subscription entity, final String user )
    {
        SubscriptionHome.update( entity );
        this.addNewHistory( entity.getUuid( ), HistoryTypeEnum.UPDATE, user,
                (entity.getApi() !=null && entity.getClient()!=null? "SOUSCRIPTION "+ (entity.getApi() != null? entity.getApi().getName(): "") + " par " +(entity.getClient() != null? entity.getClient().getName(): ""):"") );
    }

    @Override
    public void delete( final String uuid, final String user )
    {
        SubscriptionHome.remove( uuid );
        this.addNewHistory( uuid, HistoryTypeEnum.DELETE, user, "SOUSCRIPTION " + uuid );
    }

    @Override
    public List<String> getIdEntitiesList( final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy )
    {
        return SubscriptionHome.getIdSubscriptionsList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Subscription> getEntitiesListByIds( final List<String> listIds )
    {
        return SubscriptionHome.getSubscriptionsListByIds( listIds );
    }

    public List<String> getIdSubscriptionsByResourceAndEnvironementAndClient(final String resourceUuid, final String environementUuid, final String clientUuid)
    {
        return SubscriptionHome.getIdSubscriptionsByResourceAndEnvironementAndClient( resourceUuid, environementUuid,clientUuid );
    }

    public List<String> getIdSubscriptionsByResource(final String resourceUuid)
    {
        return SubscriptionHome.getIdSubscriptionsByResource( resourceUuid);
    }

    public List<String> getIdSubscriptionsByClient(final String clientUuid)
    {
        return SubscriptionHome.getIdSubscriptionsByClient( clientUuid);
    }


    public List<String> getIdSubscriptionsByApi(final String apiUuid)
    {
        return SubscriptionHome.getIdSubscriptionsByApi( apiUuid);
    }


    public void archive( final String uuid, final String user )
    {
        SubscriptionHome.findByPrimaryKey( uuid ).ifPresent( subscription -> {
            subscription.setArchived( true );
            SubscriptionHome.update( subscription );
            this.addNewHistory( uuid, HistoryTypeEnum.ARCHIVE, user, "" );
        } );
    }

    public List<String> getDistinctStatus() {
        return SubscriptionHome.getDistinctStatus( );
    }
}

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

import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
import fr.paris.lutece.plugins.apimanager.business.client.ClientSecret;
import fr.paris.lutece.plugins.apimanager.business.client.ClientSecretHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientService extends AbstractService<Client>
{
    private static ClientService _instance;

    private ClientService( )
    {
    }

    public static ClientService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new ClientService( );
        }
        return _instance;
    }

    @Override
    public void create( final Client entity, final String user )
    {
        final Client client = ClientHome.create( entity );
        final String uuid = client.getUuid( );
        entity.getSecretList( ).forEach( clientSecret -> {
            clientSecret.setClient( client );
            ClientSecretHome.create( clientSecret );
        } );
        this.addNewHistory( uuid, HistoryTypeEnum.CREATE, user );
    }

    @Override
    public void update( final Client entity, final String user )
    {
        ClientHome.update( entity );
        this.addNewHistory( entity.getUuid( ), HistoryTypeEnum.UPDATE, user );
    }


    public void updateStatus( final String clientUuid, final String status, final String user )
    {
        ClientHome.updateStatus(clientUuid,status);
        this.addNewHistory( clientUuid, HistoryTypeEnum.UPDATE, user );
    }

    /**
     * The delete function is not available for client. This method performs the archive action. You shouldn't use this method.
     *
     * @param uuid
     *            the client uuid
     * @param user
     *            the user
     * @see ClientService#archive(String, String)
     */
    @Override
    @Deprecated
    public void delete( final String uuid, final String user )
    {
        this.archive( uuid, user );
    }

    @Override
    public List<String> getIdEntitiesList( final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy )
    {
        return ClientHome.getIdClientsList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Client> getEntitiesListByIds( final List<String> listIds )
    {
        return ClientHome.getClientsListByIds( listIds );
    }

    public Optional<Client> getClientById( final String clientId, final Optional<String> secretEnvToLoad )
    {
        final Optional<Client> clientOpt = ClientHome.findByPrimaryKey( clientId );
        if ( secretEnvToLoad != null && secretEnvToLoad.isPresent( ) && clientOpt != null && clientOpt.isPresent( ) )
        {
            final ClientSecret clientSecret = ClientSecretHome.getByClientUuidAndEnv( clientId, secretEnvToLoad.get( ) );
            if ( clientSecret != null )
            {
                final Client client = clientOpt.get( );
                client.getSecretList( ).add( clientSecret );
                return Optional.of( client );
            }
        }
        return clientOpt;
    }

    public void archive( final String uuid, final String user )
    {
        ClientHome.findByPrimaryKey( uuid ).ifPresent( client -> {
            client.setArchived( true );
            ClientHome.update( client );
            this.addNewHistory( uuid, HistoryTypeEnum.ARCHIVE, user );
        } );
    }

    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getAvailableTags(List<String> listIds )
    {
        return ClientHome.getAvailableTags( listIds );
    }


    /**
     * returns the TAGS of all the entities.
     *
     * @return List of tags
     */
    public List<String> getClientsByTags(List<String> tags )
    {
        return ClientHome.getuuidsByTags( tags );
    }
}

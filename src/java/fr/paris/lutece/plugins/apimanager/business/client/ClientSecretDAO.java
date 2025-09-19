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
package fr.paris.lutece.plugins.apimanager.business.client;

import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientSecretDAO extends AbstractFilterDao implements IClientSecretDAO
{

    // Constants
    private static final String TABLE_NAME = "apimanager_client_secret";

    private static final String SQL_QUERY_INSERT = "INSERT INTO " + TABLE_NAME + " ( uuid, uuid_client, environnement, secret ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECT_BY_CLIENT_ID_AND_ENV = "SELECT uuid, uuid_client, environnement, secret FROM " + TABLE_NAME
            + " WHERE uuid_client = ? AND environnement = ? ";
    private static final String SQL_QUERY_DELETE_BY_CLIENT_ID = "DELETE FROM " + TABLE_NAME + " WHERE uuid_client = ? ";

    /**
     * Constructor
     */
    public ClientSecretDAO( )
    {
        initMapSql( ClientSecret.class ); // Maps with name and type of each databases column associated to the business class attributes
    }

    @Override
    public void insert( final ClientSecret clientSecret, final Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            final String uuid = UUID.randomUUID( ).toString( );
            daoUtil.setString( nIndex++, uuid );
            daoUtil.setString( nIndex++, clientSecret.getClient( ).getUuid() );
            daoUtil.setString( nIndex++, clientSecret.getEnvironnement( ).getUuid() );
            daoUtil.setString( nIndex, clientSecret.getSecret( ) ); // TODO HASHER LE SECRET

            daoUtil.executeUpdate( );
            clientSecret.setUuid( uuid );
        }

    }

    @Override
    public void deleteByClientId( final String clientId, final Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_CLIENT_ID, plugin ) )
        {
            daoUtil.setString( 1, clientId );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public ClientSecret selectByClientUuidAndEnv( final String clientUuid, final String env, final Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CLIENT_ID_AND_ENV, plugin ) )
        {
            int index = 1;
            daoUtil.setString( index++, clientUuid );
            daoUtil.setString( index, env );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                return loadFromDaoUtil( daoUtil );
            }
            return null;
        }
    }

    private ClientSecret loadFromDaoUtil( final DAOUtil daoUtil )
    {
        final ClientSecret clientSecret = new ClientSecret( );
        int nIndex = 1;

        clientSecret.setUuid( daoUtil.getString( nIndex++ ) );
        clientSecret.setClient(ClientHome.findByPrimaryKey(daoUtil.getString( nIndex++ )).orElse(null) );
        clientSecret.setEnvironnement(EnvironementHome.findByPrimaryKey(daoUtil.getString( nIndex++ )).orElse(null) );
        clientSecret.setSecret( daoUtil.getString( nIndex++ ) );

        return clientSecret;
    }

}

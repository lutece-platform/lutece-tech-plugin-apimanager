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

package fr.paris.lutece.plugins.apimanager.business.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
import fr.paris.lutece.plugins.apimanager.business.IDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides Data Access methods for Api objects
 */
public final class ApiDAO extends AbstractFilterDao implements IApiDAO
{
    // Constants
    private static final String TABLE_NAME = "apimanager_api";

    private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_api ( uuid, name, description, path, openapi ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_api WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_api SET name = ?, description = ?, path = ?, openapi = ? WHERE uuid = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT uuid, name, description, path, openapi FROM apimanager_api";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_api";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";

    private final ObjectMapper objectMapper = new ObjectMapper( );

    /**
     * Constructor
     */
    public ApiDAO( )
    {

        initMapSql( Api.class ); // Maps with name and type of each databases column associated to the business class attributes
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Api api, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            final String uuid = UUID.randomUUID( ).toString( );
            daoUtil.setString( nIndex++, uuid );
            daoUtil.setString( nIndex++, api.getName( ) );
            daoUtil.setString( nIndex++, api.getDescription( ) );
            daoUtil.setString( nIndex++, api.getPath( ) );
            daoUtil.setString( nIndex++, objectMapper.writeValueAsString( api.getOpenapi( ) ) );

            daoUtil.executeUpdate( );
            api.setUuid( uuid );
            this.insertTags( uuid, api.getTags( ), plugin );
        }
        catch( JsonProcessingException e )
        {
            throw new AppException( e.getMessage( ), e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Api> load( String nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setString( 1, nKey );
            daoUtil.executeQuery( );
            Api api = null;

            if ( daoUtil.next( ) )
            {
                api = loadFromDaoUtil( daoUtil, plugin );
            }

            return Optional.ofNullable( api );
        }
        catch( JsonProcessingException e )
        {
            throw new AppException( e.getMessage( ), e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( String nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setString( 1, nKey );
            daoUtil.executeUpdate( );
            this.deleteTags( nKey, plugin );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Api api, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, api.getName( ) );
            daoUtil.setString( nIndex++, api.getDescription( ) );
            daoUtil.setString( nIndex++, api.getPath( ) );
            daoUtil.setString( nIndex++, objectMapper.writeValueAsString( api.getOpenapi( ) ) );
            daoUtil.setString( nIndex, api.getUuid( ) );

            daoUtil.executeUpdate( );
            this.deleteAndInsertTags( api.getUuid( ), api.getTags( ), plugin );
        }
        catch( JsonProcessingException e )
        {
            throw new AppException( e.getMessage( ), e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Api> selectEntitiesList( Plugin plugin )
    {
        List<Api> apiList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                apiList.add( loadFromDaoUtil( daoUtil, plugin ) );
            }

            return apiList;
        }
        catch( JsonProcessingException e )
        {
            throw new AppException( e.getMessage( ), e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdEntitiesList( Plugin plugin, Map<String, String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> apiList = new ArrayList<>( );

        String strSelectStatement = prepareSelectStatement( SQL_QUERY_SELECTALL_ID, TABLE_NAME, mapFilterCriteria, strColumnToOrder, strSortMode );

        try ( DAOUtil daoUtil = new DAOUtil( strSelectStatement, plugin ) )
        {

            int nIndex = 1;

            for ( Map.Entry<String, String> filter : mapFilterCriteria.entrySet( ) )
            {

                if ( StringUtils.isNotBlank( filter.getValue( ) ) && _mapSql.containsKey( filter.getKey( ) ) )
                {
                    daoUtil.setString( nIndex++, filter.getValue( ) );
                }
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                apiList.add( daoUtil.getString( 1 ) );
            }

            return apiList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectEntitiesReferenceList( Plugin plugin )
    {
        ReferenceList apiList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                apiList.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
            }

            return apiList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Api> selectEntitiesListByIds( Plugin plugin, List<String> listIds )
    {
        List<Api> apiList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( String id : listIds )
                {
                    daoUtil.setString( index++, id );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    apiList.add( loadFromDaoUtil( daoUtil, plugin ) );
                }
            }
            catch( JsonProcessingException e )
            {
                throw new AppException( e.getMessage( ), e );
            }
        }
        return apiList;

    }

    private Api loadFromDaoUtil( DAOUtil daoUtil, Plugin plugin ) throws JsonProcessingException
    {

        Api api = new Api( );
        int nIndex = 1;

        final String uuid = daoUtil.getString( nIndex++ );
        api.setUuid( uuid );
        api.setName( daoUtil.getString( nIndex++ ) );
        api.setDescription( daoUtil.getString( nIndex++ ) );
        api.setPath( daoUtil.getString( nIndex++ ) );
        final String jsonMap = daoUtil.getString( nIndex );
        if ( StringUtils.isNotEmpty( jsonMap ) )
        {
            api.setOpenapi( objectMapper.readValue( jsonMap, new TypeReference<Map<String, Object>>( )
            {
            } ) );
        }
        api.setTags( this.selectTags( uuid, plugin ) );

        return api;
    }
}

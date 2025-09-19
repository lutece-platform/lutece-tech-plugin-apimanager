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

package fr.paris.lutece.plugins.apimanager.business.resource;

import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
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
 * This class provides Data Access methods for Resource objects
 */
public final class ResourceDAO extends AbstractFilterDao implements IResourceDAO
{
    // Constants
    private static final String TABLE_NAME = "apimanager_resource";

    private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_resource ( uuid, uuid_plan, path, verb, uuid_rewrite_url, matcher_type, name,uuid_environement,uuid_api,status, trace_enabled ) VALUES ( ?, ?, ?, ?, ?, ?, ?,?,?,?,? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_resource WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_resource SET uuid_plan = ?, path = ?, verb = ?, uuid_rewrite_url = ?, matcher_type = ?, name = ?, uuid_environement = ?, uuid_api = ?, status = ? , trace_enabled = ? WHERE uuid = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT uuid, uuid_plan, path, verb, uuid_rewrite_url, matcher_type, name ,uuid_environement,uuid_api,status, trace_enabled FROM apimanager_resource";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_resource";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";


    private static final String SQL_QUERY_SELECTALL_ID_LINKED_TO_INSTANCE = "SELECT uuid_resource FROM apimanager_deployed WHERE uuid_instance = ?";
    private static final String SQL_QUERY_SELECTALL_ID_NOT_LINKED_TO_INSTANCE = SQL_QUERY_SELECTALL_ID + " WHERE uuid NOT IN ( "
            + SQL_QUERY_SELECTALL_ID_LINKED_TO_INSTANCE + " )";
    private static final String SQL_QUERY_LINK_RESOURCE = "INSERT INTO apimanager_deployed (uuid, uuid_resource, uuid_instance) VALUES ( ?, ?, ? )";

    private static final String SQL_QUERY_DELETE_LINK_RESOURCE = "DELETE FROM apimanager_deployed WHERE uuid_instance = ? AND uuid_resource = ?";
    private static final String SQL_QUERY_DELETE_LINKS = "DELETE FROM apimanager_deployed WHERE uuid_resource = ?";

    /**
     * Constructor
     */
    public ResourceDAO( )
    {

        initMapSql( Resource.class ); // Maps with name and type of each databases column associated to the business class attributes
        _mapSql.remove( "plan" );
        _mapSql.remove( "environement" );
        _mapSql.remove( "api" );
        _mapSql.put( "uuid_plan", "String" );
        _mapSql.put( "uuid_environement", "String" );
        _mapSql.put( "uuid_api", "String" );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Resource resource, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            final String uuid = UUID.randomUUID( ).toString( );
            daoUtil.setString( nIndex++, uuid );
            daoUtil.setString( nIndex++, resource.getPlan( ) != null ? resource.getPlan( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, resource.getPath( ) );
            daoUtil.setString( nIndex++, resource.getVerb( ).name( ) );
            daoUtil.setString( nIndex++, resource.getRewriteUrl( ) != null ? resource.getRewriteUrl( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, resource.getMatcherType( ) );
            daoUtil.setString( nIndex++, resource.getName( ) );
            daoUtil.setString( nIndex++, resource.getEnvironement( )!=null?resource.getEnvironement().getUuid( ):null );
            daoUtil.setString( nIndex++, resource.getApi( )!=null?resource.getApi().getUuid( ):null );
            daoUtil.setString( nIndex++, resource.getStatus( ));
            daoUtil.setBoolean( nIndex++, resource.getTraceEnabled( ));

            daoUtil.executeUpdate( );
            resource.setUuid( uuid );

            resource.getHeaderMatchings( ).forEach( hm -> {
                hm.setUuidResource( uuid );
                ResourceHeaderMatchingHome.create( hm );
            } );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Resource> load( String nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setString( 1, nKey );
            daoUtil.executeQuery( );
            Resource resource = null;

            if ( daoUtil.next( ) )
            {
                resource = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( resource );
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
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Resource resource, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, resource.getPlan( ) != null ? resource.getPlan( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, resource.getPath( ) );
            daoUtil.setString( nIndex++, resource.getVerb( ).name( ) );
            daoUtil.setString( nIndex++, resource.getRewriteUrl( ) != null ? resource.getRewriteUrl( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, resource.getMatcherType( ) );
            daoUtil.setString( nIndex++, resource.getName( ) );
            daoUtil.setString( nIndex++, resource.getUuid( ) );
            daoUtil.setString( nIndex++, resource.getEnvironement( ) != null ? resource.getEnvironement( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, resource.getApi( ) != null ? resource.getApi( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, resource.getStatus( ) );
            daoUtil.setBoolean( nIndex++, resource.getTraceEnabled( ) );

            daoUtil.executeUpdate( );


            ResourceHeaderMatchingHome.getIdResourceHeaderMatchingsList( Map.of( "uuid_resource", resource.getUuid( ) ), null, null ).forEach( ResourceHeaderMatchingHome::remove );
            resource.getHeaderMatchings( ).forEach( hm -> {
                hm.setUuidResource( resource.getUuid( ) );
                ResourceHeaderMatchingHome.create( hm );
            } );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Resource> selectEntitiesList( Plugin plugin )
    {
        List<Resource> resourceList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                resourceList.add( loadFromDaoUtil( daoUtil ) );
            }

            return resourceList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdEntitiesList( Plugin plugin, Map<String, String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> resourceList = new ArrayList<>( );

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
                resourceList.add( daoUtil.getString( 1 ) );
            }

            return resourceList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectEntitiesReferenceList( Plugin plugin )
    {
        ReferenceList resourceList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                resourceList.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
            }

            return resourceList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Resource> selectEntitiesListByIds( Plugin plugin, List<String> listIds )
    {
        List<Resource> resourceList = new ArrayList<>( );

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
                    resourceList.add( loadFromDaoUtil( daoUtil ) );
                }
            }
        }
        return resourceList;

    }

    private Resource loadFromDaoUtil( DAOUtil daoUtil )
    {

        Resource resource = new Resource( );
        int nIndex = 1;

        final String uuidResource = daoUtil.getString( nIndex++ );
        resource.setUuid( uuidResource );
        resource.setPlan( PlanHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse( null ) );
        resource.setPath( daoUtil.getString( nIndex++ ) );
        final String verbStr = daoUtil.getString( nIndex++ );
        resource.setVerb( verbStr != null ? ResourceVerbEnum.valueOf( verbStr ) : null );
        resource.setRewriteUrl( ResourceRewriteUrlHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse( null ) );
        resource.setMatcherType( daoUtil.getString( nIndex++ ) );
        resource.setName( daoUtil.getString( nIndex++ ) );
        resource.setEnvironement(EnvironementHome.findByPrimaryKey( daoUtil.getString( nIndex++ )).orElse( null ) );
        resource.setApi(ApiHome.findByPrimaryKey( daoUtil.getString( nIndex++ )).orElse( null ) );
        resource.setStatus( daoUtil.getString( nIndex++ ) );
        resource.setTraceEnabled( daoUtil.getBoolean( nIndex++ ) );

        resource.setHeaderMatchings( ResourceHeaderMatchingHome
                .getResourceHeaderMatchingsListByIds( ResourceHeaderMatchingHome.getIdResourceHeaderMatchingsList( Map.of( "uuid_resource", uuidResource ), null, null ) ) );


        return resource;
    }
}

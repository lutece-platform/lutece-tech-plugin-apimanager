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

package fr.paris.lutece.plugins.apimanager.business.meecrogate;

import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
import fr.paris.lutece.plugins.apimanager.business.environement.EnvironementHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Statement;
import java.util.*;

/**
 * This class provides Data Access methods for Instance objects
 */
public final class MeecrogateDAO extends AbstractFilterDao implements IMeecrogateDAO
{
    // Constants
    private static final String TABLE_NAME = "apimanager_instance";

    private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_meecrogate_instance ( uuid, name, description, base_url) VALUES ( ?, ?, ?, ?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_meecrogate_instance WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_meecrogate_instance SET name = ?, description = ?, base_url = ? WHERE uuid = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT uuid, name, description, base_url FROM apimanager_meecrogate_instance";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_meecrogate_instance";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";



    /**
     * Constructor
     */
    public MeecrogateDAO( )
    {

        initMapSql( Meecrogate.class ); // Maps with name and type of each databases column associated to the business class attributes

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert(Meecrogate meecrogate, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            final String uuid = UUID.randomUUID( ).toString( );
            daoUtil.setString( nIndex++, uuid );
            daoUtil.setString( nIndex++, meecrogate.getName( ) );
            daoUtil.setString( nIndex++, meecrogate.getDescription( ) );
            daoUtil.setString( nIndex++, meecrogate.getBaseUrl( ) );

            daoUtil.executeUpdate( );
            meecrogate.setUuid( uuid );
            this.insertTags( uuid, meecrogate.getTags( ), plugin );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Meecrogate> load(String nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setString( 1, nKey );
            daoUtil.executeQuery( );
            Meecrogate instance = null;

            if ( daoUtil.next( ) )
            {
                instance = loadFromDaoUtil( daoUtil, plugin );
            }

            return Optional.ofNullable( instance );
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
    public void store(Meecrogate instance, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, instance.getName( ) );
            daoUtil.setString( nIndex++, instance.getDescription( ) );
            daoUtil.setString( nIndex++, instance.getBaseUrl( ) );
            daoUtil.setString( nIndex, instance.getUuid( ) );

            daoUtil.executeUpdate( );
            this.deleteAndInsertTags( instance.getUuid( ), instance.getTags( ), plugin );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Meecrogate> selectEntitiesList(Plugin plugin )
    {
        List<Meecrogate> instanceList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                instanceList.add( loadFromDaoUtil( daoUtil, plugin ) );
            }

            return instanceList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdEntitiesList( Plugin plugin, Map<String, String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> instanceList = new ArrayList<>( );

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
                instanceList.add( daoUtil.getString( 1 ) );
            }

            return instanceList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectEntitiesReferenceList( Plugin plugin )
    {
        ReferenceList instanceList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                instanceList.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
            }

            return instanceList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Meecrogate> selectEntitiesListByIds(Plugin plugin, List<String> listIds )
    {
        List<Meecrogate> instanceList = new ArrayList<>( );

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
                    instanceList.add( loadFromDaoUtil( daoUtil, plugin ) );
                }
            }
        }
        return instanceList;

    }



    private Meecrogate loadFromDaoUtil(DAOUtil daoUtil, Plugin plugin )
    {

        Meecrogate instance = new Meecrogate( );
        int nIndex = 1;

        final String uuid = daoUtil.getString( nIndex++ );
        instance.setUuid( uuid );
        instance.setName( daoUtil.getString( nIndex++ ) );
        instance.setDescription( daoUtil.getString( nIndex++ ) );
        instance.setBaseUrl( daoUtil.getString( nIndex++ ) );

        return instance;
    }


}

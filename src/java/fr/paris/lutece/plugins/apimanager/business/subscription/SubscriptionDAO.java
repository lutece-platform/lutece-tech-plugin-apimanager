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

package fr.paris.lutece.plugins.apimanager.business.subscription;

import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
import fr.paris.lutece.plugins.apimanager.business.IDAO;
import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
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
 * This class provides Data Access methods for Subscription objects
 */
public final class SubscriptionDAO extends AbstractFilterDao implements ISubscriptionDAO
{
    // Constants
    private static final String TABLE_NAME = "apimanager_subscription";

    private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_subscription ( uuid, uuid_client, uuid_plan, environnement, trace_enabled ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_subscription WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_subscription SET uuid_client = ?, uuid_plan = ?, environnement = ?, trace_enabled = ? WHERE uuid = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT uuid, uuid_client, uuid_plan, environnement, trace_enabled FROM apimanager_subscription";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_subscription";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";

    private static final String FILTER_CLIENT = "client";
    private static final String FILTER_API = "api";
    private static final String FILTER_PLAN = "plan";

    /**
     * Constructor
     */
    public SubscriptionDAO( )
    {

        initMapSql( Subscription.class ); // Maps with name and type of each databases column associated to the business class attributes
        _mapSql.remove( "plan" );
        _mapSql.remove( "client" );
        _mapSql.remove( "api" );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Subscription subscription, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            final String uuid = UUID.randomUUID( ).toString( );
            daoUtil.setString( nIndex++, uuid );
            daoUtil.setString( nIndex++, subscription.getClient( ) != null ? subscription.getClient( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, subscription.getPlan( ) != null ? subscription.getPlan( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, subscription.getEnvironnement( ) );
            daoUtil.setBoolean( nIndex++, subscription.getTraceEnabled( ) );

            daoUtil.executeUpdate( );
            subscription.setUuid( uuid );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Subscription> load( String nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setString( 1, nKey );
            daoUtil.executeQuery( );
            Subscription subscription = null;

            if ( daoUtil.next( ) )
            {
                subscription = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( subscription );
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
    public void store( Subscription subscription, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, subscription.getClient( ) != null ? subscription.getClient( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, subscription.getPlan( ) != null ? subscription.getPlan( ).getUuid( ) : null );
            daoUtil.setString( nIndex++, subscription.getEnvironnement( ) );
            daoUtil.setBoolean( nIndex++, subscription.getTraceEnabled( ) );
            daoUtil.setString( nIndex, subscription.getUuid( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Subscription> selectEntitiesList( Plugin plugin )
    {
        List<Subscription> subscriptionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                subscriptionList.add( loadFromDaoUtil( daoUtil ) );
            }

            return subscriptionList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdEntitiesList( Plugin plugin, Map<String, String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> subscriptionList = new ArrayList<>( );

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
                subscriptionList.add( daoUtil.getString( 1 ) );
            }

            return subscriptionList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectEntitiesReferenceList( Plugin plugin )
    {
        ReferenceList subscriptionList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                subscriptionList.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
            }

            return subscriptionList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Subscription> selectEntitiesListByIds( Plugin plugin, List<String> listIds )
    {
        List<Subscription> subscriptionList = new ArrayList<>( );

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
                    subscriptionList.add( loadFromDaoUtil( daoUtil ) );
                }
            }
        }
        return subscriptionList;

    }

    private Subscription loadFromDaoUtil( DAOUtil daoUtil )
    {

        Subscription subscription = new Subscription( );
        int nIndex = 1;

        subscription.setUuid( daoUtil.getString( nIndex++ ) );
        subscription.setClient( ClientHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse( null ) );
        subscription.setPlan( PlanHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse( null ) );
        subscription.setEnvironnement( daoUtil.getString( nIndex++ ) );
        subscription.setTraceEnabled( daoUtil.getBoolean( nIndex ) );

        return subscription;
    }

    @Override
    protected String addWhereClauses( Map<String, String> mapFilterCriteria, String tableName )
    {
        final String whereClauses = super.addWhereClauses( mapFilterCriteria, tableName );
        final StringBuilder additionalClauses = new StringBuilder( );
        for ( final Map.Entry<String, String> filter : mapFilterCriteria.entrySet( ) )
        {
            if ( StringUtils.isNotBlank( filter.getValue( ) ) )
            {
                if ( filter.getKey( ).equals( FILTER_CLIENT ) )
                {
                    additionalClauses.append( whereClauses.isEmpty( ) ? " WHERE " : " AND " );
                    additionalClauses.append( " uuid_client in ( select uuid from apimanager_client where name like '%" ).append( filter.getValue( ) )
                            .append( "%' )" );
                }
                if ( filter.getKey( ).equals( FILTER_API ) )
                {
                    additionalClauses.append( ( whereClauses.isEmpty( ) && additionalClauses.length( ) == 0 ) ? " WHERE " : " AND " );
                    additionalClauses.append( " uuid_plan in ( select uuid from apimanager_plan where name like '%" ).append( filter.getValue( ) )
                            .append( "%' )" );
                }
                if ( filter.getKey( ).equals( FILTER_PLAN ) )
                {
                    additionalClauses.append( ( whereClauses.isEmpty( ) && additionalClauses.length( ) == 0 ) ? " WHERE " : " AND " );
                    additionalClauses.append( " uuid_plan in ( select uuid from apimanager_plan where name like '%" ).append( filter.getValue( ) )
                            .append( "%' )" );
                }
            }
        }
        return whereClauses + additionalClauses.toString( );
    }
}

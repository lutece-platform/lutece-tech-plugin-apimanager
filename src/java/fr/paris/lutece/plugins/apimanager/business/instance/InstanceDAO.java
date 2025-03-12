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


package fr.paris.lutece.plugins.apimanager.business.instance;

import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
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
 * This class provides Data Access methods for Instance objects
 */
public final class InstanceDAO extends AbstractFilterDao implements IInstanceDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_instance ( uuid, uuid_api, host, port, name, environnement, health_path, health_port, health_freq ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_instance WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_instance SET uuid_api = ? host = ?, port = ?, name = ?, environnement = ?, health_path = ?, health_port = ?, health_freq = ? WHERE uuid = ?";
   
	private static final String SQL_QUERY_SELECTALL = "SELECT uuid, uuid_api, host, port, name, environnement, health_path, health_port, health_freq FROM apimanager_instance";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_instance";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
	private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";


	/**
     * Constructor
     */
	public InstanceDAO() {

		initMapSql(Instance.class); //Maps with name and type of each databases column associated to the business class attributes 
	}

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Instance instance, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
			final String uuid = UUID.randomUUID().toString();
			daoUtil.setString( nIndex++, uuid );
			daoUtil.setString( nIndex++, instance.getApi() != null ? instance.getApi().getUuid( ) : null );
			daoUtil.setString( nIndex++ , instance.getHost( ) );
            daoUtil.setString( nIndex++ , instance.getPort( ) );
            daoUtil.setString( nIndex++ , instance.getName( ) );
            daoUtil.setString( nIndex++ , instance.getEnvironnement( ) );
            daoUtil.setString( nIndex++ , instance.getHealthPath( ) );
            daoUtil.setString( nIndex++ , instance.getHealthPort( ) );
            daoUtil.setInt( nIndex++ , instance.getHealthFreq( ) );
            
            daoUtil.executeUpdate( );
			instance.setUuid( uuid );
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Instance> load( String nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
	        daoUtil.setString( 1 , nKey );
	        daoUtil.executeQuery( );
	        Instance instance = null;
	
	        if ( daoUtil.next( ) )
	        {
	            instance = loadFromDaoUtil( daoUtil );
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
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setString( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Instance instance, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
				daoUtil.setString( nIndex++, instance.getApi() != null ? instance.getApi().getUuid( ) : null );
				daoUtil.setString( nIndex++ , instance.getHost( ) );
            	daoUtil.setString( nIndex++ , instance.getPort( ) );
            	daoUtil.setString( nIndex++ , instance.getName( ) );
            	daoUtil.setString( nIndex++ , instance.getEnvironnement( ) );
            	daoUtil.setString( nIndex++ , instance.getHealthPath( ) );
            	daoUtil.setString( nIndex++ , instance.getHealthPort( ) );
            	daoUtil.setInt( nIndex++ , instance.getHealthFreq( ) );
	        daoUtil.setString( nIndex , instance.getUuid( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Instance> selectInstancesList( Plugin plugin )
    {
        List<Instance> instanceList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
				instanceList.add( loadFromDaoUtil( daoUtil ) );
	        }
	
	        return instanceList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdInstancesList( Plugin plugin,  Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> instanceList = new ArrayList<>( );
        
        String strSelectStatement =  prepareSelectStatement(SQL_QUERY_SELECTALL_ID, mapFilterCriteria, strColumnToOrder, strSortMode);  
        
        try( DAOUtil daoUtil = new DAOUtil( strSelectStatement, plugin ) )
        {
        
        	int nIndex = 1;
    	        
   	        for(Map.Entry<String, String> filter : mapFilterCriteria.entrySet()) {
   	        	
   	        	if(StringUtils.isNotBlank(filter.getValue())  && _mapSql.containsKey(filter.getKey())) {
   	        		daoUtil.setString( nIndex++ , filter.getValue() );
   	        	}
   	        }
    	        
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
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
    public ReferenceList selectInstancesReferenceList( Plugin plugin )
    {
        ReferenceList instanceList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            instanceList.addItem( daoUtil.getString( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return instanceList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<Instance> selectInstancesListByIds( Plugin plugin, List<String> listIds ) {
		List<Instance> instanceList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( String id : listIds ) {
					daoUtil.setString(  index++, id );
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		            instanceList.add( loadFromDaoUtil( daoUtil ) );
		        }
	        }
	    }
		return instanceList;
		
	}


	private Instance loadFromDaoUtil (DAOUtil daoUtil) {
		
		Instance instance = new Instance(  );
		int nIndex = 1;
		
		instance.setUuid( daoUtil.getString( nIndex++ ) );
		instance.setApi(ApiHome.findByPrimaryKey(daoUtil.getString(nIndex++)).orElse( null ));
		instance.setHost( daoUtil.getString( nIndex++ ) );
		instance.setPort( daoUtil.getString( nIndex++ ) );
		instance.setName( daoUtil.getString( nIndex++ ) );
		instance.setEnvironnement( daoUtil.getString( nIndex++ ) );
		instance.setHealthPath( daoUtil.getString( nIndex++ ) );
		instance.setHealthPort( daoUtil.getString( nIndex++ ) );
		instance.setHealthFreq( daoUtil.getInt( nIndex ) );
		
		return instance;
	}
}

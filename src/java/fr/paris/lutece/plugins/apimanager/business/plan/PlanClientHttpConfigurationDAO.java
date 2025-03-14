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


package fr.paris.lutece.plugins.apimanager.business.plan;

import fr.paris.lutece.plugins.apimanager.business.AbstractFilterDao;
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
 * This class provides Data Access methods for PlanClientHttpConfiguration objects
 */
public final class PlanClientHttpConfigurationDAO extends AbstractFilterDao implements IPlanClientHttpConfigurationDAO
{
	// Constants
	private static final String TABLE_NAME = "apimanager_plan_client_http_configuration";

	private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_plan_client_http_configuration ( uuid, connection_ttl, connect_timeout, read_timeout, request_timeout, codec_max_chunk_size, codec_initial_buffer_size, codec_max_header_size, codec_max_initial_line_length ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_plan_client_http_configuration WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_plan_client_http_configuration SET connection_ttl = ?, connect_timeout = ?, read_timeout = ?, request_timeout = ?, codec_max_chunk_size = ?, codec_initial_buffer_size = ?, codec_max_header_size = ?, codec_max_initial_line_length = ? WHERE uuid = ?";
   
	private static final String SQL_QUERY_SELECTALL = "SELECT uuid, connection_ttl, connect_timeout, read_timeout, request_timeout, codec_max_chunk_size, codec_initial_buffer_size, codec_max_header_size, codec_max_initial_line_length FROM apimanager_plan_client_http_configuration";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_plan_client_http_configuration";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
	private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";


	/**
     * Constructor
     */
	public PlanClientHttpConfigurationDAO() {

		initMapSql(PlanClientHttpConfiguration.class); //Maps with name and type of each databases column associated to the business class attributes 
	}

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( PlanClientHttpConfiguration planClientHttpConfiguration, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
			final String uuid = UUID.randomUUID().toString();
			daoUtil.setString( nIndex++, uuid );
			daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getConnectionTtl( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getConnectTimeout( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getReadTimeout( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getRequestTimeout( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecMaxChunkSize( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecInitialBufferSize( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecMaxHeaderSize( ) );
            daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecMaxInitialLineLength( ) );
            
            daoUtil.executeUpdate( );
			planClientHttpConfiguration.setUuid( uuid );
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<PlanClientHttpConfiguration> load( String nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
	        daoUtil.setString( 1 , nKey );
	        daoUtil.executeQuery( );
	        PlanClientHttpConfiguration planClientHttpConfiguration = null;
	
	        if ( daoUtil.next( ) )
	        {
	            planClientHttpConfiguration = loadFromDaoUtil( daoUtil );
	        }
	
	        return Optional.ofNullable( planClientHttpConfiguration );
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
    public void store( PlanClientHttpConfiguration planClientHttpConfiguration, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getConnectionTtl( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getConnectTimeout( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getReadTimeout( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getRequestTimeout( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecMaxChunkSize( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecInitialBufferSize( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecMaxHeaderSize( ) );
            	daoUtil.setInt( nIndex++ , planClientHttpConfiguration.getCodecMaxInitialLineLength( ) );
	        daoUtil.setString( nIndex , planClientHttpConfiguration.getUuid( ));
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<PlanClientHttpConfiguration> selectPlanClientHttpConfigurationsList( Plugin plugin )
    {
        List<PlanClientHttpConfiguration> planClientHttpConfigurationList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
				planClientHttpConfigurationList.add( loadFromDaoUtil( daoUtil ) );
	        }
	
	        return planClientHttpConfigurationList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdPlanClientHttpConfigurationsList( Plugin plugin,  Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> planClientHttpConfigurationList = new ArrayList<>( );
        
        String strSelectStatement =  prepareSelectStatement(SQL_QUERY_SELECTALL_ID, TABLE_NAME, mapFilterCriteria, strColumnToOrder, strSortMode);  
        
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
	            planClientHttpConfigurationList.add( daoUtil.getString( 1 ) );
	        }
	
	        return planClientHttpConfigurationList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectPlanClientHttpConfigurationsReferenceList( Plugin plugin )
    {
        ReferenceList planClientHttpConfigurationList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            planClientHttpConfigurationList.addItem( daoUtil.getString( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return planClientHttpConfigurationList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<PlanClientHttpConfiguration> selectPlanClientHttpConfigurationsListByIds( Plugin plugin, List<String> listIds ) {
		List<PlanClientHttpConfiguration> planClientHttpConfigurationList = new ArrayList<>(  );
		
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
		            planClientHttpConfigurationList.add( loadFromDaoUtil( daoUtil ) );
		        }
	        }
	    }
		return planClientHttpConfigurationList;
		
	}


	private PlanClientHttpConfiguration loadFromDaoUtil (DAOUtil daoUtil) {
		
		PlanClientHttpConfiguration planClientHttpConfiguration = new PlanClientHttpConfiguration(  );
		int nIndex = 1;
		
		planClientHttpConfiguration.setUuid( daoUtil.getString( nIndex++ ) );
		planClientHttpConfiguration.setConnectionTtl( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setConnectTimeout( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setReadTimeout( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setRequestTimeout( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setCodecMaxChunkSize( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setCodecInitialBufferSize( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setCodecMaxHeaderSize( daoUtil.getInt( nIndex++ ) );
		planClientHttpConfiguration.setCodecMaxInitialLineLength( daoUtil.getInt( nIndex ) );
		
		return planClientHttpConfiguration;
	}
}

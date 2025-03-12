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
 * This class provides Data Access methods for Plan objects
 */
public final class PlanDAO extends AbstractFilterDao implements IPlanDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_plan ( uuid, uuid_api, name, description, active, version, uuid_rate_limiting, uuid_client_http_configuration, request_timeout, load_balancing_strategy, uuid_header_matching, oauth_enabled, uuid_oauth_configuration, trace_enabled ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_plan WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_plan SET uuid_api = ?, name = ?, description = ?, active = ?, version = ?, uuid_rate_limiting = ?, uuid_client_http_configuration = ?, request_timeout = ?, load_balancing_strategy = ?, uuid_header_matching = ?, oauth_enabled = ?, uuid_oauth_configuration = ?, trace_enabled = ? WHERE uuid = ?";
   
	private static final String SQL_QUERY_SELECTALL = "SELECT uuid, uuid_api, name, description, active, version, uuid_rate_limiting, uuid_client_http_configuration, request_timeout, load_balancing_strategy, uuid_header_matching, oauth_enabled, uuid_oauth_configuration, trace_enabled FROM apimanager_plan";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_plan";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
	private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";


	/**
     * Constructor
     */
	public PlanDAO() {

		initMapSql(Plan.class); //Maps with name and type of each databases column associated to the business class attributes 
	}

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Plan plan, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
			final String uuid = UUID.randomUUID().toString();
			daoUtil.setString( nIndex++, uuid);
			daoUtil.setString( nIndex++, plan.getApi() != null ? plan.getApi().getUuid() : null );
            daoUtil.setString( nIndex++ , plan.getName( ) );
            daoUtil.setString( nIndex++ , plan.getDescription( ) );
            daoUtil.setBoolean( nIndex++ , plan.getActive( ) );
            daoUtil.setString( nIndex++ , plan.getVersion( ) );
            daoUtil.setString( nIndex++ , plan.getRateLimiting() != null ? plan.getRateLimiting().getUuid() : null );
            daoUtil.setString( nIndex++ , plan.getClientHttpConfiguration() != null ? plan.getClientHttpConfiguration().getUuid() : null );
            daoUtil.setInt( nIndex++ , plan.getRequestTimeout( ) );
            daoUtil.setString( nIndex++ , plan.getLoadBalancingStrategy( ) );
			daoUtil.setString( nIndex++, plan.getHeaderMatching() != null ? plan.getHeaderMatching().getUuid() : null);
            daoUtil.setBoolean( nIndex++ , plan.getOauthEnabled( ) );
            daoUtil.setString( nIndex++ , plan.getOauthConfiguration() != null ?plan.getOauthConfiguration().getUuid():null );
            daoUtil.setBoolean( nIndex++ , plan.getTraceEnabled( ) );
            
            daoUtil.executeUpdate( );
			plan.setUuid(uuid);
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Plan> load( String nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
	        daoUtil.setString( 1 , nKey );
	        daoUtil.executeQuery( );
	        Plan plan = null;
	
	        if ( daoUtil.next( ) )
	        {
	            plan = loadFromDaoUtil( daoUtil );
	        }
	
	        return Optional.ofNullable( plan );
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
    public void store( Plan plan, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;

				daoUtil.setString(nIndex++, plan.getApi() != null ? plan.getApi().getUuid() : null );
            	daoUtil.setString( nIndex++ , plan.getName( ) );
            	daoUtil.setString( nIndex++ , plan.getDescription( ) );
            	daoUtil.setBoolean( nIndex++ , plan.getActive( ) );
            	daoUtil.setString( nIndex++ , plan.getVersion( ) );
				daoUtil.setString( nIndex++ , plan.getRateLimiting() != null ? plan.getRateLimiting().getUuid() : null );
				daoUtil.setString( nIndex++ , plan.getClientHttpConfiguration() != null ? plan.getClientHttpConfiguration().getUuid() : null );
            	daoUtil.setInt( nIndex++ , plan.getRequestTimeout( ) );
            	daoUtil.setString( nIndex++ , plan.getLoadBalancingStrategy( ) );
				daoUtil.setString( nIndex++, plan.getHeaderMatching() != null ? plan.getHeaderMatching().getUuid() : null);
				daoUtil.setBoolean( nIndex++ , plan.getOauthEnabled( ) );
				daoUtil.setString( nIndex++ , plan.getOauthConfiguration() != null ?plan.getOauthConfiguration().getUuid():null );
            	daoUtil.setBoolean( nIndex++ , plan.getTraceEnabled( ) );
	        daoUtil.setString( nIndex , plan.getUuid( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Plan> selectPlansList( Plugin plugin )
    {
        List<Plan> planList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
				planList.add( loadFromDaoUtil( daoUtil ) );
	        }
	
	        return planList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdPlansList( Plugin plugin,  Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> planList = new ArrayList<>( );
        
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
	            planList.add( daoUtil.getString( 1 ) );
	        }
	
	        return planList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectPlansReferenceList( Plugin plugin )
    {
        ReferenceList planList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            planList.addItem( daoUtil.getString( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return planList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<Plan> selectPlansListByIds( Plugin plugin, List<String> listIds ) {
		List<Plan> planList = new ArrayList<>(  );
		
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
		            planList.add( loadFromDaoUtil( daoUtil ) );
		        }
	        }
	    }
		return planList;
		
	}


	private Plan loadFromDaoUtil (DAOUtil daoUtil) {
		
		Plan plan = new Plan(  );
		int nIndex = 1;
		
		plan.setUuid( daoUtil.getString( nIndex++ ) );
		plan.setApi(ApiHome.findByPrimaryKey(daoUtil.getString(nIndex++)).orElse(null));
		plan.setName( daoUtil.getString( nIndex++ ) );
		plan.setDescription( daoUtil.getString( nIndex++ ) );
		plan.setActive( daoUtil.getBoolean( nIndex++ ) );
		plan.setVersion( daoUtil.getString( nIndex++ ) );
		plan.setRateLimiting( PlanRateLimitingHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse(null) );
		plan.setClientHttpConfiguration( PlanClientHttpConfigurationHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse(null) );
		plan.setRequestTimeout( daoUtil.getInt( nIndex++ ) );
		plan.setLoadBalancingStrategy( daoUtil.getString( nIndex++ ) );
		plan.setHeaderMatching( PlanHeaderMatchingHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse(null));
		plan.setOauthEnabled( daoUtil.getBoolean( nIndex++ ) );
		plan.setOauthConfiguration( PlanOauthConfigurationHome.findByPrimaryKey( daoUtil.getString( nIndex++ ) ).orElse(null));
		plan.setTraceEnabled( daoUtil.getBoolean( nIndex ) );
		
		return plan;
	}
}

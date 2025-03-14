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
 * This class provides Data Access methods for PlanHeaderMatching objects
 */
public final class PlanHeaderMatchingDAO extends AbstractFilterDao implements IPlanHeaderMatchingDAO
{
	// Constants
	private static final String TABLE_NAME = "apimanager_plan_header_matching";

	private static final String SQL_QUERY_INSERT = "INSERT INTO apimanager_plan_header_matching ( uuid, name, value, type ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM apimanager_plan_header_matching WHERE uuid = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE apimanager_plan_header_matching SET name = ?, value = ?, type = ? WHERE uuid = ?";
   
	private static final String SQL_QUERY_SELECTALL = "SELECT uuid, name, value, type FROM apimanager_plan_header_matching";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT uuid FROM apimanager_plan_header_matching";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE uuid IN (  ";
	private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE uuid = ?";


	/**
     * Constructor
     */
	public PlanHeaderMatchingDAO() {

		initMapSql(PlanHeaderMatching.class); //Maps with name and type of each databases column associated to the business class attributes 
	}

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( PlanHeaderMatching planHeaderMatching, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.NO_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
			final String uuid = UUID.randomUUID().toString();
			daoUtil.setString( nIndex++, uuid );
			daoUtil.setString( nIndex++ , planHeaderMatching.getName( ) );
            daoUtil.setString( nIndex++ , planHeaderMatching.getValue( ) );
            daoUtil.setString( nIndex++ , planHeaderMatching.getType( ) );
            
            daoUtil.executeUpdate( );
			planHeaderMatching.setUuid(uuid);
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<PlanHeaderMatching> load( String nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
	        daoUtil.setString( 1 , nKey );
	        daoUtil.executeQuery( );
	        PlanHeaderMatching planHeaderMatching = null;
	
	        if ( daoUtil.next( ) )
	        {
	            planHeaderMatching = loadFromDaoUtil( daoUtil );
	        }
	
	        return Optional.ofNullable( planHeaderMatching );
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
    public void store( PlanHeaderMatching planHeaderMatching, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            	daoUtil.setString( nIndex++ , planHeaderMatching.getName( ) );
            	daoUtil.setString( nIndex++ , planHeaderMatching.getValue( ) );
            	daoUtil.setString( nIndex++ , planHeaderMatching.getType( ) );
	        daoUtil.setString( nIndex , planHeaderMatching.getUuid( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<PlanHeaderMatching> selectPlanHeaderMatchingsList( Plugin plugin )
    {
        List<PlanHeaderMatching> planHeaderMatchingList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
				planHeaderMatchingList.add( loadFromDaoUtil( daoUtil ) );
	        }
	
	        return planHeaderMatchingList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> selectIdPlanHeaderMatchingsList( Plugin plugin,  Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<String> planHeaderMatchingList = new ArrayList<>( );
        
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
	            planHeaderMatchingList.add( daoUtil.getString( 1 ) );
	        }
	
	        return planHeaderMatchingList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectPlanHeaderMatchingsReferenceList( Plugin plugin )
    {
        ReferenceList planHeaderMatchingList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            planHeaderMatchingList.addItem( daoUtil.getString( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return planHeaderMatchingList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<PlanHeaderMatching> selectPlanHeaderMatchingsListByIds( Plugin plugin, List<String> listIds ) {
		List<PlanHeaderMatching> planHeaderMatchingList = new ArrayList<>(  );
		
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
		            planHeaderMatchingList.add( loadFromDaoUtil( daoUtil ) );
		        }
	        }
	    }
		return planHeaderMatchingList;
		
	}


	private PlanHeaderMatching loadFromDaoUtil (DAOUtil daoUtil) {
		
		PlanHeaderMatching planHeaderMatching = new PlanHeaderMatching(  );
		int nIndex = 1;
		
		planHeaderMatching.setUuid( daoUtil.getString( nIndex++ ) );
		planHeaderMatching.setName( daoUtil.getString( nIndex++ ) );
		planHeaderMatching.setValue( daoUtil.getString( nIndex++ ) );
		planHeaderMatching.setType( daoUtil.getString( nIndex ) );
		
		return planHeaderMatching;
	}
}

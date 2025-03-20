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

import fr.paris.lutece.plugins.apimanager.business.IDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Api objects
 */
public final class ApiHome
{
    // Static variable pointed at the DAO instance
    private static IApiDAO _dao = SpringContextService.getBean("apimanager.apiDAO");
    private static Plugin _plugin = PluginService.getPlugin( "apimanager" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ApiHome(  )
    {
    }

    /**
     * Create an instance of the api class
     * @param api The instance of the Api which contains the informations to store
     * @return The  instance of api which has been created with its primary key.
     */
    public static Api create( Api api )
    {
        _dao.insert( api, _plugin );

        return api;
    }

    /**
     * Update of the api which is specified in parameter
     * @param api The instance of the Api which contains the data to store
     * @return The instance of the  api which has been updated
     */
    public static Api update( Api api )
    {
        _dao.store( api, _plugin );

        return api;
    }

    /**
     * Remove the api whose identifier is specified in parameter
     * @param nKey The api Id
     */
    public static void remove( String nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a api whose identifier is specified in parameter
     * @param nKey The api primary key
     * @return an instance of Api
     */
    public static Optional<Api> findByPrimaryKey( String nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the api objects and returns them as a list
     * @return the list which contains the data of all the api objects
     */
    public static List<Api> getApisList( )
    {
        return _dao.selectEntitiesList( _plugin );
    }
    
        /**
     * Load the id of all the api objects and returns them as a list
     * @param mapFilterCriteria contains search bar names/values inputs 
     * @param strColumnToOrder contains the column name to use for orderBy statement in case of sorting request (must be null)
     * @param strSortMode contains the sortMode in case of sorting request : ASC or DESC (must be null)
     * @return the list which contains the id of all the project objects
     */
    public static List<String> getIdApisList( Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        return _dao.selectIdEntitiesList( _plugin,mapFilterCriteria,strColumnToOrder,strSortMode );
    }
    
    /**
     * Load the data of all the api objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the api objects
     */
    public static ReferenceList getApisReferenceList( )
    {
        return _dao.selectEntitiesReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Api> getApisListByIds( List<String> listIds )
    {
        return _dao.selectEntitiesListByIds( _plugin, listIds );
    }

}


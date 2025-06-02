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
package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.history.History;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public abstract class AbstractService<T>
{

    /**
     * create a new entity in database
     * 
     * @param entity
     *            the entity
     * @param user
     *            the user
     */
    public abstract void create( T entity, String user );

    /**
     * update an existing entity in database
     * 
     * @param entity
     *            the entity
     * @param user
     *            the user
     */
    public abstract void update( T entity, String user );

    /**
     * delete an existing entity in database, corresponding to the provided uuid
     * 
     * @param uuid
     *            the uuid
     * @param user
     *            the user
     */
    public abstract void delete( String uuid, String user );

    /**
     * search for entities according to the provided criterias, and returns their uuid, ordered according to the provided ordering parameters
     * 
     * @param mapFilterCriteria
     *            the criterias
     * @param columnToOrder
     *            the column to order
     * @param orderBy
     *            ASC or DESC
     * @return a list of uuid
     */
    public abstract List<String> getIdEntitiesList( Map<String, String> mapFilterCriteria, String columnToOrder, String orderBy );

    /**
     * returns the ID of all the entities.
     * 
     * @return List of uuid
     */
    public List<String> getIdEntitiesList( )
    {
        return getIdEntitiesList( Map.of( ) );
    }

    /**
     * search for entities according to the provided criterias, and returns their uuid, unordered
     * 
     * @param mapFilterCriteria
     *            the criterias
     * @return a list of uuid
     */
    public List<String> getIdEntitiesList( Map<String, String> mapFilterCriteria )
    {
        return getIdEntitiesList( mapFilterCriteria, null, null );
    }

    /**
     * get the entities corresponding to the provided uuid list
     * 
     * @param listIds
     *            the uuid list
     * @return list of entities
     */
    public abstract List<T> getEntitiesListByIds( List<String> listIds );

    /**
     * Adds a new history record
     * 
     * @param uuidRef
     *            the uuid of the referencing object
     * @param type
     *            the action type
     * @param user
     *            the user who initiated the action
     * @return the new history record
     */
    public History addNewHistory( final String uuidRef, final HistoryTypeEnum type, final String user )
    {
        final History history = new History( );
        history.setUuidRef( uuidRef );
        history.setType( type );
        history.setDate( Timestamp.from( Instant.now( ) ) );
        history.setUser( user );
        return HistoryHome.create( history );
    }

    /**
     * Get the full history of the entity identified by the provided UUID
     * 
     * @param uuidRef
     *            the entity UUID
     * @return the history list
     */
    public List<History> getHistoryListByUuidRef( final String uuidRef )
    {
        return HistoryHome.getHistoryListByUuidRef( uuidRef );
    }

}

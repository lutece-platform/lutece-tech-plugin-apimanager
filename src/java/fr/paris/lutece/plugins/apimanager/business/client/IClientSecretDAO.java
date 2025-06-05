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
package fr.paris.lutece.plugins.apimanager.business.client;

import fr.paris.lutece.plugins.apimanager.business.IDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IClientSecretDAO extends IDAO<ClientSecret>
{

    void deleteByClientId( final String clientId, final Plugin plugin );

    @Override
    default void store( final ClientSecret clientSecret, final Plugin plugin )
    {
        // no-op
    }

    @Override
    default List<ClientSecret> selectEntitiesList( final Plugin plugin )
    {
        // no-op
        return List.of( );
    }

    @Override
    default Optional<ClientSecret> load( final String nKey, final Plugin plugin )
    {
        // no-op
        return Optional.empty( );
    }

    @Override
    default void delete( final String nKey, final Plugin plugin )
    {
        // no-op
    }

    @Override
    default List<String> selectIdEntitiesList( final Plugin plugin, final Map<String, String> mapFilterCriteria, final String strColumnToOrder,
            final String strSortMode )
    {
        // no-op
        return List.of( );
    }

    @Override
    default ReferenceList selectEntitiesReferenceList( final Plugin plugin )
    {
        // no-op
        return new ReferenceList( );
    }

    @Override
    default List<ClientSecret> selectEntitiesListByIds( final Plugin plugin, final List<String> listIds )
    {
        // no-op
        return List.of( );
    }

    ClientSecret selectByClientUuidAndEnv( final String clientUuid, final String env, final Plugin plugin );
}

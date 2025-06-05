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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

public class ClientSecretHome
{
    // Static variable pointed at the DAO instance
    private static IClientSecretDAO _dao = SpringContextService.getBean( "apimanager.clientSecretDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "apimanager" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ClientSecretHome( )
    {
    }

    /**
     * Create an instance of the clientSecret class
     *
     * @param clientSecret
     *            The instance of the Client which contains the informations to store
     * @return The instance of clientSecret which has been created with its primary key.
     */
    public static ClientSecret create( final ClientSecret clientSecret )
    {
        _dao.insert( clientSecret, _plugin );

        return clientSecret;
    }

    public static ClientSecret getByClientUuidAndEnv( final String clientUuid, final String env )
    {
        return _dao.selectByClientUuidAndEnv( clientUuid, env, _plugin );
    }

    /**
     * Remove the ClientSecrets whose client identifier is specified in parameter
     *
     * @param clientId
     *            The client Id
     */
    public static void removeByClientId( final String clientId )
    {
        _dao.deleteByClientId( clientId, _plugin );
    }

}

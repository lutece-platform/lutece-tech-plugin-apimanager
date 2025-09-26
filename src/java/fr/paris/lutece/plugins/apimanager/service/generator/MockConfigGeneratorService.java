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
package fr.paris.lutece.plugins.apimanager.service.generator;

import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MockConfigGeneratorService implements IConfigGeneratorService
{
    @Override
    public void generateOauth2Client( final Client client, final Environement environment, final String comment, final String user ) throws AppException
    {
        AppLogService.info( "====================================" );
        AppLogService.info( "MOCK => generateOauth2Client called." );
        AppLogService.info( "client : " + client.getUuid( ) );
        AppLogService.info( "client secrets : " + client.getSecretList( ).stream( ).map( secret -> secret.getEnvironnement( ) + " :: " + secret.getSecret( ) )
                .collect( Collectors.toList( ) ) );
        AppLogService.info( "environment : " + environment );
        AppLogService.info( "comment : " + comment );
        AppLogService.info( "user : " + user );
        AppLogService.info( "====================================" );
        AppLogService.info( "WAIT 2 SECONDS" );
        AppLogService.info( "====================================" );

        try
        {
            TimeUnit.SECONDS.sleep( 2 );
        }
        catch( InterruptedException e )
        {
            throw new AppException( "Sleep failed", e );
        }
        throw new AppException( "Mock exception happened : you should see this on screen" );
    }

    @Override
    public void generateSubscriptions(final List<Subscription> subscriptions, final String comment, final String user ) throws AppException
    {
        for(Subscription subscription: subscriptions){
            AppLogService.info( "====================================" );
            AppLogService.info( "MOCK => generateApiManager called." );
            AppLogService.info( "client : " + subscription.getClient().getUuid( ) );
            AppLogService.info( "plan : " + subscription.getResource().getPlan().getName( ) + " => " + subscription.getResource().getPlan().getUuid( ));
            AppLogService.info( "resource : " + subscription.getResource().getName() + " => " + subscription.getResource().getUuid( ) );
            AppLogService.info( "instances : " + subscription.getResource().getInstances().stream( ).map( Instance::getUuid ).collect( Collectors.joining( " | ", "[", "]" ) ) );
            AppLogService.info( "environment : " + subscription.getEnvironement().getName() + " => " + subscription.getEnvironement().getUuid( ) );
            AppLogService.info( "comment : " + comment );
            AppLogService.info( "user : " + user );
            AppLogService.info( "====================================" );
            AppLogService.info( "WAIT 2 SECONDS" );
            AppLogService.info( "====================================" );
        }

        try
        {
            TimeUnit.SECONDS.sleep( 2 );
        }
        catch( InterruptedException e )
        {
            throw new AppException( "Sleep failed", e );
        }
    }

    @Override
    public void deleteOauth2Client(final Client client, final Environement environment, final String user ) throws AppException
    {
        AppLogService.info( "====================================" );
        AppLogService.info( "MOCK => deleteOauth2Client called." );
        AppLogService.info( "client : " + client.getUuid( ) );
        AppLogService.info( "environment : " + environment.getUuid() );
        AppLogService.info( "user : " + user );
        AppLogService.info( "====================================" );
        AppLogService.info( "WAIT 2 SECONDS" );
        AppLogService.info( "====================================" );

        // try
        // {
        // TimeUnit.SECONDS.sleep( 2 );
        // }
        // catch( InterruptedException e )
        // {
        // throw new AppException( "Sleep failed", e );
        // }
        // throw new AppException( "Mock exception happened : you should see this on screen" );
    }

    @Override
    public void deleteSubscriptions( final List<Subscription> subscriptions, final String comment, final String user ) throws AppException
    {
        for(Subscription subscription: subscriptions) {
            AppLogService.info("====================================");
            AppLogService.info("MOCK => deleteApiManager called.");
            AppLogService.info("client : " + subscription.getClient().getUuid());
            AppLogService.info("plan : " + subscription.getPlan().getUuid());
            AppLogService.info("planResources : " + subscription.getResource().getName() + " => " + subscription.getResource().getUuid( ) );
            AppLogService.info("planApiInstances : " + subscription.getResource().getInstances().stream( ).map( Instance::getUuid ).collect( Collectors.joining( " | ", "[", "]" ) ) );
            AppLogService.info("environment : " + subscription.getEnvironement().getName() + " => " + subscription.getEnvironement().getUuid( ) );
            AppLogService.info("user : " + user);
            AppLogService.info("====================================");
            AppLogService.info("WAIT 2 SECONDS");
            AppLogService.info("====================================");
        }

        // try
        // {
        // TimeUnit.SECONDS.sleep( 2 );
        // }
        // catch( InterruptedException e )
        // {
        // throw new AppException( "Sleep failed", e );
        // }
        // throw new AppException( "Mock exception happened : you should see this on screen" );

    }
}

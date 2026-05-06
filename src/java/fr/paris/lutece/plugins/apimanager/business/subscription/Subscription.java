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

import fr.paris.lutece.plugins.apimanager.business.api.Api;
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;

import java.io.Serializable;

/**
 * This is the business class for the object Subscription
 */
public class Subscription implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private Client _client;
    private String _strStatus;

    private Resource _resource;

    private Plan _plan;
    private Api _api;
    private Environement _environement;

    private boolean _bTraceEnabled;

    private boolean _bArchived;

    /**
     * Returns the Uuid
     * 
     * @return The Uuid
     */
    public String getUuid( )
    {
        return _strUuid;
    }

    /**
     * Sets the Uuid
     * 
     * @param strUuid
     *            The Uuid
     */
    public void setUuid( String strUuid )
    {
        _strUuid = strUuid;
    }

    /**
     * Returns the Client
     * 
     * @return The Client
     */
    public Client getClient( )
    {
        return _client;
    }

    /**
     * Sets the Client
     * 
     * @param client
     *            The Client
     */
    public void setClient( Client client )
    {
        _client = client;
    }

    /**
     * Returns the Resource
     * 
     * @return The Resource
     */
    public Resource getResource( )
    {
        return _resource;
    }

    /**
     * Sets the Create new scratch file from selection
     * 
     * @param resource
     *            The Resource
     */
    public void setResource( Resource resource )
    {
        _resource = resource;
    }

    /**
     * Returns the Environnement
     *
     * @return The Environnement
     */
    public Environement getEnvironement( )
    {
        return _environement;
    }

    /**
     * Sets the Environnement
     *
     * @param environnement
     *            The Environnement
     */
    public void setEnvironement( final Environement environnement )
    {
        _environement = environnement;
    }


    /**
     * Returns the Plan
     *
     * @return The Plan
     */
    public Plan getPlan( )
    {
        return _plan;
    }

    /**
     * Sets the Plan
     *
     * @param plan
     *            The Plan
     */
    public void setPlan( Plan plan )
    {
        _plan = plan;
    }

    /**
     * Returns the Api
     *
     * @return The Api
     */
    public Api getApi( )
    {
        return _api;
    }

    /**
     * Sets the Api
     *
     * @param api
     *            The Api
     */
    public void setApi( Api api )
    {
        _api = api;
    }

    /**
     * Returns the Status
     *
     * @return The Status
     */
    public String getStatus( )
    {
        return _strStatus;
    }

    /**
     * Sets the Status
     *
     * @param strStatus
     *            The Status
     */
    public void setStatus( String strStatus )
    {
        _strStatus = strStatus;
    }


    /**
     * Returns the TraceEnabled
     * 
     * @return The TraceEnabled
     */
    public boolean getTraceEnabled( )
    {
        return _bTraceEnabled;
    }

    /**
     * Sets the TraceEnabled
     * 
     * @param bTraceEnabled
     *            The TraceEnabled
     */
    public void setTraceEnabled( boolean bTraceEnabled )
    {
        _bTraceEnabled = bTraceEnabled;
    }

    /**
     * get the archived flag
     *
     * @return the archived flag
     */
    public boolean getArchived( )
    {
        return _bArchived;
    }

    /**
     * set the archived flag
     *
     * @param bArchived
     *            the archived flag
     */
    public void setArchived( final boolean bArchived )
    {
        _bArchived = bArchived;
    }

}

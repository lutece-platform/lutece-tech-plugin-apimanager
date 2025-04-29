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

import fr.paris.lutece.plugins.apimanager.business.api.Api;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the business class for the object Plan
 */
public class Plan implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private Api _api;

    @Size( max = 50, message = "#i18n{apimanager.validation.plan.Name.size}" )
    private String _strName;

    private String _strDescription;

    private boolean _bActive;

    @Size( max = 50, message = "#i18n{apimanager.validation.plan.Version.size}" )
    private String _strVersion;

    private PlanRateLimiting _rateLimiting;

    private PlanClientHttpConfiguration _clientHttpConfiguration;

    private Integer _nRequestTimeout;

    private String _strLoadBalancingStrategy;

    private List<PlanHeaderMatching> _headerMatchings = new ArrayList<>( );

    private boolean _bOauthEnabled;

    private PlanOauthConfiguration _oauthConfiguration;

    private boolean _bTraceEnabled;

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
    public void setApi( final Api api )
    {
        _api = api;
    }

    /**
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * 
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Description
     * 
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * 
     * @param strDescription
     *            The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the Active
     * 
     * @return The Active
     */
    public boolean getActive( )
    {
        return _bActive;
    }

    /**
     * Sets the Active
     * 
     * @param bActive
     *            The Active
     */
    public void setActive( boolean bActive )
    {
        _bActive = bActive;
    }

    /**
     * Returns the Version
     * 
     * @return The Version
     */
    public String getVersion( )
    {
        return _strVersion;
    }

    /**
     * Sets the Version
     * 
     * @param strVersion
     *            The Version
     */
    public void setVersion( String strVersion )
    {
        _strVersion = strVersion;
    }

    /**
     * Returns the RateLimiting
     * 
     * @return The RateLimiting
     */
    public PlanRateLimiting getRateLimiting( )
    {
        return _rateLimiting;
    }

    /**
     * Sets the RateLimiting
     * 
     * @param rateLimiting
     *            The RateLimiting
     */
    public void setRateLimiting( PlanRateLimiting rateLimiting )
    {
        _rateLimiting = rateLimiting;
    }

    /**
     * Returns the ClientHttpConfiguration
     * 
     * @return The ClientHttpConfiguration
     */
    public PlanClientHttpConfiguration getClientHttpConfiguration( )
    {
        return _clientHttpConfiguration;
    }

    /**
     * Sets the ClientHttpConfiguration
     * 
     * @param clientHttpConfiguration
     *            The ClientHttpConfiguration
     */
    public void setClientHttpConfiguration( PlanClientHttpConfiguration clientHttpConfiguration )
    {
        _clientHttpConfiguration = clientHttpConfiguration;
    }

    /**
     * Returns the RequestTimeout
     * 
     * @return The RequestTimeout
     */
    public Integer getRequestTimeout( )
    {
        return _nRequestTimeout;
    }

    /**
     * Sets the RequestTimeout
     * 
     * @param nRequestTimeout
     *            The RequestTimeout
     */
    public void setRequestTimeout( Integer nRequestTimeout )
    {
        _nRequestTimeout = nRequestTimeout;
    }

    /**
     * Returns the LoadBalancingStrategy
     * 
     * @return The LoadBalancingStrategy
     */
    public String getLoadBalancingStrategy( )
    {
        return _strLoadBalancingStrategy;
    }

    /**
     * Sets the LoadBalancingStrategy
     * 
     * @param strLoadBalancingStrategy
     *            The LoadBalancingStrategy
     */
    public void setLoadBalancingStrategy( String strLoadBalancingStrategy )
    {
        _strLoadBalancingStrategy = strLoadBalancingStrategy;
    }

    /**
     * Returns the HeaderMatchings
     * 
     * @return The HeaderMatchings
     */
    public List<PlanHeaderMatching> getHeaderMatchings( )
    {
        return _headerMatchings;
    }

    /**
     * Sets the HeaderMatchings
     * 
     * @param headerMatchings
     *            The HeaderMatchings
     */
    public void setHeaderMatchings( final List<PlanHeaderMatching> headerMatchings )
    {
        _headerMatchings = headerMatchings;
    }

    /**
     * Returns the OauthEnabled
     * 
     * @return The OauthEnabled
     */
    public boolean getOauthEnabled( )
    {
        return _bOauthEnabled;
    }

    /**
     * Sets the OauthEnabled
     * 
     * @param bOauthEnabled
     *            The OauthEnabled
     */
    public void setOauthEnabled( boolean bOauthEnabled )
    {
        _bOauthEnabled = bOauthEnabled;
    }

    /**
     * Returns the OauthConfiguration
     * 
     * @return The OauthConfiguration
     */
    public PlanOauthConfiguration getOauthConfiguration( )
    {
        return _oauthConfiguration;
    }

    /**
     * Sets the OauthConfiguration
     * 
     * @param oauthConfiguration
     *            The OauthConfiguration
     */
    public void setOauthConfiguration( PlanOauthConfiguration oauthConfiguration )
    {
        _oauthConfiguration = oauthConfiguration;
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

}

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
package fr.paris.lutece.plugins.apimanager.business.instance;

import fr.paris.lutece.plugins.apimanager.business.AbstractTagBean;
import fr.paris.lutece.plugins.apimanager.business.api.Api;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * This is the business class for the object Instance
 */
public class Instance extends AbstractTagBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private Api _api;

    private InstanceProtocolEnum _protocol;

    @Size( max = 255, message = "#i18n{apimanager.validation.instance.Host.size}" )
    private String _strHost;

    @Size( max = 50, message = "#i18n{apimanager.validation.instance.Port.size}" )
    private String _strPort;

    @Size( max = 255, message = "#i18n{apimanager.validation.instance.Name.size}" )
    private String _strName;

    @Size( max = 255, message = "#i18n{apimanager.validation.instance.Environnement.size}" )
    private String _strEnvironnement;

    @Size( max = 255, message = "#i18n{apimanager.validation.instance.HealthPath.size}" )
    private String _strHealthPath;

    @Size( max = 50, message = "#i18n{apimanager.validation.instance.HealthPort.size}" )
    private String _strHealthPort;

    private int _nHealthFreq;

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
     * Returns the Protocol
     *
     * @return The Protocol
     */
    public InstanceProtocolEnum getProtocol( )
    {
        return _protocol;
    }

    /**
     * Sets the Protocol
     *
     * @param protocol
     *            The Protocol
     */
    public void setProtocol( final InstanceProtocolEnum protocol )
    {
        _protocol = protocol;
    }

    /**
     * Returns the Host
     * 
     * @return The Host
     */
    public String getHost( )
    {
        return _strHost;
    }

    /**
     * Sets the Host
     * 
     * @param strHost
     *            The Host
     */
    public void setHost( String strHost )
    {
        _strHost = strHost;
    }

    /**
     * Returns the Port
     * 
     * @return The Port
     */
    public String getPort( )
    {
        return _strPort;
    }

    /**
     * Sets the Port
     * 
     * @param strPort
     *            The Port
     */
    public void setPort( String strPort )
    {
        _strPort = strPort;
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
     * Returns the Environnement
     * 
     * @return The Environnement
     */
    public String getEnvironnement( )
    {
        return _strEnvironnement;
    }

    /**
     * Sets the Environnement
     * 
     * @param strEnvironnement
     *            The Environnement
     */
    public void setEnvironnement( String strEnvironnement )
    {
        _strEnvironnement = strEnvironnement;
    }

    /**
     * Returns the HealthPath
     * 
     * @return The HealthPath
     */
    public String getHealthPath( )
    {
        return _strHealthPath;
    }

    /**
     * Sets the HealthPath
     * 
     * @param strHealthPath
     *            The HealthPath
     */
    public void setHealthPath( String strHealthPath )
    {
        _strHealthPath = strHealthPath;
    }

    /**
     * Returns the HealthPort
     * 
     * @return The HealthPort
     */
    public String getHealthPort( )
    {
        return _strHealthPort;
    }

    /**
     * Sets the HealthPort
     * 
     * @param strHealthPort
     *            The HealthPort
     */
    public void setHealthPort( String strHealthPort )
    {
        _strHealthPort = strHealthPort;
    }

    /**
     * Returns the HealthFreq
     * 
     * @return The HealthFreq
     */
    public int getHealthFreq( )
    {
        return _nHealthFreq;
    }

    /**
     * Sets the HealthFreq
     * 
     * @param nHealthFreq
     *            The HealthFreq
     */
    public void setHealthFreq( int nHealthFreq )
    {
        _nHealthFreq = nHealthFreq;
    }

}

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

import fr.paris.lutece.plugins.apimanager.business.AbstractTagBean;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
/**
 * This is the business class for the object Client
 */ 
public class Client extends AbstractTagBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private String _strUuid;
    
    @NotEmpty( message = "#i18n{apimanager.validation.client.Name.notEmpty}" )
    @Size( max = 50 , message = "#i18n{apimanager.validation.client.Name.size}" ) 
    private String _strName;
    
    @Size( max = 50 , message = "#i18n{apimanager.validation.client.ClientId.size}" ) 
    private String _strClientId;
    
    @Size( max = 50 , message = "#i18n{apimanager.validation.client.ClientSecret.size}" ) 
    private String _strClientSecret;
    
    @Size( max = 50 , message = "#i18n{apimanager.validation.client.CodeApp.size}" ) 
    private String _strCodeApp;
    
    private boolean _bTraceEnabled;

    /**
     * Returns the Uuid
     * @return The Uuid
     */
    public String getUuid( )
    {
        return _strUuid;
    }

    /**
     * Sets the Uuid
     * @param strUuid The Uuid
     */ 
    public void setUuid( String strUuid )
    {
        _strUuid = strUuid;
    }
    
    /**
     * Returns the Name
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * @param strName The Name
     */ 
    public void setName( String strName )
    {
        _strName = strName;
    }
    
    
    /**
     * Returns the ClientId
     * @return The ClientId
     */
    public String getClientId( )
    {
        return _strClientId;
    }

    /**
     * Sets the ClientId
     * @param strClientId The ClientId
     */ 
    public void setClientId( String strClientId )
    {
        _strClientId = strClientId;
    }
    
    
    /**
     * Returns the ClientSecret
     * @return The ClientSecret
     */
    public String getClientSecret( )
    {
        return _strClientSecret;
    }

    /**
     * Sets the ClientSecret
     * @param strClientSecret The ClientSecret
     */ 
    public void setClientSecret( String strClientSecret )
    {
        _strClientSecret = strClientSecret;
    }
    
    
    /**
     * Returns the CodeApp
     * @return The CodeApp
     */
    public String getCodeApp( )
    {
        return _strCodeApp;
    }

    /**
     * Sets the CodeApp
     * @param strCodeApp The CodeApp
     */ 
    public void setCodeApp( String strCodeApp )
    {
        _strCodeApp = strCodeApp;
    }
    
    
    /**
     * Returns the TraceEnabled
     * @return The TraceEnabled
     */
    public boolean getTraceEnabled( )
    {
        return _bTraceEnabled;
    }

    /**
     * Sets the TraceEnabled
     * @param bTraceEnabled The TraceEnabled
     */ 
    public void setTraceEnabled( boolean bTraceEnabled )
    {
        _bTraceEnabled = bTraceEnabled;
    }
    
}

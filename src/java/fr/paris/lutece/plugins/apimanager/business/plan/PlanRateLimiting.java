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

import javax.validation.constraints.Size;
import java.io.Serializable;
/**
 * This is the business class for the object PlanRateLimiting
 */ 
public class PlanRateLimiting implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private String _strUuid;
    
    private int _nMaxRequests;
    
    private int _nTimeWindow;
    
    private boolean _bDecrement;
    
    @Size( max = 50 , message = "#i18n{apimanager.validation.planratelimiting.Criteria.size}" ) 
    private String _strCriteria;
    
    @Size( max = 50 , message = "#i18n{apimanager.validation.planratelimiting.Implementation.size}" ) 
    private String _strImplementation;
    
    @Size( max = 50 , message = "#i18n{apimanager.validation.planratelimiting.Backend.size}" ) 
    private String _strBackend;

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
     * Returns the MaxRequests
     * @return The MaxRequests
     */
    public int getMaxRequests( )
    {
        return _nMaxRequests;
    }

    /**
     * Sets the MaxRequests
     * @param nMaxRequests The MaxRequests
     */ 
    public void setMaxRequests( int nMaxRequests )
    {
        _nMaxRequests = nMaxRequests;
    }
    
    
    /**
     * Returns the TimeWindow
     * @return The TimeWindow
     */
    public int getTimeWindow( )
    {
        return _nTimeWindow;
    }

    /**
     * Sets the TimeWindow
     * @param nTimeWindow The TimeWindow
     */ 
    public void setTimeWindow( int nTimeWindow )
    {
        _nTimeWindow = nTimeWindow;
    }
    
    
    /**
     * Returns the Decrement
     * @return The Decrement
     */
    public boolean getDecrement( )
    {
        return _bDecrement;
    }

    /**
     * Sets the Decrement
     * @param bDecrement The Decrement
     */ 
    public void setDecrement( boolean bDecrement )
    {
        _bDecrement = bDecrement;
    }
    
    
    /**
     * Returns the Criteria
     * @return The Criteria
     */
    public String getCriteria( )
    {
        return _strCriteria;
    }

    /**
     * Sets the Criteria
     * @param strCriteria The Criteria
     */ 
    public void setCriteria( String strCriteria )
    {
        _strCriteria = strCriteria;
    }
    
    
    /**
     * Returns the Implementation
     * @return The Implementation
     */
    public String getImplementation( )
    {
        return _strImplementation;
    }

    /**
     * Sets the Implementation
     * @param strImplementation The Implementation
     */ 
    public void setImplementation( String strImplementation )
    {
        _strImplementation = strImplementation;
    }
    
    
    /**
     * Returns the Backend
     * @return The Backend
     */
    public String getBackend( )
    {
        return _strBackend;
    }

    /**
     * Sets the Backend
     * @param strBackend The Backend
     */ 
    public void setBackend( String strBackend )
    {
        _strBackend = strBackend;
    }
    
}

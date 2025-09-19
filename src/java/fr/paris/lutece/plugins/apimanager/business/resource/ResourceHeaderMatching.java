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
package fr.paris.lutece.plugins.apimanager.business.resource;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * This is the business class for the object ResourceHeaderMatching
 */
public class ResourceHeaderMatching implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private String _strUuidResource;

    @Size( max = 50, message = "#i18n{apimanager.validation.resourceheadermatching.Name.size}" )
    private String _strName;

    @Size( max = 255, message = "#i18n{apimanager.validation.resourceheadermatching.Value.size}" )
    private String _strValue;

    @Size( max = 50, message = "#i18n{apimanager.validation.resourceheadermatching.Type.size}" )
    private String _strType;

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
     * Returns the Resource Uuid
     *
     * @return The Resource Uuid
     */
    public String getUuidResource( )
    {
        return _strUuidResource;
    }

    /**
     * Sets the Resource Uuid
     * 
     * @param strUuidResource
     *            The Resource Uuid
     */
    public void setUuidResource( final String strUuidResource )
    {
        _strUuidResource = strUuidResource;
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
     * Returns the Value
     * 
     * @return The Value
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * Sets the Value
     * 
     * @param strValue
     *            The Value
     */
    public void setValue( String strValue )
    {
        _strValue = strValue;
    }

    /**
     * Returns the Type
     * 
     * @return The Type
     */
    public String getType( )
    {
        return _strType;
    }

    /**
     * Sets the Type
     * 
     * @param strType
     *            The Type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

}

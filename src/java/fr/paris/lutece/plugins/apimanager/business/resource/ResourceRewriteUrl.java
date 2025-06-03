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

import java.io.Serializable;

public class ResourceRewriteUrl implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String _strUuid;
    private String _strTarget;
    private String _strValue;
    private ResourceRewriteUrlTypeEnum _type;

    /**
     * Get the uuid
     * 
     * @return the uuid
     */
    public String getUuid( )
    {
        return _strUuid;
    }

    /**
     * Set the uuid
     * 
     * @param strUuid
     *            the uuid
     */
    public void setUuid( final String strUuid )
    {
        _strUuid = strUuid;
    }

    /**
     * get the target
     * 
     * @return the target
     */
    public String getTarget( )
    {
        return _strTarget;
    }

    /**
     * set the target
     * 
     * @param strTarget
     *            the target
     */
    public void setTarget( final String strTarget )
    {
        _strTarget = strTarget;
    }

    /**
     * get the value
     * 
     * @return the value
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * set the value
     * 
     * @param strValue
     *            the value
     */
    public void setValue( final String strValue )
    {
        _strValue = strValue;
    }

    /**
     * get the type
     * 
     * @return the type
     */
    public ResourceRewriteUrlTypeEnum getType( )
    {
        return _type;
    }

    /**
     * set the type
     * 
     * @param type
     *            the type
     */
    public void setType( final ResourceRewriteUrlTypeEnum type )
    {
        _type = type;
    }
}

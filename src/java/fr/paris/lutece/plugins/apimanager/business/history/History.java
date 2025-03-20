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
package fr.paris.lutece.plugins.apimanager.business.history;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import javax.validation.constraints.NotNull;

/**
 * This is the business class for the object History
 */
public class History implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private String _strUuidRef;

    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    private Timestamp _dateDate;

    private HistoryTypeEnum _type;

    @Size( max = 50, message = "#i18n{apimanager.validation.history.User.size}" )
    private String _strUser;

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
    public void setUuid( final String strUuid )
    {
        _strUuid = _strUuid;
    }

    /**
     * Returns the UuidRef
     * 
     * @return The UuidRef
     */
    public String getUuidRef( )
    {
        return _strUuidRef;
    }

    /**
     * Sets the UuidRef
     * 
     * @param strUuidRef
     *            The UuidRef
     */
    public void setUuidRef( String strUuidRef )
    {
        _strUuidRef = strUuidRef;
    }

    /**
     * Returns the Date
     * 
     * @return The Date
     */
    public Timestamp getDate( )
    {
        return _dateDate;
    }

    /**
     * Sets the Date
     * 
     * @param dateDate
     *            The Date
     */
    public void setDate( Timestamp dateDate )
    {
        _dateDate = dateDate;
    }

    /**
     * Returns the Type
     * 
     * @return The Type
     */
    public HistoryTypeEnum getType( )
    {
        return _type;
    }

    /**
     * Sets the Type
     * 
     * @param type
     *            The Type
     */
    public void setType( HistoryTypeEnum type )
    {
        _type = type;
    }

    /**
     * Returns the User
     * 
     * @return The User
     */
    public String getUser( )
    {
        return _strUser;
    }

    /**
     * Sets the User
     * 
     * @param strUser
     *            The User
     */
    public void setUser( String strUser )
    {
        _strUser = strUser;
    }

}

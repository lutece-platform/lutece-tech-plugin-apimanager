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

import java.io.Serializable;

/**
 * This is the business class for the object PlanClientHttpConfiguration
 */
public class PlanClientHttpConfiguration implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private int _nConnectionTtl;

    private int _nConnectTimeout;

    private int _nReadTimeout;

    private int _nRequestTimeout;

    private int _nCodecMaxChunkSize;

    private int _nCodecInitialBufferSize;

    private int _nCodecMaxHeaderSize;

    private int _nCodecMaxInitialLineLength;

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
     * Returns the ConnectionTtl
     * 
     * @return The ConnectionTtl
     */
    public int getConnectionTtl( )
    {
        return _nConnectionTtl;
    }

    /**
     * Sets the ConnectionTtl
     * 
     * @param nConnectionTtl
     *            The ConnectionTtl
     */
    public void setConnectionTtl( int nConnectionTtl )
    {
        _nConnectionTtl = nConnectionTtl;
    }

    /**
     * Returns the ConnectTimeout
     * 
     * @return The ConnectTimeout
     */
    public int getConnectTimeout( )
    {
        return _nConnectTimeout;
    }

    /**
     * Sets the ConnectTimeout
     * 
     * @param nConnectTimeout
     *            The ConnectTimeout
     */
    public void setConnectTimeout( int nConnectTimeout )
    {
        _nConnectTimeout = nConnectTimeout;
    }

    /**
     * Returns the ReadTimeout
     * 
     * @return The ReadTimeout
     */
    public int getReadTimeout( )
    {
        return _nReadTimeout;
    }

    /**
     * Sets the ReadTimeout
     * 
     * @param nReadTimeout
     *            The ReadTimeout
     */
    public void setReadTimeout( int nReadTimeout )
    {
        _nReadTimeout = nReadTimeout;
    }

    /**
     * Returns the RequestTimeout
     * 
     * @return The RequestTimeout
     */
    public int getRequestTimeout( )
    {
        return _nRequestTimeout;
    }

    /**
     * Sets the RequestTimeout
     * 
     * @param nRequestTimeout
     *            The RequestTimeout
     */
    public void setRequestTimeout( int nRequestTimeout )
    {
        _nRequestTimeout = nRequestTimeout;
    }

    /**
     * Returns the CodecMaxChunkSize
     * 
     * @return The CodecMaxChunkSize
     */
    public int getCodecMaxChunkSize( )
    {
        return _nCodecMaxChunkSize;
    }

    /**
     * Sets the CodecMaxChunkSize
     * 
     * @param nCodecMaxChunkSize
     *            The CodecMaxChunkSize
     */
    public void setCodecMaxChunkSize( int nCodecMaxChunkSize )
    {
        _nCodecMaxChunkSize = nCodecMaxChunkSize;
    }

    /**
     * Returns the CodecInitialBufferSize
     * 
     * @return The CodecInitialBufferSize
     */
    public int getCodecInitialBufferSize( )
    {
        return _nCodecInitialBufferSize;
    }

    /**
     * Sets the CodecInitialBufferSize
     * 
     * @param nCodecInitialBufferSize
     *            The CodecInitialBufferSize
     */
    public void setCodecInitialBufferSize( int nCodecInitialBufferSize )
    {
        _nCodecInitialBufferSize = nCodecInitialBufferSize;
    }

    /**
     * Returns the CodecMaxHeaderSize
     * 
     * @return The CodecMaxHeaderSize
     */
    public int getCodecMaxHeaderSize( )
    {
        return _nCodecMaxHeaderSize;
    }

    /**
     * Sets the CodecMaxHeaderSize
     * 
     * @param nCodecMaxHeaderSize
     *            The CodecMaxHeaderSize
     */
    public void setCodecMaxHeaderSize( int nCodecMaxHeaderSize )
    {
        _nCodecMaxHeaderSize = nCodecMaxHeaderSize;
    }

    /**
     * Returns the CodecMaxInitialLineLength
     * 
     * @return The CodecMaxInitialLineLength
     */
    public int getCodecMaxInitialLineLength( )
    {
        return _nCodecMaxInitialLineLength;
    }

    /**
     * Sets the CodecMaxInitialLineLength
     * 
     * @param nCodecMaxInitialLineLength
     *            The CodecMaxInitialLineLength
     */
    public void setCodecMaxInitialLineLength( int nCodecMaxInitialLineLength )
    {
        _nCodecMaxInitialLineLength = nCodecMaxInitialLineLength;
    }

}

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

import fr.paris.lutece.plugins.apimanager.business.api.Api;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the business class for the object Resource
 */
public class Resource implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    private Plan _plan;
    private Api _api;
    private Environement _environement;

    @Size( max = 255, message = "#i18n{apimanager.validation.resource.Path.size}" )
    private String _strPath;

    private ResourceVerbEnum _verb;

    private String _strMatcherType;
    private String _strStatus;

    private ResourceRewriteUrl _rewriteUrl;
    private boolean _bTraceEnabled;

    private List<ResourceHeaderMatching> _headerMatchings = new ArrayList<>( );
    private List<Instance> _instances = new ArrayList<>( );

    private Integer _nRequestTimeout;
    @Valid
    @Pattern( regexp = "[a-z0-9\\-]+", message = "#i18n{apimanager.resource.labelName.help}" )
    private String _strName;


    public Resource() {

    }

    public Resource(Resource resource) {
        this.setMatcherType(resource.getMatcherType() );
        this.setTraceEnabled(resource.getTraceEnabled() );
        resource.setHeaderMatchings(new ArrayList<>());
        if(resource.getHeaderMatchings() != null){
            for(ResourceHeaderMatching headerMatching: resource.getHeaderMatchings()){
                resource.getHeaderMatchings().add(new ResourceHeaderMatching(headerMatching));
            }
        }
        this.setHeaderMatchings(resource.getHeaderMatchings() );
        this.setInstances(resource.getInstances());
        this.setPath(resource.getPath());
        this.setVerb(resource.getVerb());
        this.setName(resource.getName());
        this.setStatus(resource.getStatus());
        this.setEnvironement(resource.getEnvironement());
        this.setApi(resource.getApi());
        this.setPlan(resource.getPlan());
        this.setRewriteUrl(resource.getRewriteUrl());
    }

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
     * @param status
     *            The Status
     */
    public void setStatus( String status )
    {
        _strStatus = status;
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

    public Environement getEnvironement() {
        return _environement;
    }

    public void setEnvironement(Environement environement) {
        this._environement = environement;
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
     * Returns the HeaderMatchings
     *
     * @return The HeaderMatchings
     */
    public List<ResourceHeaderMatching> getHeaderMatchings( )
    {
        return _headerMatchings;
    }

    /**
     * Sets the HeaderMatchings
     *
     * @param headerMatchings
     *            The HeaderMatchings
     */
    public void setHeaderMatchings( final List<ResourceHeaderMatching> headerMatchings )
    {
        _headerMatchings = headerMatchings;
    }


    public List<String> getInstanceUuids() {
        return _instances != null ? _instances.stream().map(Instance::getUuid).collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<Instance> getInstances() {
        return _instances;
    }

    public void setInstances(List<Instance> _instances) {
        this._instances = _instances;
    }

    /**
     * Returns the Path
     * 
     * @return The Path
     */
    public String getPath( )
    {
        return _strPath;
    }

    /**
     * Sets the Path
     * 
     * @param strPath
     *            The Path
     */
    public void setPath( String strPath )
    {
        _strPath = strPath;
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
     * Returns the Verb
     * 
     * @return The Verb
     */
    public ResourceVerbEnum getVerb( )
    {
        return _verb;
    }

    /**
     * Sets the Verb
     * 
     * @param verb
     *            The Verb
     */
    public void setVerb( ResourceVerbEnum verb )
    {
        _verb = verb;
    }

    /**
     * Returns the Matcher Type
     *
     * @return The Matcher Type
     */
    public String getMatcherType( )
    {
        return _strMatcherType;
    }

    /**
     * Sets the Matcher Type
     *
     * @param matcherType
     *            The Matcher Type
     */
    public void setMatcherType( final String matcherType )
    {
        _strMatcherType = matcherType;
    }

    /**
     * get the rewrite url
     * 
     * @return the rewrite url
     */
    public ResourceRewriteUrl getRewriteUrl( )
    {
        return _rewriteUrl;
    }

    /**
     * set the rewrite url
     * 
     * @param rewriteUrl
     *            the rewrite url
     */
    public void setRewriteUrl( final ResourceRewriteUrl rewriteUrl )
    {
        _rewriteUrl = rewriteUrl;
    }

    /**
     * get the Name
     *
     * @return the Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * set the Name
     *
     * @param strName
     *            the Name
     */
    public void setName( final String strName )
    {
        _strName = strName;
    }
}

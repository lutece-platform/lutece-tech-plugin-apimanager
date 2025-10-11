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
package fr.paris.lutece.plugins.apimanager.business.api;

import fr.paris.lutece.plugins.apimanager.business.AbstractTagBean;
import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.environement.Environement;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This is the business class for the object Api
 */
public class Api extends AbstractTagBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private String _strUuid;

    @Size( max = 255, message = "#i18n{apimanager.validation.api.Name.size}" )
    private String _strName;

    private String _strDescription;
    private String _strVersion;
    private String _strStatus;

    @Size( max = 255, message = "#i18n{apimanager.validation.api.Path.size}" )
    private String _strPath;

    private boolean _bActive;

    private boolean _bInMaintenance;

    private Integer _nWait;

    private Map<String, Object> _openapi;

    private boolean _bArchived;

    private List<Resource> _resourceList;


    private List<Environement> _environementList;


    private List<Plan> _planList;
    private List<Client> _subscriberList;
    private List<Subscription> _subscriptionList;

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
     * Returns the Description
     *
     * @return The Description
     */
    public List<Environement> getEnvironementList( )
    {
        return _environementList;
    }

    /**
     * Sets the Description
     *
     * @param environementList
     *            The Description
     */
    public void setEnvironementList( List<Environement> environementList )
    {
        _environementList = environementList;
    }


    /**
     * Returns the Description
     *
     * @return The Description
     */
    public List<Plan> getPlanList( )
    {
        return _planList;
    }

    /**
     * Sets the Description
     *
     * @param planList
     *            The Description
     */
    public void setPlantList( List<Plan> planList )
    {
        _planList = planList;
    }


    /**
     * Returns the Subscriber
     *
     * @return The Subscriber
     */
    public List<Client> getSubscriberList( )
    {
        return _subscriberList;
    }

    /**
     * Sets the Subscriber
     *
     * @param subscriberList
     *            The Subscriber
     */
    public void setSubscriberList( List<Client> subscriberList ){this._subscriberList = subscriberList;}



    /**
     * Returns the Subscription
     *
     * @return The Subscription
     */
    public List<Subscription> getSubscriptionList( )
    {
        return _subscriptionList;
    }

    /**
     * Sets the Subscription
     *
     * @param subscriptionList
     *            The Description
     */
    public void setSubscriptionList( List<Subscription> subscriptionList ){this._subscriptionList =subscriptionList;}



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
     * @param isActive
     *            The Active
     */
    public void setActive( final boolean isActive )
    {
        _bActive = isActive;
    }

    /**
     * Returns the InMaintenance
     *
     * @return The InMaintenance
     */
    public boolean isInMaintenance( )
    {
        return _bInMaintenance;
    }

    /**
     * Sets the InMaintenance
     *
     * @param inMaintenance
     *            The InMaintenance
     */
    public void setInMaintenance( final boolean inMaintenance )
    {
        _bInMaintenance = inMaintenance;
    }

    /**
     * Returns the Wait
     *
     * @return The Wait
     */
    public Integer getWait( )
    {
        return _nWait;
    }

    /**
     * Sets the Wait
     *
     * @param nWait
     *            The Wait
     */
    public void setWait( final Integer nWait )
    {
        _nWait = nWait;
    }

    /**
     * Returns the Openapi
     * 
     * @return The Openapi
     */
    public Map<String, Object> getOpenapi( )
    {
        return _openapi;
    }

    /**
     * Sets the Openapi
     * 
     * @param openapi
     *            The Openapi
     */
    public void setOpenapi( Map<String, Object> openapi )
    {
        _openapi = openapi;
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

    public List<Resource> getResourceList() {
        return _resourceList;
    }

    public void setResourceList(List<Resource> _resourceList) {
        this._resourceList = _resourceList;
    }
}

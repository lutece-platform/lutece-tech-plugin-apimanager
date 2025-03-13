package fr.paris.lutece.plugins.apimanager.business;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTagBean {

    protected List<String> _tags = new ArrayList<>( );

    /**
     * Returns the Tags
     * @return The Tags
     */
    public List<String> getTags() {
        return _tags;
    }

    /**
     * Sets the Tags
     * @param tags The Tags
     */
    public void setTags(final List<String> tags) {
        _tags = tags;
    }
}

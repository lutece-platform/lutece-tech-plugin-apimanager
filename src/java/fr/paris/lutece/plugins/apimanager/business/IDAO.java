package fr.paris.lutece.plugins.apimanager.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IDAO<T> {

    /**
     * Insert a new record in the table.
     * @param entity instance of the entity object to insert
     * @param plugin the Plugin
     */
    void insert(T entity, Plugin plugin);

    /**
     * Update the record in the table
     * @param entity the reference of the entity
     * @param plugin the Plugin
     */
    void store( T entity, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nKey The identifier of the entity to delete
     * @param plugin the Plugin
     */
    void delete( String nKey, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @param nKey The identifier of the entity
     * @param plugin the Plugin
     * @return The instance of the entity
     */
    Optional<T> load(String nKey, Plugin plugin);

    /**
     * Load the data of all the entity objects and returns them as a list
     * @param plugin the Plugin
     * @return The list which contains the data of all the entity objects
     */
    List<T> selectEntitiesList(Plugin plugin);

    /**
     * Load the id of all the entity objects and returns them as a list
     * @param plugin the Plugin
     * @param mapFilterCriteria contains search bar names/values inputs
     * @param strColumnToOrder contains the column name to use for orderBy statement in case of sorting request (must be null)
     * @param strSortMode contains the sortMode in case of sorting request : ASC or DESC (must be null)
     * @return The list which contains the id of all the project objects fitting with the seach criteria.
     */
    List<String> selectIdEntitiesList(Plugin plugin, Map<String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode);

    /**
     * Load the data of all the entity objects and returns them as a referenceList
     * @param plugin the Plugin
     * @return The referenceList which contains the data of all the entity objects
     */
    ReferenceList selectEntitiesReferenceList(Plugin plugin);

    /**
     * Load the data of all the avant objects and returns them as a list
     * @param plugin the Plugin
     * @param listIds liste of ids
     * @return The list which contains the data of all the avant objects
     */
    List<T> selectEntitiesListByIds( Plugin plugin, List<String> listIds );
}

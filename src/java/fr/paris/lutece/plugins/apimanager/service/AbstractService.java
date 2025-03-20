package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.history.History;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public abstract class AbstractService<T> {

    /**
     * create a new entity in database
     * @param entity the entity
     * @param user the user
     */
    public abstract void create( T entity, String user );

    /**
     * update an existing entity in database
     * @param entity the entity
     * @param user the user
     */
    public abstract void update( T entity, String user );

    /**
     * delete an existing entity in database, corresponding to the provided uuid
     * @param uuid the uuid
     * @param user the user
     */
    public abstract void delete( String uuid, String user );

    /**
     * search for entities according to the provided criterias, and returns their uuid, ordered according to the provided ordering parameters
     * @param mapFilterCriteria the criterias
     * @param columnToOrder the column to order
     * @param orderBy ASC or DESC
     * @return a list of uuid
     */
    public abstract List<String> getIdEntitiesList( Map<String, String> mapFilterCriteria, String columnToOrder, String orderBy );

    /**
     * search for entities according to the provided criterias, and returns their uuid, unordered
     * @param mapFilterCriteria the criterias
     * @return a list of uuid
     */
    public List<String> getIdEntitiesList( Map<String, String> mapFilterCriteria) {
        return getIdEntitiesList( mapFilterCriteria, null, null );
    }

    /**
     * get the entities corresponding to the provided uuid list
     * @param listIds the uuid list
     * @return list of entities
     */
    public abstract List<T> getEntitiesListByIds( List<String> listIds );

    /**
     * Adds a new history record
     * @param uuidRef the uuid of the referencing object
     * @param type the action type
     * @param user the user who initiated the action
     * @return the new history record
     */
    protected History addNewHistory(final String uuidRef, final HistoryTypeEnum type, final String user) {
        final History history = new History();
        history.setUuidRef(uuidRef);
        history.setType(type);
        history.setDate(Timestamp.from(Instant.now()));
        history.setUser(user);
        return HistoryHome.create(history);
    }

}

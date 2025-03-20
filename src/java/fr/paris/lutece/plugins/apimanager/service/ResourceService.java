package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.resource.Resource;
import fr.paris.lutece.plugins.apimanager.business.resource.ResourceHome;

import java.util.List;
import java.util.Map;

public class ResourceService extends AbstractService<Resource> {

    private static ResourceService _instance;

    private ResourceService() {
    }

    public static ResourceService getInstance() {
        if (_instance == null) {
            _instance = new ResourceService();
        }
        return _instance;
    }

    @Override
    public void create(final Resource entity, final String user) {
        final String uuid = ResourceHome.create(entity).getUuid();
        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user);
    }

    @Override
    public void update(final Resource entity, final String user) {
        ResourceHome.update( entity );
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user);
    }

    @Override
    public void delete(final String uuid, final String user) {
        ResourceHome.remove( uuid );
        this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user);
    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return ResourceHome.getIdResourcesList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Resource> getEntitiesListByIds(final List<String> listIds) {
        return ResourceHome.getResourcesListByIds( listIds );
    }
}

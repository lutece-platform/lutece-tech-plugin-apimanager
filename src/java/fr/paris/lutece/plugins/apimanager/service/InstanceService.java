package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.instance.Instance;
import fr.paris.lutece.plugins.apimanager.business.instance.InstanceHome;

import java.util.List;
import java.util.Map;

public class InstanceService extends AbstractService<Instance> {

    private static InstanceService _instance = new InstanceService();

    private InstanceService() {
    }

    public static InstanceService getInstance() {
        if (_instance == null) {
            _instance = new InstanceService();
        }
        return _instance;
    }

    @Override
    public void create(final Instance entity, final String user) {
        final String uuid = InstanceHome.create(entity).getUuid();
        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user);
    }

    @Override
    public void update(final Instance entity, final String user) {
        InstanceHome.update(entity);
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user);
    }

    @Override
    public void delete(final String uuid, final String user) {
        InstanceHome.remove(uuid);
        this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user);
    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return InstanceHome.getIdInstancesList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Instance> getEntitiesListByIds(final List<String> listIds) {
        return InstanceHome.getInstancesListByIds(listIds);
    }
}

package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.subscription.Subscription;
import fr.paris.lutece.plugins.apimanager.business.subscription.SubscriptionHome;

import java.util.List;
import java.util.Map;

public class SubscriptionService extends AbstractService<Subscription> {

    private static SubscriptionService _instance;

    private SubscriptionService() {
    }

    public static SubscriptionService getInstance() {
        if (_instance == null) {
            _instance = new SubscriptionService();
        }
        return _instance;
    }

    @Override
    public void create(final Subscription entity, final String user) {
        final String uuid = SubscriptionHome.create(entity).getUuid();
        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user);
    }

    @Override
    public void update(final Subscription entity, final String user) {
        SubscriptionHome.update(entity);
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user);
    }

    @Override
    public void delete(final String uuid, final String user) {
        SubscriptionHome.remove(uuid);
        this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user);
    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return SubscriptionHome.getIdSubscriptionsList(mapFilterCriteria, columnToOrder, orderBy);
    }

    @Override
    public List<Subscription> getEntitiesListByIds(final List<String> listIds) {
        return SubscriptionHome.getSubscriptionsListByIds(listIds);
    }
}

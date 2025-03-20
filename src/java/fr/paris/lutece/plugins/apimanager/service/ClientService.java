package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.client.Client;
import fr.paris.lutece.plugins.apimanager.business.client.ClientHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;

import java.util.List;
import java.util.Map;

public class ClientService extends AbstractService<Client> {
    private static ClientService _instance;

    private ClientService() {
    }

    public static ClientService getInstance() {
        if (_instance == null) {
            _instance = new ClientService();
        }
        return _instance;
    }

    @Override
    public void create(final Client entity, final String user) {
        final String uuid = ClientHome.create(entity).getUuid();
        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user);
    }

    @Override
    public void update(final Client entity, final String user) {
        ClientHome.update( entity );
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user);
    }

    @Override
    public void delete(final String uuid, final String user) {
        final List<String> clientSubscriptionIds = SubscriptionService.getInstance().getIdEntitiesList(Map.of("uuid_client", uuid));
        clientSubscriptionIds.forEach( subscriptionId -> SubscriptionService.getInstance().delete(subscriptionId, user));
        ClientHome.remove(uuid);
        this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user);
    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return ClientHome.getIdClientsList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Client> getEntitiesListByIds(final List<String> listIds) {
        return ClientHome.getClientsListByIds( listIds );
    }


}

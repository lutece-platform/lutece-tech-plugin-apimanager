package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.api.Api;
import fr.paris.lutece.plugins.apimanager.business.api.ApiHome;
import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;

import java.util.List;
import java.util.Map;

public class ApiService extends AbstractService<Api> {

    private static ApiService _instance;

    private ApiService() {
    }

    public static ApiService getInstance() {
        if (_instance == null) {
            _instance = new ApiService();
        }
        return _instance;
    }

    @Override
    public void create(final Api entity, final String user) {
        final String uuid = ApiHome.create(entity).getUuid();
        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user);
    }

    @Override
    public void update(final Api entity, final String user) {
        ApiHome.update(entity);
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user);
    }

    @Override
    public void delete(final String uuid, final String user) {
        ApiHome.findByPrimaryKey(uuid).ifPresent( api -> {
            // Delete API plans
            PlanService.getInstance().getIdEntitiesList(Map.of("uuid_api", uuid)).forEach( planId -> PlanService.getInstance().delete(planId, user) );

            // Delete API instances
            InstanceService.getInstance().getIdEntitiesList(Map.of("uuid_api", uuid)).forEach( instanceId -> InstanceService.getInstance().delete(instanceId, user) );

            // Delete API
            ApiHome.remove(uuid);
            this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user);
        });
        ApiHome.remove(uuid);
    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return ApiHome.getIdApisList(mapFilterCriteria, columnToOrder, orderBy);
    }

    @Override
    public List<Api> getEntitiesListByIds(final List<String> listIds) {
        return ApiHome.getApisListByIds(listIds);
    }
}

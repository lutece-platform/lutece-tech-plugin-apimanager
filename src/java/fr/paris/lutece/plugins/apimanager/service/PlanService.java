package fr.paris.lutece.plugins.apimanager.service;

import fr.paris.lutece.plugins.apimanager.business.history.HistoryTypeEnum;
import fr.paris.lutece.plugins.apimanager.business.plan.Plan;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanClientHttpConfigurationHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHeaderMatchingHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanOauthConfigurationHome;
import fr.paris.lutece.plugins.apimanager.business.plan.PlanRateLimitingHome;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlanService extends AbstractService<Plan> {

    private static PlanService _instance = new PlanService();

    private PlanService() {
    }

    public static PlanService getInstance() {
        if (_instance == null) {
            _instance = new PlanService();
        }
        return _instance;
    }

    @Override
    public void create(final Plan entity, final String user) {
        Optional.ofNullable(entity.getRateLimiting()).ifPresent(PlanRateLimitingHome::create);
        Optional.ofNullable(entity.getClientHttpConfiguration()).ifPresent(PlanClientHttpConfigurationHome::create);
        Optional.ofNullable(entity.getHeaderMatching()).ifPresent(PlanHeaderMatchingHome::create);
        Optional.ofNullable(entity.getOauthConfiguration()).ifPresent(PlanOauthConfigurationHome::create);

        final String uuid = PlanHome.create(entity).getUuid();
        this.addNewHistory(uuid, HistoryTypeEnum.CREATE, user);
    }

    @Override
    public void update(final Plan entity, final String user) {
        Optional.ofNullable(entity.getRateLimiting()).ifPresent(PlanRateLimitingHome::update);
        Optional.ofNullable(entity.getClientHttpConfiguration()).ifPresent(PlanClientHttpConfigurationHome::update);
        Optional.ofNullable(entity.getHeaderMatching()).ifPresent(PlanHeaderMatchingHome::update);
        Optional.ofNullable(entity.getOauthConfiguration()).ifPresent(PlanOauthConfigurationHome::update);

        PlanHome.update( entity );
        this.addNewHistory(entity.getUuid(), HistoryTypeEnum.UPDATE, user);
    }

    @Override
    public void delete(final String uuid, final String user) {
        PlanHome.findByPrimaryKey(uuid).ifPresent( plan -> {
            // Delete resources
            ResourceService.getInstance().getIdEntitiesList(Map.of("uuid_plan", uuid)).forEach( resourceId -> ResourceService.getInstance().delete(resourceId, user) );

            // Delete plan
            PlanHome.remove( uuid );

            // Delete plan config objects
            Optional.ofNullable(plan.getRateLimiting()).ifPresent(rl -> PlanRateLimitingHome.remove(rl.getUuid()));
            Optional.ofNullable(plan.getClientHttpConfiguration()).ifPresent(chc -> PlanClientHttpConfigurationHome.remove(chc.getUuid()));
            Optional.ofNullable(plan.getHeaderMatching()).ifPresent(hm -> PlanHeaderMatchingHome.remove(hm.getUuid()));
            Optional.ofNullable(plan.getOauthConfiguration()).ifPresent(oac -> PlanOauthConfigurationHome.remove(oac.getUuid()));

            this.addNewHistory(uuid, HistoryTypeEnum.DELETE, user);
        });

    }

    @Override
    public List<String> getIdEntitiesList(final Map<String, String> mapFilterCriteria, final String columnToOrder, final String orderBy) {
        return PlanHome.getIdPlansList( mapFilterCriteria, columnToOrder, orderBy );
    }

    @Override
    public List<Plan> getEntitiesListByIds(final List<String> listIds) {
        return PlanHome.getPlansListByIds( listIds );
    }
}

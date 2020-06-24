package main;

import behaviour.GeneralBehaviour;
import com.springrts.ai.oo.AbstractOOAI;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;
import units.controller.UnitsController;
import units.keepers.EnemyUnitsKeeper;
import units.keepers.UnitsKeeper;
import units.others.UnitsCategories;
import units.others.UnitsConfig;


public class ScbAI extends AbstractOOAI {

    private OOAICallback callback;
    private AILogger logger;

    private UnitsController unitsController;
    private UnitsKeeper unitsKeeper;
    private UnitsCategories unitsCategories;
    private UnitsConfig unitsConfig;
    private UnitsContainer unitsContainer;

    private EnemyUnitsKeeper enemyUnitsKeeper;

    private GeneralBehaviour generalBehaviour;

    private GeneralMap generalMap;


    public static void main(String[] args) {
        // Doar pentru a-mi permite IntelliJ IDEA sa dau build la JAR

    }


    @Override
    public int init(int teamId, OOAICallback ooaiCallback) {
        try {
            this.callback = ooaiCallback;
            this.logger = new AILogger(callback);

            this.generalMap = new GeneralMap(logger);
            this.unitsCategories = new UnitsCategories(logger);
            this.unitsConfig = new UnitsConfig(logger, unitsCategories);

            int aiTickRate = unitsConfig.getFirstSettingValue("ai_tick_rate");
            int ucTickRate = unitsConfig.getFirstSettingValue("units_controller_tick_rate") * aiTickRate;

            this.unitsController = new UnitsController(logger, ucTickRate);
            this.unitsKeeper = new UnitsKeeper(logger, unitsCategories, unitsController);

            this.enemyUnitsKeeper = new EnemyUnitsKeeper(logger, unitsCategories);

            this.unitsContainer = new UnitsContainer(unitsKeeper, unitsConfig, enemyUnitsKeeper);

            this.generalBehaviour = new GeneralBehaviour(logger, unitsContainer, generalMap);


        } catch (Exception e) {
            logger.log(e);
        }
        return super.init(teamId, ooaiCallback);
    }


    @Override
    public int unitCreated(Unit unit, Unit creator) {
        try {
            if (unitsCategories.isBuilding(unit.getDef().getName())) {
                generalMap.addBuildingToBMap(unit);
                unitsController.updateParams("BuilderFinishedCreating", unit, creator);
            }
            unitsKeeper.addBeingBuilt(unit);

        } catch (Exception e) {
            logger.log(e);
        }
        return super.unitCreated(unit, creator);
    }


    @Override
    public int unitFinished(Unit unit) {
        try {
            if (unitsCategories.isBuilding(unit.getDef().getName())) {
                unitsController.updateParams("BuilderFinishedBuilding", unit);
                generalBehaviour.updateEvents("BuilderFinishedBuilding", unit);
            }
            unitsKeeper.addUnit(unit);
            unitsKeeper.addIdleUnit(unit, true);

        } catch (Exception e) {
            logger.log(e);
        }
        return super.unitFinished(unit);
    }


    @Override
    public int unitIdle(Unit unit) {
        try {
            if (unitsCategories.isTroop(unit.getDef().getName())) {
                unitsController.updateParams("IdleUnit", unit);
                generalBehaviour.updateEvents("IdleUnit", unit);
            }
            unitsKeeper.addIdleUnit(unit, true);

        } catch (Exception e) {
            logger.log(e);
        }
        return super.unitIdle(unit);
    }


    @Override
    public int unitDestroyed(Unit unit, Unit unit1) {
        try {
            if (unitsCategories.isBuilding(unit.getDef().getName())) {
                generalMap.removeBuilding(unit);
            }
            unitsKeeper.removeUnit(unit);

        } catch (Exception e) {
            logger.log(e);
        }
        return super.unitDestroyed(unit, unit1);
    }


    @Override
    public int unitMoveFailed(Unit unit) {
        try {
            if (unitsCategories.isTroop(unit.getDef().getName())) {
                unitsController.updateParams("MoveFailed", unit);
            }
        } catch (Exception e) {
            logger.log(e);
        }
        return super.unitMoveFailed(unit);
    }


    @Override
    public int enemyEnterLOS(Unit unit) {
        try {
            if (unit.getDef().getName().startsWith("arm")) {
                enemyUnitsKeeper.addVisibleEnemy(unit);
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return super.enemyEnterLOS(unit);
    }

    @Override
    public int enemyLeaveLOS(Unit unit) {
        try {
            if (unit.getDef().getName().startsWith("arm")) {
                enemyUnitsKeeper.removeVisibleEnemy(unit);
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return super.enemyLeaveLOS(unit);
    }

    @Override
    public int enemyDestroyed(Unit enemy, Unit attacker) {
        try {
            if (enemy != null && enemy.getDef() != null && enemy.getDef().getName() != null) {
                if (enemy.getDef().getName().startsWith("arm")) {
                    enemyUnitsKeeper.removeSeenEnemy(enemy);
                }
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return super.enemyDestroyed(enemy, attacker);
    }

    @Override
    public int update(int frame) {
        try {

            if (frame == 0) {
                Unit commander = unitsKeeper.getFirstUnitByName("armcom", false);
                if (commander != null) {
                    // Adauga commander-ul in idle (deoarece la inceput acesta nu va fi considerat idle
                    // pentru ca nu a intrat niciodata in functia unitIdle() )
                    unitsKeeper.addIdleUnit(commander, true);
                }
            }

            generalBehaviour.update(frame);

        } catch (Exception e) {
            logger.log(e);
        }

        return super.update(frame);
    }


}
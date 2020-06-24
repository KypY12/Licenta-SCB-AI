package units;

import com.springrts.ai.oo.clb.OOAICallback;
import logger.AILogger;
import units.controller.UnitsController;
import units.others.UnitsCategories;
import units.keepers.EnemyUnitsKeeper;
import units.keepers.UnitsKeeper;
import units.others.UnitsConfig;


public class UnitsContainer {

    private UnitsKeeper unitsKeeper;
    private UnitsCategories unitsCategories;
    private UnitsConfig unitsConfig;
    private UnitsController unitsController;

    private EnemyUnitsKeeper enemyUnitsKeeper;


    public UnitsContainer(UnitsKeeper unitsKeeper, UnitsConfig unitsConfig, EnemyUnitsKeeper enemyUnitsKeeper){
        this.unitsKeeper = unitsKeeper;
        this.unitsCategories = unitsKeeper.getUnitsCategories();
        this.unitsConfig = unitsConfig;
        this.unitsController = unitsKeeper.getUnitsController();
        this.enemyUnitsKeeper = enemyUnitsKeeper;
    }

    public UnitsKeeper getUnitsKeeper() {
        return unitsKeeper;
    }

    public UnitsCategories getUnitsCategories() {
        return unitsCategories;
    }

    public UnitsConfig getUnitsConfig() {
        return unitsConfig;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public EnemyUnitsKeeper getEnemyUnitsKeeper() {
        return enemyUnitsKeeper;
    }
}

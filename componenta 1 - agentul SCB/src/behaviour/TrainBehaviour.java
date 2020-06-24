package behaviour;

import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import handlers.BuildHandler;
import handlers.HandlersContainer;
import logger.AILogger;
import map.GeneralMap;
import units.others.UnitsCategories;
import units.others.UnitsConfig;
import units.keepers.UnitsKeeper;
import units.UnitsContainer;

import java.util.Arrays;
import java.util.List;

public class TrainBehaviour extends Behaviour {

    private UnitsKeeper unitsKeeper;
    private UnitsConfig unitsConfig;
    private UnitsCategories unitsCategories;

    private int buildersTrainCount = 2;

    private BuildHandler buildHandler;

    private List<String> unitNames;

    public TrainBehaviour(AILogger logger, UnitsContainer unitsContainer, HandlersContainer handlersContainer, GeneralMap generalMap) {
        super(logger, unitsContainer, handlersContainer, generalMap);

        this.buildHandler = handlersContainer.getBuildHandler();

        this.unitsKeeper = unitsContainer.getUnitsKeeper();
        this.unitsConfig = unitsContainer.getUnitsConfig();
        this.unitsCategories = unitsContainer.getUnitsCategories();

        this.unitNames = unitsConfig.getAllUnits();

        this.buildersTrainCount = unitsConfig.getFirstSettingValue("builders_train_count");
        logger.log("builders_train_count:" + Integer.valueOf(buildersTrainCount).toString());
    }

    public boolean sendTrainCommand(List<String> categories, int count) {
        for (String unitName : unitNames) {
            if (unitsCategories.unitContainsCategories(unitName, (String[]) categories.toArray())) {
                Unit trainer = unitsKeeper.getFactoryFor(unitName, true);
                if (trainer != null) {
                    buildHandler.buildUnit(trainer, callback.getUnitDefByName(unitName), count);
                    return true;
                }
            }
        }
        // trebuie trimisa comanda sa contruiasca factory pentru unitatea de tipul asta
        return false;
    }

    public boolean sendTrainCommand(List<String> categories, int count, boolean isIdle) {
        for (String unitName : unitNames) {
            if (unitsCategories.unitContainsCategories(unitName, (String[]) categories.toArray())) {
                Unit trainer = unitsKeeper.getFactoryFor(unitName, isIdle);
                if (trainer != null) {
                    buildHandler.buildUnit(trainer, callback.getUnitDefByName(unitName), count);
                    return true;
                }
            }
        }
        // trebuie trimisa comanda sa contruiasca factory pentru unitatea de tipul asta
        return false;
    }

    @Override
    public void updateEvents(Object... params) {
    }

    @Override
    public void update(Object... params) {

        if (params.length != 0) {
            return;
        }

        if (unitsKeeper.getBuildersCount() < 5) {
            sendTrainCommand(Arrays.asList("BUILDER"), buildersTrainCount, true);
        }

    }

}

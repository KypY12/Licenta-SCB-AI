package behaviour;

import com.springrts.ai.oo.clb.Unit;
import handlers.BuildHandler;
import handlers.HandlersContainer;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;
import units.controller.UnitsController;
import units.controller.tasks.BuildSingle;
import units.keepers.UnitsKeeper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DefenceBehaviour extends Behaviour{

    private ExpansionBehaviour expansionBehaviour;

    private BuildHandler buildHandler;

    private UnitsKeeper unitsKeeper;
    private UnitsController unitsController;

    private int maxTowerBuilders;
    private List<Unit> towersBuilders;
    private List<Unit> newMetalExtractors = new LinkedList<>();

    public DefenceBehaviour(AILogger logger, UnitsContainer unitsContainer, HandlersContainer handlersContainer,
                            GeneralMap generalMap, ExpansionBehaviour expansionBehaviour) {
        super(logger, unitsContainer, handlersContainer, generalMap);

        this.unitsKeeper = unitsContainer.getUnitsKeeper();
        this.unitsController = unitsContainer.getUnitsController();

        this.buildHandler = handlersContainer.getBuildHandler();

        this.expansionBehaviour = expansionBehaviour;

        this.towersBuilders = new LinkedList<>();
        this.maxTowerBuilders = unitsContainer.getUnitsConfig().getFirstSettingValue("max_tower_builders");
        logger.log("max_tower_builders:" + Integer.valueOf(maxTowerBuilders).toString());
    }

    @Override
    public void updateEvents(Object... params) {
        if ((((String) params[0]).equals("IdleUnit"))) {
            Unit unit = (Unit) params[1];
            if (towersBuilders.contains(unit)) {
                towersBuilders.remove(unit);
            }
        } else if (((String) params[0]).equals("BuilderFinishedBuilding")) {
            Unit unit = (Unit) params[1];
            if (unitsContainer.getUnitsCategories().unitContainsCategories(unit.getDef().getName(), new String[]{"ECONOMY", "PRODUCER", "METAL"})) {
                newMetalExtractors.add(unit);
            }
        }
    }

    @Override
    public void update(Object... params) {
        if (params.length != 2){
            return;
        }

        boolean isMetalLow = (boolean)params[0];
        boolean isEnergyLow = (boolean)params[1];

        if (towersBuilders.size() < maxTowerBuilders) {
            Unit builder = unitsKeeper.getBuilder(0);
            if (builder != null) {

                if (newMetalExtractors.size() > 0) {
                    BuildSingle buildSingleTask = new BuildSingle(new LinkedList<>(Arrays.asList(builder)), logger,
                            expansionBehaviour.getUnitDefWithCategories(builder.getDef(), Arrays.asList("TOWER", "ANTILAND")),
                            buildHandler, unitsKeeper, generalMap, newMetalExtractors.get(0).getPos());
                    newMetalExtractors.remove(0);
                    unitsKeeper.removeIdleUnit(builder);
                    unitsController.addTask(buildSingleTask);
                    towersBuilders.add(builder);

                } else if (!isMetalLow && !isEnergyLow) {
                    if (unitsContainer.getEnemyUnitsKeeper().getAirSeenEnemies().size() > 0 && new Random().nextBoolean()) {
                        BuildSingle buildSingleTask = new BuildSingle(new LinkedList<>(Arrays.asList(builder)), logger,
                                expansionBehaviour.getUnitDefWithCategories(builder.getDef(), Arrays.asList("TOWER", "ANTIAIR")),
                                buildHandler, unitsKeeper, generalMap, logger.getCallback().getMap().getStartPos());
                        unitsController.addTask(buildSingleTask);

                    } else {
                        BuildSingle buildSingleTask = new BuildSingle(new LinkedList<>(Arrays.asList(builder)), logger,
                                expansionBehaviour.getUnitDefWithCategories(builder.getDef(), Arrays.asList("TOWER", "ANTILAND")),
                                buildHandler, unitsKeeper, generalMap, logger.getCallback().getMap().getStartPos());
                        unitsController.addTask(buildSingleTask);

                    }

                    unitsKeeper.removeIdleUnit(builder);
                    towersBuilders.add(builder);
                }

            }
        }


    }


}

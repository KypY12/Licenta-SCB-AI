package behaviour;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import handlers.*;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;
import units.controller.UnitsController;
import units.keepers.UnitsKeeper;

import java.util.*;


public class GeneralBehaviour extends Behaviour{

    private boolean isMetalLow = false;
    private boolean isEnergyLow = false;

    private int startingBuildersCount;
    private int aiTickRate;
    private int startingFactoryIndex;

    private UnitsKeeper unitsKeeper;
    private UnitsController unitsController;

    private AttackHandler attackHandler;
    private BuildHandler buildHandler;
    private MoveHandler moveHandler;

    private ExpansionBehaviour expansionBehaviour;
    private TrainBehaviour trainBehaviour;
    private EconomyBehaviour economyBehaviour;
    private OffenceBehaviour offenceBehaviour;
    private DefenceBehaviour defenceBehaviour;

    private boolean init = false;
    private boolean initTower = false;
    private boolean initFirstFactory = false;
    private boolean initMetal = false;
    private boolean initEnergy = false;


    public GeneralBehaviour(AILogger logger, UnitsContainer unitsContainer, GeneralMap generalMap) {

        super(logger, unitsContainer, null, generalMap);

        this.unitsKeeper = unitsContainer.getUnitsKeeper();
        this.unitsController = unitsKeeper.getUnitsController();

        this.aiTickRate = unitsContainer.getUnitsConfig().getFirstSettingValue("ai_tick_rate");
        this.startingBuildersCount = unitsContainer.getUnitsConfig().getFirstSettingValue("starting_builders_count");
        this.startingFactoryIndex = unitsContainer.getUnitsConfig().getFirstSettingValue("starting_factory_index");
        logger.log("ai_tick_rate:" + Integer.valueOf(aiTickRate).toString());
        logger.log("starting_builders_count:" + Integer.valueOf(startingBuildersCount).toString());
        logger.log("starting_factory_index:" + Integer.valueOf(startingFactoryIndex).toString());

        this.attackHandler = new AttackHandler(logger, unitsKeeper);
        this.buildHandler = new BuildHandler(logger, unitsKeeper);
        this.moveHandler = new MoveHandler(logger, unitsKeeper);
        this.handlersContainer = new HandlersContainer(attackHandler, buildHandler, moveHandler);

        this.trainBehaviour = new TrainBehaviour(logger, unitsContainer, handlersContainer, generalMap);
        this.expansionBehaviour = new ExpansionBehaviour(logger, unitsContainer, handlersContainer, generalMap);
        this.economyBehaviour = new EconomyBehaviour(logger, unitsContainer, handlersContainer, generalMap, expansionBehaviour, trainBehaviour);
        this.offenceBehaviour = new OffenceBehaviour(logger, unitsContainer, handlersContainer, generalMap, expansionBehaviour, trainBehaviour);
        this.defenceBehaviour = new DefenceBehaviour(logger, unitsContainer, handlersContainer, generalMap, expansionBehaviour);

    }


    private void initFirstTower() {
        Unit commander = unitsKeeper.getFirstUnitByName("armcom", false);
        if (commander != null) {
            UnitDef towerDef = expansionBehaviour.getUnitDefWithCategories(commander.getDef(), Arrays.asList("TOWER", "ANTILAND"));
            if (towerDef != null) {
                AIFloat3 position = generalMap.getClosestBuildPosForFrom(towerDef, 1, 1,
                        commander.getPos(), 64, true).get(0);

                if (position != null) {
                    buildHandler.buildUnit(new LinkedList<>(Arrays.asList(commander)), towerDef, position);
                }

            }
            initTower = true;
        }
    }

    private void initMetalExtracting() {
        // Construieste 4 metal exctractor
        Unit commander = unitsKeeper.getFirstUnitByName("armcom", true);
        if (commander != null) {
            UnitDef metalProducer = expansionBehaviour.getUnitDefWithCategories(commander.getDef(), Arrays.asList("ECONOMY", "PRODUCER", "METAL"));
            if (metalProducer != null) {
                AIFloat3 metalSpot = generalMap.closestMetalSpotForLand(commander.getPos(), commander, metalProducer);
                if (metalSpot != null) {
                    buildHandler.buildUnit(new LinkedList<>(Arrays.asList(commander)), metalProducer, metalSpot);
                    initMetal = true;
                }
            }
        }
    }


    private void initFirstFactoryBuilding() {
        Unit commander = unitsKeeper.getFirstUnitByName("armcom", true);
        if (commander != null) {

            List<UnitDef> buildingsDefs = unitsContainer.getUnitsConfig().getAllBuildingsDefs();
            logger.log(buildingsDefs.size());
            for (UnitDef def : buildingsDefs){
                logger.log(def.getName());
            }

            startingFactoryIndex = startingFactoryIndex % buildingsDefs.size();
            UnitDef firstFactory = buildingsDefs.get(startingFactoryIndex );
            logger.log(startingFactoryIndex);

            int firstIndexSelected = startingFactoryIndex;
            while (!commander.getDef().getBuildOptions().contains(firstFactory)) {
                startingFactoryIndex = (startingFactoryIndex + 1) % buildingsDefs.size();

                if (firstIndexSelected == startingFactoryIndex) {
                    // Daca am parcurs toate optiunile si niciuna nu poate fi construita de commander =>
                    // => atunci pur si simplu construieste un armlab
                    firstFactory = callback.getUnitDefByName("armlab");
                    break;
                }

                firstFactory = buildingsDefs.get(startingFactoryIndex);
            }

            AIFloat3 position = generalMap.getClosestBuildPosForFrom(firstFactory, 1, 1, commander.getPos(), 64, true).get(0);
            if (position != null) {
                buildHandler.buildUnit(new LinkedList<>(Arrays.asList(commander)), firstFactory, position);
                initFirstFactory = true;
            }
        }
    }

    private void initEnergyProduction() {
        // Construieste energy producer
        Unit commander = unitsKeeper.getFirstUnitByName("armcom", true);
        if (commander != null) {
            UnitDef energyProducer = expansionBehaviour.getUnitDefWithCategories(commander.getDef(), Arrays.asList("ECONOMY", "PRODUCER", "ENERGY"));
            if (energyProducer != null) {
                AIFloat3 position = generalMap.getClosestBuildPosForFrom(energyProducer, 1, 1, commander.getPos(), 64, true).get(0);
                if (position != null) {
                    buildHandler.buildUnit(new LinkedList<>(Arrays.asList(commander)), energyProducer, position);
                    this.initEnergy = true;
                }

                trainBehaviour.sendTrainCommand(Arrays.asList("BUILDER"), startingBuildersCount);
            }
        }
    }

    private void initBehaviour() {

        if (!initTower) {
            initFirstTower();
        } else if (!initMetal) {
            initMetalExtracting();
        } else if (!initFirstFactory) {
            initFirstFactoryBuilding();
        } else if (!initEnergy) {
            initEnergyProduction();
        } else if (!init) {
            Unit commander = unitsKeeper.getFirstUnitByName("armcom", true);
            if (commander != null) {
                // Commanderul va ajuta la constructiile din jur sau va elimina elementele "features" de pe harta (cum ar fi copaci, roci, etc.)
                moveHandler.patrolTo(new LinkedList<>(Arrays.asList(commander)), new AIFloat3(commander.getPos().x + 16, 0f, commander.getPos().z + 16));
                this.init = true;
            }
        }

    }

    @Override
    public void updateEvents(Object... params) {
        defenceBehaviour.updateEvents(params);
        expansionBehaviour.updateEvents(params);
    }


    @Override
    public void update(Object... params) {
        if (params.length != 1){
            return;
        }

        int frame = (int)params[0];

        if (!init && frame % 100 == 0) {
            initBehaviour();
        }

        unitsController.update(frame);

        if (init && frame % aiTickRate == 0) {
            generalMap.updateLosMap();

            economyBehaviour.update();
            isMetalLow = economyBehaviour.isMetalLow();
            isEnergyLow = economyBehaviour.isEnergyLow();

            offenceBehaviour.update(frame, isMetalLow, isEnergyLow);
            defenceBehaviour.update(isMetalLow, isEnergyLow);
            trainBehaviour.update();
            expansionBehaviour.update(isMetalLow);

        }

    }


}
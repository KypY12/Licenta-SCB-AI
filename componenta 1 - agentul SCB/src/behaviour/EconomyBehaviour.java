package behaviour;


import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Economy;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import handlers.BuildHandler;
import handlers.HandlersContainer;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;
import units.controller.tasks.BuildMultiple;
import units.keepers.UnitsKeeper;

import java.util.*;

public class EconomyBehaviour extends Behaviour {

    private Economy economy;
    private UnitsKeeper unitsKeeper;

    private BuildHandler buildHandler;

    private float currentMetal;
    private float currentEnergy;

    private float incomeMetal;
    private float incomeEnergy;

    private float usageMetal;
    private float usageEnergy;

    private float storageMetal;
    private float storageEnergy;

    private boolean metalLow = false;
    private boolean energyLow = false;

    private ExpansionBehaviour expansionBehaviour;
    private TrainBehaviour trainBehaviour;

    private Map<String, Integer> workingMaxRequests = new HashMap<>();
    private Map<String, List<Unit>> currentWorkingRequests = new HashMap<>();

    private int maxWorkingUnits = 2;


    public EconomyBehaviour(AILogger logger, UnitsContainer unitsContainer, HandlersContainer handlersContainer, GeneralMap generalMap,
                            ExpansionBehaviour expansionBehaviour, TrainBehaviour trainBehaviour) {
        super(logger, unitsContainer, handlersContainer, generalMap);

        this.economy = callback.getEconomy();

        this.unitsKeeper = unitsContainer.getUnitsKeeper();

        this.buildHandler = handlersContainer.getBuildHandler();

        this.expansionBehaviour = expansionBehaviour;
        this.trainBehaviour = trainBehaviour;

        List<String> requestNames = new ArrayList<>(Arrays.asList(
                "build_metal_generator",
                "build_metal_storage",
                "build_energy_generator",
                "build_energy_storage"
        ));

        addRequestsMaxInit(requestNames);
        addRequestsInit(requestNames);

        this.maxWorkingUnits = unitsContainer.getUnitsConfig().getFirstSettingValue("max_working_units");
        logger.log("max_working_units:" + Integer.valueOf(maxWorkingUnits).toString());

    }

    private void addRequestsMaxInit(List<String> requestNames) {
        for (String reqName : requestNames) {
            workingMaxRequests.put(reqName, maxWorkingUnits);
        }
    }

    private void addRequestsInit(List<String> requestNames) {
        for (String reqName : requestNames) {
            currentWorkingRequests.put(reqName, new ArrayList<Unit>());
        }
    }

    private String getReqNameFromResource(String resource, String type) {
        if (resource.equals("Energy")) {
            return "build_energy_" + type;
        } else if (resource.equals("Metal")) {
            return "build_metal_" + type;
        }
        return "";
    }

    private void removeIfFinished() {
        for (String key : currentWorkingRequests.keySet()) {
            Iterator<Unit> it = currentWorkingRequests.get(key).iterator();
            while (it.hasNext()) {
                Unit unit = it.next();
                if (unit == null || !unitsKeeper.isUnitStillAlive(unit) || unitsKeeper.isIdle(unit)) {
                    try {
                        it.remove();

                    } catch (Exception e) {
                        logger.log(e);
                    }
                }
            }
        }
    }


    @Override
    public void updateEvents(Object... params) {
    }

    @Override
    public void update(Object... params) {
        if (params.length != 0){
            return;
        }
        
        removeIfFinished();
        getCurrents();
        if (isMetalLow()) {
            sendBuildCommand("Metal");
        }
        if (isEnergyLow()) {
            sendBuildCommand("Energy");
        }

    }


    private void getCurrents() {
        currentMetal = economy.getCurrent(callback.getResourceByName("Metal"));
        currentEnergy = economy.getCurrent(callback.getResourceByName("Energy"));

        incomeMetal = economy.getIncome(callback.getResourceByName("Metal"));
        incomeEnergy = economy.getIncome(callback.getResourceByName("Energy"));

        usageMetal = economy.getUsage(callback.getResourceByName("Metal"));
        usageEnergy = economy.getUsage(callback.getResourceByName("Energy"));

        storageMetal = economy.getStorage(callback.getResourceByName("Metal"));
        storageEnergy = economy.getStorage(callback.getResourceByName("Energy"));
    }


    public boolean isEnergyLow() {
        if (currentEnergy < (storageEnergy * 6 / 10) || incomeEnergy < usageEnergy * 7 / 10) {
            energyLow = true;
            return true;

        } else {
            energyLow = false;

        }

        return false;
    }

    public boolean isMetalLow() {
        if (currentMetal < (storageMetal * 6 / 10) || incomeMetal < usageMetal * 7 / 10) {
            metalLow = true;
            return true;

        } else {
            metalLow = false;

        }

        return false;
    }


    public void sendBuildCommand(String resource) {
        String reqName = getReqNameFromResource(resource, "generator");
        if (currentWorkingRequests.get(reqName).size() < workingMaxRequests.get(reqName)) {
            Unit builder = unitsKeeper.getBuilder(0);
            if (builder != null) {
                if (resource.equals("Energy")) {
                    UnitDef energyProducer = expansionBehaviour.getUnitDefWithCategories(builder.getDef(), Arrays.asList("ECONOMY", "PRODUCER", "ENERGY"));

                    if (energyProducer != null) {
                        BuildMultiple buildMultipleTask = new BuildMultiple(new LinkedList<>(Arrays.asList(builder)), logger, energyProducer, 1, 1, buildHandler,
                                unitsKeeper, generalMap, false);
                        unitsContainer.getUnitsController().addTask(buildMultipleTask);
                        currentWorkingRequests.get(reqName).add(builder);
                        unitsKeeper.removeIdleUnit(builder);

                    }

                } else if (resource.equals("Metal")) {
                    UnitDef metalProducer;
                    if (energyLow || new Random().nextInt(100) < 70) {
                        metalProducer = callback.getUnitDefByName("armmex");

                        if (metalProducer != null) {
                            AIFloat3 nearestMetalSpot;
                            if (unitsContainer.getUnitsCategories().unitContainsCategory(builder.getDef().getName(), "LAND")) {
                                nearestMetalSpot = generalMap.closestMetalSpotForAir(builder.getPos(), metalProducer);
                            } else {
                                nearestMetalSpot = generalMap.closestMetalSpotForAir(builder.getPos(), metalProducer);
                            }

                            if (nearestMetalSpot != null) {
                                BuildMultiple buildMultipleTask = new BuildMultiple(new LinkedList<>(Arrays.asList(builder)), logger, metalProducer,
                                        1, 1, buildHandler, unitsKeeper, generalMap, false, nearestMetalSpot);
                                unitsContainer.getUnitsController().addTask(buildMultipleTask);
                                currentWorkingRequests.get(reqName).add(builder);
                                unitsKeeper.removeIdleUnit(builder);

                            }
                        }

                    } else {
                        metalProducer = expansionBehaviour.getUnitDefWithCategories(builder.getDef(), Arrays.asList("ECONOMY", "CONVERTER", "METAL"));

                        if (metalProducer != null) {
                            BuildMultiple buildMultipleTask = new BuildMultiple(new LinkedList<>(Arrays.asList(builder)), logger, metalProducer, 1, 1, buildHandler,
                                    unitsKeeper, generalMap, false);
                            unitsContainer.getUnitsController().addTask(buildMultipleTask);
                            currentWorkingRequests.get(reqName).add(builder);
                            unitsKeeper.removeIdleUnit(builder);

                        }
                    }


                }

            } else {
                boolean trainPossible = trainBehaviour.sendTrainCommand(Arrays.asList("BUILDER"), 5);
            }
        }
    }


}

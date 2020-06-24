package behaviour;

import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import handlers.BuildHandler;
import handlers.HandlersContainer;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;
import units.controller.tasks.BuildSingle;
import units.keepers.UnitsKeeper;
import units.others.UnitsCategories;
import units.others.UnitsConfig;

import java.util.*;

public class ExpansionBehaviour extends Behaviour{

    private UnitsKeeper unitsKeeper;

    private BuildHandler buildHandler;

    private int maxFactoryBuilders = 1;
    private List<Unit> factoryBuilders;
    private List<String> builtSafeFactories = new LinkedList<>();

    public ExpansionBehaviour(AILogger logger, UnitsContainer unitsContainer, HandlersContainer handlersContainer, GeneralMap generalMap) {
        super(logger, unitsContainer, handlersContainer, generalMap);

        this.buildHandler = handlersContainer.getBuildHandler();

        this.unitsKeeper = unitsContainer.getUnitsKeeper();

        this.factoryBuilders = new LinkedList<>();

        this.maxFactoryBuilders = unitsContainer.getUnitsConfig().getFirstSettingValue("max_factory_builders");
        logger.log("max_factory_builders:" + Integer.valueOf(maxFactoryBuilders).toString());
    }


    // trimite comanda sa se construiasca o cladire ce poate antrena unitNameToTrain
    public boolean sendBuildCommandFor(String unitNameToTrain, int count, int test) {
        List<UnitDef> usedDefs = unitsContainer.getUnitsConfig().getAllBuildingsDefs();

        for (UnitDef def : usedDefs) {
            // daca def poate antrena unitatea pe care vreau sa o antrenez
            if (def.getBuildOptions().contains(callback.getUnitDefByName(unitNameToTrain))) {
                // cautam un builder pentru cladirea respectiva
                Unit builder = unitsKeeper.getBuilderFor(0, def);
                if (builder != null) {
                    buildHandler.buildUnit(new LinkedList<>(Arrays.asList(builder)), def);
                    return true;
                }
            }
        }
        // trimite comanda sa antreneze builderi
        return false;
    }


    // trimite comanda sa se construiasca o cladire ce poate antrena o unitate ce contine categoriile
    // de ex: daca vreau sa contruiesc un vehicle antiair imi va construi un armvp (
    public boolean sendBuildCommandForCategories(List<String> categories) {
        List<UnitDef> usedDefs = unitsContainer.getUnitsConfig().getAllBuildingsDefs();

        for (UnitDef def : usedDefs) {
            List<UnitDef> buildOptions = def.getBuildOptions();
            UnitDef toTrainDef = null;
            for (UnitDef buildOp : buildOptions) {
                // daca buildOp contine categoriile dorite
                if (unitsContainer.getUnitsCategories().unitContainsCategories(buildOp.getName(), (String[]) categories.toArray()) &&
                        usedDefs.contains(buildOp)) {
                    toTrainDef = buildOp;
                    break;
                }
            }

            // daca am gasit o unitate ce contine categoriile si poate fi antrenata de def
            if (toTrainDef != null) {
                // cautam un builder pentru cladirea respectiva
                Unit builder = unitsKeeper.getBuilderFor(0, def);
                if (builder != null) {
                    buildHandler.buildUnit(new LinkedList<>(Arrays.asList(builder)), def);
                    return true;
                }
                // daca nu am gasit niciun builder pentru cladirea respectiva, cautam alta cladire (deci alt def)
            }
        }
        // daca nu gaseste niciun builder pentru a construi o cladire ce poate antrena o unitate cu categoriile dorite
        // intoarce false si in afara functiei ar trebui sa obtin un comportament mai complex (sau sa renunte si sa mearga mai departe)
        return false;
    }


    public List<UnitDef> getUnitDefsWithCategories(UnitDef builderDef, List<String> udCategories){
        List<UnitDef> builderOptions = builderDef.getBuildOptions();
        UnitsCategories unitsCategories = unitsContainer.getUnitsCategories();
        UnitsConfig unitsConfig = unitsContainer.getUnitsConfig();
        List<UnitDef> possibleUnitDefs = new ArrayList<>();

        // Iau unitDef-urile care se incadreaza in categoriile dorite
        for (UnitDef ud : builderOptions) {
            if (unitsCategories.unitContainsCategories(ud.getName(), (String[]) udCategories.toArray()) &&
                    unitsConfig.containsUnit(ud.getName())) {
                possibleUnitDefs.add(ud);
            }
        }

        return possibleUnitDefs;
    }


    // Intoarce UnitDef-ul unei cladiri ce poate fi construita de builderDef si are categoriile udCategories
    public UnitDef getUnitDefWithCategories(UnitDef builderDef, List<String> udCategories) {
        List<UnitDef> possibleUnitDefs = getUnitDefsWithCategories(builderDef, udCategories);
        return chooseRandomUnitDef(possibleUnitDefs);
    }


    public UnitDef getFactoryDef(UnitDef builderDef) {
        List<UnitDef> possibleUnitDefs = getUnitDefsWithCategories(builderDef, Arrays.asList("FACTORY"));

        for (UnitDef ud : possibleUnitDefs){
            if (!builtSafeFactories.contains(ud.getName())) {
                builtSafeFactories.add(ud.getName());
                return ud;
            }
        }
        builtSafeFactories.clear();

        return chooseRandomUnitDef(possibleUnitDefs);
    }


    public UnitDef chooseRandomUnitDef(List<UnitDef> possibleUnitDefs){
        UnitDef result = null;

        if (possibleUnitDefs.size() > 0) {
            Random rand = new Random();
            result = possibleUnitDefs.get(rand.nextInt(possibleUnitDefs.size()));
        }

        return result;
    }

    @Override
    public void updateEvents(Object... params) {
        if ((((String) params[0]).equals("IdleUnit"))) {
            Unit unit = (Unit) params[1];
            if (factoryBuilders.contains(unit)) {
                factoryBuilders.remove(unit);
                unitsKeeper.addIdleUnit(unit, false);
            }
        }
    }

    @Override
    public void update(Object... params) {
        if (params.length != 1){
            return;
        }

        boolean isMetalLow = (boolean)params[0];

        if (factoryBuilders.size() < maxFactoryBuilders && !isMetalLow) {
            Unit builder = unitsKeeper.getBuilder(0);

            if (builder != null) {
                BuildSingle buildSingleTask = new BuildSingle(new LinkedList<>(Arrays.asList(builder)), logger,
                        getFactoryDef(builder.getDef()),
                        buildHandler, unitsKeeper, generalMap);
                unitsContainer.getUnitsController().addTask(buildSingleTask);
                unitsKeeper.removeIdleUnit(builder);
                factoryBuilders.add(builder);

            }

        }

    }

}
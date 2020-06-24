package units.keepers;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import logger.AILogger;
import map.GeneralMap;
import units.controller.UnitsController;
import units.others.UnitsCategories;

import java.util.*;

public class UnitsKeeper extends Keeper{

    private UnitsController unitsController;
    private List<Unit> unitsBeingBuilt = new LinkedList<>();


    // nume unitate -> lista cu grupuri
    private Map<String, List<Unit>> troops = new HashMap<>();
    private Map<String, List<Unit>> idleTroops = new HashMap<>();
    private Map<String, List<Unit>> buildings = new HashMap<>();
    private Map<String, List<Unit>> idleBuildings = new HashMap<>();


    private List<Unit> landAntiLandTroops = new LinkedList<>();
    private List<Unit> landAntiAirTroops = new LinkedList<>();
    private List<Unit> airAntiLandTroops = new LinkedList<>();
    private List<Unit> airAntiAirTroops = new LinkedList<>();
    private List<Unit> landScoutTroops = new LinkedList<>();
    private List<Unit> airScoutTroops = new LinkedList<>();
    private List<Unit> landBuilderTroops = new LinkedList<>();
    private List<Unit> airBuilderTroops = new LinkedList<>();

    public UnitsKeeper(AILogger logger, UnitsCategories unitsCategories, UnitsController unitsController) {
        super(logger, unitsCategories);
        this.unitsController = unitsController;
    }

    private void addToLists(Unit unit) {
        String unitName = unit.getDef().getName();

        if (unitsCategories.isLand(unitName)) {
            if (unitsCategories.unitContainsCategory(unitName, "ANTILAND")) {
                landAntiLandTroops.add(unit);
            }
            if (unitsCategories.unitContainsCategory(unitName, "ANTIAIR")) {
                landAntiAirTroops.add(unit);
            }
            if (unitsCategories.unitContainsCategory(unitName, "SCOUT")) {
                landScoutTroops.add(unit);
            }
            if (unitsCategories.unitContainsCategory(unitName, "BUILDER")) {
                landBuilderTroops.add(unit);
            }
        } else if (unitsCategories.isAir(unitName)) {
            if (unitsCategories.unitContainsCategory(unitName, "ANTILAND")) {
                airAntiLandTroops.add(unit);
            }
            if (unitsCategories.unitContainsCategory(unitName, "ANTIAIR")) {
                airAntiAirTroops.add(unit);
            }
            if (unitsCategories.unitContainsCategory(unitName, "SCOUT")) {
                airScoutTroops.add(unit);
            }
            if (unitsCategories.unitContainsCategory(unitName, "BUILDER")) {
                airBuilderTroops.add(unit);
            }
        }

    }

    private void removeFromLists(Unit unit) {
        try {
            String unitName = unit.getDef().getName();
            if (unitsCategories.isLand(unitName)) {
                landAntiLandTroops.remove(unit);
                landAntiAirTroops.remove(unit);
                landScoutTroops.remove(unit);
                landBuilderTroops.remove(unit);
            } else if (unitsCategories.isAir(unitName)) {
                airAntiLandTroops.remove(unit);
                airAntiAirTroops.remove(unit);
                airScoutTroops.remove(unit);
                airBuilderTroops.remove(unit);
            }
        } catch (Exception e) {
            logger.log(e);
        }

    }


    // Cele de mai jos sunt pentru unitati idle
    public Unit getBuilder(int type) {
        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (isIdle(builder)) {
                    return builder;
                }
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (isIdle(builder)) {
                    return builder;
                }
            }
        }

        return null;

    }

    public List<Unit> getBuilders(int type, int count) {
        List<Unit> builders = new ArrayList<>(count);
        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (isIdle(builder)) {
                    builders.add(builder);
                    if (builders.size() == count) {
                        return builders;
                    }
                }
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (isIdle(builder)) {
                    builders.add(builder);
                    if (builders.size() == count) {
                        return builders;
                    }
                }
            }
        }

        return null;

    }

    public Unit getBuilderFor(int type, UnitDef toBuild) {
        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (isIdle(builder) && builder.getDef().getBuildOptions().contains(toBuild)) {
                    return builder;
                }
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (isIdle(builder) && builder.getDef().getBuildOptions().contains(toBuild)) {
                    return builder;
                }
            }
        }

        return null;

    }

    public Unit getScout(int type) {

        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landScoutTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit scout = unitIterator.next();
                if (isIdle(scout)) {
                    return scout;
                }
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airScoutTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit scout = unitIterator.next();
                if (isIdle(scout)) {
                    return scout;
                }
            }
        }

        return null;
    }

    // Cele de mai jos sunt pentru orice unitati (idle sau busy)
    public Unit getBuilderIfExists(int type) {
        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                return builder;
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                return builder;

            }
        }

        return null;

    }

    public Unit getBuilderForIfExists(int type, UnitDef toBuild) {
        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (builder.getDef().getBuildOptions().contains(toBuild)) {
                    return builder;
                }
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airBuilderTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit builder = unitIterator.next();
                if (builder.getDef().getBuildOptions().contains(toBuild)) {
                    return builder;
                }
            }
        }

        return null;

    }

    public Unit getScoutIfExists(int type) {

        if (type == 0 || type == 1) {
            // land
            Iterator<Unit> unitIterator = landScoutTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit scout = unitIterator.next();
                return scout;
            }

        }

        if (type == 0 || type == 2) {
            // air
            Iterator<Unit> unitIterator = airScoutTroops.iterator();
            while (unitIterator.hasNext()) {
                Unit scout = unitIterator.next();
                return scout;
            }
        }

        return null;
    }

    // =================================================================================================================


    public UnitsCategories getUnitsCategories() {
        return unitsCategories;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    /*
            Functii low level pentru add/remove dintr-un map de mai sus
            Functiile publice ce urmeaza dupa acestea sunt cele utilizate in exterior
         */
    private Map<String, List<Unit>> getCorrespondingMap(String unitName, boolean idle) {
        if (unitsCategories.isBuilding(unitName)) {
            if (idle) {
                return idleBuildings;
            } else {
                return buildings;
            }
        } else {
            if (idle) {
                return idleTroops;
            } else {
                return troops;
            }
        }
    }

    private void addOp(Map<String, List<Unit>> correspondingMap, Unit unit) {
        String unitName = unit.getDef().getName();
        if (correspondingMap.containsKey(unitName)) {
            if (!correspondingMap.get(unitName).contains(unit)) {
                correspondingMap.get(unitName).add(unit);
            }
        } else {
            List<Unit> newUnitList = new LinkedList<>();
            newUnitList.add(unit);
            correspondingMap.put(unitName, newUnitList);
        }
    }

    private void removeOp(Map<String, List<Unit>> correspondingMap, Unit unit) {
        String unitName = unit.getDef().getName();
        if (correspondingMap.containsKey(unitName) && correspondingMap.get(unitName).contains(unit)) {
            correspondingMap.get(unitName).remove(unit);
        }
    }


    /*
        Add/Remove pentru map-ul cu toate unitatile
     */
    public void addUnit(Unit unit) {
        removeBeingBuilt(unit);
        addOp(getCorrespondingMap(unit.getDef().getName(), false), unit);
        if (!unitsCategories.isBuilding(unit.getDef().getName())) {
            addToLists(unit);
        }
    }

    public void removeUnit(Unit unit) {
        unitsController.removeUnit(unit);
        removeBeingBuilt(unit);
        removeOp(getCorrespondingMap(unit.getDef().getName(), false), unit);
        removeIdleUnit(unit);
        removeFromLists(unit);
    }

    /*
        Add/Remove pentru map-ul cu unitatile idle
     */
    public void addIdleUnit(Unit unit, boolean fromMain) {
        if (fromMain && (unitsCategories.isBuilding(unit.getDef().getName()) || !unitsController.isUnitBusy(unit))) {
            if (unitsCategories.unitContainsCategory(unit.getDef().getName(), "BUILDER")) {
                unit.patrolTo(new AIFloat3(unit.getPos().x + 256, 0, unit.getPos().z + 256), (short) 0, Integer.MAX_VALUE);
            }
            addOp(getCorrespondingMap(unit.getDef().getName(), true), unit);
        } else if (!fromMain) {
            if (unitsCategories.unitContainsCategory(unit.getDef().getName(), "BUILDER")) {
                unit.patrolTo(new AIFloat3(unit.getPos().x + 256, 0, unit.getPos().z + 256), (short) 0, Integer.MAX_VALUE);
            }
            addOp(getCorrespondingMap(unit.getDef().getName(), true), unit);
        }

    }

    public void removeIdleUnit(Unit unit) {
        removeOp(getCorrespondingMap(unit.getDef().getName(), true), unit);
    }

    /*
        Add/Remove pentru unitatile aflate in constructie
     */
    public void addBeingBuilt(Unit unit) {
        if (!unitsBeingBuilt.contains(unit)) {
            unitsBeingBuilt.add(unit);
        }
    }

    public void removeBeingBuilt(Unit unit) {
        if (unitsBeingBuilt.contains(unit)) {
            unitsBeingBuilt.remove(unit);
        }
    }


    /*
        Verifica daca unit este idle
     */
    public boolean isIdle(Unit unit) {
        if (unit == null) {
            return false;
        }
        String unitName = unit.getDef().getName();
        Map<String, List<Unit>> correspondingMap = getCorrespondingMap(unitName, true);
        if (correspondingMap.containsKey(unitName)) {
            if (correspondingMap.get(unitName).contains(unit)) {
                return true;
            }
        }
        return false;
    }

    /*
        Intoarce o lista cu toate unitatile cu numele unitName
     */
    public List<Unit> getUnitsByName(String unitName, boolean idle) {
        Map<String, List<Unit>> correspondingMap = getCorrespondingMap(unitName, idle);
        if (correspondingMap.containsKey(unitName)) {
            if (correspondingMap.get(unitName).size() > 0) {
                return correspondingMap.get(unitName);
            }
        }
        return null;
    }

    /*
        Intoarce prima unitate cu numele unitName (echivalentul a getUnitsByName(unitName, idle)[0])
     */
    public Unit getFirstUnitByName(String unitName, boolean idle) {
        Map<String, List<Unit>> correspondingMap = getCorrespondingMap(unitName, idle);
        if (correspondingMap.containsKey(unitName)) {
            if (correspondingMap.get(unitName).size() > 0) {
                return correspondingMap.get(unitName).get(0);
            }
        }
        return null;
    }


    public boolean isUnitStillAlive(Unit unit) {
        if (unit != null && unit.getDef() != null) {
            String unitName = unit.getDef().getName();
            Map<String, List<Unit>> correspondingMap = getCorrespondingMap(unitName, false);
            if (correspondingMap.containsKey(unitName)) {
                if (correspondingMap.get(unitName).contains(unit)) {
                    return true;
                }
            }
        }
        return false;
    }


    public Unit getFactoryFor(String unitName, boolean idle) {
        UnitDef currentUnitDef = callback.getUnitDefByName(unitName);
        if (idle) {
            for (String buildingName : idleBuildings.keySet()) {
                if (callback.getUnitDefByName(buildingName).getBuildOptions().contains(currentUnitDef) &&
                        !idleBuildings.get(buildingName).isEmpty()) {
                    return idleBuildings.get(buildingName).get(0);
                }
            }
        } else {
            for (String buildingName : buildings.keySet()) {
                if (callback.getUnitDefByName(buildingName).getBuildOptions().contains(currentUnitDef) &&
                        !buildings.get(buildingName).isEmpty()) {
                    return buildings.get(buildingName).get(0);
                }
            }
        }

        return null;
    }


    //==================================================================================================================

    public int getLandAntiLandCount() {
        return landAntiLandTroops.size();
    }

    public int getLandAntiAirCount() {
        return landAntiAirTroops.size();
    }

    public int getLandCount() {
        return landAntiAirTroops.size() + landAntiLandTroops.size();
    }


    public int getAirAntiLandCount() {
        return airAntiLandTroops.size();
    }

    public int getAirAntiAirCount() {
        return airAntiAirTroops.size();
    }

    public int getAirCount() {
        return airAntiAirTroops.size() + airAntiLandTroops.size();
    }


    public int getAntiLandCount() {
        return landAntiLandTroops.size() + airAntiLandTroops.size();
    }

    public int getAntiAirCount() {
        return landAntiAirTroops.size() + airAntiAirTroops.size();
    }


    public int getLandScoutCount() {
        return landScoutTroops.size();
    }

    public int getAirScoutCount() {
        return airScoutTroops.size();
    }


    public int getScoutCount() {
        return airScoutTroops.size() + landScoutTroops.size();
    }


    public int getLandBuildersCount() {
        return landBuilderTroops.size();
    }

    public int getAirBuildersCount() {
        return airBuilderTroops.size();
    }

    public int getBuildersCount() {
        return landBuilderTroops.size() + airBuilderTroops.size();
    }


    public Unit getClosestBuilding(AIFloat3 position) {
        float minDistance = Float.MAX_VALUE;
        Unit closestBuilding = null;

        for (String buildingName : buildings.keySet()) {
            for (Unit building : buildings.get(buildingName)) {
                float currentDist = GeneralMap.calculateDistance(position, building.getPos());
                if (currentDist < minDistance) {
                    minDistance = currentDist;
                    closestBuilding = building;
                }
            }
        }
        return closestBuilding;
    }


    public List<Unit> getAntiAirArmyWithSize(int size, boolean idleUnits) {
        List<Unit> army = new ArrayList<>(size);

        for (Unit landUnit : landAntiAirTroops) {
            if (isIdle(landUnit) || !idleUnits) {
                army.add(landUnit);
            }
        }

        for (Unit airUnit : airAntiAirTroops) {
            if (isIdle(airUnit) || !idleUnits) {
                army.add(airUnit);
            }
        }

        List<Unit> result = new ArrayList<>(army.size());
        for (int index = 0; index < size; ++index) {
            if (army.size() == 0) {
                break;
            }
            int randInt = new Random().nextInt(army.size());
            result.add(army.get(randInt));
            army.remove(army.get(randInt));
        }

        return result;
    }

    public List<Unit> getAntiLandArmyWithSize(int size, boolean idleUnits) {
        List<Unit> army = new ArrayList<>(size);

        for (Unit landUnit : landAntiLandTroops) {
            if (isIdle(landUnit) || !idleUnits) {
                army.add(landUnit);
            }
        }

        for (Unit airUnit : airAntiLandTroops) {
            if (isIdle(airUnit) || !idleUnits) {
                army.add(airUnit);
            }
        }

        List<Unit> result = new ArrayList<>(army.size());
        for (int index = 0; index < size; ++index) {
            if (army.size() == 0) {
                break;
            }
            int randInt = new Random().nextInt(army.size());
            result.add(army.get(randInt));
            army.remove(army.get(randInt));
        }

        return result;
    }

    public List<Unit> getAnyArmyWithSize(int size, boolean idleUnits) {
        List<Unit> army = new ArrayList<>(size);

        for (Unit landUnit : landAntiLandTroops) {
            if (isIdle(landUnit) || !idleUnits) {
                army.add(landUnit);
            }
        }

        for (Unit airUnit : airAntiLandTroops) {
            if (isIdle(airUnit) || !idleUnits) {
                army.add(airUnit);
            }
        }

        for (Unit landUnit : landAntiAirTroops) {
            if (isIdle(landUnit) || !idleUnits) {
                army.add(landUnit);
            }
        }

        for (Unit airUnit : airAntiAirTroops) {
            if (isIdle(airUnit) || !idleUnits) {
                army.add(airUnit);
            }
        }

        List<Unit> result = new ArrayList<>(army.size());
        for (int index = 0; index < size; ++index) {
            if (army.size() == 0) {
                break;
            }
            int randInt = new Random().nextInt(army.size());
            result.add(army.get(randInt));
            army.remove(army.get(randInt));
        }

        return result;
    }

}

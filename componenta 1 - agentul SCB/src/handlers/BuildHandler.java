package handlers;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import logger.AILogger;
import units.keepers.UnitsKeeper;

import java.util.List;

public class BuildHandler extends Handler{

    public BuildHandler(AILogger logger, UnitsKeeper unitsKeeper) {
        super(logger, unitsKeeper);
    }


    public AIFloat3 getClosestBuildSite(UnitDef toBuildDef, AIFloat3 builderPos) {
        Map map = logger.getCallback().getMap();
        int maxRadius = Math.max(logger.getCallback().getMap().getHeight(), logger.getCallback().getMap().getWidth());

        AIFloat3 position = map.findClosestBuildSite(toBuildDef, builderPos, (float) maxRadius, 0, 0);
        return position;
    }

    // =========================================
    // ============ Build Unit =================
    // =========================================

    public void buildUnit(Unit building, UnitDef toBuildDef, int count) {

        for (int index = 0; index < count; index++) {
            if (unitsKeeper.isUnitStillAlive(building)){
                building.build(toBuildDef, building.getPos(), 0, (short) 0, maxTimeoutValue);
            }
        }
        unitsKeeper.removeIdleUnit(building);
    }

    public void buildUnit(List<Unit> units, UnitDef toBuildDef) {
        AIFloat3 position = getClosestBuildSite(toBuildDef, units.get(0).getPos());
        for (Unit unit : units) {
            if (unitsKeeper.isUnitStillAlive(unit)){
                unit.build(toBuildDef, position, 0, (short) 0, maxTimeoutValue);
                unitsKeeper.removeIdleUnit(unit);
            }
        }
    }

    public void buildUnit(List<Unit> units, UnitDef toBuildDef, AIFloat3 position) {
        for (Unit unit : units) {
            if (unitsKeeper.isUnitStillAlive(unit)){
                unit.build(toBuildDef, position, 0, (short) 0, maxTimeoutValue);
                unitsKeeper.removeIdleUnit(unit);
            }
        }
    }

    public void buildUnit(List<Unit> units, UnitDef toBuildDef, AIFloat3 position, int timeout) {
        for (Unit unit : units) {
            if (unitsKeeper.isUnitStillAlive(unit)){
                unit.build(toBuildDef, position, 0, (short) 0, timeout);
                unitsKeeper.removeIdleUnit(unit);
            }
        }
    }

}

package units.controller.tasks;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import handlers.BuildHandler;
import logger.AILogger;
import map.GeneralMap;
import units.keepers.UnitsKeeper;

import java.util.List;

public class BuildSingle extends UnitsTask {

    private UnitsKeeper unitsKeeper;
    private GeneralMap generalMap;
    private BuildHandler buildHandler;


    private UnitDef toBuild;
    private boolean isFinished = false;
    private boolean init = false;


    private AIFloat3 startingPos;

    public BuildSingle(List<Unit> units, AILogger logger, UnitDef toBuild, BuildHandler buildHandler, UnitsKeeper unitsKeeper, GeneralMap generalMap) {
        super(units, logger);
        this.unitsKeeper = unitsKeeper;
        this.generalMap = generalMap;
        this.buildHandler = buildHandler;
        this.toBuild = toBuild;
    }

    public BuildSingle(List<Unit> units, AILogger logger, UnitDef toBuild, BuildHandler buildHandler, UnitsKeeper unitsKeeper, GeneralMap generalMap, AIFloat3 startingPos) {
        super(units, logger);
        this.unitsKeeper = unitsKeeper;
        this.generalMap = generalMap;
        this.buildHandler = buildHandler;
        this.toBuild = toBuild;
        this.startingPos = startingPos;
    }

    private AIFloat3 getMidPosition() {
        float xSum = 0, zSum = 0;

        for (Unit unit : this.units) {
            xSum += unit.getPos().x;
            zSum += unit.getPos().z;
        }
        xSum /= this.units.size();
        zSum /= this.units.size();

        return new AIFloat3(xSum, 0, zSum);
    }

    public void addUnitsToIdle() {
        for (Unit unit : this.units) {
            unitsKeeper.addIdleUnit(unit, false);
        }
    }

    @Override
    public void execute(int frame) {
        if (this.units.size() == 0) {
            isFinished = true;

        }
        if (!init) {
            init = true;
            if (startingPos == null) {
                Unit closestBuilding = unitsKeeper.getClosestBuilding(getMidPosition());
                if (closestBuilding != null){
                    startingPos = closestBuilding.getPos();
                }
            }

            if (startingPos != null) {

                AIFloat3 position = generalMap.getClosestBuildPosForFrom(toBuild, startingPos, 64);
                if (position != null) {
                    buildHandler.buildUnit(this.units, toBuild, position);
                } else {
                    isFinished = true;
                    addUnitsToIdle();
                }
            } else {
                isFinished = true;
                addUnitsToIdle();
            }
        } else {
            isFinished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void updateParams(Object... params) {

    }
}

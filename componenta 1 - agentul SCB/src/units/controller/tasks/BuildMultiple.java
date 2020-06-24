package units.controller.tasks;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import handlers.BuildHandler;
import logger.AILogger;
import map.GeneralMap;
import units.keepers.UnitsKeeper;

import java.util.*;

public class BuildMultiple extends UnitsTask {

    private UnitsKeeper unitsKeeper;
    private GeneralMap generalMap;
    private BuildHandler buildHandler;

    private UnitDef toBuild;
    private boolean isFinished = false;
    private boolean init = false;

    private int doneCount = 0;

    private int countX;
    private int countZ;

    private List<AIFloat3> positions = null;

    private boolean useMatrixCoords = true;

    public BuildMultiple(List<Unit> units, AILogger logger, UnitDef toBuild, int countX, int countZ
            , BuildHandler buildHandler, UnitsKeeper unitsKeeper, GeneralMap generalMap) {
        super(units, logger);
        this.unitsKeeper = unitsKeeper;
        this.generalMap = generalMap;
        this.buildHandler = buildHandler;
        this.toBuild = toBuild;

        this.countX = countX;
        this.countZ = countZ;
    }

    public BuildMultiple(List<Unit> units, AILogger logger, UnitDef toBuild, int countX, int countZ
            , BuildHandler buildHandler, UnitsKeeper unitsKeeper, GeneralMap generalMap, boolean useMatrixCoords) {
        super(units, logger);
        this.useMatrixCoords = useMatrixCoords;
        this.unitsKeeper = unitsKeeper;
        this.generalMap = generalMap;
        this.buildHandler = buildHandler;
        this.toBuild = toBuild;

        this.countX = countX;
        this.countZ = countZ;
    }

    public BuildMultiple(List<Unit> units, AILogger logger, UnitDef toBuild, int countX, int countZ
            , BuildHandler buildHandler, UnitsKeeper unitsKeeper, GeneralMap generalMap, boolean useMatrixCoords, AIFloat3 startingPosition) {
        super(units, logger);
        this.useMatrixCoords = useMatrixCoords;
        this.unitsKeeper = unitsKeeper;
        this.generalMap = generalMap;
        this.buildHandler = buildHandler;
        this.toBuild = toBuild;

        this.positions = new LinkedList<>(Arrays.asList(startingPosition));
        this.countX = countX;
        this.countZ = countZ;
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

        } else if (!init) {
            init = true;

            boolean validClosestBuilding = false;
            if (this.positions == null) {
                Unit closestBuilding = unitsKeeper.getClosestBuilding(getMidPosition());
                if (closestBuilding != null) {
                    validClosestBuilding = true;
                } else {
                    isFinished = true;
                    addUnitsToIdle();
                    return;
                }

                this.positions = generalMap.getClosestBuildPosForFrom(toBuild, countX, countZ, closestBuilding.getPos(), 64, this.useMatrixCoords);
            } else {
                AIFloat3 startPos = this.positions.get(0);

                this.positions = generalMap.getBuildGroupPositions((int) (startPos.x / 8), (int) (startPos.z / 8), countX, countZ,
                        toBuild.getXSize(), toBuild.getZSize(), 8, useMatrixCoords);
                validClosestBuilding = true;
            }

            if (positions != null && validClosestBuilding) {

                for (int index = 0; index < units.size(); ++index) {

                    if (this.doneCount >= this.positions.size()) {
                        this.isFinished = true;
                        addUnitsToIdle();
                        break;
                    } else {
                        if (unitsKeeper.isUnitStillAlive(this.units.get(index))){
                            buildHandler.buildUnit(new LinkedList<>(Arrays.asList(this.units.get(index))), toBuild, positions.get(index));
                            this.doneCount++;
                        } else {
                            this.units.remove(index);
                        }
                    }
                }

                this.doneCount--;
            } else {
                isFinished = true;
                addUnitsToIdle();
            }
        }


    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void updateParams(Object... params) {

        if (((String) params[0]).equals("IdleUnit")) {
            Unit unit = (Unit) params[1];
            if (units.contains(unit) && this.positions != null) {
                this.doneCount++;
                if (this.doneCount >= this.positions.size()) {
                    this.isFinished = true;
                    addUnitsToIdle();
                } else {
                    buildHandler.buildUnit(new LinkedList<>(Arrays.asList(unit)), toBuild, positions.get(this.doneCount));
                }
            }
        }

    }
}

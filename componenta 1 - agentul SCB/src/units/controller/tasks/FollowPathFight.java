package units.controller.tasks;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import handlers.AttackHandler;
import handlers.MoveHandler;
import logger.AILogger;

import java.util.List;

public class FollowPathFight extends UnitsTask {

    float maxDistance;
    float maxWaypointDistance;
    private List<AIFloat3> path;
    private AttackHandler attackHandler;
    private MoveHandler moveHandler;
    private int currentWaypointIndex = 0;
    private int step = 15;

    private boolean direction = true; // true inainte | false inapoi

    private boolean init = false;

    private boolean isFinishedStart = false;
    private boolean isFinishedEnd = false;

    public FollowPathFight(AILogger logger, List<Unit> units, List<AIFloat3> path,
                           AttackHandler attackHandler, MoveHandler moveHandler) {
        super(units, logger);
        this.path = path;
        this.attackHandler = attackHandler;
        this.moveHandler = moveHandler;
        this.maxDistance = units.size() * 256;
        this.maxWaypointDistance = 256;
    }

    public void removeUnit(Unit unit) {
        super.removeUnit(unit);
    }

    public void addUnit(Unit unit) {
        super.addUnit(unit);
    }

    public void execute(int frame) {
        if (this.units.size() == 0) {
            this.isFinishedEnd = true;
        }
        if (!isFinishedStart && !isFinishedEnd) {
            if (!init) {
                attackHandler.fight(this.units, path.get(currentWaypointIndex), 0, frame + 1000);
                init = true;
            }

            if (isGroupAtWaypoint()) {
                if (direction) {
                    if (!goNext(frame + 1000)) {
                        isFinishedEnd = true;
                    }
                } else {
                    if (goPrevious(frame + 1000)) {
                        isFinishedStart = true;
                    }
                }
            }

        }
    }

    @Override
    public boolean isFinished() {
        return isFinishedEnd || isFinishedStart;
    }

    @Override
    public void updateParams(Object... params) {

    }

    public boolean goNext(int timeout) {
        if (currentWaypointIndex < path.size() - 1) {
            this.currentWaypointIndex += this.step;
            if (this.currentWaypointIndex >= path.size()) {
                this.currentWaypointIndex = path.size() - 1;
                moveHandler.moveTo(this.units, path.get(currentWaypointIndex));
            } else {
                attackHandler.fight(this.units, path.get(currentWaypointIndex), 0, timeout);
            }
            return true;
        }
        return false;
    }

    public boolean goPrevious(int timeout) {
        if (currentWaypointIndex > 0) {
            this.currentWaypointIndex -= this.step;
            if (this.currentWaypointIndex <= 0) {
                this.currentWaypointIndex = 0;
                moveHandler.moveTo(this.units, path.get(currentWaypointIndex));
            } else {
                attackHandler.fight(this.units, path.get(currentWaypointIndex), 0, timeout);
            }
            return true;
        }
        return false;
    }


    private boolean isUnitCloseToWaypoint(Unit unit) {
        AIFloat3 waypoint = path.get(currentWaypointIndex);
        return (Math.abs(waypoint.x - unit.getPos().x) < maxWaypointDistance
                && Math.abs(waypoint.z - unit.getPos().z) < maxWaypointDistance);
    }

    private Unit isAtLeastOneUnitClose() {
        for (Unit unit : units) {
            if (isUnitCloseToWaypoint(unit)) {
                return unit;
            }
        }
        return null;
    }

    private boolean areUnitsCloseToUnit(Unit unitParam) {
        for (Unit unit : units) {
            if (Math.abs(unit.getPos().x - unitParam.getPos().x) > maxDistance
                    && Math.abs(unit.getPos().z - unitParam.getPos().z) > maxDistance) {
                return false;
            }
        }
        return true;
    }


    public boolean isGroupAtWaypoint() {
        // Verific daca macar o unitate a ajuns
        // si daca da  atunci verific si daca unitatie sunt in apropiere unele de altele
        // ( sau eventual daca se misca pt ca daca stau pe loc inseamna ca au ajuns unde trebuie dar e posibil sa nu fie chiar bine asa)

        Unit closeUnit = isAtLeastOneUnitClose();
        if (closeUnit != null) {
            if (areUnitsCloseToUnit(closeUnit)) {
                return true;
            }
        }
        return false;
    }

}

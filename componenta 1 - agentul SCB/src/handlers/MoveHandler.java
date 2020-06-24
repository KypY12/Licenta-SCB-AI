package handlers;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import logger.AILogger;
import units.keepers.UnitsKeeper;

import java.util.List;

public class MoveHandler extends Handler{

    public MoveHandler(AILogger logger, UnitsKeeper unitsKeeper) {
        super(logger, unitsKeeper);
    }


    // =========================================
    // =============== Move To =================
    // =========================================

    public void moveTo(List<Unit> units, AIFloat3 toPosition){
        for (Unit unit : units){
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.moveTo(toPosition, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void moveTo(List<Unit> units, AIFloat3 toPosition, int options, int timeout){
        for (Unit unit : units){
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.moveTo(toPosition, (short)options, timeout);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    // =========================================
    // ============= Patrol To =================
    // =========================================

    public void patrolTo(List<Unit> units, AIFloat3 toPosition){
        for (Unit unit : units){
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.patrolTo(toPosition, (short)0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void patrolTo(List<Unit> units, AIFloat3 toPosition, int options, int timeout){
        for (Unit unit : units){
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.patrolTo(toPosition, (short)options, timeout);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

}

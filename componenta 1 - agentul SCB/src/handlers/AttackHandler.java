package handlers;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import logger.AILogger;
import units.keepers.UnitsKeeper;

import java.util.List;

public class AttackHandler extends Handler{

    public AttackHandler(AILogger logger, UnitsKeeper unitsKeeper) {
        super(logger, unitsKeeper);
    }

    // =========================================
    // ============ Attack Unit ================
    // =========================================
    public void attackUnit(List<Unit> units, Unit unitToAttack) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.attack(unitToAttack, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void attackUnit(List<Unit> units, Unit unitToAttack, int options, int timeout) {
        for (Unit unit : units) {
           try {
               if (unitsKeeper.isUnitStillAlive(unit)){
                   unit.attack(unitToAttack, (short) options, timeout);
                   unitsKeeper.removeIdleUnit(unit);
               }

           } catch (Exception e) {
               logger.log(e);
           }
        }
    }


    // =========================================
    // ============ Attack Area ================
    // =========================================

    public void attackArea(List<Unit> units, AIFloat3 positionToAttack) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.attackArea(positionToAttack, (float) 1, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void attackArea(List<Unit> units, AIFloat3 positionToAttack, float radius) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.attackArea(positionToAttack, radius, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void attackArea(List<Unit> units, AIFloat3 positionToAttack, int radius) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.attackArea(positionToAttack, (float) radius, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void attackArea(List<Unit> units, AIFloat3 positionToAttack, float radius, int options, int timeout) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.attackArea(positionToAttack, radius, (short) options, timeout);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void attackArea(List<Unit> units, AIFloat3 positionToAttack, int radius, int options, int timeout) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.attackArea(positionToAttack, radius, (short) options, timeout);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }


    // =========================================
    // ================ Fight ==================
    // =========================================

    public void fight(List<Unit> units, AIFloat3 toPosition) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.fight(toPosition, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }

    public void fight(List<Unit> units, AIFloat3 toPosition, int options, int timeout) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.fight(toPosition, (short) options, timeout);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }


    // =========================================
    // ============= Guard Unit ================
    // =========================================

    public void guardUnit(List<Unit> units, Unit unitToGuard) {
        for (Unit unit : units) {
           try {
               if (unitsKeeper.isUnitStillAlive(unit)){
                   unit.guard(unitToGuard, (short) 0, maxTimeoutValue);
                   unitsKeeper.removeIdleUnit(unit);
               }

           } catch (Exception e) {
               logger.log(e);
           }
        }
    }

    public void guardUnit(List<Unit> units, Unit unitToGuard, int options, int timeout) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    unit.guard(unitToGuard, (short) 0, maxTimeoutValue);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }


    // =========================================
    // =========== Self Destruct ===============
    // =========================================

    public void selfDestruct(Unit unit) {
       try {
           if (unitsKeeper.isUnitStillAlive(unit)){
               unit.selfDestruct((short) 0, maxTimeoutValue);
               unitsKeeper.removeIdleUnit(unit);
           }

       } catch (Exception e) {
           logger.log(e);
       }
    }

    public void selfDestruct(List<Unit> units) {
        for (Unit unit : units) {
            try {
                if (unitsKeeper.isUnitStillAlive(unit)){
                    selfDestruct(unit);
                    unitsKeeper.removeIdleUnit(unit);
                }

            } catch (Exception e) {
                logger.log(e);
            }
        }
    }


}

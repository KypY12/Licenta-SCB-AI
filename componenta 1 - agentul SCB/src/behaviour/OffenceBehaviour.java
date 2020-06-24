package behaviour;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import handlers.AttackHandler;
import handlers.HandlersContainer;
import handlers.MoveHandler;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;
import units.controller.tasks.FollowPathFight;
import units.keepers.EnemyUnitsKeeper;
import units.keepers.UnitsKeeper;
import units.others.EnemyUnitEntry;
import units.others.UnitsCategories;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OffenceBehaviour extends Behaviour {

    private UnitsKeeper unitsKeeper;

    private AttackHandler attackHandler;
    private MoveHandler moveHandler;

    private ExpansionBehaviour expansionBehaviour;
    private TrainBehaviour trainBehaviour;

    private EnemyUnitsKeeper enemyUnitsKeeper;

    private boolean isMetalLow = false;
    private boolean isEnergyLow = false;

    private boolean firstAttack = true;

    private int attackArmyCount;
    private int attackArmyMinCount;
    private int attackArmyTrainCount;

    private int scoutingAttackArmyCount;
    private int scoutingAttackArmyMinCount;
    private int scoutingAttackArmyTrainCount;

    private int unitAttackArmyCount;
    private int unitAttackArmyMinCount;
    private int unitAttackArmyTrainCount;

    private int scoutTrainCount;


    public OffenceBehaviour(AILogger logger, UnitsContainer unitsContainer, HandlersContainer handlersContainer,
                            GeneralMap generalMap, ExpansionBehaviour expansionBehaviour, TrainBehaviour trainBehaviour) {
        super(logger, unitsContainer, handlersContainer, generalMap);

        this.attackHandler = handlersContainer.getAttackHandler();
        this.moveHandler = handlersContainer.getMoveHandler();

        this.unitsKeeper = unitsContainer.getUnitsKeeper();

        this.expansionBehaviour = expansionBehaviour;
        this.trainBehaviour = trainBehaviour;

        this.enemyUnitsKeeper = unitsContainer.getEnemyUnitsKeeper();

        this.attackArmyCount = unitsContainer.getUnitsConfig().getFirstSettingValue("attack_army_count");
        // x% din attackArmyCount sa fie minim
        this.attackArmyMinCount = this.attackArmyCount *
                unitsContainer.getUnitsConfig().getFirstSettingValue("attack_army_min_count") / 100;
        this.attackArmyTrainCount = unitsContainer.getUnitsConfig().getFirstSettingValue("attack_army_train_count");

        this.scoutingAttackArmyCount = unitsContainer.getUnitsConfig().getFirstSettingValue("scouting_attack_army_count");
        // x% din scoutingAttackArmyCount sa fie minim
        this.scoutingAttackArmyMinCount = this.scoutingAttackArmyCount *
                unitsContainer.getUnitsConfig().getFirstSettingValue("scouting_attack_army_min_count") / 100;
        this.scoutingAttackArmyTrainCount = unitsContainer.getUnitsConfig().getFirstSettingValue("scouting_attack_army_train_count");

        this.unitAttackArmyCount = unitsContainer.getUnitsConfig().getFirstSettingValue("unit_attack_army_count");
        // x% din unitAttackArmyCount sa fie minim
        this.unitAttackArmyMinCount = this.unitAttackArmyCount *
                unitsContainer.getUnitsConfig().getFirstSettingValue("unit_attack_army_min_count") / 100;
        this.unitAttackArmyTrainCount = unitsContainer.getUnitsConfig().getFirstSettingValue("unit_attack_army_train_count");

        this.scoutTrainCount = unitsContainer.getUnitsConfig().getFirstSettingValue("scout_train_count");

        logger.log("attack_army_count:" + Integer.valueOf(attackArmyCount).toString());
        logger.log("attack_army_min_count:" + Integer.valueOf(attackArmyMinCount).toString());
        logger.log("attack_army_train_count:" + Integer.valueOf(attackArmyTrainCount).toString());
        logger.log("scouting_attack_army_count:" + Integer.valueOf(scoutingAttackArmyCount).toString());
        logger.log("scouting_attack_army_min_count:" + Integer.valueOf(scoutingAttackArmyMinCount).toString());
        logger.log("scouting_attack_army_train_count:" + Integer.valueOf(scoutingAttackArmyTrainCount).toString());
        logger.log("unit_attack_army_count:" + Integer.valueOf(unitAttackArmyCount).toString());
        logger.log("unit_attack_army_min_count:" + Integer.valueOf(unitAttackArmyMinCount).toString());
        logger.log("unit_attack_army_train_count:" + Integer.valueOf(unitAttackArmyTrainCount).toString());
        logger.log("scout_train_count:" + Integer.valueOf(scoutTrainCount).toString());

    }


    @Override
    public void updateEvents(Object... params) {
    }

    @Override
    public void update(Object... params) {
        if (params.length != 3){
            return;
        }

        int frame = (int) params[0];
        this.isMetalLow = (boolean) params[1];
        this.isEnergyLow = (boolean) params[2];

        sendRandomScout();

        Unit unitToAttack = getUnitToAttack();
        if (unitToAttack != null) {
            sendAttackCommand(unitToAttack);
        }

        AIFloat3 positionToAttack = getAttackPosition();
        if (positionToAttack != null) {
            sendAttackCommand(positionToAttack);
        } else if (firstAttack) {
            sendAttackCommand(inferEnemyStartingPosition());
            firstAttack = false;
        } else {
            sendScoutingAttackCommand(frame);
        }

    }


    private AIFloat3 inferEnemyStartingPosition() {
        Map map = this.logger.getCallback().getMap();
        AIFloat3 startPos = map.getStartPos();

        Integer maxWidth = map.getWidth() * 8 - 1;
        Integer maxHeight = map.getHeight() * 8 - 1;
        AIFloat3 enemyStartPos = new AIFloat3(maxWidth - startPos.x, startPos.y, maxHeight - startPos.z);

        return enemyStartPos;
    }


    private AIFloat3 getAttackPosition() {
        List<EnemyUnitEntry> enemySeenBuildings = enemyUnitsKeeper.getBuildingsSeenEnemies();
        if (enemySeenBuildings.size() > 0 && enemySeenBuildings.get(0) != null) {

            Iterator<EnemyUnitEntry> it = enemySeenBuildings.iterator();
            while (it.hasNext()) {
                EnemyUnitEntry current = it.next();
                if (current != null && current.getEnemyUnit() != null && current.getEnemyUnit().getDef() != null) {
                    return current.getPosition();
                } else {
                    it.remove();
                }
            }
        }
        return null;
    }


    private Unit getUnitToAttack() {
        if (enemyUnitsKeeper.getAirSeenEnemies().size() > 0) {
            return enemyUnitsKeeper.getAirSeenEnemies().get(0).getEnemyUnit();
        } else if (enemyUnitsKeeper.getLandSeenEnemies().size() > 0) {
            return enemyUnitsKeeper.getLandSeenEnemies().get(0).getEnemyUnit();
        }
        return null;
    }


    // intoarce unitatea cu panta maxima cea mai mica
    public Unit getMaxSlopeUnit(List<Unit> units) {
        float minSlope = 1f;
        Unit minSlopeUnit = null;

        for (Unit unit : units) {
            {
                if (unitsContainer.getUnitsCategories().unitContainsCategory(unit.getDef().getName(), "AIR")) {
                    continue;
                }
                if (unit.getDef().getMoveData().getMaxSlope() < minSlope) {
                    minSlope = unit.getDef().getMoveData().getMaxSlope();
                    minSlopeUnit = unit;
                }
            }

        }

        return minSlopeUnit;
    }


    private void sendAttackCommand(AIFloat3 position) {

        List<Unit> units = unitsKeeper.getAnyArmyWithSize(attackArmyCount, true);

        if (units.size() >= attackArmyMinCount) {
            Unit maxSlopeUnit = getMaxSlopeUnit(units);

            float maxSlope = 1f;
            if (maxSlopeUnit != null) {
                maxSlope = maxSlopeUnit.getDef().getMoveData().getMaxSlope();
            }

            List<AIFloat3> path = generalMap.constructPath(maxSlopeUnit.getPos(), position, maxSlope, generalMap.getMaxSize(units));
            if (path == null) {
                return;
            }

            FollowPathFight followPathFightTask = new FollowPathFight(logger, units, path, attackHandler, moveHandler);
            unitsContainer.getUnitsController().addTask(followPathFightTask);
            firstAttack = false;

        } else {
            boolean canTrain;

            canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("ANTILAND"), attackArmyTrainCount, true);
            if (!isMetalLow && !isEnergyLow) {
                if (!canTrain) {
                    expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("ANTILAND"));
                }
            }

            if (enemyUnitsKeeper.getAirSeenEnemies().size() > 0) {
                canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("ANTIAIR"), attackArmyTrainCount, true);
                if (!isMetalLow && !isEnergyLow) {
                    if (!canTrain) {
                        expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("ANTIAIR"));
                    }
                }
            }

        }

    }

    private void sendScoutingAttackCommand(int frame) {

        List<Unit> units = unitsKeeper.getAnyArmyWithSize(scoutingAttackArmyCount, true);

        if (units.size() >= scoutingAttackArmyMinCount) {

            AIFloat3 scoutingPosition = generalMap.randomNotExploredPosition();
            if (scoutingPosition != null) {
                attackHandler.fight(units, scoutingPosition, 0, frame + 1000);

            }

        } else {
            boolean canTrain;
            canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("ANTILAND"), scoutingAttackArmyTrainCount, true);

            if (!isMetalLow && !isEnergyLow) {
                if (!canTrain) {
                    expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("ANTILAND"));
                }
            }

            if (enemyUnitsKeeper.getAirSeenEnemies().size() > 0) {
                canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("ANTIAIR"), scoutingAttackArmyTrainCount, true);
                if (!isMetalLow && !isEnergyLow) {
                    if (!canTrain) {
                        expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("ANTIAIR"));
                    }
                }
            }

        }
    }

    private void sendAttackCommand(Unit unit) {
        UnitsCategories unitsCategories = unitsContainer.getUnitsCategories();
        if (unitsCategories == null || unit == null) {
            return;
        }

        UnitDef unitDef = unit.getDef();
        if (unitDef == null) {
            return;
        }

        String unitName = unitDef.getName();
        if (unitName == null) {
            return;
        }

        if (unitsCategories.isAir(unitName)) {
            List<Unit> units = unitsKeeper.getAntiAirArmyWithSize(unitAttackArmyCount, false);

            if (units.size() >= unitAttackArmyMinCount) {
                attackHandler.attackUnit(units, unit);
            } else {
                boolean canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("ANTIAIR"), unitAttackArmyTrainCount, true);
                if (!isMetalLow && !isEnergyLow) {
                    if (!canTrain) {
                        expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("ANTIAIR"));
                    }
                }

            }

        } else if (unitsCategories.isLand(unitName)) {
            List<Unit> units = unitsKeeper.getAntiLandArmyWithSize(unitAttackArmyCount, false);

            if (units.size() >= unitAttackArmyMinCount) {
                attackHandler.attackUnit(units, unit);
            } else {
                boolean canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("ANTILAND"), unitAttackArmyTrainCount, true);

                if (!isMetalLow && !isEnergyLow) {
                    if (!canTrain) {
                        expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("ANTILAND"));
                    }
                }

            }
        }
    }


    private void sendRandomScout() {
        Unit scout = unitsKeeper.getScout(0);
        if (scout != null) {
            AIFloat3 scoutingPosition = generalMap.randomNotExploredPosition();
            if (scoutingPosition != null) {
                moveHandler.moveTo(new LinkedList<>(Arrays.asList(scout)), scoutingPosition);
            }

        } else {
            boolean canTrain = trainBehaviour.sendTrainCommand(Arrays.asList("SCOUT"), scoutTrainCount, true);
            if (!canTrain && !isMetalLow && !isEnergyLow) {
                expansionBehaviour.sendBuildCommandForCategories(Arrays.asList("SCOUT"));
            }
        }

    }


}


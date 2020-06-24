package units.keepers;

import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import logger.AILogger;
import units.others.EnemyUnitEntry;
import units.others.UnitsCategories;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EnemyUnitsKeeper extends Keeper{

    private List<Unit> buildingsVisibleEnemies = new LinkedList<>();

    private List<Unit> landVisibleEnemies = new LinkedList<>();
    private List<Unit> airVisibleEnemies = new LinkedList<>();

    private List<EnemyUnitEntry> buildingsSeenEnemies = new LinkedList<>();
    private List<EnemyUnitEntry> landSeenEnemies = new LinkedList<>();
    private List<EnemyUnitEntry> airSeenEnemies = new LinkedList<>();


    public EnemyUnitsKeeper(AILogger logger, UnitsCategories unitsCategories) {
        super(logger, unitsCategories);
    }


    private void removeFromAllSeen(Unit enemy, List<EnemyUnitEntry> seenEnemies) {
        try {
            if (enemy != null && seenEnemies != null) {

                Iterator<EnemyUnitEntry> it = seenEnemies.iterator();
                while (it.hasNext()) {
                    EnemyUnitEntry currentEU = it.next();
                    if (currentEU.getEnemyUnit().equals(enemy)) {
                        it.remove();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            logger.log(e);
        }
    }


    private boolean containsAllSeen(Unit enemy, List<EnemyUnitEntry> seenEnemies) {
        try {
            if (enemy != null && seenEnemies != null) {
                Iterator<EnemyUnitEntry> it = seenEnemies.iterator();
                while (it.hasNext()) {
                    EnemyUnitEntry currentEU = it.next();
                    if (currentEU.getEnemyUnit().equals(enemy)) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return false;
    }


    private List<Unit> getCorrespondingVisible(Unit enemy) {
        List<Unit> visibleEnemies = null;

        try {
            if (enemy != null) {
                String enemyUnitName = enemy.getDef().getName();
                if (unitsCategories.isBuilding(enemyUnitName)) {
                    visibleEnemies = buildingsVisibleEnemies;
                } else if (unitsCategories.isAir(enemyUnitName)) {
                    visibleEnemies = airVisibleEnemies;
                } else {
                    visibleEnemies = landVisibleEnemies;
                }
            }

        } catch (Exception e) {
            logger.log(e);
        }

        return visibleEnemies;
    }


    private List<EnemyUnitEntry> getCorrespondingSeen(Unit enemy) {
        List<EnemyUnitEntry> seenEnemies = null;
        try {
            if (enemy != null) {

                String enemyUnitName = enemy.getDef().getName();
                if (unitsCategories.isBuilding(enemyUnitName)) {
                    seenEnemies = buildingsSeenEnemies;
                } else if (unitsCategories.isLand(enemyUnitName)) {
                    seenEnemies = landSeenEnemies;
                } else if (unitsCategories.isAir(enemyUnitName)) {
                    seenEnemies = airSeenEnemies;
                }
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return seenEnemies;
    }


    public void addVisibleEnemy(Unit enemy) {
        try {
            if (enemy != null && getCorrespondingVisible(enemy) != null && getCorrespondingSeen(enemy) != null) {

                getCorrespondingVisible(enemy).add(enemy);

                if (!containsAllSeen(enemy, getCorrespondingSeen(enemy))) {
                    getCorrespondingSeen(enemy).add(new EnemyUnitEntry(enemy));
                }
            }
        } catch (Exception e) {
            logger.log(e);
        }

    }


    public void removeVisibleEnemy(Unit enemy) {
        try {
            if (enemy != null && getCorrespondingVisible(enemy) != null) {
                getCorrespondingVisible(enemy).remove(enemy);
            }
        } catch (Exception e) {
            logger.log(e);
        }
    }


    public void removeSeenEnemy(Unit enemy) {
        try {
            removeFromAllSeen(enemy, getCorrespondingSeen(enemy));
            removeVisibleEnemy(enemy);
        } catch (Exception e) {
            logger.log(e);
        }
    }


    public int getVisibleCount() {
        return landVisibleEnemies.size() + airVisibleEnemies.size();
    }


    public int getSeenCount() {
        return landSeenEnemies.size() + airSeenEnemies.size();
    }


    public int getLandVisibleCount() {
        return landVisibleEnemies.size();
    }


    public int getAirVisibleCount() {
        return airVisibleEnemies.size();
    }


    public int getLandSeenCount() {
        return landSeenEnemies.size();
    }


    public int getAirSeenCount() {
        return airSeenEnemies.size();
    }

    public List<Unit> getBuildingsVisibleEnemies() {
        return buildingsVisibleEnemies;
    }

    public List<Unit> getLandVisibleEnemies() {
        return landVisibleEnemies;
    }

    public List<Unit> getAirVisibleEnemies() {
        return airVisibleEnemies;
    }

    public List<EnemyUnitEntry> getBuildingsSeenEnemies() {
        return buildingsSeenEnemies;
    }

    public List<EnemyUnitEntry> getLandSeenEnemies() {
        return landSeenEnemies;
    }

    public List<EnemyUnitEntry> getAirSeenEnemies() {
        return airSeenEnemies;
    }
}

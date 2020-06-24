package units.others;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;

public class EnemyUnitEntry {

    private Unit enemyUnit;
    private AIFloat3 position;

    public EnemyUnitEntry(Unit enemyUnit){
        this.enemyUnit = enemyUnit;
        this.position = new AIFloat3(enemyUnit.getPos());
    }

    public Unit getEnemyUnit() {
        return enemyUnit;
    }

    public AIFloat3 getPosition() {
        return position;
    }

    @Override
    public String toString() {
        String test1 = enemyUnit.toString();
        UnitDef test2 = enemyUnit.getDef();
        String testName = "null test name";
        if (test2 != null){
            testName = test2.getName();
        }

        return test1 + " ==== " + testName;
    }

    @Override
    public boolean equals(Object obj) {
        return enemyUnit.equals(((EnemyUnitEntry)obj).getEnemyUnit());
    }

}

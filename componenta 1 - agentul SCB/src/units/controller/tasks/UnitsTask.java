package units.controller.tasks;

import com.springrts.ai.oo.clb.Unit;
import logger.AILogger;

import java.util.LinkedList;
import java.util.List;

public abstract class UnitsTask {

    protected AILogger logger;
    protected List<Unit> units;

    public UnitsTask(List<Unit> units, AILogger logger){
        this.units = new LinkedList<>(units);
        this.logger = logger;
    }

    public abstract void execute(int frame);

    public abstract boolean isFinished();

    public abstract void updateParams(Object... params);

    public List<Unit> getUnits() {
        return units;
    }

    public void removeUnit(Unit unit){
        if (this.units.contains(unit)){
            this.units.remove(unit);
        }
    }

    public void addUnit(Unit unit){
        if (!this.units.contains(unit)){
            this.units.add(unit);
        }
    }

    public boolean hasUnit(Unit unit){
        return this.units.contains(unit);
    }

}

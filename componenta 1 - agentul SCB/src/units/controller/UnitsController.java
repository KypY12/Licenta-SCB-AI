package units.controller;

import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import logger.AILogger;
import units.controller.tasks.UnitsTask;

import java.util.*;

public class UnitsController {

    private AILogger logger;
    private OOAICallback callback;
    private int tickRate;

    private List<UnitsTask> tasks = new LinkedList<>();

    private Map<Unit, Integer> busyUnits = new HashMap<>();


    public UnitsController(AILogger logger, int tickRate) {
        this.logger = logger;
        this.callback = logger.getCallback();

        this.tickRate = tickRate;
        logger.log("units_controller_tick_rate:" + Integer.valueOf(tickRate).toString());

    }

    public boolean isUnitBusy(Unit unit) {
        return busyUnits.containsKey(unit);
    }

    public void removeUnit(Unit unit) {
        if (isUnitBusy(unit)) {
            busyUnits.remove(unit);

            Iterator<UnitsTask> it = tasks.iterator();
            while (it.hasNext()) {
                UnitsTask unitsTask = it.next();
                if (unitsTask.hasUnit(unit)) {
                    unitsTask.removeUnit(unit);
                    break;
                }
            }
        }
    }

    public void addTask(UnitsTask task) {
        List<Unit> taskUnits = task.getUnits();
        for (Unit unit : taskUnits) {
            busyUnits.put(unit, 1);
        }
        tasks.add(task);
    }


    public void updateParams(Object... params) {
        for (UnitsTask ut : tasks) {
            ut.updateParams(params);
        }
    }


    public void update(int frame) {

        if (frame % tickRate == 0) {
            Iterator<UnitsTask> it = tasks.iterator();
            while (it.hasNext()) {
                UnitsTask task = it.next();
                if (!task.isFinished()) {
                    task.execute(frame);
                } else {
                    List<Unit> taskUnits = task.getUnits();
                    for (Unit unit : taskUnits) {
                        busyUnits.remove(unit);
                    }
                    it.remove();
                }
            }
        }

    }


}

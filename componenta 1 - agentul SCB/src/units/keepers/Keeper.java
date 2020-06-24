package units.keepers;

import com.springrts.ai.oo.clb.OOAICallback;
import logger.AILogger;
import units.others.UnitsCategories;

public abstract class Keeper {
    protected AILogger logger;
    protected OOAICallback callback;
    protected UnitsCategories unitsCategories;

    public Keeper(AILogger logger, UnitsCategories unitsCategories){
        this.logger = logger;
        this.callback = logger.getCallback();
        this.unitsCategories = unitsCategories;
    }
}

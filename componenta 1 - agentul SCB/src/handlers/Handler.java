package handlers;

import logger.AILogger;
import units.keepers.UnitsKeeper;

public abstract class Handler {
    protected AILogger logger;
    protected UnitsKeeper unitsKeeper;
    protected int maxTimeoutValue;

    public Handler(AILogger logger, UnitsKeeper unitsKeeper) {
        this.logger = logger;
        this.unitsKeeper = unitsKeeper;
        this.maxTimeoutValue = Integer.MAX_VALUE;
    }

}

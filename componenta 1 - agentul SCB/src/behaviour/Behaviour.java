package behaviour;

import com.springrts.ai.oo.clb.OOAICallback;
import handlers.HandlersContainer;
import logger.AILogger;
import map.GeneralMap;
import units.UnitsContainer;


public abstract class Behaviour {

    protected AILogger logger;
    protected OOAICallback callback;

    protected UnitsContainer unitsContainer;

    protected HandlersContainer handlersContainer;

    protected GeneralMap generalMap;


    public Behaviour(AILogger logger, UnitsContainer unitsContainer, HandlersContainer handlersContainer, GeneralMap generalMap) {
        this.logger = logger;
        this.callback = logger.getCallback();

        this.handlersContainer = handlersContainer;

        this.unitsContainer = unitsContainer;

        this.generalMap = generalMap;
    }

    public abstract void updateEvents(Object... params);

    public abstract void update(Object... params);

}

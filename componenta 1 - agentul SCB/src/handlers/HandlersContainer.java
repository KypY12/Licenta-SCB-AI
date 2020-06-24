package handlers;

public class HandlersContainer {
    private AttackHandler attackHandler;
    private BuildHandler buildHandler;
    private MoveHandler moveHandler;

    public HandlersContainer(AttackHandler attackHandler, BuildHandler buildHandler, MoveHandler moveHandler) {
        this.attackHandler = attackHandler;
        this.buildHandler = buildHandler;
        this.moveHandler = moveHandler;
    }

    public AttackHandler getAttackHandler() {
        return attackHandler;
    }

    public BuildHandler getBuildHandler() {
        return buildHandler;
    }

    public MoveHandler getMoveHandler() {
        return moveHandler;
    }


}

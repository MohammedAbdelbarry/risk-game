package risk.game.state;

public enum Phase {
    ALLOCATE("Allocate"),
    ATTACK("Attack");

    private String value;
    Phase(String value) {
        this.value = value;
    }

    public Phase getNextPhase() {
        if (this == ALLOCATE) {
            return ATTACK;
        } else {
            return ALLOCATE;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

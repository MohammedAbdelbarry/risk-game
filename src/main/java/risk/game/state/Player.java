package risk.game.state;

public enum Player {
    PLAYER1("Player 1"),
    PLAYER2("Player 2");

    private String value;
    Player(String value) {
        this.value = value;
    }

    public Player getOpponent() {
        if (this == PLAYER1) {
            return PLAYER2;
        } else {
            return PLAYER1;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

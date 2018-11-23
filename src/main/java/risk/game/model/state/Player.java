package risk.game.model.state;

public enum Player {
    PLAYER1("player1"),
    PLAYER2("player2");

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

    public static Player valueOf(int playerNum) {
        if (playerNum == 1) {
            return PLAYER1;
        } else {
            return PLAYER2;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

package risk.game.controller;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import risk.game.model.agents.GameAgent;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;
import risk.game.view.GameVisualizer;

import java.util.Collection;

public class GameController extends Component {
    private GameApplication app;
    private GameVisualizer visualizer;
    private GameState gameState;
    private GameState initialGameState;
    private GameAgent player1;
    private GameAgent player2;
    private Player winner;
    private int ticks = 0;
    private static final int CLOCK = 50;

    public GameController(GameApplication app, GameAgent player1, GameAgent player2, GameState initialGameState) {
        this.initialGameState = initialGameState;
        this.player1 = player1;
        this.player2 = player2;
        this.app = app;
        init();
    }

    private void init() {
        this.gameState = initialGameState;
        visualizer = new GameVisualizer(app, gameState);
        visualizer.getMapEntity().addComponent(this);
    }

    public Player play(Collection<GameState> history) {
        for (GameState state : history) {
            visualizer.visualize(state);
            if (gameState.terminalTest()) {
                return gameState.isWinner(Player.PLAYER1) ? Player.PLAYER1 : Player.PLAYER2;
            }
//            try {
//                wait(30);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        return null;
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        ticks++;
        if (ticks == CLOCK) {
            ticks = 0;
            play();
        }
        if (gameState.terminalTest()) {
            pause();
            winner = gameState.isWinner(Player.PLAYER1) ? Player.PLAYER1 : Player.PLAYER2;
            showGameOver();
        }
    }

    public void play() {
        switch (gameState.getActivePlayer()) {
            case PLAYER1:
                gameState = player1.play(gameState, Player.PLAYER1);
                break;
            case PLAYER2:
                gameState = player2.play(gameState, Player.PLAYER2);
                break;
            default:
                break;
        }
        visualizer.visualize(gameState);
    }

    private void showGameOver() {
        app.getDisplay().showConfirmationBox((winner == Player.PLAYER1 ? "Player 1" : "Player 2")
                + " is the winner.\nPlay Again?", yes -> {
            if (yes) {
                app.getGameWorld().clear();
                init();
                resume();
            }
        });
    }

}

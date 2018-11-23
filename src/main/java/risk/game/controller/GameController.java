package risk.game.controller;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.gameplay.rpg.InGameClock;
import com.almasb.fxgl.ui.UIController;
import risk.game.model.agents.GameAgent;
import risk.game.model.agents.PassiveAgent;
import risk.game.model.io.InputProvider;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;
import risk.game.view.GameVisualizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

public class GameController extends Component {
    private GameVisualizer visualizer;
    private GameState gameState;
    private GameAgent player1;
    private GameAgent player2;
    private int ticks = 0;
    private static final int CLOCK = 30;

    public GameController(GameApplication app, GameAgent player1, GameAgent player2) {
        try {
            gameState = InputProvider.getInitialGameState(new File("./in.txt"));
            gameState.getWorldMap().nodes().forEach(node -> node.leavingEdges().forEach(edge -> System.out.println(
                    "Edge: (" + edge.getSourceNode() + ", " + edge.getTargetNode() + ")")));
            this.player1 = player1;
            this.player2 = player2;
            visualizer = new GameVisualizer(app, gameState);
            visualizer.getMapEntity().addComponent(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
//            return gameState.isWinner(Player.PLAYER1) ? Player.PLAYER1 : Player.PLAYER2;
        }
    }

    public Player play() {
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
        return null;
    }

}

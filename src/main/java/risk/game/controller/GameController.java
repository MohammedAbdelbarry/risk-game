package risk.game.controller;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.component.Component;

import org.graphstream.ui.view.ViewerPipe;
import risk.game.model.agents.GameAgent;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;
import risk.game.view.GameVisualizer;

import java.util.Collection;

public class GameController extends Component {
    private GameApplication app;
    private Thread pumpThread;
    private GameVisualizer visualizer;
    private GameState gameState;
    private GameState initialGameState;
    private GameAgent player1;
    private GameAgent player2;
    private Player winner;
    private int ticks = 0;
    private static final int CLOCK = 75;

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
		ViewerPipe viewerPipe = visualizer.getViewerPipe();
		pumpThread = new Thread(() -> {
			while (true) {
				try {
					viewerPipe.blockingPump();
				} catch (InterruptedException ignored) {

				}
			}
		});

		pumpThread.setDaemon(true);
		pumpThread.start();
	}

    public Player play(Collection<GameState> history) {
        for (GameState state : history) {
            visualizer.visualize(state);
            if (gameState.terminalTest()) {
                return gameState.isWinner(Player.PLAYER1) ? Player.PLAYER1 : Player.PLAYER2;
            }
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
		System.out.println("Requesting Move");
		GameState newState = null;
		switch (gameState.getActivePlayer()) {
		case PLAYER1:
			visualizer.getViewerPipe().removeViewerListener(player2);
			visualizer.getViewerPipe().addViewerListener(player1);
			newState = player1.play(gameState, Player.PLAYER1);
			if (newState != null) {
				gameState = newState;
			}
			break;
		case PLAYER2:
			visualizer.getViewerPipe().removeViewerListener(player1);
			visualizer.getViewerPipe().addViewerListener(player2);
			newState = player2.play(gameState, Player.PLAYER2);
			if (newState != null) {
				gameState = newState;
			}
			break;
		default:
			break;
		}
		visualizer.visualize(gameState);
	}

	private void showGameOver() {
		calculateAgentsPerformance();
		app.getDisplay().showConfirmationBox(
				(winner == Player.PLAYER1 ? "Player 1" : "Player 2") + " is the winner.\nPlay Again?", yes -> {
					if (yes) {
						app.getGameWorld().clear();
						player1.reset();
						player2.reset();
						pumpThread.stop();
						init();
						resume();
					}
				});
	}
	
	private void calculateAgentsPerformance() {
		int[] fValues = { 1, 100, 10000 };
		for (int f : fValues) {
			long perf = player2.calculatePerformance(f);
			if (perf != 0) {
				System.out.println("For f = "+ f);
				System.out.println("Perfomance: " + perf);
			}
		}
	}

}

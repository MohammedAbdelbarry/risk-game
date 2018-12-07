package risk.game.controller;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.component.Component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.graphstream.ui.view.ViewerPipe;
import risk.game.controller.agents.HumanAgent;
import risk.game.model.agents.GameAgent;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.AttackAction;
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
    private int player1Clock;
    private int player2Clock;

	public GameController(GameApplication app, GameAgent player1, GameAgent player2, GameState initialGameState) {
		this.initialGameState = initialGameState;
		this.player1 = player1;
		this.player2 = player2;
		this.app = app;
		if (player1 instanceof HumanAgent) {
			player1Clock = 1;
		} else {
			player1Clock = CLOCK;
		}
		if (player2 instanceof HumanAgent) {
			player2Clock = 1;
		} else {
			player2Clock = CLOCK;
		}
		init();
	}

	private void init() {
		this.gameState = initialGameState;
		visualizer = new GameVisualizer(app, gameState);
		visualizer.getCancelButton().setOnAction(event -> {
			resetActivePlayer();
			gameState.getWorldMap().nodes().forEach(node -> {
				node.removeAttribute("ui.color");
				node.removeAttribute("ui.hide");
			});
		});
		visualizer.getSkipButton().setOnAction(event -> {
			GameAgent activePlayer = getActivePlayer();
			if (activePlayer instanceof HumanAgent) {
				HumanAgent humanAgent = (HumanAgent) activePlayer;
				humanAgent.skipAttack();
			}
		});
		visualizer.getMapEntity().addComponent(this);
		ViewerPipe viewerPipe = visualizer.getViewerPipe();
		pumpThread = new Thread(() -> {
			while (true) {
				try {
					viewerPipe.blockingPump();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		pumpThread.setDaemon(true);
		pumpThread.start();
	}

	private void resetActivePlayer() {
		getActivePlayer().reset();
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

	private GameAgent getActivePlayer() {
		if (gameState.getActivePlayer() == Player.PLAYER1) {
			return player1;
		}
		return player2;
	}

	private GameAgent getInactivePlayer() {
		if (gameState.getActivePlayer() == Player.PLAYER1) {
			return player2;
		}
		return player1;
	}

	private int getActivePlayerClock() {
		if (gameState.getActivePlayer() == Player.PLAYER1) {
			return player1Clock;
		}
		return player2Clock;
	}

	@Override
	public void onUpdate(double tpf) {
		super.onUpdate(tpf);
		ticks++;
		if (getActivePlayer() instanceof HumanAgent && gameState.getCurrentPhase() == Phase.ATTACK) {
			visualizer.getSkipButton().setVisible(true);
			visualizer.getCancelButton().setVisible(true);
		} else {
			visualizer.getCancelButton().setVisible(false);
			visualizer.getSkipButton().setVisible(false);
		}
		if (ticks == getActivePlayerClock()) {
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
		GameState newState;
		visualizer.getViewerPipe().removeViewerListener(getInactivePlayer());
		visualizer.getViewerPipe().addViewerListener(getActivePlayer());
		newState = getActivePlayer().play(gameState, gameState.getActivePlayer());
		if (newState != null) {
			gameState = newState;
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
						pumpThread = null;
						init();
						resume();
					} else {
						FXGL.exit();
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

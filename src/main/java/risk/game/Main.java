package risk.game;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.menu.FXGLDefaultMenu;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.ChoiceBox;
import org.graphstream.graph.Node;
import risk.game.controller.GameController;
import risk.game.model.agents.GameAgent;
import risk.game.model.agents.GreedyAgent;
import risk.game.model.agents.PassiveAgent;
import risk.game.model.agents.util.AgentFactory;
import risk.game.model.conf.RiskConfig;
import risk.game.model.io.InputProvider;
import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;
import risk.game.model.util.Constants;
import risk.game.view.RiskSceneFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.BiFunction;

public class Main extends GameApplication {

	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings gameSettings) {
		gameSettings.setFullScreenAllowed(true);
		gameSettings.setManualResizeEnabled(true);
		gameSettings.setHeight(711);
		gameSettings.setWidth(1264);
		gameSettings.setTitle("Risk");
		gameSettings.setVersion("1.0");
		gameSettings.setAppIcon("Risk-Icon.png");
		gameSettings.setMenuEnabled(true);
		gameSettings.setSceneFactory(new RiskSceneFactory());
		gameSettings.setConfigClass(RiskConfig.class);
	}

	@Override
	protected void initGame() {
		super.initGame();
		try {
			GameState initialGameState = InputProvider.getInitialGameState(new File("./in.txt"));
			RiskConfig config = getGameConfig();
			BiFunction<GameState, Player, Long> heuristic = (state, player) -> {
				if (state.isWinner(player)) {
					return Long.MIN_VALUE;
				} else if (state.isLoser(player)) {
					return Long.MAX_VALUE;
				}

				final int[] minTurns = {Integer.MAX_VALUE};
				state.getWorldMap().nodes().forEach(node -> {
					Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
					if (country.getControllingPlayer() == player) {
						node.leavingEdges().forEach(edge -> {
							Country otherCountry = edge.getTargetNode().getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
							if (otherCountry.getControllingPlayer() != player) {
								minTurns[0] = Math.min(minTurns[0], Math.max(0,
										(country.getNumberOfTroops() - otherCountry.getNumberOfTroops() + 2)
												/ state.getPlayerState(player).getTroopsPerTurn()));
							}
						});
					}
				});

				return minTurns[0] + state.getWorldMap().
						nodes().map(node -> node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class)).
						map(Country::getControllingPlayer).filter(p -> p == player.getOpponent()).count();

			};
			GameAgent player1 = AgentFactory.getInstance().newAgent(config.getPlayer1Agent(), initialGameState, heuristic, this);
			GameAgent player2 = AgentFactory.getInstance().newAgent(config.getPlayer2Agent(), initialGameState, heuristic, this);
			GameController controller = new GameController(this, player1, player2, initialGameState);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

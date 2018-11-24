package risk.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import risk.game.controller.GameController;
import risk.game.model.agents.AstarAgent;
import risk.game.model.agents.GreedyAgent;
import risk.game.model.agents.PassiveAgent;
import risk.game.model.agents.RealtimeAstarAgent;
import risk.game.model.io.InputProvider;
import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;

public class Main extends GameApplication {

	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings gameSettings) {
		gameSettings.setFullScreenAllowed(true);
		gameSettings.setManualResizeEnabled(true);
		gameSettings.setHeight(720);
		gameSettings.setWidth(1280);
		gameSettings.setTitle("Risk");
		gameSettings.setVersion("1.0");
		gameSettings.setAppIcon("Risk-Icon.png");
		gameSettings.setMenuEnabled(true);
	}

	@Override
	protected void initGame() {
		super.initGame();
		try {
			GameState initialGameState = InputProvider.getInitialGameState(new File("./in.txt"));
			GameController controller = new GameController(this, new PassiveAgent(),
					new AstarAgent((state, player) -> {
						if (state.isWinner(player)) {
							return Long.MIN_VALUE;
						} else if (state.isLoser(player)) {
							return Long.MAX_VALUE;
						}

						return state.getWorldMap().
								nodes().map(node -> node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class)).
								map(Country::getControllingPlayer).filter(p -> p == player.getOpponent()).count();

					}), initialGameState);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

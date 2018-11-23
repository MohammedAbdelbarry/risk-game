package risk.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.TextField;
import risk.game.controller.GameController;
import risk.game.model.agents.GreedyAgent;
import risk.game.model.agents.PassiveAgent;
import risk.game.model.state.Country;
import risk.game.model.util.Constants;

public class Main extends GameApplication {

	private GameController controller;

	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings gameSettings) {
		gameSettings.setFullScreenAllowed(true);
		gameSettings.setManualResizeEnabled(true);
		gameSettings.setHeight(600);
		gameSettings.setWidth(800);
		gameSettings.setTitle("Risk");
		gameSettings.setVersion("1.0");
		gameSettings.setAppIcon("Risk-Icon.png");
		gameSettings.setMenuEnabled(true);
	}

	@Override
	protected void initGame() {
		super.initGame();
		Entities.builder().at(0 ,0).viewFromNode(new TextField("test")).buildAndAttach(getGameWorld());
		controller = new GameController(this, new PassiveAgent(),
				new GreedyAgent((state, player) -> {
					if (state.isWinner(player)) {
						return Long.MAX_VALUE;
					} else if (state.isLoser(player)) {
						return Long.MIN_VALUE;
					}

					return state.getWorldMap().
							nodes().map(node -> node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class)).
							map(Country::getControllingPlayer).filter(p -> p == player).count();

				}));
	}
}

package risk.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import risk.game.controller.GameController;

public class Main extends GameApplication {

	private GameController controller;

	public static void main(String[] args)  {
//		System.setProperty("org.graphstream.ui", "javafx");
//		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings gameSettings) {
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
		controller = new GameController(this);
	}
}

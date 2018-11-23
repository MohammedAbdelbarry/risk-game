package risk.game.controller;

import com.almasb.fxgl.app.GameApplication;
import risk.game.model.io.InputProvider;
import risk.game.model.state.GameState;
import risk.game.view.GameVisualizer;

import java.io.File;
import java.io.FileNotFoundException;

public class GameController {
    private GameVisualizer visualizer;

    public GameController(GameApplication app) {
        try {
            GameState initialState = InputProvider.getInitialGameState(new File("./in.txt"));
            visualizer = new GameVisualizer(app, initialState);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

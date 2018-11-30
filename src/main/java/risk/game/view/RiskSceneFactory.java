package risk.game.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.menu.MenuType;
import org.jetbrains.annotations.NotNull;

public class RiskSceneFactory extends SceneFactory {

    @NotNull
    @Override
    public FXGLMenu newGameMenu(GameApplication app) {
        return new RiskMenu(app, MenuType.GAME_MENU);
    }

    @NotNull
    @Override
    public FXGLMenu newMainMenu(GameApplication app) {
        return new RiskMenu(app, MenuType.MAIN_MENU);
    }

}

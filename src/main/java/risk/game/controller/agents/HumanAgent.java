package risk.game.controller.agents;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Predicate;
import risk.game.model.agents.GameAgent;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.Action;
import risk.game.model.state.action.AllocationAction;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class HumanAgent extends GameAgent {
    private GameApplication app;
    private Predicate<String> intPredicate;

    public HumanAgent(GameApplication app) {
        this.app = app;
        intPredicate = (s) -> {
            try {
                Long.parseLong(s);
                return true;
            } catch (Throwable ignored) {
                return false;
            }
        };
    }

    @Override
    public GameState play(GameState state, Player player) {
        if (state.getActivePlayer() != player) {
            return state;
        }

        return null;
    }

    @Override
    public void reset() {

    }
}

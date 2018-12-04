package risk.game.model.agents.util;

import com.almasb.fxgl.app.GameApplication;
import risk.game.controller.agents.HumanAgent;
import risk.game.model.agents.AggressiveAgent;
import risk.game.model.agents.AstarAgent;
import risk.game.model.agents.GameAgent;
import risk.game.model.agents.GreedyAgent;
import risk.game.model.agents.PacifistAgent;
import risk.game.model.agents.PassiveAgent;
import risk.game.model.agents.RealtimeAstarAgent;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;

import java.util.function.BiFunction;

public class AgentFactory {

    private AgentFactory() {

    }

    public GameAgent newAgent(String key, GameState initialGameState, BiFunction<GameState, Player, Long> heuristic, GameApplication gameApp) {
        switch (key) {
            case PassiveAgent.KEY:
                return new PassiveAgent();
            case AggressiveAgent.KEY:
                return new AggressiveAgent(initialGameState.getContinents());
            case PacifistAgent.KEY:
                return new PacifistAgent();
            case GreedyAgent.KEY:
                return new GreedyAgent(heuristic);
            case AstarAgent.KEY:
                return new AstarAgent(heuristic);
            case RealtimeAstarAgent.KEY:
                return new RealtimeAstarAgent(heuristic);
            case "Human":
                return new HumanAgent(gameApp);
            default:
                throw new IllegalArgumentException("Agent: " + key + " is not a valid agent");
        }
    }

    public static AgentFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final AgentFactory INSTANCE = new AgentFactory();
    }
}

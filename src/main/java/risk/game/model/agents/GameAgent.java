package risk.game.model.agents;

import risk.game.model.state.GameState;
import risk.game.model.state.Player;

import java.util.Collection;

public abstract class GameAgent {

	public abstract GameState play(GameState state, Player player);

	public boolean terminalTest(GameState state, Collection<?> actions) {
		return state.terminalTest() || actions.isEmpty();
	}

}

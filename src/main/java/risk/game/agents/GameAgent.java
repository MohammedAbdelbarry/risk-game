package risk.game.agents;

import risk.game.state.GameState;
import risk.game.state.Player;

import java.util.Collection;

public abstract class GameAgent {

	public abstract GameState play(GameState state, Player player);

	public boolean terminalTest(GameState state, Collection<?> actions) {
		return state.terminalTest() || actions.isEmpty();
	}

}

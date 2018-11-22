package risk.game.agents;

import risk.game.state.GameState;

public abstract class GameAgent {

	public abstract GameState play(GameState state);
	public abstract boolean terminalTest(GameState state);

}

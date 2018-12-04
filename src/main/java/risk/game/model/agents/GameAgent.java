package risk.game.model.agents;

import org.graphstream.ui.view.ViewerListener;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;

import java.util.Collection;

public abstract class GameAgent implements ViewerListener {

	public abstract GameState play(GameState state, Player player);

	public boolean terminalTest(GameState state, Collection<?> actions) {
		return state.terminalTest() || actions.isEmpty();
	}

	public abstract void reset();
	public abstract long calculatePerformance(int f);

	@Override
	public void viewClosed(String s) {

	}

	@Override
	public void buttonPushed(String s) {

	}

	@Override
	public void buttonReleased(String s) {
		System.out.println("CLICKED NODE: " + s);
	}

	@Override
	public void mouseOver(String s) {

	}

	@Override
	public void mouseLeft(String s) {

	}
}

package risk.game.state;

import org.graphstream.graph.Graph;
import risk.game.state.action.Action;

import java.util.Collection;

public class GameState {
	private Player player;
	private Phase phase;
	private Graph worldMap;
	private PlayerState player1State;
	private PlayerState player2State;

	public GameState(Graph worldMap) {
		this.worldMap = worldMap;
	}

	public Collection<Action> getPossibleMoves(Player activePlayer, Phase curPhase) {
		return null;
	}

	public Collection<Action> getPossibleMoves() {
		return getPossibleMoves(player, phase);
	}

	public boolean isLegalMove(Action move) {
		return false;
	}

	public PlayerState getPlayer1State() {
		return player1State;
	}

	public PlayerState getPlayer2State() {
		return player2State;
	}

	public GameState forecastMove(Action move) {
		return null;
	}

	public boolean isWinner(Player player) {
		return false;
	}

	public boolean isLoser(Player player) {
		return false;
	}

	public Player getActivePlayer() {
		return player;
	}

	public Player getInactivePlayer() {
		return player.getOpponent();
	}

	public Phase getCurrentPhase() {
		return phase;
	}

	public Phase getNextPhase() {
		return phase.getNextPhase();
	}
}

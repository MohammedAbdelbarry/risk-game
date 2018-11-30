package risk.game.model.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;

import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.RTAnode;
import risk.game.model.state.action.Action;

public class RealtimeAstarAgent extends GameAgent {

	private BiFunction<GameState, Player, Long> heuristic;
	private int turn;
	private int expandedNodes;

	public static final String KEY = "Real-time A*";

	public RealtimeAstarAgent(BiFunction<GameState, Player, Long> heuristic) {
		this.heuristic = heuristic;
		turn = 0;
		expandedNodes = 0;
	}

	public RealtimeAstarAgent() {
		this((state, player) -> 0L);
	}

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getActivePlayer() != player) {
			return state;
		}

		Set<RTAnode> visited = new HashSet<>();

		PriorityQueue<RTAnode> frontier = new PriorityQueue<>(Comparator
				.comparingLong(o -> heuristic.apply(o.getGameState(), o.getGameState().getActivePlayer())));

		RTAnode node = new RTAnode(state, null);
		frontier.add(node);
		long alpha = Integer.MAX_VALUE;
		Collection<Action> moves;
		RTAnode bestMove = null;

		while (!frontier.isEmpty()) {
			node = frontier.poll();
			state = node.getGameState();

			visited.add(node);

			if (state.isWinner(player)) {
				bestMove = node;
				break;
			} else if (state.isLoser(player)) {
				continue;
			}

			if (state.getCurrentPhase() == Phase.ATTACK) {
				moves = new ArrayList<>(state.getPossibleAttacks());
			} else {
				moves = new ArrayList<>(state.getPossibleAllocations());
			}

			if (state.getCurrentPhase() == Phase.ATTACK && player == Player.PLAYER2)
				turn++;

			for (Action move : moves) {
				GameState newState = state.forcastMove(move);
				long f = turn + heuristic.apply(newState, newState.getActivePlayer());

				RTAnode newNode = new RTAnode(newState, node);

				if (f < alpha && !visited.contains(newNode)) {
					if (newState.isWinner(player)) {
						alpha = f;
						bestMove = newNode;
					} else {
						frontier.add(newNode);
					}
				}
			}
		}

		if (bestMove == null)
			return state;

		while (bestMove.getParent() != null) {
			bestMove = bestMove.getParent();
		}

		expandedNodes++;
		return bestMove.getGameState();
	}

	@Override
	public void reset() {
		expandedNodes = 0;
		turn = 0;
	}

}

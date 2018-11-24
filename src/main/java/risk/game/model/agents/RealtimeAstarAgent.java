package risk.game.model.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.BiFunction;

import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.RTAnode;
import risk.game.model.state.action.Action;

public class RealtimeAstarAgent extends GameAgent {

	private BiFunction<GameState, Player, Integer> heuristic;
	private int turn;
	private int expandedNodes;

	public RealtimeAstarAgent(BiFunction<GameState, Player, Integer> heuristic) {
		this.heuristic = heuristic;
		turn = 0;
		expandedNodes = 0;
	}

	public RealtimeAstarAgent() {
		this((state, player) -> 0);
	}

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getActivePlayer() != player) {
			return state;
		}

		PriorityQueue<RTAnode> frontier = new PriorityQueue<>(new Comparator<RTAnode>() {
			@Override
			public int compare(RTAnode o1, RTAnode o2) {
				return heuristic.apply(o1.getGameState(), o1.getGameState().getActivePlayer())
						- heuristic.apply(o2.getGameState(), o2.getGameState().getActivePlayer());
			}
		});

		RTAnode node = new RTAnode(state, null);
		frontier.add(node);
		int alpha = Integer.MAX_VALUE;
		Collection<Action> moves;
		RTAnode bestMove = null;

		while (!frontier.isEmpty()) {
			node = frontier.poll();
			state = node.getGameState();

			if (state.getCurrentPhase() == Phase.ATTACK) {
				moves = new ArrayList<>(state.getPossibleAttacks());
			} else {
				moves = new ArrayList<>(state.getPossibleAllocations());
			}

			if (state.getCurrentPhase() == Phase.ATTACK && player == Player.PLAYER2)
				turn++;

			for (Action move : moves) {
				GameState newState = state.forcastMove(move);
				int f = turn + heuristic.apply(newState, newState.getActivePlayer());
				if (f >= alpha)
					continue;
				else {
					if (terminalTest(newState, moves)) {
						alpha = f;
						bestMove = new RTAnode(newState, node);
					} else {
						RTAnode child = new RTAnode(newState, node);
						frontier.add(child);
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

}

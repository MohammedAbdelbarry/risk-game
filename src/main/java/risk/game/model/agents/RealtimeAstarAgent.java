package risk.game.model.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;

import risk.game.model.state.AstarNode;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.RTAnode;
import risk.game.model.state.action.Action;
import risk.game.model.state.action.AttackAction;

public class RealtimeAstarAgent extends GameAgent {

	private BiFunction<GameState, Player, Long> heuristic;
	private int turn;
	private int expandedNodes;
	private final int LIMIT = 10;

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

		PriorityQueue<RTAnode> frontier = new PriorityQueue<>(Comparator.comparingLong(RTAnode::getCost));

		RTAnode node = new RTAnode(state, null, 0, heuristic.apply(state, player));
		frontier.add(node);
		long alpha = Integer.MAX_VALUE;
		Collection<Action> moves;
		RTAnode bestMove = null;

		while (!frontier.isEmpty()) {
			node = frontier.poll();
			state = node.getGameState();
			visited.add(node);

			if (state.isWinner(player) || node.getDepth() == LIMIT) {
				bestMove = node;
				break;
			} else if (state.isLoser(player)) {
				continue;
			}

			if (state.getActivePlayer() == player.getOpponent() && state.getCurrentPhase() == Phase.ALLOCATE) {
				PassiveAgent agent = new PassiveAgent();
				state = agent.play(agent.play(state, state.getActivePlayer()), state.getActivePlayer());
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
				long f = Long.MAX_VALUE;
				if (state.getCurrentPhase() == Phase.ATTACK) {
					f = turn + heuristic.apply(newState, newState.getActivePlayer());
				} else {
					Collection<AttackAction> attacks = newState.getPossibleAttacks();
					for (AttackAction a : attacks) {
						GameState attackState = newState.forecastAttack(a);
						long newf = turn + heuristic.apply(attackState, attackState.getActivePlayer());
						if (f > newf)
							f = newf;
					}
				}
				int depth = node.getDepth() + 1;
				RTAnode newNode = new RTAnode(newState, node, depth, f);

				if (f < alpha && !visited.contains(newNode)) {
					if (newState.isWinner(player) || newNode.getDepth() == LIMIT) {
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

		while (bestMove.getParent().getParent() != null) {
			bestMove = bestMove.getParent();
		}

		expandedNodes++;
		return bestMove.getGameState();
	}

	public long calculatePerformance(int f) {
		return f * turn + expandedNodes;
	}

	@Override
	public void reset() {
		expandedNodes = 0;
		turn = 0;
	}

}

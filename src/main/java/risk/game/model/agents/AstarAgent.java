package risk.game.model.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

import risk.game.model.state.AstarNode;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.Action;
import risk.game.model.state.action.AttackAction;

public class AstarAgent extends GameAgent implements SearchAgent {

	private BiFunction<GameState, Player, Long> heuristic;
	private Stack<AstarNode> expanded;
	private int expandedNodes;

	public static final String KEY = "A*";

	public AstarAgent(BiFunction<GameState, Player, Long> heuristic) {
		this.heuristic = heuristic;
		expandedNodes = 0;
	}

	public AstarAgent() {
		this((state, player) -> 0L);
	}

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getActivePlayer() != player) {
			return state;
		}
		if (expanded == null) {
			init(state, player);
		} else if (expanded.isEmpty()) {
			return state;
		}

		return expanded.pop().getGameState();
	}

	@Override
	public void reset() {
		expanded = null;
		expandedNodes = 0;
	}

	public void init(GameState state, Player player) {
		expanded = new Stack<>();
		PriorityQueue<AstarNode> frontier = new PriorityQueue<>(Comparator.comparingLong(AstarNode::getCost));
		Set<AstarNode> visited = new HashSet<>();
		AstarNode node = new AstarNode(state, heuristic.apply(state, player), null);
		frontier.add(node);
		Collection<Action> moves;
		AstarNode terminalState = null;

		while (!frontier.isEmpty()) {
			node = frontier.poll();
			visited.add(node);
			expandedNodes++;
			state = node.getGameState();

			if (state.isWinner(player)) {
				terminalState = node;
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

			for (Action move : moves) {
				GameState newState = state.forcastMove(move);
				long f = Long.MAX_VALUE;
				if (state.getCurrentPhase() == Phase.ATTACK) {
					f = newState.getTurns(player) + heuristic.apply(newState, player);
				} else {
					Collection<AttackAction> attacks = newState.getPossibleAttacks();
					for (AttackAction a : attacks) {
						GameState attackState = newState.forecastAttack(a);
						long newf = attackState.getTurns(player) + heuristic.apply(attackState, player);
						if (f > newf)
							f = newf;
					}
				}
				AstarNode n = new AstarNode(newState, f, node);
				if (!visited.contains(n)) {
					frontier.add(n);
				}
			}
		}

		while (terminalState.getParent() != null) {
			expanded.push(terminalState);
			terminalState = terminalState.getParent();
		}

		expanded.push(terminalState);
	}

    @Override
    public int getExpandedNodes() {
        return expandedNodes;
    }

}

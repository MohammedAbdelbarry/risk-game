package risk.game.model.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.BiFunction;

import risk.game.model.state.AstarNode;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.Action;

public class AstarAgent extends GameAgent {

	private BiFunction<GameState, Player, Integer> heuristic;
	private Stack<AstarNode> expanded;
	private int turn;
	private int expandedNodes;

	public AstarAgent(BiFunction<GameState, Player, Integer> heuristic) {
		this.heuristic = heuristic;
		turn = 0;
		expandedNodes = 0;
	}

	public AstarAgent() {
		this((state, player) -> 0);
	}

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getActivePlayer() != player) {
			return state;
		}
		if (expanded.isEmpty())
			init(state, player);

		return expanded.pop().getGameState();
	}

	public void init(GameState state, Player player) {
		PriorityQueue<AstarNode> frontier = new PriorityQueue<>(new Comparator<AstarNode>() {
			@Override
			public int compare(AstarNode o1, AstarNode o2) {
				return o1.getCost() - o2.getCost();
			}
		});

		AstarNode node = new AstarNode(state, heuristic.apply(state, player), null);
		frontier.add(node);
		Collection<Action> moves;
		AstarNode terminalState = null;

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
				if (state.getCurrentPhase() == Phase.ATTACK) {
					PassiveAgent agent = new PassiveAgent();
					newState = agent.play(agent.play(newState, newState.getActivePlayer()), newState.getActivePlayer());
				}
				int f = turn + heuristic.apply(newState, newState.getActivePlayer());
				AstarNode n = new AstarNode(newState, f, node);
				frontier.add(n);
			}
		}

		while (terminalState.getParent() != null) {
			expanded.push(terminalState);
			terminalState = terminalState.getParent();
		}

		expanded.push(terminalState);
	}

}

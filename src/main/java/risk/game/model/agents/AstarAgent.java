package risk.game.model.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.BiFunction;

import risk.game.model.state.AstarNode;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.Action;

public class AstarAgent extends GameAgent {

	private BiFunction<GameState, Player, Integer> heuristic;
	private PriorityQueue<AstarNode> frontier;
	private int turn;
	private int expandedNodes;

	public AstarAgent(BiFunction<GameState, Player, Integer> heuristic) {
		this.heuristic = heuristic;
		turn = 0;
		expandedNodes = 0;
		frontier = new PriorityQueue<>(new Comparator<AstarNode>() {
			@Override
			public int compare(AstarNode o1, AstarNode o2) {
				return o1.getCost() - o2.getCost();
			}
		});
	}

	public AstarAgent() {
		this((state, player) -> 0);
	}

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getActivePlayer() != player) {
			return state;
		}

		Collection<Action> moves;
		if (state.getCurrentPhase() == Phase.ATTACK) {
			moves = new ArrayList<>(state.getPossibleAttacks());
		} else {
			moves = new ArrayList<>(state.getPossibleAllocations());
		}

		if (terminalTest(state, moves))
			return state;

		if (state.getCurrentPhase() == Phase.ATTACK && player == Player.PLAYER2)
			turn++;

		for (Action move : moves) {
			GameState newState = state.forcastMove(move);
			int f = turn + heuristic.apply(newState, newState.getActivePlayer());
			AstarNode node = new AstarNode(newState, f);
			frontier.add(node);
		}

		if (frontier.isEmpty())
			return state;
		expandedNodes++;
		
		return frontier.poll().getGameState();
	}

}

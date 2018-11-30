package risk.game.model.agents;

import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GreedyAgent extends GameAgent {
    private BiFunction<GameState, Player, Long> heuristic;

    public static final String KEY = "Greedy";

    public GreedyAgent(BiFunction<GameState, Player, Long> heuristic) {
        this.heuristic = heuristic;
    }

    public GreedyAgent() {
        this((state, player) -> 0L);
    }

    @Override
    public GameState play(GameState state, Player player) {
        if (state.getActivePlayer() != player) {
            return state;
        }

        System.out.println(state.getActivePlayer() + ":" + state.getCurrentPhase());

        if (state.getCurrentPhase() == Phase.ALLOCATE) {
            Collection<Action> moves = new ArrayList<>(state.getPossibleAllocations());
            if (terminalTest(state, moves)) {
                return state;
            }
            Optional<GameState> bestAlloc = moves.stream()
                    .map(state::forcastMove)
                    .min(Comparator.comparingLong(allocState -> allocState.getPossibleAttacks()
                            .stream()
                            .map(allocState::forcastMove)
                            .map(newState -> heuristic.apply(newState, player))
                            .min(Long::compareTo)
                            .orElse(Long.MAX_VALUE)));
            return bestAlloc.orElse(state);
        }

        Collection<Action> moves = new ArrayList<>(state.getPossibleAttacks());
        if (terminalTest(state, moves)) {
            return state;
        }

        Optional<GameState> possibleState = moves
                .stream()
                .map(state::forcastMove)
                .min(Comparator.comparingLong(newState -> heuristic.apply(newState, player)));

        return possibleState.orElse(state);
    }

    @Override
    public void reset() {

    }
}

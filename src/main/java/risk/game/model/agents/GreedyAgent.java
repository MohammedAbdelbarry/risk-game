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

public class GreedyAgent extends GameAgent {
    private BiFunction<GameState, Player, Long> heuristic;

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

        Collection<Action> moves;
        if (state.getCurrentPhase() == Phase.ATTACK) {
            moves = new ArrayList<>(state.getPossibleAttacks());
        } else {
            moves = new ArrayList<>(state.getPossibleAllocations());
        }

        if (terminalTest(state, moves)) {
            return state;
        }

        System.out.println(moves);

        Optional<GameState> possibleState = moves
                .stream()
                .map(state::forcastMove)
                .max(Comparator.comparingLong(newState -> heuristic.apply(newState, player)));

        return possibleState.orElse(state);
    }
}

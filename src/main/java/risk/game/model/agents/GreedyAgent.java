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
    private BiFunction<GameState, Player, Integer> heuristic;

    public GreedyAgent(BiFunction<GameState, Player, Integer> heuristic) {
        this.heuristic = heuristic;
    }

    public GreedyAgent() {
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

        if (terminalTest(state, moves)) {
            return state;
        }

        Optional<GameState> possibleState = moves
                .stream()
                .map(state::forcastMove)
                .max(Comparator.comparingInt(newState -> heuristic.apply(newState, player)));

        return possibleState.orElse(state);
    }
}

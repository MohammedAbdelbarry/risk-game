package risk.game.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

import risk.game.state.Country;
import risk.game.state.GameState;
import risk.game.state.Player;
import risk.game.state.action.AllocationAction;

public class PassiveAgent extends GameAgent {

	@Override
	public GameState play(GameState state, Player player) {

		if (state.getActivePlayer() != player) {
			return state;
		}

		Collection<AllocationAction> allocationActions = state.getPossibleAllocations();

		if (terminalTest(state, allocationActions)) {
			return state;
		}

		Optional<AllocationAction> possibleAction = allocationActions
				.stream().min((action1, action2) -> {
					Country country1 = action1.getCountry();
					Country country2 = action2.getCountry();
					if (country1.getNumberOfTroops() == country2.getNumberOfTroops()) {
						return country1.getId() - country2.getId();
					} else {
						return country1.getNumberOfTroops() - country2.getNumberOfTroops();
					}
				});

		if (!possibleAction.isPresent()) {
			return state;
		}

		AllocationAction bestAction = possibleAction.get();


		return state.forecastAllocation(bestAction);
	}

}

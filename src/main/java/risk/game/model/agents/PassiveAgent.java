package risk.game.model.agents;

import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.AllocationAction;
import risk.game.model.state.action.AttackAction;

import java.util.Collection;
import java.util.Optional;

public class PassiveAgent extends GameAgent {

	public static final String KEY = "Passive";

	@Override
	public GameState play(GameState state, Player player) {

		if (state.getActivePlayer() != player) {
			return state;
		}

		if (!state.terminalTest() && state.getCurrentPhase() == Phase.ATTACK) {
			return state.forecastAttack(AttackAction.SKIP_ACTION);
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

	@Override
	public void reset() {

	}

	@Override
	public long calculatePerformance(int f) {
		return 0;
	}

}

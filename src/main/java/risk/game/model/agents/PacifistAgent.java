package risk.game.model.agents;

import java.util.Collection;
import java.util.Optional;

import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.AttackAction;

public class PacifistAgent extends GameAgent{

	public static final String KEY = "Pacifist";

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getActivePlayer() != player) {
			return state;
		}

		if (state.getCurrentPhase() == Phase.ALLOCATE) {
			PassiveAgent agent = new PassiveAgent();
			return agent.play(state, player);
		}
		
		Collection<AttackAction> attackActions = state.getPossibleAttacks();
		if (terminalTest(state, attackActions)) {
			return state;
		}

		Optional<AttackAction> possibleAction = attackActions
				.stream().filter(action -> action != AttackAction.SKIP_ACTION).min((action1, action2) -> {
					Country country1 = action1.getAttackedCountry();
					Country country2 = action2.getAttackedCountry();
					return country1.getNumberOfTroops() - country2.getNumberOfTroops();
				});
		
		if (!possibleAction.isPresent()) {
			return state.forecastAttack(AttackAction.SKIP_ACTION);
		}

		AttackAction bestAction = possibleAction.get();
		return state.forecastAttack(bestAction);
	}

	@Override
	public void reset() {

	}

	@Override
	public long calculatePerformance(int f) {
		return 0;
	}
}

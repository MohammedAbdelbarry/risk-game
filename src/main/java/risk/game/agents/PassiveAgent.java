package risk.game.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import risk.game.state.Country;
import risk.game.state.GameState;
import risk.game.state.action.AllocationAction;

public class PassiveAgent extends GameAgent {

	private Collection<AllocationAction> legalActions;

	@Override
	public GameState play(GameState state) {
		
		if(terminalTest(state))
			return state;
		
		Iterator<AllocationAction> iter = legalActions.iterator();
		AllocationAction bestAction = iter.next();

		while (iter.hasNext()) {
			AllocationAction newAction = iter.next();
			Country country1 = newAction.getAllocationResult();
			Country country2 = bestAction.getAllocationResult();
			if (country1.getNumberOfTroops() < country2.getNumberOfTroops()) {
				bestAction = newAction;
			} else if (country1.getNumberOfTroops() == country2.getNumberOfTroops()) {
				if (country1.getId() < country2.getId())
					bestAction = newAction;
			}
		}
		return state.forecastAllocation(bestAction);
	}

	@Override
	public boolean terminalTest(GameState state) {
		Collection<AllocationAction> actions = state.getPossibleAllocations();
		Iterator<AllocationAction> iter = actions.iterator();
		legalActions = new ArrayList<>();

		while (iter.hasNext()) {
			AllocationAction action = iter.next();
			if (state.isLegalAllocation(action))
				legalActions.add(action);
		}

		return legalActions.isEmpty() ? true : false;
	}

}

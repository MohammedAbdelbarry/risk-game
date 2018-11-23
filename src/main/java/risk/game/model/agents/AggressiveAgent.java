package risk.game.model.agents;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;

import risk.game.model.state.Continent;
import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.PlayerState;
import risk.game.model.state.action.AllocationAction;
import risk.game.model.state.action.AttackAction;

public class AggressiveAgent extends GameAgent {

	Collection<Continent> continents;

	public AggressiveAgent(Collection<Continent> continents) {
		this.continents = continents;
	}

	@Override
	public GameState play(GameState state, Player player) {
		if (state.getInactivePlayer() != player)
			return state;

		if (state.getCurrentPhase() == Phase.ATTACK)
			return getAttackMove(state);
		
		Collection<AllocationAction> allocationActions = state.getPossibleAllocations();
		if(terminalTest(state, allocationActions)) {
			return state;
		}
		
		Optional<AllocationAction> possibleAction = allocationActions.stream()
				.max((action1, action2) -> {
					Country country1 = action1.getCountry();
					Country country2 = action2.getCountry();
					if(country1.getNumberOfTroops() == country2.getNumberOfTroops()) {
						return country1.getId() - country2.getId();
					} else {
						return country1.getNumberOfTroops() - country2.getNumberOfTroops();
					}		
				});
		if(!possibleAction.isPresent())
			return state;
		
		AllocationAction bestAction = possibleAction.get();
		
		return state.forecastAllocation(bestAction);
	}

	public GameState getAttackMove(GameState state) {
		Collection<AttackAction> possibleAttacks = state.getPossibleAttacks();
		if (terminalTest(state, possibleAttacks)) {
			return state;
		}

		Map<Continent, Integer> remainingNumberOfCountries = new HashMap<>();
		PlayerState playerState = state.getPlayerState(state.getActivePlayer());
		Collection<Country> playerCountries = playerState.getTerritories();

		//see if all countries in each continent belong to the oponent and if not then 
		//keep each continent as a key with the remaining number of lands for 
		//the oponent to have the countries of that continent  
		for (Continent continent : continents) {
			Collection<Integer> countriesIDs = continent.getCountriesIds();
			int counter = 0;
			for (int countryID : countriesIDs) {
				for (Country country : playerCountries)
					if (countryID == country.getId()) {
						counter++;
						break;
					}
			}
			remainingNumberOfCountries.put(continent, countriesIDs.size() - counter);
		}
		
		//let the priority be for those with least number of countries remaining  
		//to attack one of them. and if there exist multiple continents with 
		//the same number of remaining countries choose the one with most bonus

		PriorityQueue<Entry<Continent, Integer>> queue =
				new PriorityQueue<>( new Comparator<Entry<Continent, Integer>>() {
					@Override
					public int compare(Entry<Continent, Integer> o1, Entry<Continent, Integer> o2) {
						if(o1.getValue() == o2.getValue())
							return o2.getKey().getBonus() - o1.getKey().getBonus();
						return o1.getValue() - o2.getValue();
					}
				});
		
		remainingNumberOfCountries.entrySet().forEach(entry -> queue.add(entry));

		//iterate on the countries in higher priority continent and choose the 
		//one which can be attacked and have the highest number of troops
		
		AttackAction attackMove = null;

		while (!queue.isEmpty()) {
			Entry e = queue.poll();
			for (AttackAction action : possibleAttacks) {
				int attackedCountryID = action.getAttackedCountry().getId();
				if (((Continent) e.getKey()).getCountriesIds().contains(attackedCountryID)) {
					if(attackMove == null ||
							attackMove.getAttackedCountry().getNumberOfTroops() < action.getAttackedCountry().getNumberOfTroops())
					attackMove = action;
				}
			}
			if(attackMove != null)
				break;
			
		}

		if(attackMove == null)
			return state;
		
		return state.forecastAttack(attackMove);
	}

}

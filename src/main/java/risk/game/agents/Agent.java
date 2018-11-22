package risk.game.agents;

import java.util.Collection;

import risk.game.state.Country;
import risk.game.state.GameState;
import risk.game.state.Player;
import risk.game.state.PlayerState;

public class Agent {

	public void runPassiveAgent(GameState state) {
		Player player = state.getActivePlayer();
		PlayerState playerState = state.getPlayerState(player);

		Collection<Country> territories = playerState.getTerritories();
		int min = Integer.MAX_VALUE;
		Country minCountry = null;

		for (Country country : territories) {
			if (country.getNumberOfTroops() < min) {
				min = country.getNumberOfTroops();
				minCountry = country;
			} else if (country.getNumberOfTroops() == min) {
				if (country.getId() < minCountry.getId()) {
					min = country.getNumberOfTroops();
					minCountry = country;
				}
			}
		}
		int totalNumberOfTroops = min + playerState.getTroopsPerTurn();
		minCountry.setNumberOfTroops(totalNumberOfTroops);
	}

	public void runAgressiveAgent(GameState state) {
		
	}

	public void runPacifistAgent(GameState state) {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package risk.game.model.state;

import java.util.ArrayList;
import java.util.Collection;

import org.graphstream.graph.Graph;

import risk.game.model.util.Constants;

public class PlayerState {
	private int troopsPerTurn;
	private Collection<Country> territories; 

	public PlayerState(Collection<Country> territories, int troopsPerTurn) {
		this.territories = territories;
		this.troopsPerTurn = troopsPerTurn;
	}

	public PlayerState(Graph worldMap, Player player, int troopsPerTurn) {
		this.troopsPerTurn = troopsPerTurn;
		territories = new ArrayList<>();
		worldMap.nodes().forEach(node -> {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() == player) {
				territories.add(country);
			}
		});
	}

	public void addTerritory(Country territory) {
		territories.add(territory);
	}

	public Collection<Country> getTerritories() {
		return territories;
	}

	public int getTroopsPerTurn() {
		return troopsPerTurn;
	}

	public void setTroopsPerTurn(int troopsPerTurn) {
		this.troopsPerTurn = troopsPerTurn;
	}

}

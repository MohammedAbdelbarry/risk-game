package risk.game.state;

import java.util.Collection;

public class PlayerState {
	//private int numberOfTerritories;
	private int troopsPerTurn;
	private Collection<Country> territories; 

	public PlayerState(Collection<Country> territories, int troopsPerTurn) {
		this.territories = territories;
		this.troopsPerTurn = troopsPerTurn;
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

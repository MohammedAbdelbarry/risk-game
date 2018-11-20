package risk.game.state;

public class PlayerState {
	private int numberOfTerritories;
	private int troopsPerTurn;

	public PlayerState(int numberOfTerritories, int troopsPerTurn) {
		this.numberOfTerritories = numberOfTerritories;
		this.troopsPerTurn = troopsPerTurn;
	}

	public int getNumberOfTerritories() {
		return numberOfTerritories;
	}

	public void setNumberOfTerritories(int numberOfTerritories) {
		this.numberOfTerritories = numberOfTerritories;
	}

	public int getTroopsPerTurn() {
		return troopsPerTurn;
	}

	public void setTroopsPerTurn(int troopsPerTurn) {
		this.troopsPerTurn = troopsPerTurn;
	}

}

package states;

public class Country {
	private int id;
	private int continentBonus;
	private int numberOfTroops;
	private int player;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getContinentBonus() {
		return continentBonus;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public void setContinentBonus(int continentBonus) {
		this.continentBonus = continentBonus;
	}

	public int getNumberOfTroops() {
		return numberOfTroops;
	}

	public void setNumberOfTroops(int numberOfTroops) {
		this.numberOfTroops = numberOfTroops;
	}

}

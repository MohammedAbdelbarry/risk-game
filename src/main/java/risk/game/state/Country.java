package risk.game.state;

import java.util.Objects;

public class Country {
	private int id;
	private int continentBonus;
	private int numberOfTroops;
	private int player;

	public Country(int id, int continentBonus, int player) {
		this.id = id;
		this.continentBonus = continentBonus;
		this.player = player;
	}

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


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Country country = (Country) o;
		return id == country.id &&
				continentBonus == country.continentBonus &&
				numberOfTroops == country.numberOfTroops &&
				player == country.player;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, continentBonus, numberOfTroops, player);
	}
}

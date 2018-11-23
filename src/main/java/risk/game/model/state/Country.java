package risk.game.model.state;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Objects;

public class Country {
	private int id;
	private int numberOfTroops;
	private Player  controllingPlayer;

	public Country(int id, Player player, int numberOfTroops) {
		this.id = id;
		this.controllingPlayer = player;
		this.numberOfTroops = numberOfTroops;
	}

	public Country(Country other) {
		this.id = other.id;
		this.numberOfTroops = other.numberOfTroops;
		this.controllingPlayer = other.controllingPlayer;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumberOfTroops() {
		return numberOfTroops;
	}

	public void setNumberOfTroops(int numberOfTroops) {
		this.numberOfTroops = numberOfTroops;
	}

	public Player getControllingPlayer() {
		return controllingPlayer;
	}

	public void setControllingPlayer(Player controllingPlayer) {
		this.controllingPlayer = controllingPlayer;
	}

	public boolean isNeighbor(Graph worldMap, Country otherCountry) {
		if (worldMap == null || otherCountry == null) {
			return false;
		}

		Node node = worldMap.getNode(String.valueOf(id));
		return node.hasEdgeToward(String.valueOf(otherCountry.id));
	}

	public boolean canAttack(Graph worldMap, Country otherCountry) {
		return isNeighbor(worldMap, otherCountry)
				&& controllingPlayer != otherCountry.controllingPlayer
				&& numberOfTroops - otherCountry.numberOfTroops >= 2;
	}

	public boolean canAttack(Country otherCountry) {
		return controllingPlayer != otherCountry.controllingPlayer
				&& numberOfTroops - otherCountry.numberOfTroops >= 2;
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
				numberOfTroops == country.numberOfTroops &&
				controllingPlayer == country.controllingPlayer;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, numberOfTroops, controllingPlayer);
	}

	@Override
    public String toString() {
	    return String.format("ID: %d & Owner: %s & Units: %d", id, controllingPlayer, numberOfTroops);
    }
}

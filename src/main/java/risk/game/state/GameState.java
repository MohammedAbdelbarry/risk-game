package risk.game.state;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import risk.game.state.action.AllocationAction;
import risk.game.state.action.AttackAction;
import risk.game.util.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class GameState {
	private Player player;
	private Phase phase;
	private Graph worldMap;
	private PlayerState player1State;
	private PlayerState player2State;
	Collection<Continent> continents;
	private static final int NUM_TROOPS = 2;

	public GameState(Graph worldMap, Collection<Continent> continents) {
		this.worldMap = worldMap;
		this.continents = continents;
		player = Player.PLAYER1;
		phase = Phase.ALLOCATE;

		player1State = new PlayerState(worldMap, Player.PLAYER1, NUM_TROOPS);
		player1State = new PlayerState(worldMap, Player.PLAYER2, NUM_TROOPS);
		player1State.setTroopsPerTurn(calculateTroopsPerTurn(Player.PLAYER1));
		player2State.setTroopsPerTurn(calculateTroopsPerTurn(Player.PLAYER2));
	}

	public GameState(GameState other) {
		this.worldMap = Graphs.clone(other.worldMap);
		player = other.player;
		phase = other.phase;
		player1State = other.player1State;
		player2State = other.player2State;
		continents = other.continents;
	}

	public Collection<AllocationAction> getPossibleAllocations(Player activePlayer) {
		Collection<AllocationAction> moves = new ArrayList<>();
		for (Node node : worldMap.getEachNode()) {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() == activePlayer) {
				moves.add(new AllocationAction(country, getPlayerState(activePlayer).getTroopsPerTurn()));
			}
		}
		return moves;
	}

	public Collection<AttackAction> getPossibleAttacks(Player activePlayer) {
		Collection<AttackAction> moves= new ArrayList<>();
		for (Node node : worldMap.getEachNode()) {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() == activePlayer) {
				for (Edge edge : node.getEachLeavingEdge()) {
					Country otherCountry = edge.getTargetNode().getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
					if (country.canAttack(otherCountry)) {
						for (int troops = otherCountry.getNumberOfTroops() + 1;
							 troops <= country.getNumberOfTroops() - 1; troops++) {
							moves.add(new AttackAction(country, otherCountry, troops));
						}
					}
				}
			}
		}
		return moves;
	}

	public Collection<AllocationAction> getPossibleAllocations() {
		return getPossibleAllocations(player);
	}

	public Collection<AttackAction> getPossibleAttacks() {
		return getPossibleAttacks(player);
	}

	public boolean isLegalAllocation(AllocationAction allocation, Player player) {
		return allocation.getCountry().getControllingPlayer() == player;
	}

	public boolean isLegalAllocation(AllocationAction allocation) {
		return isLegalAllocation(allocation, player);
	}

	public boolean isLegalAttack(AttackAction attack, Player player) {
		return attack.getAttackingCountry().canAttack(attack.getAttackedCountry())
				&& attack.getAttackingCountry().getControllingPlayer() == player;
	}

	public boolean isLegalAttack(AttackAction attack) {
		return isLegalAttack(attack, player);
	}

	public PlayerState getPlayerState(Player player) {
		if(player == Player.PLAYER1) {
			return player1State;
		} else {
			return player2State;
		}
	}

	private void updateGraphCountry(Graph graph, Country newCountry) {
		for (Node node : worldMap.getEachNode()) {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getId() == newCountry.getId()) {
				node.setAttribute(Constants.COUNTRY_ATTRIBUTE, newCountry);
				return;
			}
		}
	}

	public GameState forecastAllocation(AllocationAction move) {
		GameState newState = new GameState(this);
		newState.player = this.player;
		newState.phase = Phase.ATTACK;
		updateGraphCountry(newState.worldMap, move.getAllocationResult());
		newState.player1State = new PlayerState(newState.worldMap, Player.PLAYER1, player1State.getTroopsPerTurn());
		newState.player2State = new PlayerState(newState.worldMap, Player.PLAYER2, player2State.getTroopsPerTurn());

		return newState;
	}

	public GameState forecastAttack(AttackAction move) {
		GameState newState = new GameState(this);
		newState.player = this.player.getOpponent();
		newState.phase = Phase.ALLOCATE;
		updateGraphCountry(newState.worldMap, move.getModifiedAttacker());
		updateGraphCountry(newState.worldMap, move.getModifiedAttackee());

		newState.player1State = new PlayerState(newState.worldMap, Player.PLAYER1, player1State.getTroopsPerTurn());
		newState.player2State = new PlayerState(newState.worldMap, Player.PLAYER2, player2State.getTroopsPerTurn());

		newState.player1State.setTroopsPerTurn(newState.calculateTroopsPerTurn(Player.PLAYER1));
		newState.player2State.setTroopsPerTurn(newState.calculateTroopsPerTurn(Player.PLAYER2));
		return newState;
	}

	private int calculateTroopsPerTurn(Player player) {
		PlayerState state = getPlayerState(player);
		int troopsPerTurn = NUM_TROOPS;
		for (Continent continent : continents) {
			if (continent.isSubsetOf(state.getTerritories())) {
				troopsPerTurn += continent.getBonus();
			}
		}
		return troopsPerTurn;
	}

	public boolean isWinner(Player player) {
		for (Node node : worldMap.getEachNode()) {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() != player) {
				return false;
			}
		}
		return true;
	}

	public boolean isLoser(Player player) {
		return isWinner(player.getOpponent());
	}

	public Player getActivePlayer() {
		return player;
	}

	public Player getInactivePlayer() {
		return player.getOpponent();
	}

	public Phase getCurrentPhase() {
		return phase;
	}

	public Phase getNextPhase() {
		return phase.getNextPhase();
	}
}

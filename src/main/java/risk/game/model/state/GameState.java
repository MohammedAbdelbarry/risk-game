package risk.game.model.state;

import java.util.ArrayList;
import java.util.Collection;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;

import risk.game.model.state.action.Action;
import risk.game.model.state.action.AllocationAction;
import risk.game.model.state.action.AttackAction;
import risk.game.model.util.Constants;

public class GameState {
	private Player player;
	private Phase phase;
	private Graph worldMap;
	private PlayerState player1State;
	private PlayerState player2State;
	private Collection<Continent> continents;

	private static final int NUM_TROOPS = 2;

	public GameState(Graph worldMap, Collection<Continent> continents) {
		this.worldMap = worldMap;
		this.continents = continents;
		player = Player.PLAYER1;
		phase = Phase.ALLOCATE;

		player1State = new PlayerState(worldMap, Player.PLAYER1, NUM_TROOPS);
		player2State = new PlayerState(worldMap, Player.PLAYER2, NUM_TROOPS);
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

    public Graph getWorldMap() {
        return worldMap;
    }

    public Collection<AllocationAction> getPossibleAllocations(Player activePlayer) {
		Collection<AllocationAction> moves = new ArrayList<>();
		worldMap.nodes().forEach(node -> {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() == activePlayer) {
				moves.add(new AllocationAction(country, getPlayerState(activePlayer).getTroopsPerTurn()));
			}
		});
		return moves;
	}

	public Collection<AttackAction> getPossibleAttacks(Player activePlayer) {
		Collection<AttackAction> moves = new ArrayList<>();
		moves.add(AttackAction.SKIP_ACTION);
		worldMap.nodes().forEach(node -> {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() == activePlayer) {
				node.leavingEdges().forEach(edge -> {
					Country otherCountry = edge.getTargetNode().getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
					if (country.canAttack(otherCountry)) {
						for (int troops = otherCountry.getNumberOfTroops() + 1;
							 troops <= country.getNumberOfTroops() - 1; troops++) {
							moves.add(new AttackAction(country, otherCountry, troops));
						}
					}
				});
			}
		});
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
		return attack == AttackAction.SKIP_ACTION || (attack.getAttackingCountry().canAttack(attack.getAttackedCountry())
				&& attack.getAttackingCountry().getControllingPlayer() == player);
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
		Node node = graph.getNode(String.valueOf(newCountry.getId()));
		node.setAttribute(Constants.COUNTRY_ATTRIBUTE, newCountry);
		node.setAttribute(Constants.UI_LABEL_ATTRIBUTE,
				newCountry.getId() + " (" + newCountry.getNumberOfTroops() + ")");
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
		if (move == AttackAction.SKIP_ACTION) {
			return newState;
		}
		updateGraphCountry(newState.worldMap, move.getModifiedAttacker());
		updateGraphCountry(newState.worldMap, move.getModifiedAttackee());

		newState.player1State = new PlayerState(newState.worldMap, Player.PLAYER1, player1State.getTroopsPerTurn());
		newState.player2State = new PlayerState(newState.worldMap, Player.PLAYER2, player2State.getTroopsPerTurn());

		newState.player1State.setTroopsPerTurn(newState.calculateTroopsPerTurn(Player.PLAYER1)
				+ getPlayerAttackBonus(Player.PLAYER1, player));
		newState.player2State.setTroopsPerTurn(newState.calculateTroopsPerTurn(Player.PLAYER2)
				+ getPlayerAttackBonus(Player.PLAYER2, player));
		return newState;
	}

	public GameState forcastMove(Action action) {
		if (action instanceof AttackAction) {
			return forecastAttack((AttackAction) action);
		} else if (action instanceof AllocationAction) {
			return forecastAllocation((AllocationAction) action);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private int getPlayerAttackBonus(Player player, Player attackingPlayer) {
		if (player == attackingPlayer) {
			return 2;
		}

		return 0;
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
		return worldMap.nodes().noneMatch(node -> {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			return country.getControllingPlayer() != player;
		});
	}

	public boolean isLoser(Player player) {
		return isWinner(player.getOpponent());
	}

	public boolean terminalTest() {
		return isWinner(player) || isLoser(player);
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

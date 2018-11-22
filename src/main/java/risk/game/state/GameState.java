package risk.game.state;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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

	public GameState(Graph worldMap) {
		this.worldMap = worldMap;
	}

	private Collection<AllocationAction> getPossibleAllocations(Player activePlayer) {
		Collection<AllocationAction> moves = new ArrayList<>();
		for (Node node : worldMap.getEachNode()) {
			Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
			if (country.getControllingPlayer() == activePlayer) {
				moves.add(new AllocationAction(country, 0)); // FIXME: Add the correct number of troops to action
			}
		}
		return moves;
	}

	private Collection<AttackAction> getPossibleAttacks(Player activePlayer) {
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

	public GameState forecastAllocation(AllocationAction move) {
		return null;
	}

	public GameState forecastAttack(AttackAction move) {
		return null;
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

package risk.game.model.state;

import java.util.Objects;

public class AstarNode {

	private GameState gameState;
	private long cost;
	private AstarNode parent;
	
	public AstarNode(GameState gameState, long cost, AstarNode parent) {
		this.gameState = gameState;
		this.cost = cost;
		this.parent = parent;
	}
	
	public long getCost() {
		return cost;
	}
	
	public GameState getGameState(){
		return gameState;
	}
	
	public AstarNode getParent() {
		return parent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AstarNode astarNode = (AstarNode) o;
		return cost == astarNode.cost &&
				Objects.equals(gameState, astarNode.gameState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameState, cost);
	}
}

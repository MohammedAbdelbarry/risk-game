package risk.game.model.state;

import java.util.Objects;

public class RTAnode {

	private GameState gameState;
	private RTAnode parent;
	private int depth;
	
	public RTAnode(GameState gameState, RTAnode parent, int depth) {
		this.gameState = gameState;
		this.depth = depth;
		this.parent = parent;
	}
	
	public RTAnode getParent() {
		return parent;
	}
	
	public GameState getGameState(){
		return gameState;
	}
	
	public int getDepth() {
		return depth;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RTAnode rtAnode = (RTAnode) o;
		return Objects.equals(gameState, rtAnode.gameState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameState);
	}
}

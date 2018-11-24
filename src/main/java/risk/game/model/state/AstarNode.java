package risk.game.model.state;

public class AstarNode {

	private GameState gameState;
	private int cost;
	private AstarNode parent;
	
	public AstarNode(GameState gameState, int cost, AstarNode parent) {
		this.gameState = gameState;
		this.cost = cost;
		this.parent = parent;
	}
	
	public int getCost() {
		return cost;
	}
	
	public GameState getGameState(){
		return gameState;
	}
	
	public AstarNode getParent() {
		return parent;
	}
}

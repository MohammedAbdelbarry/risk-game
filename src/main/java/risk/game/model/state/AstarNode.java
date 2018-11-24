package risk.game.model.state;

public class AstarNode {

	private GameState gameState;
	private int cost;
	
	public AstarNode(GameState gameState, int cost) {
		this.gameState = gameState;
		this.cost = cost;
	}
	
	public int getCost() {
		return cost;
	}
	
	public GameState getGameState(){
		return gameState;
	}
}

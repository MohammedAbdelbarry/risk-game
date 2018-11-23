package risk.game.model.state;

public class RTAnode {

	private GameState gameState;
	private RTAnode parent;
	
	public RTAnode(GameState gameState, RTAnode parent) {
		this.gameState = gameState;
		this.parent = parent;
	}
	
	public RTAnode getParent() {
		return parent;
	}
	
	public GameState getGameState(){
		return gameState;
	}
	
}

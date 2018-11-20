package risk.game.state;

import java.util.List;

public class GameState {
	public boolean turn;
	public List<List<Country>> worldMap;

	public GameState(List<List<Country>> map) {
		worldMap = map;
		
	}


}

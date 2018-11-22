package risk.game;

import risk.game.io.InputProvider;
import risk.game.state.GameState;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {
		try {
			GameState initialGameState = InputProvider.getInitialGameState(new File("TEST"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

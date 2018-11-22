package risk.game.io;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import risk.game.state.Continent;
import risk.game.state.Country;
import risk.game.state.GameState;
import risk.game.state.Player;
import risk.game.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class InputProvider {
    public static GameState getInitialGameState(File inputFile) throws FileNotFoundException {
        Scanner in = new Scanner(inputFile);
        int numVertices = in.nextInt();
        int numEdges = in.nextInt();
        Graph map = new SingleGraph("world-map", false, true, numVertices, numEdges);
        for (int i = 1; i <= numVertices; i++) {
            Node newNode = map.addNode(String.valueOf(i));
            newNode.setAttribute(Constants.ID_ATTRIBUTE, i);
        }

        for (int i = 0; i < numEdges; i++) {
            int source = in.nextInt();
            int target = in.nextInt();
            map.addEdge(source + "->" + target, String.valueOf(source), String.valueOf(target));
            map.addEdge(target + "->" + source, String.valueOf(target), String.valueOf(source));
        }

        int numContinents = in.nextInt();
        Set<Continent> continents = new HashSet<>(numContinents);
        for (int i = 0; i < numContinents; i++) {
            int size = in.nextInt();
            int bonus = in.nextInt();
            Set<Integer> countries = new HashSet<>(size);
            for (int j = 0; j < size; j++) {
                countries.add(in.nextInt());
            }
            continents.add(new Continent(countries, bonus));
        }
        for (int i = 1; i <= numVertices; i++) {
            int player = in.nextInt();
            int units = in.nextInt();
            Country country = new Country(i, Player.valueOf(player), units);
            map.getNode(String.valueOf(i)).setAttribute(Constants.COUNTRY_ATTRIBUTE, country);
        }
        in.close();
        return new GameState(map, continents);
    }
}

package risk.game.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.Viewer;
import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Player;
import risk.game.model.state.PlayerState;
import risk.game.model.util.Constants;

public class GameVisualizer {
    private GameApplication gameApp;
    private Graph map;
    private Entity mapEntity;
    private Text player1Text;
    private Text player2Text;
    private Text turn;

    public GameVisualizer(GameApplication gameApp, GameState initialGameState) {
        this.gameApp = gameApp;
        map = copyToUndirected(initialGameState.getWorldMap());
//        Graphs.clone(initialGameState.getWorldMap())
        init(initialGameState);
    }

    private Graph copyToUndirected(Graph directedGraph) {
        Graph undirected = Graphs.clone(directedGraph);
        while (undirected.getEdgeCount() > 0) {
            undirected.removeEdge(0);
        }
        directedGraph.edges().forEach(edge -> {
            try {
                undirected.addEdge(edge.getId(),
                        undirected.getNode(edge.getSourceNode().getId()),
                        undirected.getNode(edge.getTargetNode().getId()));
            } catch (EdgeRejectedException ignore) {

            }
        });
        return undirected;
    }

    private void updateUIClass(Graph map) {
        map.nodes().forEach(node -> {
            Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
            node.setAttribute(Constants.UI_CLASS_ATTRIBUTE, country.getControllingPlayer().toString());
        });
    }

    private void init(GameState initialGameState) {
        map.setAttribute("ui.stylesheet", "url('graph.css')");
        map.setAttribute("ui.quality");
        map.setAttribute("ui.antialias");

        updateUIClass(map);
        map.attributeKeys().forEach(attr -> System.out.println(attr + ": " + map.getAttribute(attr)));

        Viewer viewer = new FxViewer(map, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        GraphRenderer renderer = new FxGraphRenderer();
        FxDefaultView view = (FxDefaultView) viewer.addView(FxViewer.DEFAULT_VIEW_ID, renderer);
        view.setPrefHeight(gameApp.getHeight());
        view.setPrefWidth(gameApp.getWidth());

        mapEntity = Entities.builder().viewFromNode(view).buildAndAttach(gameApp.getGameWorld());

        PlayerState player1State = initialGameState.getPlayerState(Player.PLAYER1);
        PlayerState player2State = initialGameState.getPlayerState(Player.PLAYER2);

        player1Text = new Text(gameApp.getWidth() - 150, 100, "Player1: " + player1State.getTroopsPerTurn() + "/turn");
        player2Text = new Text(gameApp.getWidth() - 150, 150, "Player2: " + player2State.getTroopsPerTurn() + "/turn");

        player1Text.setFill(Color.BLUE);
        player2Text.setFill(Color.RED);
        player1Text.setStrokeWidth(0.5);
        player1Text.setStroke(Color.WHITE);
        player2Text.setStrokeWidth(0.5);
        player2Text.setStroke(Color.WHITE);
        player1Text.setFont(Font.font(player1Text.getFont().getFamily(), FontWeight.BOLD, player1Text.getFont().getSize()));
        player2Text.setFont(Font.font(player2Text.getFont().getFamily(), FontWeight.BOLD, player2Text.getFont().getSize()));


        String turnStr = String.format("Player %s/(%s) Phase",
                (initialGameState.getActivePlayer() == Player.PLAYER1 ? "1" : 2),
                initialGameState.getCurrentPhase());
        int midPoint = gameApp.getWidth() / 2;
        turn = new Text( midPoint - 100, 50, turnStr);
        turn.setFont(Font.font(turn.getFont().getFamily(), FontWeight.BOLD, turn.getFont().getSize()));
        turn.setStrokeWidth(0.5);
        turn.setStroke(Color.WHITE);

        Entities.builder().viewFromNode(player1Text).buildAndAttach(gameApp.getGameWorld());
        Entities.builder().viewFromNode(player2Text).buildAndAttach(gameApp.getGameWorld());
        Entities.builder().viewFromNode(turn).buildAndAttach(gameApp.getGameWorld());

    }

    public void visualize(GameState state) {
        Graph newMap = state.getWorldMap();
        map.nodes().forEach(node -> {
            Node newNode = newMap.getNode(node.getId());
            newNode.attributeKeys().forEach(key -> node.setAttribute(key, newNode.getAttribute(key)));
        });
        updateUIClass(map);
        player1Text.setText("Player1: " + state.getPlayerState(Player.PLAYER1).getTroopsPerTurn() + "/turn");
        player2Text.setText("Player2: " + state.getPlayerState(Player.PLAYER2).getTroopsPerTurn() + "/turn");

        String turnStr = String.format("Player %s/(%s) Phase",
                (state.getActivePlayer() == Player.PLAYER1 ? "1" : 2),
                state.getCurrentPhase());
        turn.setText(turnStr);
    }

    public Entity getMapEntity() {
        return mapEntity;
    }
}

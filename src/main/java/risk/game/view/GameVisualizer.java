package risk.game.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.scene.menu.FXGLDefaultMenu;
import javafx.scene.image.Image;
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

        Viewer viewer = new FxViewer(map, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        GraphRenderer renderer = new FxGraphRenderer();
        FxDefaultView view = (FxDefaultView) viewer.addView(FxViewer.DEFAULT_VIEW_ID, renderer);
        view.setPrefHeight(gameApp.getHeight());
        view.setPrefWidth(gameApp.getWidth());

        Image mapImage = new Image("map.jpg");

        view.setBackLayerRenderer((graphicsContext, graphicGraph, v, i, i1, v1, v2, v3, v4)
                -> {
                        if (graphicsContext != null) {
                            graphicsContext.drawImage(mapImage, v1, v2, i, i1);
                        }
        });

        mapEntity = Entities.builder().viewFromNode(view).buildAndAttach(gameApp.getGameWorld());

        PlayerState player1State = initialGameState.getPlayerState(Player.PLAYER1);
        PlayerState player2State = initialGameState.getPlayerState(Player.PLAYER2);

        player1Text = gameApp.getUIFactory().newText("Player1: " + player1State.getTroopsPerTurn() + "/turn", Color.BLUE, 16);
        player2Text = gameApp.getUIFactory().newText("Player2: " + player2State.getTroopsPerTurn() + "/turn", Color.RED, 16);

        String turnStr = String.format("Player %s/(%s) Phase",
                (initialGameState.getActivePlayer() == Player.PLAYER1 ? "1" : 2),
                initialGameState.getCurrentPhase());
        turn = gameApp.getUIFactory().newText(turnStr, Color.BLACK, 16);
        int midPoint = gameApp.getWidth() / 2;

        Entities.builder().viewFromNode(player1Text).at(gameApp.getWidth() - 150, 100).buildAndAttach(gameApp.getGameWorld());
        Entities.builder().viewFromNode(player2Text).at(gameApp.getWidth() - 150, 150).buildAndAttach(gameApp.getGameWorld());
        Entities.builder().viewFromNode(turn).at(midPoint - 100, 50).buildAndAttach(gameApp.getGameWorld());

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

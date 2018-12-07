package risk.game.view;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
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
    private ViewerPipe viewerPipe;
    private Button cancelButton;

    private Button skipButton;

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

    private Button newButton(String label, Paint g) {
        Button button = FXGL.getUIFactory().newButton(label);
        button.setPrefWidth(100);
        button.setBackground(new Background(new BackgroundFill(g, null, null)));
        button.setFont(FXGL.getUIFactory().newFont(12));
        return button;
    }

    private void init(GameState initialGameState) {
        map.setAttribute("ui.stylesheet", "url('graph.css')");
        map.setAttribute("ui.quality");
        map.setAttribute("ui.antialias");

        updateUIClass(map);

        Viewer viewer = new FxViewer(map, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        FxDefaultView view = (FxDefaultView) viewer.addDefaultView(false);
        view.setPrefHeight(gameApp.getHeight());
        view.setPrefWidth(gameApp.getWidth());

        viewerPipe = viewer.newViewerPipe();

        Image mapImage = new Image("map.jpg");

        Platform.runLater(() -> view.setBackLayerRenderer((graphicsContext, graphicGraph, v, i, i1, v1, v2, v3, v4)
                -> graphicsContext.drawImage(mapImage, v1, v2, i, i1)));

        mapEntity = Entities.builder().viewFromNode(view).buildAndAttach(gameApp.getGameWorld());

        PlayerState player1State = initialGameState.getPlayerState(Player.PLAYER1);
        PlayerState player2State = initialGameState.getPlayerState(Player.PLAYER2);

        player1Text = gameApp.getUIFactory().newText("Player1: " + player1State.getTroopsPerTurn() + "/turn", Color.BLUE, 16);
        player2Text = gameApp.getUIFactory().newText("Player2: " + player2State.getTroopsPerTurn() + "/turn", Color.RED, 16);

        String turnStr = String.format("Player %s/(%s) Phase",
                (initialGameState.getActivePlayer() == Player.PLAYER1 ? "1" : 2),
                initialGameState.getCurrentPhase());
        turn = gameApp.getUIFactory().newText(turnStr, Color.BLACK, 16);
        int spacing = 15;
        VBox textVBox = new VBox(spacing, player1Text, player2Text);

        Paint g = new LinearGradient(0, 1, 1, 0.2, true, CycleMethod.NO_CYCLE,
                new Stop(0.6, Color.color(1, 0.8, 0, 0.34)),
                new Stop(0.85, Color.color(1, 0.8, 0, 0.74)),
                new Stop(1, Color.WHITE));

        skipButton = newButton("Skip", g);
        cancelButton = newButton("Cancel", g);

        skipButton.setVisible(false);
        cancelButton.setVisible(false);

        VBox buttonVBox = new VBox(0, skipButton, cancelButton);
        HBox hBox = new HBox(0, textVBox, turn, buttonVBox);
        HBox.setMargin(textVBox, new Insets(0, 400, 0, 0));
        HBox.setMargin(turn, new Insets(0, 485, 0, 0));
        hBox.setPrefHeight(50);
        hBox.setPrefWidth(gameApp.getWidth());
        hBox.setPickOnBounds(false);

        Entities.builder().at(0, 0).viewFromNode(hBox).buildAndAttach(gameApp.getGameWorld());
    }

    public void visualize(GameState state) {
        Graph newMap = state.getWorldMap();
        map.nodes().forEach(node -> {
            Node newNode = newMap.getNode(node.getId());
            newNode.attributeKeys().forEach(key -> node.setAttribute(key, newNode.getAttribute(key)));
            if (!newNode.hasAttribute("ui.hide")) {
                node.removeAttribute("ui.hide");
            }
            if (!newNode.hasAttribute("ui.color")) {
                node.removeAttribute("ui.color");
            }
        });
        updateUIClass(map);
        player1Text.setText("Player1: " + state.getPlayerState(Player.PLAYER1).getTroopsPerTurn() + "/turn");
        player2Text.setText("Player2: " + state.getPlayerState(Player.PLAYER2).getTroopsPerTurn() + "/turn");

//        map.nodes().forEach(node -> {
//            System.out.println("Node " + node.getId() + ":");
//            node.attributeKeys().forEach(attr -> System.out.println("\t" + attr + ": " + node.getAttribute(attr)));
//        });

        String turnStr = String.format("Player %s/(%s) Phase",
                (state.getActivePlayer() == Player.PLAYER1 ? "1" : 2),
                state.getCurrentPhase());
        turn.setText(turnStr);
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getSkipButton() {
        return skipButton;
    }

    public Entity getMapEntity() {
        return mapEntity;
    }

    public ViewerPipe getViewerPipe() {
        return viewerPipe;
    }
}

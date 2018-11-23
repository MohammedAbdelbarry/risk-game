package risk.game.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
import risk.game.model.util.Constants;

public class GameVisualizer {
    private GameApplication gameApp;
    private Graph map;
    private Entity mapEntity;

    public GameVisualizer(GameApplication gameApp, GameState initialGameState) {
        this.gameApp = gameApp;
        map = Graphs.clone(initialGameState.getWorldMap());
        init();
    }

    private void updateUIClass(Graph map) {
        map.nodes().forEach(node -> {
            Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
            node.setAttribute(Constants.UI_CLASS_ATTRIBUTE, country.getControllingPlayer().toString());
        });
    }

    private void init() {
        map.setAttribute("ui.stylesheet", "url('graph.css')");
        map.setAttribute("ui.quality");
        map.setAttribute("ui.antialias");
        updateUIClass(map);
        map.attributeKeys().forEach(attr -> System.out.println(attr + ": " + map.getAttribute(attr)));
        System.out.println();
        Viewer viewer = new FxViewer(map, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        GraphRenderer renderer = new FxGraphRenderer();
        FxDefaultView view = (FxDefaultView) viewer.addView(FxViewer.DEFAULT_VIEW_ID, renderer);
        view.setBackground(new Background(new BackgroundFill(null, null, null)));
        mapEntity = Entities.builder().viewFromNode(view).buildAndAttach(gameApp.getGameWorld());
    }

    public void visualize(GameState state) {
        Graph newMap = state.getWorldMap();
        map.nodes().forEach(node -> {
            Node newNode = newMap.getNode(node.getId());
            newNode.attributeKeys().forEach(key -> {
                node.setAttribute(key, newNode.getAttribute(key));
            });
        });
        updateUIClass(map);
    }

    public Entity getMapEntity() {
        return mapEntity;
    }
}

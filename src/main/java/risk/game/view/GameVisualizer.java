package risk.game.view;

import com.almasb.fxgl.app.GameApplication;
import org.graphstream.graph.Graph;
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

    public GameVisualizer(GameApplication gameApp, GameState initialGameState) {
        this.gameApp = gameApp;
        map = Graphs.clone(initialGameState.getWorldMap());
        visualize(initialGameState);
    }

    private void updateUIClass(Graph map) {
        map.nodes().forEach(node -> {
            Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
            node.setAttribute(Constants.UI_CLASS_ATTRIBUTE, country.getControllingPlayer().toString());
            System.out.println(node.getAttribute(Constants.UI_CLASS_ATTRIBUTE));
        });
    }

    private void visualize(GameState state) {
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

        gameApp.getGameScene().addUINode(view);

    }

}

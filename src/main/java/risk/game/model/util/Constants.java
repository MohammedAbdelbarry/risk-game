package risk.game.model.util;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String COUNTRY_ATTRIBUTE = "country";
    public static final String ID_ATTRIBUTE = "id";
    public static final String UI_LABEL_ATTRIBUTE = "ui.label";
    public static final String UI_CLASS_ATTRIBUTE = "ui.class";
    public static final List<String> agents
            = Arrays.asList("Human", "Passive", "Aggressive", "Pacifist", "Greedy", "A*", "Real-time A*");

}

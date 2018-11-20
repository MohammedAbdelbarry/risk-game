package risk.game.state.action;

import risk.game.state.Country;
import risk.game.state.Phase;

import java.util.Objects;

public class Action {
    private Phase actionType;
    private Country country;
    private int troops;

    public Action(Phase actionType, Country country, int troops) {
        this.actionType = actionType;
        this.country = country;
        this.troops = troops;
    }

    public Phase getActionType() {
        return actionType;
    }

    public Country getCountry() {
        return country;
    }

    public int getTroops() {
        return troops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Action action = (Action) o;
        return troops == action.troops &&
                actionType == action.actionType &&
                Objects.equals(country, action.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, country, troops);
    }
}

package risk.game.model.state.action;

import risk.game.model.state.Country;

import java.util.Objects;

public class AllocationAction implements Action {
    private Country country;
    private int troops;

    public AllocationAction(Country country, int troops) {
        this.country = country;
        this.troops = troops;
    }

    public Country getCountry() {
        return country;
    }

    public int getTroops() {
        return troops;
    }

    public Country getAllocationResult() {
        Country modifiedCountry = new Country(country);
        modifiedCountry.setNumberOfTroops(modifiedCountry.getNumberOfTroops() + troops);
        return modifiedCountry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AllocationAction allocationAction = (AllocationAction) o;
        return troops == allocationAction.troops &&
                Objects.equals(country, allocationAction.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, troops);
    }
}

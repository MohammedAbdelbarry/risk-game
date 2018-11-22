package risk.game.state;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Continent {
    private Set<Integer> countriesIds;
    private int bonus;

    public Continent(Set<Integer> countriesIds, int bonus) {
        this.countriesIds = countriesIds;
        this.bonus = bonus;
    }

    public int getBonus() {
        return bonus;
    }

    public Set<Integer> getCountriesIds() {
        return Collections.unmodifiableSet(countriesIds);
    }

    public boolean isSubsetOf(Collection<Country> countries) {
        return countries.stream().map(Country::getId).collect(Collectors.toSet()).containsAll(countriesIds);
    }
}

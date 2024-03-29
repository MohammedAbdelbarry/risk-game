package risk.game.model.state;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
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

    @Override
    public String toString() {
        return String.format("Continent: %s & Bonus: %d", countriesIds, bonus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Continent continent = (Continent) o;
        return bonus == continent.bonus &&
                Objects.equals(countriesIds, continent.countriesIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countriesIds, bonus);
    }
}

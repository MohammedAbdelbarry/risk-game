package risk.game.state.action;

import risk.game.state.Country;

import java.util.Objects;

public class AttackAction {
    private Country attackingCountry;
    private Country attackedCountry;
    private int troops;

    public AttackAction(Country attackingCountry, Country attackedCountry, int troops) {
        this.attackingCountry = attackingCountry;
        this.attackedCountry = attackedCountry;
        this.troops = troops;
    }

    public Country getAttackingCountry() {
        return attackingCountry;
    }

    public void setAttackingCountry(Country attackingCountry) {
        this.attackingCountry = attackingCountry;
    }

    public Country getAttackedCountry() {
        return attackedCountry;
    }

    public void setAttackedCountry(Country attackedCountry) {
        this.attackedCountry = attackedCountry;
    }

    public int getTroops() {
        return troops;
    }

    public void setTroops(int troops) {
        this.troops = troops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttackAction that = (AttackAction) o;
        return troops == that.troops &&
                Objects.equals(attackingCountry, that.attackingCountry) &&
                Objects.equals(attackedCountry, that.attackedCountry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attackingCountry, attackedCountry, troops);
    }
}

package risk.game.model.state.action;

import risk.game.model.state.Country;

import java.util.Objects;

public class AttackAction implements Action {
    public static final AttackAction SKIP_ACTION = new AttackAction(null, null, -1);
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

    public Country getModifiedAttacker() {
        Country modifiedCountry = new Country(attackingCountry);
        modifiedCountry.setNumberOfTroops(modifiedCountry.getNumberOfTroops() - troops);
        return modifiedCountry;
    }

    public Country getModifiedAttackee() {
        Country modifiedCountry = new Country(attackedCountry);
        modifiedCountry.setNumberOfTroops(troops - modifiedCountry.getNumberOfTroops());
        modifiedCountry.setControllingPlayer(attackingCountry.getControllingPlayer());
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
        AttackAction that = (AttackAction) o;
        return troops == that.troops &&
                Objects.equals(attackingCountry, that.attackingCountry) &&
                Objects.equals(attackedCountry, that.attackedCountry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attackingCountry, attackedCountry, troops);
    }


    @Override
    public String toString() {
        if (this == SKIP_ACTION) {
            return "Skip";
        }
        return String.format("%s.Attack(%d, %d, %d)", attackingCountry.getControllingPlayer(),
                attackingCountry.getId(), attackedCountry.getId(), troops);
    }
}

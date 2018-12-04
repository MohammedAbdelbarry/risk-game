package risk.game.controller.agents;

import com.almasb.fxgl.app.GameApplication;
import risk.game.model.agents.GameAgent;
import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.AllocationAction;
import risk.game.model.state.action.AttackAction;

import java.util.concurrent.atomic.AtomicInteger;

public class HumanAgent extends GameAgent {
    private String firstNode;
    private String secondNode;
    private GameApplication app;

    public HumanAgent(GameApplication app) {
        firstNode = null;
        secondNode = null;
        this.app = app;
    }

    @Override
    public GameState play(GameState state, Player player) {
        if (state.getActivePlayer() != player) {
            return state;
        }
        if (state.getCurrentPhase() == Phase.ALLOCATE) {
            if (firstNode != null) {
                Country country = state.getCountry(firstNode);
                firstNode = null;
                secondNode = null;
                if (country.getControllingPlayer() == player) {
                    AllocationAction action = new AllocationAction(country, state.getPlayerState(player).getTroopsPerTurn());
                    return state.forecastAllocation(action);
                } else {

                }
            }
        } else {
            if (firstNode != null) {
                Country country = state.getCountry(firstNode);
                if (country.getControllingPlayer() == player) {
                    if (secondNode != null) {
                        Country secondCountry = state.getCountry(secondNode);
                        secondNode = null;
                        if (secondCountry.getControllingPlayer() != player
                                && country.canAttack(state.getWorldMap(), secondCountry)) {
                            firstNode = null;
                            AtomicInteger troops = new AtomicInteger();
                            app.getDisplay().showInputBox("Enter The Number of Troops", (s) -> {
                                try {
                                    int num = Integer.parseInt(s);
                                    return num >= secondCountry.getNumberOfTroops() + 1
                                            && num <= country.getNumberOfTroops() - 1;
                                } catch (Exception ignored) {
                                    return false;
                                }
                            }, (s) -> troops.set(Integer.parseInt(s)));
                            AttackAction action = new AttackAction(country, secondCountry, troops.get());
                            System.out.println(action);
                            return state.forecastAttack(action);
                        } else {
                            secondNode = null;
                            return null;
                        }
                    }
                } else {
                    firstNode = null;
                }
            }
        }
        return null;
    }

    @Override
    public void reset() {

    }

	@Override
	public long calculatePerformance(int f) {
		return 0;
	}

    @Override
    public void buttonReleased(String s) {
        System.out.println("Clicked Node: " + s);
        if (firstNode == null) {
            firstNode = s;
        } else {
            secondNode = s;
        }
    }
}

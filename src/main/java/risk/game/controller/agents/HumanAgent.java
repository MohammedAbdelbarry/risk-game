package risk.game.controller.agents;

import com.almasb.fxgl.app.GameApplication;
import javafx.scene.paint.Color;
import org.graphstream.graph.Node;
import risk.game.model.agents.GameAgent;
import risk.game.model.state.Country;
import risk.game.model.state.GameState;
import risk.game.model.state.Phase;
import risk.game.model.state.Player;
import risk.game.model.state.action.AllocationAction;
import risk.game.model.state.action.AttackAction;
import risk.game.model.util.Constants;

public class HumanAgent extends GameAgent {
    private String firstNode;
    private String secondNode;
    private volatile Integer numberOfTroops;
    private volatile boolean skipAttack;
    private GameApplication app;

    public HumanAgent(GameApplication app) {
        this.app = app;
        reset();
    }


    @Override
    public void reset() {
        firstNode = null;
        secondNode = null;
        numberOfTroops = null;
        skipAttack = false;
    }

    private void setNumberOfTroops(String num) {
        numberOfTroops = Integer.parseInt(num);
    }

    public void skipAttack() {
        skipAttack = true;
    }

    private void showHideNodes(GameState state, Player player, boolean hide) {
        state.getWorldMap().nodes().forEach(node -> {
            Country country = node.getAttribute(Constants.COUNTRY_ATTRIBUTE, Country.class);
            if (country.getControllingPlayer() == player) {
                if (hide) {
                    node.setAttribute("ui.hide");
                } else {
                    node.removeAttribute("ui.hide");
                }
            }
        });
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
                }
            }
        } else {
            if (firstNode != null) {
                Country country = state.getCountry(firstNode);
                if (country.getControllingPlayer() == player) {
                    Node firstCountryNode = state.getWorldMap().getNode(firstNode);
                    if (skipAttack) {
                        showHideNodes(state, player, false);
                        firstCountryNode.removeAttribute("ui.color");
                        reset();
                        return state.forecastAttack(AttackAction.SKIP_ACTION);
                    }
                    showHideNodes(state, player, true);
                    firstCountryNode.removeAttribute("ui.hide");
                    firstCountryNode.setAttribute("ui.color", Color.rgb(128, 128, 156, 0.9));
//                    firstCountryNode.setAttribute("ui.class", "attacker");
                    if (secondNode != null) {
//                        Node secondCountryNode = state.getWorldMap().getNode(secondNode);
//                        secondCountryNode.setAttribute("ui.class", "target");
                        Country secondCountry = state.getCountry(secondNode);
                        if (secondCountry.getControllingPlayer() != player
                                && country.canAttack(state.getWorldMap(), secondCountry)) {
                            showHideNodes(state, player,false);
                            firstCountryNode.removeAttribute("ui.color");
                            if (numberOfTroops != null) {
                                AttackAction action = new AttackAction(country, secondCountry, numberOfTroops);
                                numberOfTroops = null;
                                firstNode = null;
                                secondNode = null;
                                System.out.println(action);
                                return state.forecastAttack(action);
                            } else {
                                app.getDisplay().showInputBox("Enter The Number of Troops", (s) -> {
                                    try {
                                        int num = Integer.parseInt(s);
                                        return num >= secondCountry.getNumberOfTroops() + 1
                                                && num <= country.getNumberOfTroops() - 1;
                                    } catch (Exception ignored) {
                                        return false;
                                    }
                                }, this::setNumberOfTroops);
//                                state.getWorldMap().nodes().forEach(node -> {
//                                    System.out.println("Node " + node.getId() + ":");
//                                    node.attributeKeys().forEach(attr -> System.out.println("\t" + attr + ": " + node.getAttribute(attr)));
//                                });
                                return state;
                            }
                        } else {
                            secondNode = null;
                            return state;
                        }
                    }
                } else {
                    firstNode = null;
                }
            } else if (skipAttack) {
                showHideNodes(state, player, false);
                reset();
                return state.forecastAttack(AttackAction.SKIP_ACTION);
            }
        }
        return state;
    }

    @Override
    public void buttonReleased(String s) {
        if (firstNode == null) {
            firstNode = s;
        } else {
            secondNode = s;
        }
    }
}

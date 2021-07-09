package script;

import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import sections.*;
import util.Sleep;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.SocketHandler;

@ScriptManifest(author = "Ryan", name = "TutorialIsland", info = "Completes Tutorial Island", version = 0, logo = "")
public final class TutorialIsland extends Script {
    public static final String VERSION = "v6.2";

    private final TutorialSection rsGuideSection = new RuneScapeGuideSection();
    private final TutorialSection survivalSection = new SurvivalSection();
    private final TutorialSection cookingSection = new CookingSection();
    private final TutorialSection questSection = new QuestSection();
    private final TutorialSection miningSection = new MiningSection();
    private final TutorialSection fightingSection = new FightingSection();
    private final TutorialSection bankSection = new BankSection();
    private final TutorialSection priestSection = new PriestSection();
    private final TutorialSection wizardSection = new WizardSection();
    private final Area grandExchange = new Area(3140, 3515, 3185, 3468);

    socketHandler sock;
    boolean getMembership;
    boolean tradeSuccessul = false;
    boolean checkedBank = false;
    boolean bondRedeemed = false;
    boolean buying = false;
    boolean getBond = true;

    @Override
    public void onStart() {
        rsGuideSection.exchangeContext(getBot());
        survivalSection.exchangeContext(getBot());
        cookingSection.exchangeContext(getBot());
        questSection.exchangeContext(getBot());
        miningSection.exchangeContext(getBot());
        fightingSection.exchangeContext(getBot());
        bankSection.exchangeContext(getBot());
        priestSection.exchangeContext(getBot());
        wizardSection.exchangeContext(getBot());
        Sleep.sleepUntil(() -> getClient().isLoggedIn() && myPlayer().isVisible(), 6000, 500);

        if (getBond == false) {
            bondRedeemed = true;
        }
        try {
            sock = new socketHandler(new Socket("127.0.0.1", 50007), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final int onLoop() throws InterruptedException {
        if (isTutorialIslandCompleted()) {

            if (!grandExchange.contains(myPosition())) {
                getWalking().webWalk(grandExchange);
            } else if (getBond) {
//            log(getWidgets().get(109,25).getMessage());
                if (!getTabs().isOpen(Tab.ACCOUNT_MANAGEMENT) && !buying) {
                    log("Opening tab");
                    getTabs().open(Tab.ACCOUNT_MANAGEMENT);
                    sleep(1000);
                }

                if (getTabs().isOpen(Tab.ACCOUNT_MANAGEMENT) && getWidgets().get(109, 25) != null && getWidgets().get(109, 25).getMessage().contains("None")) {
                    log("Account does not have a valid membership");
                    getMembership = true;
                }

                if (!getMembership) {
                    log("Successfully redeemed bond.");
                    stop(true);
                }


                if (getMembership) {
                    log("Please supply the account with a bond.");
                    sleep(10000);
                    if (getInventory().contains(i -> i.getName().contains("Old school bond"))) {
                        log("redeeming bond");

                        if (getInventory().contains(i -> i.getName().contains("Old school bond")) && getWidgets().get(66, 24, 1) == null) {
                            getInventory().interact("Redeem", i -> i.getName().contains("Old school bond"));
                        }
                        sleep(1000);
                        if (getWidgets().get(66, 24, 1) != null && getWidgets().get(66, 24, 1).isVisible()) {
                            getWidgets().get(66, 7, 0).interact();
                            sleep(1000);
                            getWidgets().get(66, 24, 1).interact();
                            sleep(1000);
                            myPlayer().interact();
                        }
                    }
                    if (getClient().isMember()) {
                        bondRedeemed = true;
                        try {
                            sock.sendRequest("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                if (false) { // replace with get membership when automated
//                if (getInventory().getAmount("Coins") < 26 && !getInventory().contains(i -> i.getName().contains("Old school bond"))) {
                    if (!getBank().isOpen() && !checkedBank) {
                        log("Opening bank");
                        getBank().open();
                        sleep(1000);
                        getBank().withdrawAll("Coins");
                        sleep(1000);
                        getBank().close();
                        checkedBank = true;
                    }

                    if (getInventory().getAmount("Coins") >= 6000000) {
                        tradeSuccessul = true;
                    }


                    try {
                        log("getting cash from mule");
                        mule mul = new mule(this, 6000000);
                        while ((getInventory().getAmount("Coins") < 26 && !getInventory().contains(i -> i.getName().contains("Old school bond"))) || !tradeSuccessul) {
                            mul.recieve_cash(600000);
                        }
//                            if(getInventory().getAmount("Coins") >= 6000000 && tradeSuccessul){
//
//                            }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (getInventory().getAmount("Coins") > 5000000 && !getInventory().contains(i -> i.getName().contains("Old school bond"))) {
                        log("buying bond");
                        buying = true;

                        if (!getGrandExchange().isOpen()) {
                            getNpcs().closest("Grand exchange clerk").interact("Exchange");
                            sleep(1000);
                        }

                        if (getGrandExchange().isOpen() && !getInventory().contains(i -> i.getName().contains("Old school bond"))) {
                            getGrandExchange().buyItem(13190, "Old school bond", 6000000, 1);
                            sleep(1000);
                        }

                        if (collectItems()) {
                            getGrandExchange().collect();
                            buying = false;
                            sleep(1000);
                        }
                    }

                    if (getInventory().contains(i -> i.getName().contains("Old school bond"))) {
                        log("redeeming bond");

                        if (getInventory().contains(i -> i.getName().contains("Old school bond")) && getWidgets().get(66, 24, 1) == null) {
                            getInventory().interact("Redeem", i -> i.getName().contains("Old school bond"));
                        }
                        sleep(1000);
                        if (getWidgets().get(66, 24, 1) != null && getWidgets().get(66, 24, 1).isVisible()) {
                            getWidgets().get(66, 7, 0).interact();
                            sleep(1000);
                            getWidgets().get(66, 24, 1).interact();
                            sleep(1000);
                            myPlayer().interact();
                            bondRedeemed = true;
                        }

                    }

                    if (!getInventory().contains(i -> i.getName().contains("Old school bond")) && bondRedeemed) {
                        sleep(5000);
                        getWorlds().hopToF2PWorld();
                        sleep(5000);
                        getWorlds().hopToP2PWorld();
                        if (getWorlds().isMembersWorld()) {
                            bondRedeemed = false;
                        }
                    }


                    if (!getTabs().isOpen(Tab.ACCOUNT_MANAGEMENT) && bondRedeemed) {
                        getTabs().open(Tab.ACCOUNT_MANAGEMENT);
                    }
                    if (bondRedeemed && getTabs().isOpen(Tab.ACCOUNT_MANAGEMENT) && getWidgets().get(109, 25) != null && !getWidgets().get(109, 25).getMessage().contains("None")) {
                        log("Account has membership now");

                        getMembership = false;

//                    }
                    }

                }
            } else {
                log("Complete. Logging out.");
                stop(true);
            }
            return 1000;
        }

        switch (getTutorialSection()) {
            case 0:
//                rsGuideSection.onLoop();
                log("case: 0");
                if (getWidgets().get(558, 9) != null && getWidgets().get(558, 9).isVisible()) { // name entry box
                    getWidgets().get(558, 9).interact();
                    sleep(500);
                    getKeyboard().typeString("0", false);    // enter for name suggestion
                    sleep(500);
                }

                if (getWidgets().get(558, 15) != null && getWidgets().get(558, 15).isVisible()) { // press enter
                    getWidgets().get(558, 15).interact();
                    sleep(500);
                }
                if (getWidgets().get(558, 13) != null && getWidgets().get(558, 13).isVisible()) { // click first suggestion
                    getWidgets().get(558, 19, 0).interact();
                    sleep(500);
                }
                if (getWidgets().get(679, 68, 9) != null && getWidgets().get(679, 68, 9).isVisible()) { // accept
                    getWidgets().get(679, 68, 9).interact();
                    sleep(500);
                }

                if (getNpcs().closest("Gielinor Guide") != null) {
                    getNpcs().closest("Gielinor Guide").interact();
                    sleep(500);
                    getDialogues().clickContinue();
                    sleep(500);
                    getDialogues().selectOption(1);
                    sleep(500);
                    getDialogues().clickContinue();
                    getMouse().click(679, 483, false);
                    if (getNpcs().closest("Gielinor Guide") != null && !getDialogues().inDialogue()) {
                        getNpcs().closest("Gielinor Guide").interact();
                        sleep(500);
                    }
                    getDialogues().clickContinue();
                    sleep(500);
                    getDialogues().clickContinue();
                    sleep(500);
                    getObjects().closest("Door").interact("Open");
                    sleep(3000);
                }
                break;
            case 1:
                log("case: 1");

//                rsGuideSection.onLoop();
                if (getWidgets().get(558, 9).isVisible()) {
                    getWidgets().get(558, 9).interact();
                }
                getKeyboard().typeString("0", true);
                break;
            case 2:
            case 3:
                survivalSection.onLoop();
                break;
            case 4:
            case 5:
                cookingSection.onLoop();
                break;
            case 6:
            case 7:
                questSection.onLoop();
                break;
            case 8:
            case 9:
                miningSection.onLoop();
                break;
            case 10:
            case 11:
            case 12:
                fightingSection.onLoop();
                break;
            case 14:
            case 15:
                bankSection.onLoop();
                break;
            case 16:
            case 17:
                priestSection.onLoop();
                break;
            case 18:
            case 19:
            case 20:
                wizardSection.onLoop();
                break;
        }
        return 200;
    }

    public boolean collectItems() {

        ArrayList<GrandExchange.Box> boxes = new ArrayList<>();
        boxes.add(GrandExchange.Box.BOX_1);
        boxes.add(GrandExchange.Box.BOX_2);
        boxes.add(GrandExchange.Box.BOX_3);
        boxes.add(GrandExchange.Box.BOX_4);
        boxes.add(GrandExchange.Box.BOX_5);
        boxes.add(GrandExchange.Box.BOX_6);
        boxes.add(GrandExchange.Box.BOX_7);
        boxes.add(GrandExchange.Box.BOX_8);
        boolean t = false;

        for (GrandExchange.Box b : boxes) {

            if (getGrandExchange().getStatus(b).equals(GrandExchange.Status.FINISHED_BUY) || getGrandExchange().getStatus(b).equals(GrandExchange.Status.FINISHED_SALE) || !getGrandExchange().getStatus(b).equals(GrandExchange.Status.EMPTY)) {
                t = true;
                break;
            }

        }

        return t;
    }

    public boolean isEmpty() {

        ArrayList<GrandExchange.Box> boxes = new ArrayList<>();
        boxes.add(GrandExchange.Box.BOX_1);
        boxes.add(GrandExchange.Box.BOX_2);
        boxes.add(GrandExchange.Box.BOX_3);
        boxes.add(GrandExchange.Box.BOX_4);
        boxes.add(GrandExchange.Box.BOX_5);
        boxes.add(GrandExchange.Box.BOX_6);
        boxes.add(GrandExchange.Box.BOX_7);
        boxes.add(GrandExchange.Box.BOX_8);
        boolean t = false;

        for (GrandExchange.Box b : boxes) {

            if (!getGrandExchange().getStatus(b).equals(GrandExchange.Status.EMPTY)) {
                t = true;
                break;
            }

        }

        return t;
    }

    private int getTutorialSection() throws InterruptedException {
        log(getConfigs().get(406));
//        getBot().getScriptExecutor().stop();
        return getConfigs().get(406);
    }

    private boolean isTutorialIslandCompleted() {
        return getConfigs().get(281) == 1000 && myPlayer().isVisible();
    }
}

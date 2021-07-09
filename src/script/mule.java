package script;

import org.osbot.rs07.utility.ConditionalSleep;

import java.io.IOException;
import java.net.Socket;

public class mule {
    socketHandler s;
    boolean muleRunning;
    String muleName;
    int coinsBeforeTrade;
    boolean hasTraded;
    Socket sock;
    TutorialIsland m;
    int amount;

    public mule(TutorialIsland m, int amount) throws IOException {
        this.m = m;
        sock = new Socket("127.0.0.1", 50007);
        s = new socketHandler(sock, m);
        muleRunning = false;
        muleName = "CH_1x604c5";
        hasTraded = false;
        this.amount = amount;
    }

    public void send_cash(int amount) throws InterruptedException, IOException {

        m.log("mule node");

        if (!muleRunning) {

            m.log("Requesting mule");
            int response = Integer.parseInt(s.sendRequest("mule:" + m.myPlayer().getName().trim() + ":false:" + amount));
            m.sleep(10000);
            if (response == 1) {
                muleRunning = true;
            } else if (response == 0) {
                m.log("Mule is currently busy. will retry soon.");
                m.log("Waiting for mule to finish other trade.");
                m.sleep(15000);
            }
        } else {

            try {


//                m.log(coinsBeforeTrade);
                if (m.getWorlds().getCurrentWorld() != 398) {
                    m.log("Hopping worlds");
                    m.getWorlds().hop(398);
                }

                if (!m.getInventory().contains(i -> !i.getName().equals("Coins")) && !m.getTrade().isCurrentlyTrading() && coinsBeforeTrade == 0) {
                    m.log("Getting coins");
                    m.getBank().open();
                    m.sleep(500);
                    m.getBank().depositAll();
                    m.sleep(500);
                    m.getBank().withdrawAll("Coins");
                    m.sleep(500);
                    coinsBeforeTrade = (int) m.getInventory().getAmount("Coins");
                    m.log("Coins before " + coinsBeforeTrade);
                }



                if (m.getInventory().getAmount("Coins") > coinsBeforeTrade && !m.getTrade().isCurrentlyTrading() && hasTraded) {
                    m.log("Trade complete.");
                    s.sendRequest("c_mule:" + m.myPlayer().getName());
                    m.log("done");
//                    s.sendRequest("startmule,done,0,0");
//                    m.sleep(1000);
//                    s.sendRequest("kill," + PID);
                    m.getWorlds().hopToP2PWorld();
                    sock.close();
                    sock = null;
//                    m.goldMuled += coinsBeforeTrade;
//                    m.MuleCount++;
                    muleRunning = false;
                    hasTraded = false;
//                    m.tradeSuccessul = true;
//                    m.timeToMule = false;
                } else if (m.getTrade().isSecondInterfaceOpen()) {
                    m.log("Accepting trade 2");
                    m.log(coinsBeforeTrade);
                    m.getWidgets().get(334, 13).interact();
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return !m.getTrade().isCurrentlyTrading();
                        }
                    }.sleep();
                    hasTraded = true;
                } else if (m.getTrade().isFirstInterfaceOpen() && m.getTrade().getTheirOffers().contains(i -> i.getName().equals("Coins"))) {
                    m.log("Accepting trade 1");
//                    m.getTrade().
                    m.getWidgets().get(335, 10).interact();
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return m.getTrade().isSecondInterfaceOpen();
                        }
                    }.sleep();
                }  else if (m.getPlayers().closest(muleName) != null && !m.getTrade().isCurrentlyTrading()) {
                    m.log("Trading Mule");
                    m.getPlayers().closest(muleName).interact("Trade with");
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return m.getTrade().isCurrentlyTrading();
                        }
                    }.sleep();
                }else{
                    m.sleep(500);
                }


            } catch (Exception e) {

//                m.logStackTrace(e);
                m.sleep(1000);
            }

        }

    }

    public void recieve_cash(int amount) throws InterruptedException, IOException {

        m.log("recieve cash");

        if (!muleRunning) {

            m.log("Requesting mule");
            int response = Integer.parseInt(s.sendRequest("mule:" + m.myPlayer().getName().trim() + ":false:" + amount));
            m.sleep(15000);
            m.log(response);
            if (response == 1) {
                m.log("Muler running");
                muleRunning = true;
            } else if (response == 0) {
                m.log("Mule is currently busy. will retry soon.");
                m.log("Waiting for mule to finish other trade.");
                m.sleep(15000);
            }
        } else {

            try {


//                m.log(coinsBeforeTrade);
                if (m.getWorlds().getCurrentWorld() != 398) {
                    m.log("Hopping worlds");
                    m.getWorlds().hop(398);
                }

                if (!m.getInventory().contains(i -> !i.getName().equals("Coins")) && !m.getTrade().isCurrentlyTrading() && coinsBeforeTrade == 0) {
                    m.log("Getting coins");
                    m.getBank().open();
                    m.sleep(500);
                    m.getBank().depositAll();
                    m.sleep(500);
                    m.getBank().withdrawAll("Coins");
                    m.sleep(500);
                    coinsBeforeTrade = (int) m.getInventory().getAmount("Coins");
                    m.log("Coins before " + coinsBeforeTrade);
                    m.getBank().close();
                }



                if (hasTraded) {
                    m.log("Trade complete.");
                    s.sendRequest("cmule:" + m.myPlayer().getName());
                    m.log("done");
//                    s.sendRequest("startmule,done,0,0");
//                    m.sleep(1000);
//                    s.sendRequest("kill," + PID);
                    m.getWorlds().hopToP2PWorld();
                    sock.close();
                    sock = null;
//                    m.goldMuled += coinsBeforeTrade;
//                    m.MuleCount++;
                    muleRunning = false;
                    hasTraded = false;
//                    m.tradeSuccessul = true;
//                    m.timeToMule = false;
                } else if (m.getTrade().isSecondInterfaceOpen()) {
                    m.log("Accepting trade 2");
                    m.log(coinsBeforeTrade);
                    m.getWidgets().get(334, 13).interact();
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return !m.getTrade().isCurrentlyTrading();
                        }
                    }.sleep();
                    hasTraded = true;

                } else if (m.getTrade().isFirstInterfaceOpen() && m.getTrade().getTheirOffers().contains(i -> i.getName().equals("Coins"))) {
                    m.log("Accepting trade 1");
//                    m.getTrade().
                    m.getWidgets().get(335, 10).interact();
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return m.getTrade().isSecondInterfaceOpen();
                        }
                    }.sleep();
                }  else if (m.getPlayers().closest(muleName) != null && !m.getTrade().isCurrentlyTrading()) {
                    m.log("Trading Mule");
                    m.getPlayers().closest(muleName).interact("Trade with");
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return m.getTrade().isCurrentlyTrading();
                        }
                    }.sleep();
                }else{
                    m.sleep(500);
                }


            } catch (Exception e) {

//                m.logStackTrace(e);
                m.sleep(1000);
            }
        }
    }


//    public void execute() throws InterruptedException, IOException {
////        if(muleRunning) {
////            s = new socketHandler(new Socket("localhost", 8080), m);
////            coinsBeforeTrade = (int) m.getInventory().getAmount("Coins");
////        }
//
//
////        m.log("both " + (m.getTrade().isFirstInterfaceOpen() && m.getTrade().getOurOffers().contains(i -> i.getName().equals("Coins"))));
////        m.log("open " + m.getTrade().isFirstInterfaceOpen());
////        m.log("offer " + m.getTrade().getOurOffers().contains(i -> i.getName().equals("Coins")));
//
//        m.log("mule node");
//
//        if (sock == null) {
//            sock = new Socket("localhost", 8080);
//            s = new socketHandler(sock, m);
//        }
//
//        if (!muleRunning) {
//
//            int pid = Integer.parseInt(s.sendRequest(String.format("mule:"));
////            m.log("getting coin amount");
////            m.log("Coins before " + coinsBeforeTrade);
//            m.log("Requesting mule");
//            int response = Integer.parseInt(s.sendRequest("startmule," + m.myPlayer().getName().trim() + ",1," + amount));
//            m.sleep(10000);
//            if (pid != -1 && response == 1) {
//                muleRunning = true;
//            } else if (response == 0) {
//                m.log("Mule is currently busy. will retry soon.");
//                m.log("Waiting for mule to finish other trade.");
//                m.sleep(15000);
//            }
//        } else {
//
//            try {
//
//
////                m.log(coinsBeforeTrade);
//                if (m.getWorlds().getCurrentWorld() != 398) {
//                    m.log("Hopping worlds");
//                    m.getWorlds().hop(398);
//                }
//
//                if (!m.getInventory().contains("Coins") && !m.getTrade().isCurrentlyTrading() && coinsBeforeTrade == 0) {
//                    m.log("Getting coins");
//                    m.getBank().open();
//                    m.sleep(500);
//                    m.getBank().depositAll();
//                    m.sleep(500);
//                    m.getBank().withdrawAll("Coins");
//                    m.sleep(500);
//                    coinsBeforeTrade = (int) m.getInventory().getAmount("Coins");
//                    m.log("Coins before " + coinsBeforeTrade);
//                }
//
//
//
//                if (m.getInventory().getAmount("Coins") > coinsBeforeTrade && !m.getTrade().isCurrentlyTrading() && hasTraded) {
//                    m.log("Trade complete.");
//                    int PID = Integer.parseInt(s.sendRequest("getmulepid"));
//                    m.log("done");
//                    s.sendRequest("startmule,done,0,0");
//                    m.sleep(1000);
//                    s.sendRequest("kill," + PID);
//                    m.getWorlds().hopToP2PWorld();
//                    sock.close();
//                    sock = null;
////                    m.goldMuled += coinsBeforeTrade;
////                    m.MuleCount++;
//                    muleRunning = false;
//                    hasTraded = false;
//                    m.tradeSuccessul = true;
////                    m.timeToMule = false;
//                } else if (m.getTrade().isSecondInterfaceOpen()) {
//                    m.log("Accepting trade 2");
//                    m.log(coinsBeforeTrade);
//                    m.getWidgets().get(334, 13).interact();
//                    new ConditionalSleep(20000) {
//                        @Override
//                        public boolean condition() throws InterruptedException {
//                            return !m.getTrade().isCurrentlyTrading();
//                        }
//                    }.sleep();
//                } else if (m.getTrade().isFirstInterfaceOpen() && m.getTrade().getTheirOffers().contains(i -> i.getName().equals("Coins"))) {
//                    m.log("Accepting trade 1");
////                    m.getTrade().
//                    m.getWidgets().get(335, 10).interact();
//                    new ConditionalSleep(20000) {
//                        @Override
//                        public boolean condition() throws InterruptedException {
//                            return m.getTrade().isSecondInterfaceOpen();
//                        }
//                    }.sleep();
//                }  else if (m.getPlayers().closest(muleName) != null && !m.getTrade().isCurrentlyTrading()) {
//                    m.log("Trading Mule");
//                    m.getPlayers().closest(muleName).interact("Trade with");
//                    new ConditionalSleep(20000) {
//                        @Override
//                        public boolean condition() throws InterruptedException {
//                            return m.getTrade().isCurrentlyTrading();
//                        }
//                    }.sleep();
//                }else{
//                    m.sleep(500);
//                }
//
//
//            } catch (Exception e) {
//
////                m.logStackTrace(e);
//                m.sleep(1000);
//            }
//
//        }
//
//    }


}

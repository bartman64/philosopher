package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;


/**
 * TODO: Implementierung waitFork remote
 * TODO: Tablemaster Test verteilt
 * TODO: Client search Test verteilt
 * TODO: Logging, Kommentare verbessern & Code prüfen
 * TODO: Paralleles stoppen bei Hinzufügen von Stühlen [DONE]
 * TODO: Dokumentation Projekt [DONE]
 * TODO: Testen Esszugriffe - Locks [DONE]
 * TODO: Proxy Methode für Server - Registry schreiben [DONE]
 * TODO: Verteilt Testen [DONE]
 */
public class ServerMain {
    public static void main(String[] args) {
        try {
            boolean running = true;
            final int totalSeats = 10;
            final int totalPhilosophers = 50;
            final Registry registry = LocateRegistry.createRegistry(1099);
            Server server = new Server(totalSeats, totalPhilosophers, registry);
            ServerControl serverStub = (ServerControl) UnicastRemoteObject.exportObject(server, 0);
            registry.bind("Server", serverStub);
            Thread.sleep(10000);
            System.out.println(Arrays.toString(registry.list()));
            server.fillClientList();
            server.initClients();
            server.startClients();
            /*while (running){
                Scanner scanner = new Scanner(System.in);

                String cmd = scanner.nextLine();

                String[] cmdSplit = cmd.split(" ");

                switch (cmdSplit[0]){
                    case "stop":
                        running = false;
                        break;
                    case "add":
                        if(cmdSplit.length == 3){
                            if(cmdSplit[1].equals("seats")){
                                server.increaseTableSize(Integer.parseInt(cmdSplit[2]));
                            }
                            if(cmdSplit[1].equals("phils")){
                                server.addPhils(Integer.parseInt(cmdSplit[2]));
                            }
                        }
                        break;
                    case "rm":
                        if(cmdSplit.length == 3){
                            if(cmdSplit[1].equals("seats")){
                                server.removeChairs(Integer.parseInt(cmdSplit[2]));
                            }
                            if(cmdSplit[1].equals("phils")){
                                server.removePhils(Integer.parseInt(cmdSplit[2]));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }*/
            Thread.sleep(60000);
            server.stopClients();

        } catch (RemoteException | AlreadyBoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}

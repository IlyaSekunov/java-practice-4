import client.Client;
import clientproducer.ClientProducer;
import elevatormanager.ElevatorManager;

import java.util.ArrayDeque;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        startSystem();
    }

    private static void startSystem() {
        Queue<Client> queue = new ArrayDeque<>();
        startClientProducer(queue);
        startElevatorManager(queue);
    }

    private static void startElevatorManager(Queue<Client> queue) {
        new Thread(new ElevatorManager(queue)).start();
    }

    private static void startClientProducer(Queue<Client> queue) {
        new Thread(new ClientProducer(1000, 0, queue)).start();
    }
}
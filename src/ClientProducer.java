import java.util.Date;
import java.util.Queue;
import java.util.Random;

public class ClientProducer implements Runnable {
    private final int averageDelayMs;
    private final int dispersionDelayMs;

    private final Queue<Client> clients;

    private final Random random = new Random(new Date().getTime());
    private int currentId = 0;

    public ClientProducer(int averageDelayMs, int dispersionDelayMs, Queue<Client> clients) {
        if (averageDelayMs < dispersionDelayMs) {
            throw new IllegalArgumentException("Dispersion delay cannot be greater than average delay");
        }
        this.averageDelayMs = averageDelayMs;
        this.dispersionDelayMs = dispersionDelayMs;
        this.clients = clients;
    }

    public Client nextClient() {
        var currentFloor = random.nextInt(Config.MIN_FLOOR, Config.MAX_FLOOR + 1);
        var targetFloor = random.nextInt(Config.MIN_FLOOR, Config.MAX_FLOOR + 1);
        if (targetFloor == currentFloor) {
            if (targetFloor == Config.MAX_FLOOR) {
                --targetFloor;
            } else {
                ++targetFloor;
            }
        }
        return new Client(currentId++, currentFloor, targetFloor);
    }

    @Override
    public void run() {
        while (true) {
            try {
                var delay = random.nextInt(averageDelayMs - dispersionDelayMs, averageDelayMs + dispersionDelayMs + 1);
                Thread.sleep(delay);
                synchronized (clients) {
                    if (clients.size() < Config.MAX_CLIENTS_IN_QUEUE) {
                        var nextClient = nextClient();
                        clients.add(nextClient);
                        Logger.log("New client appeared - " + nextClient);
                    }
                }
            } catch (InterruptedException ignored) {
                return;
            }
        }
    }
}

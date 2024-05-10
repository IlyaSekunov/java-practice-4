package elevatormanager;

import client.Client;
import config.Config;
import directions.Direction;
import elevator.Elevator;

import java.util.Optional;
import java.util.Queue;

public class ElevatorManager implements Runnable {
    private final Elevator elevator1 = new Elevator(1, Config.ELEVATOR_CAPACITY);
    private final Elevator elevator2 = new Elevator(2, Config.ELEVATOR_CAPACITY);

    private final Queue<Client> clients;

    public ElevatorManager(Queue<Client> clients) {
        this.clients = clients;
    }

    private void moveElevators() {
        moveElevator(elevator1, elevator2);
        moveElevator(elevator2, elevator1);
    }

    private void moveElevator(Elevator thisElevator, Elevator otherElevator) {
        if (thisElevator.isNotEmpty()) {
            thisElevator.landPassengers();
        }
        if (!clients.isEmpty()) {
            collectPassengers(thisElevator);
        }
        if (thisElevator.isStopped()) {
            if (thisElevator.isEmpty() && !clients.isEmpty()) {
                var newClient = firstNotOnThePath(otherElevator);
                newClient.ifPresent(client -> thisElevator.setTargetFloor(client.initialFloor()));
            }
        }
        thisElevator.move();
    }

    private void collectPassengers(Elevator elevator) {
        var clientsToBeCollected = clients.stream()
                .filter(client -> shouldTakeClient(elevator, client) && elevator.addClient(client))
                .toList();
        clients.removeAll(clientsToBeCollected);
    }

    private boolean shouldTakeClient(Elevator elevator, Client client) {
        if (client.initialFloor() == elevator.getCurrentFloor()) {
            if (elevator.getDirection() == Direction.NONE) {
                return true;
            }
            return client.getDirection() == elevator.getDirection();
        }
        return false;
    }

    private Optional<Client> firstNotOnThePath(Elevator elevator) {
        return clients.stream()
                .filter(client -> client.initialFloor() != elevator.getTargetFloor() && client.getDirection() != elevator.getDirection())
                .findFirst();
    }

    @Override
    public void run() {
        while (true) {
            try {
                moveElevators();
                Thread.sleep(Config.ELEVATOR_DELAY_MS);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

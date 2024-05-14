import java.util.ArrayList;
import java.util.List;

public class Elevator {
    private final int id;
    private final int maxCapacity;

    private int currentFloor = Config.MIN_FLOOR;
    private int targetFloor = currentFloor;
    private Direction direction = Direction.NONE;

    private final List<Client> currentClients = new ArrayList<>();

    public Elevator(int id, int maxCapacity) {
        this.id = id;
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
        Logger.log("Elevator-" + id + ": target floor now is " + targetFloor);
        updateDirection();
    }

    public void move() {
        switch (direction) {
            case UP -> {
                ++currentFloor;
                Logger.log("Elevator-" + id + ": moved up, current floor - " + currentFloor);
                if (currentFloor == targetFloor) {
                    updateDirection();
                }
            }
            case DOWN -> {
                --currentFloor;
                Logger.log("Elevator-" + id + ": moved down, current floor - " + currentFloor);
                if (currentFloor == targetFloor) {
                    updateDirection();
                }
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    private void updateDirection() {
        var previousDirection = direction;
        if (targetFloor > currentFloor) {
            direction = Direction.UP;
        } else if (targetFloor < currentFloor) {
            direction = Direction.DOWN;
        } else {
            direction = Direction.NONE;
        }
        if (previousDirection != direction) {
            if (direction == Direction.NONE) {
                Logger.log("Elevator-" + id + ": stopped");
            } else {
                Logger.log("Elevator-" + id + ": direction now is - " + direction);
            }
        }
    }

    public boolean isNotFull() {
        return currentClients.size() < maxCapacity;
    }

    public boolean isEmpty() {
        return currentClients.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean isStopped() {
        return direction == Direction.NONE;
    }

    public boolean addClient(Client client) {
        if (isNotFull()) {
            Logger.log("Elevator-" + id + ": added client - " + client);
            if (direction == Direction.NONE) {
                setTargetFloor(client.targetFloor());
            } else if (direction == Direction.UP && client.targetFloor() > targetFloor) {
                setTargetFloor(client.targetFloor());
            } else if (direction == Direction.DOWN && client.targetFloor() < targetFloor) {
                setTargetFloor(client.targetFloor());
            }
            return currentClients.add(client);
        } else return false;
    }

    public void landPassengers() {
        var clientsLanded = currentClients.stream()
                .filter(client -> client.targetFloor() == currentFloor)
                .toList();
        clientsLanded.forEach(client -> Logger.log("Elevator-" + id + ": client - " + client + " landed"));
        currentClients.removeAll(clientsLanded);
    }

    public boolean isGoingTo(Client client) {
        return targetFloor == client.initialFloor();
    }

    public boolean isNotGoingTo(Client client) {
        return !isGoingTo(client);
    }
}
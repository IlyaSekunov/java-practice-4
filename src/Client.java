public record Client(int id, int initialFloor, int targetFloor) {
    public Direction getDirection() {
        if (targetFloor > initialFloor) {
            return Direction.UP;
        } else if (targetFloor < initialFloor) {
            return Direction.DOWN;
        } else {
            return Direction.NONE;
        }
    }
}
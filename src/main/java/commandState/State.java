package commandState;

public enum State {
    NULL(-1),
    DEFAULT(0),
    CHANGE_LOGIN(1),
    CHANGE_PASSWORD(2),
    ADD_RESOURSE(3),
    DELL_RESOURSE(4),
    SET_ACCSES(5),
    GET_RESOURCE_INFO(6),
    GET_RESOURCE_PROC(7),
    GET_RESOURCE_QUEUE(8),
    ABORT(9),
    DEL_QUEUE(10),
    SET_NAME_FOR_NEW_RESOURSE(11),
    ADD_IN_QUEUE(12);

    private int state;

    State(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

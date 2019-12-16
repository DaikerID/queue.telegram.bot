package commandState;

import java.util.ArrayList;
import java.util.List;

public class CommandStateController {

    private static List<UserState> userStates = new ArrayList<UserState>();

    private CommandStateController() {
    }

    public static void setCommand(UserState userState) {
        if (userState.getCommandState() == State.DEFAULT && userStates.contains(userState)) {
            userStates.remove(userState);
        } else if (userStates.contains(userState)) {
            userStates.get(userStates.indexOf(userState)).setCommandState(userState.getCommandState());
        } else if (!userStates.contains(userState)) {
            userStates.add(userState);
        }
    }

    public static State getCommand(Long id) {
        UserState userState = new UserState(id, State.NULL);
        if (userStates.contains(userState)) {
            return userStates.get(userStates.indexOf(userState)).getCommandState();
        } else
            return State.DEFAULT;
    }

    public static UserState getUser(Long id) {
        UserState userState = new UserState(id, State.NULL);
        if (userStates.contains(userState)) {
            return userStates.get(userStates.indexOf(userState));
        } else
            return null;
    }
}

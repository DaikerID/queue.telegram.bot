package commandState;

import java.util.Objects;

public class UserState {
    private Long chatId;
    private State commandState;

    public UserState(Long chatId, State state) {
        this.chatId = chatId;
        commandState = state;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public State getCommandState() {
        return commandState;
    }

    public void setCommandState(State commandState) {
        this.commandState = commandState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserState userState = (UserState) o;
        return Objects.equals(chatId, userState.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }
}

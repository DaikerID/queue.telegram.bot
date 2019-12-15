package core;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Entity {
    @NonNull
    long ChatID;
    String name;
    Date time;
    boolean inProcess;
}

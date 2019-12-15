package core;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import static java.lang.Thread.sleep;

@Getter
@Setter
public class Observer implements Runnable{
    boolean stop;
    long timer=5000;
    EntityArray host;

    Observer(EntityArray host){
        this.host=host;
    }

    public void run() {
        while (!stop){
            try {
                sleep(timer);
                host.check();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

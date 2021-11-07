package country.pvp.practice.queue;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueTask implements Runnable {

    private final QueueManager queueManager;

    @Override
    public void run() {
        queueManager.tick();
    }
}

package country.pvp.practice.queue;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueTask implements Runnable {

    private final @NotNull QueueManager queueManager;

    @Override
    public void run() {
        queueManager.tick();
    }
}

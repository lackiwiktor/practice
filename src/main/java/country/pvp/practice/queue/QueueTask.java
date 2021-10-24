package country.pvp.practice.queue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueTask implements Runnable {

    private final QueueManager queueManager;

    @Override
    public void run() {
        queueManager.tick();
    }
}

package country.pvp.practice.team;

import country.pvp.practice.queue.QueueData;

public interface Queueable {
    void startQueuing(QueueData<?> queueData);

    void stopQueueing();
}

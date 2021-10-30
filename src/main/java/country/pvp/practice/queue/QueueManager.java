package country.pvp.practice.queue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import country.pvp.practice.ladder.Ladder;

import java.util.List;
import java.util.stream.Collectors;

public class QueueManager {

    private final Table<Ladder, MatchType, SoloQueue> soloQueues = HashBasedTable.create();

    public void initSoloQueue(Ladder ladder, MatchType... types) {
        for (MatchType type : types) {
            soloQueues.put(ladder, type, new SoloQueue(ladder, type));
        }
    }

    public SoloQueue getSoloQueue(Ladder ladder, MatchType type) {
        return soloQueues.get(ladder, type);
    }

    public List<SoloQueue> getSoloQueues(MatchType type) {
        return soloQueues.cellSet()
                .stream()
                .filter(it -> it.getColumnKey() == type)
                .map(Table.Cell::getValue).collect(Collectors.toList());
    }

    public void tick() {
        soloQueues.cellSet().forEach(cell -> cell.getValue().tick());
    }
}

package me.ponktacology.practice.event;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.event.type.ThimbleListener;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EventService extends Service {

    @Override
    public void configure() {
        addListener(new ThimbleListener());
        addCommand(new EventCommands());
    }

    private final Map<EventType, Event> runningEvents = Maps.newHashMap();

    public void addEvent(Event event) {
        runningEvents.put(event.getType(), event);
    }

    public void removeEvent(Event event) {
        runningEvents.remove(event.getType());
    }

    public @Nullable <E extends Event> E getEventByType(EventType type) {
        return (E) runningEvents.get(type);
    }
}

package me.ponktacology.practice.event;

public interface EventParticipant {
  void setCurrentEvent(Event<?> event);

  void returnToLobby();
}

package me.ponktacology.practice;

import me.ponktacology.practice.follow.FollowService;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;

public class PracticePreconditions {

  public static boolean canCreateParty(PracticePlayer player) {
    if (!player.isInLobby()) {
      Messenger.messageError(player, "You must be in the lobby in order to accept a party invite");
      return false;
    }

    if (Practice.getService(FollowService.class).isFollowing(player)) {
      Messenger.messageError(player, "You can't create a party while following someone.");
      return false;
    }

    if (Practice.getService(PartyService.class).hasParty(player)) {
      Messenger.messageError(player, "You are already in a party.");
      return false;
    }

    return true;
  }

  public static boolean canSendPartyDuel(Party inviter, Party invitee) {
    if (inviter.equals(invitee)) {
      Messenger.messageError(inviter, "You can't duel your own party.");
      return false;
    }

    if (!inviter.isInLobby()) {
      Messenger.messageError(inviter, "You must be in lobby if you want to duel someone.");
      return false;
    }

    if (!invitee.isInLobby()) {
      Messenger.messageError(inviter, "This party is busy right now.");
      return false;
    }

    return true;
  }

  public static boolean canAcceptPartyDuel(Party inviter, Party invitee) {
    return canSendPartyDuel(inviter, invitee);
  }

  public static boolean canSendPlayerDuel(PracticePlayer inviter, PracticePlayer invitee) {
    if (invitee.hasDuelRequest(inviter)) {
      Messenger.messageError(inviter, "You already invited this player for a duel.");
      return false;
    }

    return canAcceptPlayerDuel(inviter, invitee);
  }

  public static boolean canAcceptPlayerDuel(PracticePlayer inviter, PracticePlayer invitee) {
    /*
    if (inviter.isInEvent()) {
      Messenger.messageError(inviter, "You can't be in an event if you want to duel a player.");
      return false;
    }

    if (invitee.isInEvent()) {
      Messenger.messageError(inviter, "Player can't be in an event if you want to duel him.");
      return false;
    }
     */

    PartyService partyService = Practice.getService(PartyService.class);

    if (partyService.hasParty(invitee)) {
      Messenger.messageError(inviter, "Player can't be in a party if you want to duel him.");
      return false;
    }

    if (partyService.hasParty(inviter)) {
      Messenger.messageError(inviter, "You can't be in a party if you want to duel a player.");
      return false;
    }

    if (!inviter.isInLobby()) {
      Messenger.messageError(inviter, "You must be in lobby if you want to duel a player.");
      return false;
    }

    if (!invitee.isInLobby()) {
      Messenger.messageError(inviter, "This player is busy right now.");
      return false;
    }

    return true;
  }

  // TODO: Change this
  public static boolean canJoinParty(PracticePlayer player, Party party) {
    if (!party.isPlayerInvited(player)) {
      Messenger.messageError(
          player, "You have not been invited to this party or the request has expired.");
      return false;
    }

    if (!player.isInLobby()) {
      Messenger.messageError(player, "You must be in the lobby in order to accept a party invite");
      return false;
    }

    if (Practice.getService(FollowService.class).isFollowing(player)) {
      Messenger.messageError(player, "You can't create a party while following someone.");
      return false;
    }

    if (Practice.getService(PartyService.class).hasParty(player)) {
      Messenger.messageError(player, "You are already in a party.");
      return false;
    }

    return true;
  }

  public static boolean canJoinQueue(PracticePlayer player) {
    if (!player.isInLobby()) {
      Messenger.messageError(player, "You can join a queue only in the lobby.");
      return false;
    }

    if (Practice.getService(PartyService.class).hasParty(player)) {
      Messenger.messageError(player, "You can join a queue while being in a party.");
      return false;
    }

    return true;
  }
}

package me.ponktacology.practice.player;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerSaveTask implements Runnable {

    private final PlayerService partyDuelService;
    private final PlayerRepository playerRepository;

    @Override
    public void run() {
        for (PracticePlayer player : partyDuelService.getAll()) {
            if (!player.isOnline()) continue;

            playerRepository.save(player);
        }
    }
}

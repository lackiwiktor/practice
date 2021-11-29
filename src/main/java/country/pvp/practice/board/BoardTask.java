package country.pvp.practice.board;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BoardTask implements Runnable {

    private final PracticeBoard board;

    @Override
    public void run() {
        board.update();
    }
}

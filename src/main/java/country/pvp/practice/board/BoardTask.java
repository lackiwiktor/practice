package country.pvp.practice.board;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BoardTask implements Runnable {

    private final @NotNull PracticeBoard board;

    @Override
    public void run() {
        board.update();
    }
}

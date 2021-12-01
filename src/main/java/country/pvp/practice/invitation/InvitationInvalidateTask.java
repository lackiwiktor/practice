package country.pvp.practice.invitation;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class InvitationInvalidateTask implements Runnable {

    private final InvitationManager invitationManager;

    @Override
    public void run() {
        invitationManager.invalidate();
    }
}

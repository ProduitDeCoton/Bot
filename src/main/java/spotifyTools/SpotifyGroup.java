package spotifyTools;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.User;
import spotify.models.players.requests.TransferPlaybackRequestBody;

import java.util.List;

public class SpotifyGroup {
    private User leader;

    public SpotifyGroup(User leader) {
        this.leader = leader;
    }

    public User getLeader() {
        return leader;
    }

    public void transferPlayback(String device) {

        if (ActiveUsers.getSession(leader).getTokenExpiresIn() <= 30) {
            ActiveUsers.getSession(leader).authorizeByRefreshToken();
        }

        var devices = ActiveUsers.getSession(leader).getSpotifyApi().getAvailableDevices().getDevices();

        for (var device_iter : devices) {
            if (device_iter.getName().equals(device)) {
                TransferPlaybackRequestBody requestBody = new TransferPlaybackRequestBody();
                requestBody.setDeviceIds(List.of(device_iter.getId()));

                ActiveUsers.getSession(leader).getSpotifyApi().transferPlayback(requestBody);
            }
        }
    }
}

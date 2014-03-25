package ac.ic.chaturaji.android;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.PlayerType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Haider
 */
public class GameRoomAdapter extends BaseAdapter {

    Context context;
    List<Game> gamesList;

    public GameRoomAdapter(Context context, List<Game> gamesList) {
        this.context = context;
        this.gamesList = gamesList;
    }

    @Override
    public int getCount() {
        return gamesList.size();
    }

    @Override
    public Object getItem(int i) {
        return gamesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int humans = 1;
        int ais = 0;

        Game current = gamesList.get(i);
        List<Player> players = current.getPlayers();

        for (int j = 1; j < players.size(); j++) {
            if (players.get(j) != null) {
                if (players.get(j).getType() == PlayerType.AI)
                    ais++;
                if (players.get(j).getType() == PlayerType.HUMAN)
                    humans++;
            }
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.game_room_item, viewGroup, false);
        row.setTag(current.getId());
        TextView username = (TextView) row.findViewById(R.id.game_username);
        TextView ais_text = (TextView) row.findViewById(R.id.game_ais);
        TextView human_text = (TextView) row.findViewById(R.id.game_humans);
        ImageView icon = (ImageView) row.findViewById(R.id.gameImage);
        Button joinButton = (Button) row.findViewById(R.id.join_button);
        joinButton.setTag(current.getId());

        switch (humans) {
            case 2:
                icon.setImageResource(R.drawable.twoplayer);
                break;
            case 3:
                icon.setImageResource(R.drawable.threeplayer);
                break;
            case 4:
                icon.setImageResource(R.drawable.ic_launcher);
                break;
            default:
                break;
        }

        if (players.size() > 0 && players.get(0) != null) {
            username.setText(players.get(0).getUser().getNickname());
        }

        if (username.getText().length() < 1) {
            username.setText("No Name");
        }

        String display_ais = String.valueOf(ais);
        String display_humans = String.valueOf(humans);
        ais_text.setText(display_ais);
        human_text.setText(display_humans);


        return row;
    }
}

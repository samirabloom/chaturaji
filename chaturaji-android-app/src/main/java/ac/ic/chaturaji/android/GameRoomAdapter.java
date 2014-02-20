package ac.ic.chaturaji.android;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.PlayerType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Haider on 12/02/14.
 */

/* Following code by Haider Qazi */

public class GameRoomAdapter extends BaseAdapter {

    Context context;
    List<Game> gamesList;

    public GameRoomAdapter (Context context, List<Game> gamesList){
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
        Player[] players = current.getPlayer();

        for(int j = 1; j < 4; j++){
            if(players[j] != null){
                if(players[j].getType() == PlayerType.AI)
                    ais++;
                if(players[j].getType() == PlayerType.HUMAN)
                    humans++;
            }
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.game_room_item, viewGroup, false);
        TextView username = (TextView) row.findViewById(R.id.game_username);
        TextView ais_text = (TextView) row.findViewById(R.id.game_ais);
        TextView human_text = (TextView) row.findViewById(R.id.game_humans);
        ImageView icon = (ImageView) row.findViewById(R.id.gameImage);

        switch (humans){
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
;
        if(players[0] != null)
        username.setText(players[0].getId());

        String display_ais = "AIs: " + String.valueOf(ais);
        String display_humans = "HUMANs: " + String.valueOf(humans);
        ais_text.setText(display_ais);
        human_text.setText(display_humans);



        return row;
    }
}

package ac.ic.chaturaji.android;

import ac.ic.chaturaji.model.Game;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Haider on 12/02/14.
 */
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.game_room_item, viewGroup, false);
        TextView title = (TextView) row.findViewById(R.id.room_item);
        Game current = gamesList.get(i);
        title.setText(current.getId());

        return row;
    }
}

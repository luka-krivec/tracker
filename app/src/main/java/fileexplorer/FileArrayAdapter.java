package fileexplorer;

import java.util.List;

import si.krivec.tracker.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<Item> {

    private Context c;
    private int id;
    private List<Item> items;

    public FileArrayAdapter(Context context, int textViewResourceId, List<Item> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        final Item o = items.get(position);
        if (o != null) {
            TextView txtName = (TextView) v.findViewById(R.id.txtFileExplorerName);
            TextView txtData = (TextView) v.findViewById(R.id.txtFileExplorerData);
            TextView txtDate = (TextView) v.findViewById(R.id.txtFileExplorerDate);

            ImageView imageView = (ImageView) v.findViewById(R.id.imgFileExplorer);
            if(o.getImage().equalsIgnoreCase("directory_up") || o.getImage().equalsIgnoreCase("downloads_icon"))
                imageView.setPadding(5, 5, 5, 5);
            String uri = "drawable/" + o.getImage();
            int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
            Drawable image = c.getResources().getDrawable(imageResource);
            imageView.setImageDrawable(image);

            if (txtName != null)
                txtName.setText(o.getName());
            if (txtData != null)
                txtData.setText(o.getData());
            if (txtDate != null)
                txtDate.setText(o.getDate());

        }
        return v;
    }

}

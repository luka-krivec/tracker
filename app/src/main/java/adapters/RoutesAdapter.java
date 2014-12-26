package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gpslogger.CyclingRoute;
import luka.cyclingmaster.R;
import utils.DateUtilities;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private Context context;
    private int rowLayout;
    CyclingRoute[] objects;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RoutesAdapter(Context context, int rowLayout, CyclingRoute[] objects) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.objects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CyclingRoute route = objects[position];

        if(route.isFirst())
            holder.textViewDate.setText(DateUtilities.formatShortDate(route.getStartTime()));
        else
        {
            holder.textViewDate.setText("");
            holder.textViewDate.setHeight(1);
        }

        holder.textViewName.setText(route.getName());
        holder.textViewDistance.setText(String.format("%.2f km", route.getDistance() / 1000));
        holder.textViewTime.setText(DateUtilities.timeToString((route.getTime())));
        holder.textViewAvgSpeed.setText(String.format("%.2f km/h", route.getAverageSpeed()));
    }

    @Override
    public int getItemCount() {
        return objects == null ? 0 : objects.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDate;
        public TextView textViewName;
        public TextView textViewDistance;
        public TextView textViewTime;
        public TextView textViewAvgSpeed;

        public ViewHolder(View v) {
            super(v);
            textViewDate = (TextView) v.findViewById(R.id.txtRowRouteDate);
            textViewName = (TextView) v.findViewById(R.id.txtRowRouteName);
            textViewDistance = (TextView) v.findViewById(R.id.txtRowRouteDistance);
            textViewTime = (TextView) v.findViewById(R.id.txtRowRouteTime);
            textViewAvgSpeed = (TextView) v.findViewById(R.id.txtRowRouteAvgSpeed);
        }
    }
}

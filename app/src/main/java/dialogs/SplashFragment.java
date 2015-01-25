package dialogs;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import si.krivec.tracker.R;
import utils.DrawableImage;


public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.biker_girl_wallpaper_land);
            view.setBackground(new DrawableImage(getResources(), backgroundImage));
        }
        else {
            // Portrait
            Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.biker_girl_wallpaper);
            view.setBackground(new DrawableImage(getResources(), backgroundImage));
        }

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            View view = inflater.inflate(R.layout.fragment_splash, viewGroup);
            Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.biker_girl_wallpaper_land);
            view.setBackground(new DrawableImage(getResources(), backgroundImage));
        }
        else {
            // Portrait
            View view = inflater.inflate(R.layout.fragment_splash, viewGroup);
            Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.biker_girl_wallpaper);
            view.setBackground(new DrawableImage(getResources(), backgroundImage));
        }

    }
}

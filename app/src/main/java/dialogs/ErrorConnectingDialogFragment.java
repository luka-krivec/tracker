package dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.GooglePlayServicesUtil;

import si.krivec.tracker.TrackingActivity;


public class ErrorConnectingDialogFragment extends DialogFragment{

    public ErrorConnectingDialogFragment() { }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the error code and retrieve the appropriate dialog
        int errorCode = this.getArguments().getInt(TrackingActivity.DIALOG_ERROR);
        return GooglePlayServicesUtil.getErrorDialog(errorCode,
                this.getActivity(), 1);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((TrackingActivity)getActivity()).onDialogDismissed();
    }
}

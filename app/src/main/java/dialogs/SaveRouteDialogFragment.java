package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import si.krivec.tracker.R;


public class SaveRouteDialogFragment extends DialogFragment {

    private EditText mEditTextSaveRoute;

    public SaveRouteDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogSaveRoutePositiveClick(DialogFragment dialog, String routeName);
        public void onDialogSaveRouteNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_save_route, null);
        mEditTextSaveRoute = (EditText) view.findViewById(R.id.editRouteName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String routeName = mEditTextSaveRoute.getText().toString();
                        // Send the positive button event back to the host activity
                        mListener.onDialogSaveRoutePositiveClick(SaveRouteDialogFragment.this, routeName);
                    }
                })
                .setNegativeButton(R.string.btn_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SaveRouteDialogFragment.this.getDialog().cancel();
                        // Send the negative button event back to the host activity
                        mListener.onDialogSaveRouteNegativeClick(SaveRouteDialogFragment.this);
                    }
                });
        return builder.create();
    }

}


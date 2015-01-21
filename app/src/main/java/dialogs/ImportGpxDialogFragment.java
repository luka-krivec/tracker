package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import fileexplorer.FileChooser;
import si.krivec.tracker.R;


public class ImportGpxDialogFragment extends DialogFragment implements View.OnClickListener {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    private EditText editImportGpxFileName;
    private ImageButton btnBrowseFile;

    private int REQUEST_PATH = 1;
    private String curFileName;
    private String curFilePath;

    // Empty constructor must exists in DialogFragment
    public ImportGpxDialogFragment() { }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFileBrowse:
                Intent intentFileChooser = new Intent(getActivity().getApplicationContext(), FileChooser.class);
                intentFileChooser.putExtra("currentDir", curFilePath);
                startActivityForResult(intentFileChooser, REQUEST_PATH);
                break;
        }
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogImportGpxPositiveClick(DialogFragment dialog, String fileName, String currentFileName, String currentFilePath);
        public void onDialogImportGpxNegativeClick(DialogFragment dialog);
    }

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
        View view = inflater.inflate(R.layout.dialog_import_gpx, null);

        editImportGpxFileName = (EditText) view.findViewById(R.id.editFileGpxImport);
        btnBrowseFile = (ImageButton) view.findViewById(R.id.btnFileBrowse);
        btnBrowseFile.setOnClickListener(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.btn_upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String fileName = editImportGpxFileName.getText().toString();
                        // Send the positive button event back to the host activity
                        mListener.onDialogImportGpxPositiveClick(ImportGpxDialogFragment.this, fileName, curFileName, curFilePath);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImportGpxDialogFragment.this.getDialog().cancel();
                        // Send the negative button event back to the host activity
                        mListener.onDialogImportGpxNegativeClick(ImportGpxDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PATH){
            if (resultCode == Activity.RESULT_OK) {
                curFilePath = data.getStringExtra("GetPath");
                curFileName = data.getStringExtra("GetFileName");
                editImportGpxFileName.setText(curFileName);
            }
        }
    }
}

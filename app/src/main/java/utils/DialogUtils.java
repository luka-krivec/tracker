package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import luka.cyclingmaster.R;


public class DialogUtils {

    public static class CommandWrapper implements
            DialogInterface.OnClickListener {
        private Command command;

        public CommandWrapper(Command command) {
            this.command = command;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            command.execute();
        }
    }

    private static final CommandWrapper DISMISS = new CommandWrapper(Command.NO_OP);

    public static AlertDialog createWaitDialog(final Context context,
                                               final String title, final Command command) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton(R.string.yes, new CommandWrapper(command));
        builder.setNegativeButton(R.string.no, DISMISS);
        return builder.create();
    }
}
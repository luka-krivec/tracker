package utils;

/**
 * Functor object that allows us to execute arbitrary code.
 *
 * This is used in conjunction with dialog boxes to allow us to execute any
 * actions we like when a button is pressed in a dialog box (dialog boxes
 * are no longer blocking, meaning we need to register listeners for the
 * various buttons of the dialog instead of waiting for the result)
 *
 * @author NDUNN
 *
 */
public interface Command {
    public void execute();

    public static final Command NO_OP = new Command() {
        public void execute() {
        }
    };
}

package utils;

public final class Constants {

    //public static final String BACKEND_URL = "http://cyclingmaster-mobilebackend.rhcloud.com";
    public static final String BACKEND_URL = "https://live-sports-tracker.herokuapp.com";
    public static final String MONGODB_URL = "cluster0.leml1.mongodb.net";

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 2;

    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";

    // Number of points that are written in MongoDB in one API call
    public static final int INSERT_N_POINTS_WRITE_DB = 3;

    // Number of seconds between live tracking update map
    public static final int LIVE_TRACKING_UPDATE_INTERVAL = 5;

    // Android Shared Preferences name
    public static final String preferencesTracker = "TrackerPreferences";

    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }
}

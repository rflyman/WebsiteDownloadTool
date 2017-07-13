package Utils;

import java.util.Date;

public class Logger {

    private Logger() {
    }

    /**
     * Called internally to log information. This basic method just writes the
     * log out to the stderr but includes a timestamp.
     *
     * @param error_msg The information to be written to the log.
     */
    public static void err(String error_msg) {
        System.err.println((new Date()) + ":" + error_msg);
    }

    /**
     * Called internally to log information. This basic method just writes the
     * log out to the stdout but includes a timestamp.
     *
     * @param entry The information to be written to the log.
     */
    public static void log(String entry) {
        System.out.println((new Date()) + ":" + entry);
    }
}

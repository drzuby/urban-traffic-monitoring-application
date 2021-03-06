package pl.edu.agh.pp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.pp.adapters.Connector;
import pl.edu.agh.pp.exceptions.IllegalPreferenceObjectExpected;
import pl.edu.agh.pp.settings.IOptions;
import pl.edu.agh.pp.settings.Options;
import pl.edu.agh.pp.settings.PreferencesNamesHolder;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Maciej on 15.11.2016.
 * 01:12
 * Project: server.
 */
public class BaselineWindowSizeInfoHelper {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BaselineWindowSizeInfoHelper.class);
    private static final Object lock = new Object();
    private static IOptions options = Options.getInstance();
    private static int baselineWindowSize = 0;
    private static boolean updated = false;
    private static String preferenceName = PreferencesNamesHolder.BASELINE_WINDOW_SIZE;

    private BaselineWindowSizeInfoHelper() {
    }

    public static BaselineWindowSizeInfoHelper getInstance() {
        return Holder.INSTANCE;
    }

    public int getBaselineWindowSizeValue() {
        synchronized (lock) {
            if (updated)
                return baselineWindowSize;

            try {
                baselineWindowSize = ((int) options.getPreference(preferenceName, Integer.class));
            } catch (IllegalPreferenceObjectExpected illegalPreferenceObjectExpected) {
                logger.error("BaselineWindowSizeInfoHelper:  baselineWindowSize error! " +
                                "Could not getPreference BaselineWindowSize from registry!" + illegalPreferenceObjectExpected,
                        illegalPreferenceObjectExpected);
                baselineWindowSize = 0;
                logger.error("BaselineWindowSizeInfoHelper:  baselineWindowSize error! " +
                        "Assigned 0 mins to Baseline Window Size due to emergency mode!");
            }
            updated = true;
            return baselineWindowSize;
        }
    }

    public void setBaselineWindowSizeValue(int newBaselineWindowSizeValue) throws IOException {

        HashMap<String, Object> newPreference = new HashMap<>();
        newPreference.put(preferenceName, newBaselineWindowSizeValue);

        synchronized (lock) {
            updated = false;
            options.setPreferences(newPreference);
        }
        logger.info("Baseline window size has been set to <{}>", newBaselineWindowSizeValue);
        Connector.updateSystem(null);

    }

    public static class Holder {
        static final BaselineWindowSizeInfoHelper INSTANCE = new BaselineWindowSizeInfoHelper();
    }

}

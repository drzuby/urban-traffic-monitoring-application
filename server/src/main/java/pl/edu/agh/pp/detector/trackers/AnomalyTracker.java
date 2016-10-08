package pl.edu.agh.pp.detector.trackers;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.pp.detector.helpers.JodaTimeHelper;
import pl.edu.agh.pp.settings.IOptions;
import pl.edu.agh.pp.settings.Options;
import pl.edu.agh.pp.settings.exceptions.IllegalPreferenceObjectExpected;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Maciej on 05.10.2016.
 *
 * @author Maciej Makówka
 *         11:12
 *         Project: server.
 */
public final class AnomalyTracker implements IAnomalyTracker {

    // In the future, it can be replaced by the structure in which objects terminates - to make it more memory efficiently.
    private static ConcurrentHashMap<Integer, DateTime> anomalyTime = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Long> anomalyID = new ConcurrentHashMap<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(AnomalyTracker.class);
    private IOptions options = Options.getInstance();
    private Seconds liveTime;
    private Random random = new Random();

    public AnomalyTracker() {
        try {
            liveTime = Seconds.seconds((Integer) options.getPreference("AnomalyLiveTime", Integer.class));
        } catch (IllegalPreferenceObjectExpected illegalPreferenceObjectExpected) {
            logger.error("AnomalyTracker LiveTime ilegal preference object expected: " + illegalPreferenceObjectExpected);
        }
    }

    public static AnomalyTracker getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public synchronized long put(int routeID, DateTime dateTime) {

        DateTime lastAnomalyOnThisRoute = anomalyTime.get(routeID);

        if (lastAnomalyOnThisRoute == null)
            lastAnomalyOnThisRoute = JodaTimeHelper.MINIMUM_ANOMALY_DATE;

        Seconds diff = Seconds.secondsBetween(dateTime, lastAnomalyOnThisRoute);
        if (Math.abs(diff.getSeconds()) > liveTime.getSeconds()) {
            long newAnomalyID = DateTime.now().getMillis() + random.nextInt(liveTime.getSeconds());
            anomalyID.put(routeID, newAnomalyID);
        }
        anomalyTime.put(routeID, dateTime);

        return anomalyID.get(routeID);
    }

    @Override
    public long get(int routeID) {

        Long anomaly = anomalyID.get(routeID);

        if (anomaly != null)
            return anomaly;
        return -1;
    }

    @Override
    public DateTime getLastUpdate(int routeID) {

        DateTime anomaly = anomalyTime.get(routeID);

        if (anomaly != null)
            return anomaly;
        return JodaTimeHelper.MINIMUM_ANOMALY_DATE;
    }

    public static class Holder {
        static final AnomalyTracker INSTANCE = new AnomalyTracker();
    }
}
package pl.edu.agh.pp.detector.trackers;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Maciej on 05.10.2016.
 *
 * @author Maciej Makówka
 *         11:13
 *         Project: server.
 */
public interface IAnomalyTracker {

    /**
     * Puts the anomaly to the structure with current anomalies.
     *
     * @param routeID  The ID / Index of the route on which the anomaly occurred.
     * @param dateTime The Joda DateTime object with the Date and Time when the anomaly occurred.
     * @return The ID of persisted anomaly.
     */
    long put(int routeID, DateTime dateTime);

    /**
     * Get the ID of the last anomaly on the desired route.
     *
     * @param routeID The Index/ID of the route.
     * @return The ID of last anomaly on this route, or -1 if the anomaly never occurred there.
     */
    long get(int routeID);

    /**
     * Marks anomaly at give route ID as finished.
     *
     * @param routeID The Index/ID of the route.
     */
    void remove(int routeID);

    /**
     * Get the last update of anomalies for the route which ID is passed by value in argument.
     *
     * @param routeID The Index/ID of the route.
     * @return The DateTime object that represents the Date and Time of last update. If its the first
     * update then MINIMUM_ANOMALY_DATE is returned.
     * @see pl.edu.agh.pp.detector.helpers.JodaTimeHelper
     */
    DateTime getLastUpdate(int routeID);

    boolean has(int routeID);

    List<Integer> getCurrentAnomaliesRoutesIds();
}

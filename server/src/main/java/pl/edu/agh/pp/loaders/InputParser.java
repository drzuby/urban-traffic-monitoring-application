package pl.edu.agh.pp.loaders;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import pl.edu.agh.pp.utils.Record;

/**
 * Created by Maciej on 24.08.2016.
 * 20:08
 * Project: detector.
 */
public class InputParser {

    public Record parse(String buffer) {

        JSONObject json = new JSONObject(buffer);
        Record record = new Record();
        record.setRouteID(Integer.valueOf(json.getString("id")));
        record.setDistance(json.getString("distance"));
        record.setDuration(Integer.valueOf(json.getString("duration")));
        record.setDurationInTraffic(Integer.valueOf(json.getString("durationInTraffic")));
        record.setDateTime(convertStringDateToDateTime(json.getString("timeStamp")));
        record.setAnomalyID(json.getString("anomalyId"));
        if (!"default".equals(json.getString("waypoints"))) {
            record.setWaypoints("alternative");
        } else {
            record.setWaypoints(json.getString("waypoints"));
        }
        return record;
    }

    private DateTime convertStringDateToDateTime(String Date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS");
        return formatter.parseDateTime(Date);
    }
}

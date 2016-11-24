package pl.edu.agh.pp.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.pp.detectors.Detector;
import pl.edu.agh.pp.utils.enums.DayOfWeek;
import pl.edu.agh.pp.operations.AnomalyOperationProtos;

/**
 * Created by Maciej on 18.07.2016.
 * 21:36
 * Project: detector.
 */
public class BayesianPatternBuilder implements Detector {

    private final Logger logger = (Logger) LoggerFactory.getLogger(IPatternBuilder.class);

    @Override
    public AnomalyOperationProtos.AnomalyMessage isAnomaly(DayOfWeek dayOfWeek, int routeIdx, long secondOfDay, long travelDuration) {
        return null;
    }
}
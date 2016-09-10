package pl.edu.agh.pp.detector.builders;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import pl.edu.agh.pp.detector.detectors.Detector;
import pl.edu.agh.pp.detector.distributions.GaussianDistribution;
import pl.edu.agh.pp.detector.enums.DayOfWeek;
import pl.edu.agh.pp.detector.records.Record;

import java.util.*;

/**
 * Created by Maciej on 18.07.2016.
 * 21:35
 * Project: detector.
 */
public final class PolynomialPatternBuilder implements IPatternBuilder, Detector {


    // allocate memory for each day of week
    private String[] days = new String[7];
    // consider records for each day independently
    private static Map<DayOfWeek, List<Record>> recordsOfDay = new HashMap<>();
    // WeightedObservedPoint list
    private static Map<WeightedObservedPoint, DayOfWeek> points = new HashMap<>();
    // or ...
    //private Map<Integer, List<WeightedObservedPoint>> points = new HashMap<>();
//    private static PolynomialFunction polynomialFunction; // TODO:Should be list of poly function - for each day and for each route.
    private static Map<DayOfWeek, List<PolynomialFunction>> polynomialFunctions = new HashMap<>();

    public static class Holder {
        static final PolynomialPatternBuilder INSTANCE = new PolynomialPatternBuilder();
    }

    public static PolynomialPatternBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public void addRecord(DayOfWeek dayOfWeek, Record record) {
//        recordsOfDay.put(dayOfWeek, record); TODO
    }

    public Map<DayOfWeek, List<Record>> getRecordsOfDay() {
        return recordsOfDay;
    }

    public void setRecordsOfDay(Map<DayOfWeek, List<Record>> recordsOfDay) {
        PolynomialPatternBuilder.recordsOfDay = recordsOfDay;
    }

    // rather to parse point to WeightedObservedPoint
    private static void loadRecords() {
        // use loader
        // if (day == MONDAY) recordsOfDay.add(<MONDAY, RECORD>)
        String s1 = "";

        GaussianDistribution gaussian = new GaussianDistribution();
        double MEAN = 0.0f;
        double VARIANCE = 1.000f;
        Collection<WeightedObservedPoint> weightedObservedPoints = new LinkedList<>();
        for (int idx = 1; idx <= 7; ++idx) {
            System.out.println(gaussian.getGaussian(MEAN, VARIANCE));
            //weightedObservedPoints.add(new WeightedObservedPoint(1, idx, gaussian.getGaussian(MEAN, VARIANCE)));
        }

        weightedObservedPoints.add(new WeightedObservedPoint(1, 0, 500));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 14400, 501));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 28800, 898));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 36000, 720));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 46000, 690));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 57200, 907));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 57800, 898));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 59300, 917));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 72000, 625));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 82000, 540));
        weightedObservedPoints.add(new WeightedObservedPoint(1, 85000, 528));

//        points.addAll(weightedObservedPoints);
    }

    // rather to parse point to WeightedObservedPoint
    private static void loadRecords(List<Record> records) {
        // use loader
        // if (day == MONDAY) recordsOfDay.add(<MONDAY, RECORD>)
        String s1 = "";

        GaussianDistribution gaussian = new GaussianDistribution();
        double MEAN = 0.0f;
        double VARIANCE = 1.000f;

//        for (DayOfWeek day : DayOfWeek.values()){
//            List<WeightedObservedPoint> weightedObservedPoints = new ArrayList<>();
//            points.put(day, weightedObservedPoints);
//        }

        for (Record record : records) {
            WeightedObservedPoint weightedObserverPoint = new WeightedObservedPoint(1, record.getTimeInSeconds(), record.getDuration());
            points.put(weightedObserverPoint, record.getDayOfWeek());
        }


//        points.addAll(weightedObservedPoints);
    }

    // computes the value expected
    // TODO
    private static double function(DayOfWeek dayOfWeek, int routeIdx, int second) {
//        System.out.println("Oridinal: " + dayOfWeek.ordinal() + " "+ routeIdx + " " + second);
//        System.out.println(polynomialFunctions.get(dayOfWeek));
        return polynomialFunctions.get(dayOfWeek).get(routeIdx).value(second);
//        return polynomialFunctions.get(dayOfWeek.ordinal()).get(routeIdx).value(second);
//        return polynomialFunction.value(second);
    }

    // Please notice that, we don't want to put here method responsible for deciding whether the value is an anomaly or not.
    // I think so...
//    public static void computePolynomial() {
//        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(7);
//
//        loadRecords();
//
////        polynomialFunction = new PolynomialFunction(fitter.fit(points));
//        HashMap<Integer, PolynomialFunction> polynomial = new HashMap();
//        polynomial.put(10, new PolynomialFunction(fitter.fit(points))); // FIXME
//        polynomialFunctions.add(polynomial);
//        System.out.println(polynomialFunctions.get(0));
//    }

    public static void computePolynomial(List<Record> records) {
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(15);

        //////////////////////////////////////////////////
//        loadRecords(records);
//
////        polynomialFunction = new PolynomialFunction(fitter.fit(points));
//        HashMap<DayOfWeek, PolynomialFunction> polynomial = new HashMap();
//
//        polynomial.put(10, new PolynomialFunction(fitter.fit(points))); // FIXME
//        polynomialFunctions.add(polynomial);
//        System.out.println(polynomialFunctions.get(0));
        //////////////////////////////////////////////////

        List<Record> _records = new LinkedList<>();
        _records.addAll(records);

        for (DayOfWeek day : DayOfWeek.values()) {

            Map<Integer, List<WeightedObservedPoint>> weightedObservedPointsMap = new HashMap<>();

            //TODO // FIXME: 23.08.2016
            for (int i = 0; i < 100; i++) {
                weightedObservedPointsMap.put(i, new ArrayList<>());
            }
            // END TODO

            for (Record record : _records) {
                if (record.getDayOfWeek().compareTo(day) == 0) {
                    int recordRouteID = record.getRouteID();
                    List<WeightedObservedPoint> points = weightedObservedPointsMap.get(recordRouteID);
                    points.add(new WeightedObservedPoint(1, record.getTimeInSeconds(), record.getDurationInTraffic()));
                    weightedObservedPointsMap.put(recordRouteID, points);
//                    _records.remove(record);
                }
            }

            List<PolynomialFunction> polynomialFunctionRoutes = new LinkedList<>();

            for (Integer routeID : weightedObservedPointsMap.keySet()) {
                //System.out.println("DAY= " + day + " routeID " + routeID + " = " + weightedObservedPointsMap.get(routeID).size());
                if (weightedObservedPointsMap.get(routeID).size() != 0)
                    polynomialFunctionRoutes.add(new PolynomialFunction(fitter.fit(weightedObservedPointsMap.get(routeID))));
            }

            polynomialFunctions.put(day, polynomialFunctionRoutes);
        }
    }

    //TODO
    public boolean isAnomaly(DayOfWeek dayOfWeek, int routeIdx, long secondOfDay, long travelDuration) {
        double predictedTravelDuration = function(dayOfWeek, routeIdx, (int) secondOfDay);
        double bounds = 0.12;// + Math.abs(polynomialFunctions.get(dayOfWeek).get(routeIdx).polynomialDerivative().value(secondOfDay)); //%
        double errorRate = predictedTravelDuration * bounds;

        System.out.println("#####################");
        System.out.println("Error rate: " + errorRate);
        System.out.println(predictedTravelDuration - errorRate);
        System.out.println(predictedTravelDuration + errorRate);

        return (travelDuration > predictedTravelDuration + errorRate) || (travelDuration < predictedTravelDuration - errorRate);
    }

    @Deprecated
    public static double[] getValueForEachSecondOfDay(DayOfWeek dayOfWeek, int routeIdx) {
        double[] values = new double[1440];
        int idx = 0;
        for (int i = 0; i < 86400; i = i + 60) {
            double value = function(dayOfWeek, routeIdx, i);
            values[idx] = value;
            idx++;
        }
        return values;
    }
}
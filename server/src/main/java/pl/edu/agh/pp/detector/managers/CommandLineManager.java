package pl.edu.agh.pp.detector.managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import pl.edu.agh.pp.detector.builders.IPatternBuilder;
import pl.edu.agh.pp.detector.builders.PolynomialPatternBuilder;
import pl.edu.agh.pp.detector.enums.DayOfWeek;
import pl.edu.agh.pp.detector.loaders.FilesLoader;
import pl.edu.agh.pp.detector.serializers.FileBaselineSerializer;
import pl.edu.agh.pp.detector.serializers.IBaselineSerializer;

/**
 * Created by Jakub Janusz on 31.10.2016.
 * 20:35
 * server
 */
public class CommandLineManager extends Thread
{
    private static final IPatternBuilder patternBuilder = PolynomialPatternBuilder.getInstance();
    private static final IBaselineSerializer baselineSerializer = FileBaselineSerializer.getInstance();

    @Override
    public void run()
    {
        String buffer;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            try
            {
                buffer = in.readLine();
                if (buffer.startsWith("count"))
                {
                    buffer = StringUtils.removeStart(buffer, "count ");
                    String[] args = buffer.split(" ");
                    FilesLoader filesLoader = new FilesLoader(args);
                    filesLoader.processLineByLine();
                    PolynomialPatternBuilder.computePolynomial(filesLoader.getRecords(), false);
                }
                else if (buffer.startsWith("load"))
                {
                    String timestamp = StringUtils.removeStart(buffer, "load ");
                    Map<DayOfWeek, Map<Integer, PolynomialFunction>> baseline = baselineSerializer.deserialize(timestamp);
                    if (baseline != null)
                    {
                        patternBuilder.setBaseline(baseline);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
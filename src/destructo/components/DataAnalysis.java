package destructo.components;

import destructo.datastructures.CircularQueue;
import java.util.List;

import static destructo.config.Constants.*;

/**
 * Created by rnull on 6/5/15.
 */
public class DataAnalysis {

    private List<Double> historicalData;

    public DataAnalysis() {
        historicalData = new CircularQueue<Double>(MAX_QUEUE_LENGTH);
    }

    public void add(double value) {
        historicalData.add(value);
    }


}

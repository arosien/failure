package asr.failure;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * A failure detector as described in "The Phi Accrual Failure Detector" (Hayashibara, et al., 2004).
 */
public class FailureDetector
{
    private final DescriptiveStatistics samples;
    private final long                  minSamples;
    private volatile long               latestHeartbeat = -1;

    /**
     * Create a detector.
     * 
     * @param windowSize size of sampling window
     * @param minSamples minimum samples required for returning phi
     */
    public FailureDetector(int windowSize, long minSamples)
    {
        samples = new DescriptiveStatistics(windowSize);
        this.minSamples = minSamples;
    }

    /**
     * Record the signal from an external process that it is alive.
     * 
     * @param now the current time in milliseconds
     */
    public void recordHeartbeat(long now)
    {
        synchronized (samples) {
            if (latestHeartbeat != -1) {
                samples.addValue(now - latestHeartbeat);
            }

            latestHeartbeat = now;
        }
    }

    /**
     * Compute the {@link PhiMeasure}.
     * 
     * @param now
     * @return phi, null if there are not enough samples
     */
    public Double phi(long now)
    {
        if (latestHeartbeat == -1 || samples.getN() < minSamples) {
            return null;
        }

        synchronized (samples) {
            return new Double(PhiMeasure.compute(samples, now - latestHeartbeat));
        }
    }
}

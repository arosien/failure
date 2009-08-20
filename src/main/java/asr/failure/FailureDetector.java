/*
 * Copyright 2009 Adam Rosien
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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

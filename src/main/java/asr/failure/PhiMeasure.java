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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Computes the phi measure as defined in "The Phi Accrual Failure Detector" (Hayashibara, et al., 2004).
 * 
 * Phi describes the confidence of how unusual is a test sample from an external process compared to the distribution of samples. Specifically, if one chose to suspect an external
 * process when phi == 1 then there is a 10% chance the test is incorrectly considered abnormal; if phi == 2 then there is a 1% chance, etc. Hence the greater phi is the greater
 * chance there is a real discrepancy versus a random one.
 * 
 */
public class PhiMeasure
{
    /**
     * Compute phi assuming the samples have an exponential distribution.
     * 
     * @param samples
     * @param test
     * @return phi
     */
    public static double compute(DescriptiveStatistics samples, double test)
    {
        try {
            double probability = 1 - new ExponentialDistributionImpl(samples.getMean()).cumulativeProbability(test);

            return -1 * Math.log10(probability);
        }
        catch (MathException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

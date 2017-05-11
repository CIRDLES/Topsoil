/*
 * Copyright 2016 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.app.plot.variable.format;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.plot.VariableBinding;

/**
 * A collection of common variable formats and standard groupings.
 *
 * @author John Zeringue
 */
public final class VariableFormats {

    /**
     * A {@code VariableFormat} that transforms 1σ absolute uncertainties into
     * 1σ absolute uncertainties.
     */
    public static final VariableFormat<Number> ONE_SIGMA_ABSOLUTE;

    /**
     * A {@code VariableFormat} that transforms 1σ percent uncertainties into 1σ
     * absolute uncertainties.
     */
    public static final VariableFormat<Number> ONE_SIGMA_PERCENT;

    /**
     * A {@code VariableFormat} that transforms 2σ absolute uncertainties into
     * 1σ absolute uncertainties.
     */
    public static final VariableFormat<Number> TWO_SIGMA_ABSOLUTE;

    /**
     * A {@code VariableFormat} that transforms 2σ percent uncertainties into 1σ
     * absolute uncertainties.
     */
    public static final VariableFormat<Number> TWO_SIGMA_PERCENT;

    /**
     * A list of the most common uncertainty formats.
     */
    public static final List<VariableFormat<Number>> UNCERTAINTY_FORMATS;

    /*
     * DEFINITIONS
     */
    static {
        ONE_SIGMA_ABSOLUTE = new BaseVariableFormat<Number>("1σ (Abs)") {

            @Override
            public Number normalize(VariableBinding<Number> binding,
                    Entry entry) {
                return entry.get(binding.getField()).get().doubleValue();
            }

        };

        ONE_SIGMA_PERCENT = new DependentVariableFormat<Number>("1σ (%)") {

            @Override
            public Number normalize(Number variableValue,
                    Number dependencyValue) {
                return variableValue.doubleValue()
                        * dependencyValue.doubleValue() / 100;
            }

        };

        TWO_SIGMA_ABSOLUTE = new BaseVariableFormat<Number>("2σ (Abs)") {

            @Override
            public Number normalize(VariableBinding<Number> binding,
                    Entry entry) {
                return entry.get(binding.getField()).get().doubleValue() / 2;
            }

        };

        TWO_SIGMA_PERCENT = new DependentVariableFormat<Number>("2σ (%)") {

            @Override
            public Number normalize(Number variableValue,
                    Number dependencyValue) {
                return variableValue.doubleValue()
                        * dependencyValue.doubleValue() / 200;
            }

        };

        UNCERTAINTY_FORMATS = unmodifiableList(asList(
                ONE_SIGMA_PERCENT,
                TWO_SIGMA_PERCENT,
                ONE_SIGMA_ABSOLUTE,
                TWO_SIGMA_ABSOLUTE
        ));
    }

}

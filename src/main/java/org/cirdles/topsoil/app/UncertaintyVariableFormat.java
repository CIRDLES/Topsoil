/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.app;

import java.util.Objects;
import org.cirdles.topsoil.chart.DependentVariableFormat;
import static org.cirdles.topsoil.app.ExpressionType.*;

/**
 *
 * @author John Zeringue
 */
public class UncertaintyVariableFormat extends DependentVariableFormat<Number> {

    private final double errorSize;
    private final ExpressionType expressionType;

    public UncertaintyVariableFormat(double errorSize, ExpressionType expressionType) {
        super("Uncertainty");

        this.errorSize = errorSize;
        this.expressionType = expressionType;
    }

    double normalizeAbsolute(double uncertainty) {
        return uncertainty / errorSize;
    }

    double normalizePercentage(double uncertainty, double value) {
        return uncertainty / 100 * value / errorSize;
    }

    double normalize(double uncertainty, double value) {
        if (Objects.equals(expressionType, ABSOLUTE)) {
            return normalizeAbsolute(uncertainty);
        } else if (Objects.equals(expressionType, PERCENTAGE)) {
            return normalizePercentage(uncertainty, value);
        }

        throw new Error();
    }

    @Override
    public Number normalize(Number variableValue, Number dependencyValue) {
        return normalize(variableValue.doubleValue(), dependencyValue.doubleValue());
    }

}

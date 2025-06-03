/*
 * Copyright (C) 2025 Glavo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.glavo.meow.value;

import org.glavo.meow.Meow;
import org.glavo.meow.MeowContext;
import org.glavo.meow.MeowSymbolMap;
import org.glavo.meow.ast.MeowExpression;

import java.util.List;
import java.util.stream.Collectors;

import static org.glavo.meow.MeowUtils.checkArgsCount;

public record MeowLambda(
        MeowContext declarationContext,
        List<Meow> parameters,
        List<MeowExpression> body
) implements MeowFunction {

    @Override
    public String getName() {
        return "lambda@" + Integer.toHexString(System.identityHashCode(this));
    }

    @Override
    public MeowValue applyValues(MeowContext ignored, List<MeowValue> lambdaArgs) {
        checkArgsCount(lambdaArgs, parameters.size());

        MeowContext nestContext = new MeowContext(declarationContext);
        for (int i = 0; i < parameters.size(); i++) {
            nestContext.setValue(parameters.get(i), lambdaArgs.get(i));
        }

        MeowValue result = MeowUnit.UNIT;
        for (MeowExpression node : body) {
            result = node.eval(nestContext);
        }
        return result;
    }

    @Override
    public String toDebugString(MeowContext context) {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("[context=").append(context.toString()).append(", parameters=[");
        builder.append(parameters.stream().map(MeowSymbolMap.INSTANCE::get).collect(Collectors.joining(", ")));
        builder.append("], body=");
        builder.append(MeowSymbolMap.INSTANCE.toString(body));
        builder.append("]");
        return builder.toString();
    }
}

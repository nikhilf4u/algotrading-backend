package com.trading.algotrading.config;


import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import kotlin.reflect.jvm.internal.impl.resolve.constants.DoubleValue;
import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLScalarType doubleScalar() {
        return GraphQLScalarType.newScalar()
                .name("Double")
                .description("Custom scalar for handling double values")
                .coercing(new Coercing<Double, Double>() {
                    @Override
                    public Double serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof Double) {
                            return (Double) dataFetcherResult;
                        }
                        throw new CoercingSerializeException("Invalid value '" + dataFetcherResult + "' for Double");
                    }

                    @Override
                    public Double parseValue(Object input) {
                        if (input instanceof Double) {
                            return (Double) input;
                        } else if (input instanceof Integer) {
                            return ((Integer) input).doubleValue();
                        }
                        throw new CoercingParseValueException("Invalid value '" + input + "' for Double");
                    }

                    @Override
                    public Double parseLiteral(Object input) {
                        if (input instanceof DoubleValue) {
                            return ((DoubleValue) input).getValue();
                        }
                        throw new CoercingParseLiteralException("Invalid value '" + input + "' for Double");
                    }
                })
                .build();
    }

    // Other beans and configurations...
}

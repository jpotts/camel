/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.weather;

import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;

public class WeatherProducer extends DefaultProducer {

    private final String query;

    public WeatherProducer(WeatherEndpoint endpoint, String query) {
        super(endpoint);
        this.query = query;
    }

    @Override
    public WeatherEndpoint getEndpoint() {
        return (WeatherEndpoint) super.getEndpoint();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String q = query;
        String location = exchange.getIn().getHeader(WeatherConstants.WEATHER_LOCATION, String.class);
        if (location != null) {
            q = getEndpoint().getConfiguration().getQuery(location);
        }

        log.debug("Going to execute the Weather query {}", q);
        String weather = getEndpoint().getCamelContext().getTypeConverter().mandatoryConvertTo(String.class, new URL(q));
        log.debug("Got back the Weather information {}", weather);

        if (ObjectHelper.isEmpty(weather)) {
            throw new IllegalStateException("Got the unexpected value '" + weather + "' as the result of the query '" + q + "'");
        }

        exchange.getIn().setBody(weather);
        exchange.getIn().setHeader(WeatherConstants.WEATHER_QUERY, q);
    }
}

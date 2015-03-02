/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.entity.webapp.apache;


import brooklyn.enricher.Enrichers;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.webapp.PhpWebAppSoftwareProcessImpl;
import brooklyn.event.feed.function.FunctionFeed;
import brooklyn.event.feed.function.FunctionPollConfig;
import brooklyn.event.feed.http.HttpFeed;
import brooklyn.event.feed.http.HttpPollConfig;
import brooklyn.event.feed.http.HttpValueFunctions;
import brooklyn.policy.Enricher;
import brooklyn.util.guava.Functionals;
import brooklyn.event.Sensor;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class ApacheServerImpl extends PhpWebAppSoftwareProcessImpl implements ApacheServer {

    public static final Logger log = LoggerFactory.getLogger(ApacheServerImpl.class);

    private volatile FunctionFeed functionFeed;
    private volatile HttpFeed httpFeed;
    private Enricher serviceUpEnricher;


    public ApacheServerImpl() {
        super();
    }

    public ApacheServerImpl(Map flags) {
        this(flags, null);
    }

    public ApacheServerImpl(Map flags, Entity parent) {
        super(flags, parent);
    }

    @Override
    public Class getDriverInterface() {
        return ApacheDriver.class;
    }

    @Override
    public ApacheDriver getDriver() {
        return (ApacheDriver) super.getDriver();
    }

    public String getDefaultGroup() {
        return getConfig(DEFAULT_GROUP);
    }

    public void setAppUser(String appName) {
        setConfig(APP_NAME, appName);
    }

    public String getInstallDir() {
        return getConfig(ApacheServer.INSTALL_DIR);
    }

    public String getConfigurationDir() {
        return getConfig(CONFIGURATION_DIR);
    }

    public String getAvailablesSitesConfigurationFile() {
        return getConfig(AVAILABLES_SITES_CONFIGURATION_FILE);
    }

    public String getDeployRunDir() {
        return getConfig(DEPLOY_RUN_DIR);
    }

    public String getAvailableSitesConfigurationFolder() {
        return getConfig(AVAILABLE_SITES_CONFIGURATION_FOLDER);
    }

    public String getDbConnectionFileConfig() {
        return getConfig(DB_CONNECTION_FILE_CONFIG);
    }

    public Map<String, String> getDbConnectionConfigParams() {
        return getConfig(DB_CONNECTION_CONFIG_PARAMS);
    }

    public String getPhpVersion() {
        return getConfig(SUGGESTED_PHP_VERSION);
    }


    @Override
    public int getHttpPort() {
        return getAttribute(ApacheServer.HTTP_PORT);
    }


    private Function<String, String> parseApacheStatus(final String key) {
        return new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String s) {
                String result = null;
                if ((s != null) && (key != null) && (s.contains(key))) {
                    int i = s.indexOf(key) + key.length() + 1;
                    int j = s.indexOf("\n", i);
                    result = s.substring(i, j).trim();
                }
                return result;
            }
        };
    }

    private <T> Function<String, T> cast(final Class<T> expected) {
        return new Function<String, T>() {
            @Nullable
            @Override
            public T apply(@Nullable String s) {
                if (s == null) {
                    return (T) null;
                } else if (expected == long.class || expected == Long.class) {
                    return (T) (Long) Long.parseLong(s);
                } else if (expected == int.class || expected == Integer.class) {
                    return (T) (Integer) Integer.parseInt(s);
                } else if (expected == double.class || expected == Double.class) {
                    return (T) (Double) Double.parseDouble(s);
                } else {
                    return (T) (String) s;
                }
            }
        };
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();
        functionFeed = FunctionFeed.builder()
                .entity(this)
                .poll(new FunctionPollConfig<Object, Boolean>(SERVICE_UP)
                        .period(500, TimeUnit.MILLISECONDS)
                        .callable(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                return getDriver().isRunning();
                            }
                        })
                        .onException(Functions.constant(Boolean.FALSE)))
                .build();

        String monitorUri = String.format("http://%s:%s/%s",
                getAttribute(Attributes.HOSTNAME), getHttpPort(), getConfig(MONITOR_URL));
        httpFeed = HttpFeed.builder()
                .entity(this)
                .period(200)
                .baseUri(monitorUri)
                .poll(new HttpPollConfig<Boolean>(MONITOR_URL_UP)
                        .onSuccess(HttpValueFunctions.responseCodeEquals(200))
                        .onFailureOrException(Functions.constant(false)))
                .poll(new HttpPollConfig<Integer>(REQUEST_COUNT).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("Total Accesses"), cast(Integer.class))) )
                .poll(new HttpPollConfig<Long>(TOTAL_KBYTE).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("Total kBytes"), cast(Long.class))))
                .poll(new HttpPollConfig<Double>(CPU_LOAD).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("CPULoad"), cast(Double.class))))
                .poll(new HttpPollConfig<Integer>(TOTAL_PROCESSING_TIME).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("Uptime"), cast(Integer.class))))
                .poll(new HttpPollConfig<Double>(REQUEST_PER_SEC).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("ReqPerSec"), cast(Double.class))))
                .poll(new HttpPollConfig<Double>(BYTES_PER_SEC).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("BytesPerSec"), cast(Double.class))))
                .poll(new HttpPollConfig<Double>(BYTES_PER_REQ).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("BytesPerReq"), cast(Double.class))))
                .poll(new HttpPollConfig<Integer>(BUSY_WORKERS).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("BusyWorkers"), cast(Integer.class))))
                .build();
    }

    @Override
    public String getShortName() {
        return "ApacheWebServerHttpd";
    }
}

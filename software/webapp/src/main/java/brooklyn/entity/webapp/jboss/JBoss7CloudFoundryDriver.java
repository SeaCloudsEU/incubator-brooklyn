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
package brooklyn.entity.webapp.jboss;

import brooklyn.entity.basic.EntityLocal;
import brooklyn.entity.webapp.FilenameToWebContextMapper;
import brooklyn.entity.webapp.HttpsSslConfig;
import brooklyn.location.Location;

import java.io.File;
import java.util.Set;


public class JBoss7CloudFoundryDriver implements JBoss7Driver {
    @Override
    public String getSslKeystoreFile() {
        return null;
    }

    @Override
    public Set<String> getEnabledProtocols() {
        return null;
    }

    @Override
    public Integer getHttpPort() {
        return null;
    }

    @Override
    public Integer getHttpsPort() {
        return null;
    }

    @Override
    public HttpsSslConfig getHttpsSslConfig() {
        return null;
    }

    @Override
    public void deploy(File file) {

    }

    @Override
    public void deploy(File f, String targetName) {

    }

    @Override
    public String deploy(String url, String targetName) {
        return null;
    }

    @Override
    public void undeploy(String targetName) {

    }

    @Override
    public FilenameToWebContextMapper getFilenameContextMapper() {
        return null;
    }

    @Override
    public boolean isJmxEnabled() {
        return false;
    }

    @Override
    public EntityLocal getEntity() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void rebind() {

    }

    @Override
    public void start() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void kill() {

    }
}

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
package brooklyn.location.paas.cloudfoundry;

import brooklyn.config.ConfigKey;
import brooklyn.internal.storage.Reference;
import brooklyn.internal.storage.impl.BasicReference;
import brooklyn.location.Location;
import brooklyn.location.paas.AbstractPaasLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public  class CloudFoundryPaasLocation extends AbstractPaasLocation  {
    public static final Logger LOG = LoggerFactory.getLogger(CloudFoundryPaasLocation.class);
    private Reference<String> name = new BasicReference<String>();
    private InetAddress inetAddress;


    @Override
    public InetAddress getAddress() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getCatalogItemId() {
        return null;
    }

    @Override
    public TagSupport tags() {
        return null;
    }

    @Override
    public TagSupport getTagSupport() {
        return null;
    }

    @Override
    public Location getParent() {
        return null;
    }

    @Override
    public Collection<Location> getChildren() {
        return null;
    }

    @Override
    public void setParent(Location newParent) {

    }

    @Override
    public String toVerboseString() {
        return null;
    }

    @Override
    public boolean containsLocation(Location potentialDescendent) {
        return false;
    }

    @Override
    public <T> T getConfig(ConfigKey<T> key) {
        return null;
    }

    @Override
    public <T> T getConfig(ConfigKey.HasConfigKey<T> key) {
        return null;
    }

    @Override
    public boolean hasConfig(ConfigKey<?> key, boolean includeInherited) {
        return false;
    }

    @Override
    public Map<String, Object> getAllConfig(boolean includeInherited) {
        return null;
    }

    @Override
    public boolean hasExtension(Class<?> extensionType) {
        return false;
    }

    @Override
    public <T> T getExtension(Class<T> extensionType) {
        return null;
    }

    @Nullable
    @Override
    public String getHostname() {
        return null;
    }

    @Override
    public Set<String> getPublicAddresses() {
        return null;
    }

    @Override
    public Set<String> getPrivateAddresses() {
        return null;
    }
}

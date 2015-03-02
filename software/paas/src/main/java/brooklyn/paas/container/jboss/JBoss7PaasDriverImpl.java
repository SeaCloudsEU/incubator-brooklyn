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
package brooklyn.paas.container.jboss;

import brooklyn.entity.basic.AbstractSoftwareProcessPaasDriver;
import brooklyn.location.paas.AbstractPaasLocation;

public class JBoss7PaasDriverImpl extends AbstractSoftwareProcessPaasDriver implements JBoss7PaasDriver{


    public JBoss7PaasDriverImpl(JBoss7PaasContainerImpl entity, AbstractPaasLocation location) {
        super(entity, location);
    }

    @Override
    public JBoss7PaasContainerImpl getEntity() {
        return (JBoss7PaasContainerImpl) entity;
    }

    @Override
    public AbstractPaasLocation getLocation() {
        return location;
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

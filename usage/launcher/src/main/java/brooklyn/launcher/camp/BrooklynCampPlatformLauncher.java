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
package brooklyn.launcher.camp;

import io.brooklyn.camp.CampServer;
import io.brooklyn.camp.brooklyn.BrooklynCampPlatform;
import io.brooklyn.camp.brooklyn.BrooklynCampPlatformLauncherAbstract;
import io.brooklyn.camp.spi.PlatformRootSummary;
import brooklyn.entity.basic.BrooklynShutdownHooks;
import brooklyn.launcher.BrooklynLauncher;
import brooklyn.management.ManagementContext;
import brooklyn.management.internal.LocalManagementContext;

import com.google.common.annotations.Beta;

/** variant of super who also starts a CampServer for convenience */
@Beta
public class BrooklynCampPlatformLauncher extends BrooklynCampPlatformLauncherAbstract {

    protected BrooklynLauncher brooklynLauncher;
    protected CampServer campServer;

    @Override
    public BrooklynCampPlatformLauncher launch() {
        assert platform == null;

        mgmt = newManagementContext();
        
        // We created the management context, so we are responsible for terminating it
        BrooklynShutdownHooks.invokeTerminateOnShutdown(mgmt);

        brooklynLauncher = BrooklynLauncher.newInstance().managementContext(mgmt).start();
        platform = new BrooklynCampPlatform(
                PlatformRootSummary.builder().name("Brooklyn CAMP Platform").build(),
                mgmt).setConfigKeyAtManagmentContext();
        
        campServer = new CampServer(getCampPlatform(), "").start();
        
        return this;
    }
    
    protected ManagementContext newManagementContext() {
        return new LocalManagementContext();
    }

    public static void main(String[] args) {
        new BrooklynCampPlatformLauncher().launch();
    }

    public void stopServers() throws Exception {
        brooklynLauncher.getServerDetails().getWebServer().stop();
        campServer.stop();
    }
    
}

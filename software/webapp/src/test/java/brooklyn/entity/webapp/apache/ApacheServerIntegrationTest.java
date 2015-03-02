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

import brooklyn.entity.basic.ApplicationBuilder;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.webapp.HttpsSslConfig;
import brooklyn.entity.webapp.PhpWebAppSoftwareProcess;
import brooklyn.entity.webapp.jboss.JBoss7Server;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.Sensors;
import brooklyn.location.LocationSpec;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.management.LocationManager;
import brooklyn.management.ManagementContext;
import brooklyn.test.Asserts;
import brooklyn.test.HttpTestUtils;
import brooklyn.test.entity.TestApplication;
import brooklyn.util.flags.SetFromFlag;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by Jose on 18/07/2014.
 */
public class ApacheServerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ApacheServerIntegrationTest.class);


    private SshMachineLocation loc;
    private ManagementContext managementContext;
    private LocationManager locationManager;

    private TestApplication app;
    private String gitRepoURLApp="https://github.com/kiuby88/phpHelloWorld.git";
    private String tarballResourceUrl="http://seaclouds:preview@seaclouds-dev.nurogames.com/nuro-casestudy/nurogames-seaclouds-casestudy-php-early-20140711_160033CEST.tgz";

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
        managementContext = app.getManagementContext();
        locationManager = managementContext.getLocationManager();
        loc = locationManager.createLocation(LocationSpec.create(SshMachineLocation.class)
                .configure("address", "localhost"));
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        log.info("Destroy all {}", new Object[]{this});
        if (app != null)
           Entities.destroyAll(app.getManagementContext());
    }

    @Test(groups = {"Integration"})
    public void testHttpTarballResource() throws Exception {
        try {
            integrationTestHttTarballResource();
        }
        catch (Exception e){
            log.warn("Exception catched in testHttpTarballResource {} ", new Object[]{e.fillInStackTrace()});
            tearDown();
            throw e;
        }
    }

    @Test(groups = {"Integration"})
    public void testHttpGitResource() throws Exception {
        try {
            integrationTestHttGitResource();
        }
        catch (Exception e){
            log.warn("Exception catched in testHttpGitResource{} ", new Object[]{e.fillInStackTrace()});
            tearDown();
            throw e;
        }
    }

    private void integrationTestHttTarballResource() throws Exception{
        final ApacheServer server = app.createAndManageChild(EntitySpec.create(ApacheServer.class)
                .configure("app_tarball_url", tarballResourceUrl)
                .configure("http_port", "80")
                .configure(ApacheServer.ENABLED_PROTOCOLS, ImmutableSet.of("http")));

        app.start(ImmutableList.of(loc));

        String httpUrl = "http://"+server.getAttribute(ApacheServer.HOSTNAME)+":"+server.getAttribute(ApacheServer.HTTP_PORT)+"/";
//        log.info("httpURL - VALUE generate by composition: --> " + httpUrl);
//        log.info("ROOT_URL: --> " + server.getAttribute(ApacheServer.ROOT_URL).toLowerCase());
        assertEquals(server.getAttribute(ApacheServer.ROOT_URL).toLowerCase(), httpUrl.toLowerCase());
        assertEquals(server.getAttribute(PhpWebAppSoftwareProcess.DEPLOYED_PHP_APPS).size(), 1);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertNotNull(server.getAttribute(ApacheServer.TOTAL_ACCESSES));
                assertNotNull(server.getAttribute(ApacheServer.TOTAL_KBYTE));
                assertNotNull(server.getAttribute(ApacheServer.CPU_LOAD));
                assertNotNull(server.getAttribute(ApacheServer.UP_TIME));
                assertNotNull(server.getAttribute(ApacheServer.REQUEST_PER_SEC));
                assertNotNull(server.getAttribute(ApacheServer.BYTES_PER_SEC));
                assertNotNull(server.getAttribute(ApacheServer.BYTES_PER_REQ));
                assertNotNull(server.getAttribute(ApacheServer.BUSY_WORKERS));
            }
        });
    }


    private void integrationTestHttGitResource() throws Exception{
        final ApacheServer server = app.createAndManageChild(EntitySpec.create(ApacheServer.class)
                .configure("app_git_repo_url", gitRepoURLApp)
                .configure("http_port", "80")
                .configure(ApacheServer.ENABLED_PROTOCOLS, ImmutableSet.of("http")));

        app.start(ImmutableList.of(loc));

        String httpUrl = "http://"+server.getAttribute(ApacheServer.HOSTNAME)+":"+server.getAttribute(ApacheServer.HTTP_PORT)+"/";
//        log.info("httpURL - VALUE generate by composition: --> " + httpUrl);
//        log.info("ROOT_URL: --> " + server.getAttribute(ApacheServer.ROOT_URL).toLowerCase());
        assertEquals(server.getAttribute(ApacheServer.ROOT_URL).toLowerCase(), httpUrl.toLowerCase());
        assertEquals(server.getAttribute(PhpWebAppSoftwareProcess.DEPLOYED_PHP_APPS).size(), 1);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertNotNull(server.getAttribute(ApacheServer.TOTAL_ACCESSES));
                assertNotNull(server.getAttribute(ApacheServer.TOTAL_KBYTE));
                assertNotNull(server.getAttribute(ApacheServer.CPU_LOAD));
                assertNotNull(server.getAttribute(ApacheServer.UP_TIME));
                assertNotNull(server.getAttribute(ApacheServer.REQUEST_PER_SEC));
                assertNotNull(server.getAttribute(ApacheServer.BYTES_PER_SEC));
                assertNotNull(server.getAttribute(ApacheServer.BYTES_PER_REQ));
                assertNotNull(server.getAttribute(ApacheServer.BUSY_WORKERS));
            }
        });
    }

}

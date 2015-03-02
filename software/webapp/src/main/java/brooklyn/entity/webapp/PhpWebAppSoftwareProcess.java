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
package brooklyn.entity.webapp;

import brooklyn.config.ConfigKey;
import brooklyn.entity.annotation.Effector;
import brooklyn.entity.annotation.EffectorParam;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;

import java.util.Set;


@ImplementedBy(PhpWebAppSoftwareProcessImpl.class)
public interface PhpWebAppSoftwareProcess extends SoftwareProcess, PhpWebAppService {

    public static final AttributeSensor<Set<String>> DEPLOYED_PHP_APPS = new BasicAttributeSensor(
            Set.class, "webapp.deployedApps", "Names of archives/contexts that are currently deployed");
    public static final MethodEffector<Void> DEPLOY_GIT_RESOURCE = new MethodEffector<Void>(PhpWebAppSoftwareProcess.class, "deployGitResource");
    public static final MethodEffector<Void> DEPLOY_TARBALL_RESOURCE = new MethodEffector<Void>(PhpWebAppSoftwareProcess.class, "deployTarballResource");
    public static final MethodEffector<Void> UNDEPLOY = new MethodEffector<Void>(PhpWebAppSoftwareProcess.class, "undeploy");

    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "5");

    /**
     * It deploys an application which is stored in a git repository.
     * The repo is cloned using the name specified by the targetName.
     * So, the target name is used as id to deploy the application in the server.
     *
     * @param url        A url of the git repo where the application are stored. Currently, https url are supported.
     * @param targetName name of the application used to deploy it.
     */
    @Effector(description = "Deploys the given artifact, from a source URL, to a given deployment filename/context")
    public void deployGitResource(
            @EffectorParam(name = "url", description = "URL of git Repo file") String url,
            @EffectorParam(name = "targetName", description = "Application Name") String targetName);

    /**
     * It deploys an application which is packaged like a tarball from a url.
     * The application is downloaded and unpackaged in the folder specified by the targetName.
     *
     * @param url        A url of the tarball resource where the application are stored.
     * @param targetName name of the application used to deploy it.
     */
    @Effector(description = "Deploys the given artifact, from a source URL, to a given deployment filename/context")
    public void deployTarballResource(
            @EffectorParam(name = "url", description = "URL of tarball resource") String url,
            @EffectorParam(name = "targetName", description = "Application Name") String targetName);

    /**
     * For the DEPLOYED_PHP_APP to be updated, the input must match the result of the call to deploy
     */
    @Effector(description = "Undeploys the given context/artifact")
    public void undeploy(
            @EffectorParam(name = "targetName") String targetName);

}

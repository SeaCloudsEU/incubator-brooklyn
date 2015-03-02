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

import brooklyn.entity.basic.AbstractSoftwareProcessSshDriver;
import brooklyn.entity.basic.Attributes;
import brooklyn.location.basic.SshMachineLocation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class PhpWebAppSshDriver extends AbstractSoftwareProcessSshDriver implements PhpWebAppDriver {

    public PhpWebAppSshDriver(PhpWebAppSoftwareProcessImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    @Override
    public PhpWebAppSoftwareProcessImpl getEntity() {
        return (PhpWebAppSoftwareProcessImpl) super.getEntity();
    }

    protected boolean isProtocolEnabled(String protocol) {
        Set<String> protocols = getEnabledProtocols();
        for (String contender : protocols) {
            if (protocol.equalsIgnoreCase(contender)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getEnabledProtocols() {
        return entity.getAttribute(PhpWebAppSoftwareProcess.ENABLED_PROTOCOLS);
    }

    @Override
    public Integer getHttpPort() {
        return entity.getAttribute(Attributes.HTTP_PORT);
    }

    @Override
    public Integer getHttpsPort() {
        return entity.getAttribute(Attributes.HTTPS_PORT);
    }

    @Override
    public HttpsSslConfig getHttpsSslConfig() {
        return entity.getAttribute(WebAppServiceConstants.HTTPS_SSL_CONFIG);
    }

    protected Map<String, Integer> getPortMap() {
        return ImmutableMap.of("httpPort", entity.getAttribute(WebAppService.HTTP_PORT));
    }

    @Override
    public Set<Integer> getPortsUsed() {
        return ImmutableSet.<Integer>builder()
                .addAll(super.getPortsUsed())
                .addAll(getPortMap().values())
                .build();
    }

    @Override
    public SourceNameResolver getSourceNameResolver() {
        return new SourceNameResolver();
    }

    //TODO refactor this method (abstract super class)
    protected String inferRootUrl() {
        if (isProtocolEnabled("https")) {
            Integer port = getHttpsPort();
            checkNotNull(port, "HTTPS_PORT sensors not set; is an acceptable port available?");
            return String.format("https://%s:%s/", getHostname(), port);
        } else if (isProtocolEnabled("http")) {
            Integer port = getHttpPort();
            checkNotNull(port, "HTTP_PORT sensors not set; is an acceptable port available?");
            return String.format("http://%s:%s/", getHostname(), port);
        } else {
            throw new IllegalStateException("HTTP and HTTPS protocols not enabled for " + entity + "; enabled protocols are " + getEnabledProtocols());
        }
    }

    @Override
    public void postLaunch() {
        String rootUrl = inferRootUrl();
        entity.setAttribute(Attributes.MAIN_URI, URI.create(rootUrl));
        entity.setAttribute(WebAppService.ROOT_URL, rootUrl);
    }

    protected abstract String getDeploySubdir();

    protected String getDeployDir() {
        if (getDeploySubdir() == null)
            throw new IllegalStateException("no deployment directory available for " + this);
        //getRunDir is configured in SoftwareProcess
        return getRunDir() + "/" + getDeploySubdir();
    }

    @Override
    public String deployGitResource(String url, String targetName) {
        log.info("{} deploying Git Resource {} to {}:{}", new Object[]{entity, url, getHostname()});
        String deployTargetDir = getDeployDir() + targetName;
        int copyResult = copyUsingProtocol(url, deployTargetDir);
        if (copyResult != 0)
            log.warn("Problem deploying {} to {}:{} for {}: result {}", new Object[]{url, getHostname(), deployTargetDir, entity, copyResult});
        return targetName;
    }

    public String deployTarballResource(String url, String targetName) {
        log.info("{} deploying Tarball Resource{} to {}:{}", new Object[]{entity, url, getHostname()});
        String deployTargetDir = getDeployDir() + targetName + "/";
        String tarballResourceName = getSourceNameResolver().getTarballResourceNameFromUrl(url);

        //Fixme using copyResource. The proxy it is the problem.
        int copyResult = copyResourceFromUrl(url, deployTargetDir, true);

        if (copyResult != 0)
            log.warn("Problem deploying {} to {}:{} for {}: result {}", new Object[]{url, getHostname(), deployTargetDir, entity, copyResult});

        int extractResult = extractTarballResource(deployTargetDir, tarballResourceName);

        if (extractResult != 0)
            log.warn("Problem extract {} in {} result {} for {}", new Object[]{tarballResourceName, deployTargetDir, extractResult, entity});

        return targetName;
    }

    private int copyResourceFromUrl(String url, String deployTargetDir, boolean createParent) {
        if (createParent)
            createParentDir(deployTargetDir);
        String downloadCommand = String.format("wget -P %s %s", deployTargetDir, url);
        return getMachine().execCommands("download resource", ImmutableList.of(downloadCommand));
    }

    private void createParentDir(String dir) {
        int lastSlashIndex = dir.lastIndexOf("/");
        String parent = (lastSlashIndex > 0) ? dir.substring(0, lastSlashIndex) : null;
        if (parent != null) {
            getMachine().execCommands("createParentDir", ImmutableList.of("mkdir -p " + parent));
        }
    }

    private int extractTarballResource(String deployTargetDir, String tarballResourceName) {
        String extractCommand = String.format("tar xzfv %s%s -C %s", deployTargetDir, tarballResourceName, deployTargetDir);
        return getMachine().execCommands("extract tarball resource", ImmutableList.of(extractCommand));
    }

    @Override
    public void install() {
        //Fixme use the newScript to install php

        int resultOfCommand = getMachine().execCommands("install php", ImmutableList.of("sudo apt-get -y install php5"));
        if (resultOfCommand != 0) {
            log.warn("Problem installing php result {}", resultOfCommand);
        }

        resultOfCommand = getMachine().execCommands("install php-mysql", ImmutableList.of("sudo apt-get -y install php5-mysql"));
        if (resultOfCommand != 0) {
            log.warn("Problem installing php-mysql module result {}", resultOfCommand);
        }
    }

    @Override
    public void stop() {
        newScript(STOPPING).execute();
    }

    @Override
    public void undeploy(String targetName) {
        String dest = getDeployDir() + "/" + targetName;
        log.info("{} undeploying {}:{}", new Object[]{entity, getHostname(), dest});
        int result = getMachine().execCommands("removing war on undeploy", ImmutableList.of(String.format("rm -f %s", dest)));
        log.debug("{} undeployed {}:{}: result {}", new Object[]{entity, getHostname(), dest, result});
    }

}




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
package brooklyn.location.paas;

import brooklyn.location.Location;

public interface PaasContainerLocation extends Location {

    public void createApplication(String application);
    public void createService(String service);
    public void deleteService(String service);
    public void bindService(String service, String application);
    public void updateApplication(String application, String newApplication);
    public CloudInfo getConfigInfo();
    public String  startApplication(String application);
    public void stopApplication(String application);
    public String restartApplication(String application);
    public void deleteApplication(String application);

}

/*
 * Copyright 2016 The original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.clouddriver.azure.resources.servergroup.ops.converters

import com.netflix.spinnaker.clouddriver.azure.AzureOperation
import com.netflix.spinnaker.clouddriver.azure.common.AzureAtomicOperationConverterHelper
import com.netflix.spinnaker.clouddriver.azure.resources.loadbalancer.model.AzureLoadBalancer
import com.netflix.spinnaker.clouddriver.azure.resources.servergroup.model.AzureServerGroupDescription
import com.netflix.spinnaker.clouddriver.azure.resources.servergroup.ops.CreateAzureServerGroupAtomicOperation
import com.netflix.spinnaker.clouddriver.azure.resources.servergroup.ops.CreateAzureServerGroupWithAzureLoadBalancerAtomicOperation
import com.netflix.spinnaker.clouddriver.azure.resources.servergroup.ops.CreateAzureServerGroupWithoutLoadBalancersAtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperations
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
@AzureOperation(AtomicOperations.CREATE_SERVER_GROUP)
@Component("azureServerGroupDescription")
class CreateAzureServerGroupAtomicOperationConverter extends AbstractAtomicOperationsCredentialsSupport {

  AtomicOperation convertOperation(Map input) {
    AzureServerGroupDescription asgd = convertDescription(input)
    if (asgd.loadBalancerType == AzureLoadBalancer.AzureLoadBalancerType.AZURE_LOAD_BALANCER.toString()) {
      new CreateAzureServerGroupWithAzureLoadBalancerAtomicOperation(asgd)
    } else if (asgd.loadBalancerType == AzureLoadBalancer.AzureLoadBalancerType.AZURE_APPLICATION_GATEWAY.toString()) {
      new CreateAzureServerGroupAtomicOperation(asgd)
    } else if (asgd.loadBalancerType == null) {
      new CreateAzureServerGroupWithoutLoadBalancersAtomicOperation(asgd)
    } else {
      throw new RuntimeException("Cannot create Azure server group with load balancer type $asgd.loadBalancerType")
    }
  }

  AzureServerGroupDescription convertDescription(Map input) {
    AzureAtomicOperationConverterHelper.
      convertDescription(input, this, AzureServerGroupDescription) as AzureServerGroupDescription
  }
}

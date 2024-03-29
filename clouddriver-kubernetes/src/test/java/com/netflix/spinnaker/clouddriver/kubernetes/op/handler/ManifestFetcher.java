/*
 * Copyright 2020 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.netflix.spinnaker.clouddriver.kubernetes.op.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.netflix.spinnaker.clouddriver.kubernetes.description.manifest.KubernetesManifest;
import com.netflix.spinnaker.kork.annotations.NonnullByDefault;
import io.kubernetes.client.util.Yaml;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Helper class to fetch Kubernetes manifest objects stored as resources on the classpath. Only
 * intended for use in tests.
 */
@NonnullByDefault
public final class ManifestFetcher {
  static KubernetesManifest getManifest(String basePath, String overlayPath) {
    KubernetesManifest base = getManifest(basePath);
    KubernetesManifest overlay = getManifest(overlayPath);
    base.putAll(overlay);
    return base;
  }

  public static KubernetesManifest getManifest(String basePath) {
    return getManifest(ManifestFetcher.class, basePath).get(0);
  }

  public static ImmutableList<KubernetesManifest> getManifest(
      Class<?> referenceClass, String basePath) {
    ObjectMapper mapper = new ObjectMapper();
    return StreamSupport.stream(
            Yaml.getSnakeYaml().loadAll(getResource(referenceClass, basePath)).spliterator(), false)
        .filter(Objects::nonNull)
        .map(o -> mapper.convertValue(o, KubernetesManifest.class))
        .collect(ImmutableList.toImmutableList());
  }

  public static String getResource(Class<?> referenceClass, String name) {
    try {
      return Resources.toString(referenceClass.getResource(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

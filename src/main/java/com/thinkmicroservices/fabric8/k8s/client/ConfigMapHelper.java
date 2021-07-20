package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 *
 * @author cwoodward
 */
public class ConfigMapHelper {

    private final KubernetesClient client;

    /**
     *
     * @param client
     */
    public ConfigMapHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @return
     */
    public ConfigMapList getAllConfigMaps() {
        return client.configMaps().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public ConfigMapList getAllConfigMapsByNamespace(String namespace) {
        return client.configMaps().inNamespace(namespace).list();
    }

    /**
     *
     * @param namespace
     * @param configMapName
     * @param configMapData
     * @return
     */
    public ConfigMap createConfigMap(String namespace, String configMapName, Map<String, String> configMapData) {
        ConfigMap configMap = new ConfigMapBuilder()
                .withNewMetadata()
                .withName(configMapName)
                .endMetadata()
                .addToData(configMapData)
                .build();

        return client.configMaps().inNamespace(namespace).create(configMap);
    }

    /**
     *
     * @param namespace
     * @param updatedConfigMap
     * @return
     */
    public ConfigMap updateConfigMap(String namespace, ConfigMap updatedConfigMap) {

        return client.configMaps().inNamespace(namespace).createOrReplace(updatedConfigMap);
    }

    /**
     *
     * @param namespace
     * @param configMapName
     * @return
     */
    public ConfigMap getConfigMap(String namespace, String configMapName) {
        return client.configMaps().inNamespace(namespace).withName(configMapName).get();
    }

    /**
     *
     * @param namespace
     * @param configMapName
     * @param withRuntimeState
     * @return
     * @throws JsonProcessingException
     */
    public String getConfigMapYAML(String namespace, String configMapName, boolean withRuntimeState) throws JsonProcessingException {
        ConfigMap foundConfigMap = this.getConfigMap(namespace, configMapName);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundConfigMap);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundConfigMap);
        }
    }

    /**
     *
     * @param namespace
     * @param configMapName
     * @return
     */
    public boolean deleteConfigMap(String namespace, String configMapName) {
        return client.configMaps().inNamespace(namespace).withName(configMapName).delete();
    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public ConfigMap loadConfigMapFromYAML(String filename) throws FileNotFoundException {
        return client.configMaps().load(new FileInputStream(filename)).get();

    }

}

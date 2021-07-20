package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 *
 * @author cwoodward
 */
public class SecretHelper {

    private KubernetesClient client;

    /**
     *
     * @param client
     */
    public SecretHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @return
     */
    public SecretList getAllSecrets() {
        return client.secrets().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public SecretList getAllSecretsInNamespace(String namespace) {
        return client.secrets().inNamespace(namespace).list();
    }

    /**
     *
     * @param namespace
     * @param secretName
     * @return
     */
    public Secret getSecret(String namespace, String secretName) {
        return client.secrets().inNamespace(namespace).withName(secretName).get();
    }

    /**
     *
     * @param namespace
     * @param secretName
     * @param dataMap
     * @return
     */
    public Secret createSecret(String namespace, String secretName, Map<String, String> dataMap) {
        Secret secret = new SecretBuilder()
                .withNewMetadata()
                .withName(secretName)
                .endMetadata()
                .addToData(dataMap)
                .build();

        return client.secrets().inNamespace(namespace).create(secret);
    }

    /**
     *
     * @param namespace
     * @param updatedSecret
     * @return
     */
    public Secret updateSecret(String namespace, Secret updatedSecret) {
        return client.secrets().inNamespace(namespace).createOrReplace(updatedSecret);
    }

    /**
     *
     * @param namespace
     * @param secretName
     * @param withRuntimeState
     * @return
     * @throws JsonProcessingException
     */
    public String getSecretYAML(String namespace, String secretName, boolean withRuntimeState) throws JsonProcessingException {
        Secret foundSecret = this.getSecret(namespace, secretName);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundSecret);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundSecret);
        }
    }

    /**
     *
     * @param namespace
     * @param secretName
     * @return
     */
    public boolean deleteSecret(String namespace, String secretName) {
        return client.secrets().inNamespace(namespace).withName(secretName).delete();
    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public Secret loadSecretFromYAML(String filename) throws FileNotFoundException {
        return client.secrets().load(new FileInputStream(filename)).get();
    }
}

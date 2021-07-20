package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 *
 * @author cwoodward
 */
public class NamespaceHelper {

    private KubernetesClient client;

    /**
     *
     * @param client
     */
    public NamespaceHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @param namespace
     * @return
     */
    public Namespace getNamespace(String namespace) {
        return client.namespaces().withName(namespace).get();
    }

    /**
     *
     * @return
     */
    public NamespaceList getAllNamespaces() {
        return client.namespaces().list();
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public NamespaceList getNamespaceListWithLabels(String key, String value) {

        return client.namespaces().withLabel(key, value).list();
    }

    /**
     *
     * @param labelMap
     * @return
     */
    public NamespaceList getNamespaceListWithLabels(Map<String, String> labelMap) {

        return client.namespaces().withLabels(labelMap).list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public Namespace createNamespace(String namespace) {
        Namespace newNamespace = new NamespaceBuilder().withNewMetadata().withName(namespace).endMetadata().build();
        return client.namespaces().create(newNamespace);
    }

    /**
     *
     * @param namespace
     * @param labelMap
     * @return
     */
    public Namespace createNamespace(String namespace, Map<String, String> labelMap) {

        Namespace newNamespace = new NamespaceBuilder().withNewMetadata().withName(namespace).withLabels(labelMap).endMetadata().build();
        return client.namespaces().create(newNamespace);
    }

    /**
     *
     * @param namespace
     * @return
     */
    public boolean deleteNamespace(String namespace) {
        return client.namespaces().withName(namespace).delete();

    }

    /**
     *
     * @param namespace
     * @return
     */
    public String getNamespaceYAML(String namespace, boolean withRuntimeState) throws JsonProcessingException {
        Namespace foundNamespace = this.getNamespace(namespace);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundNamespace);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundNamespace);
        }

    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public Namespace loadNamespaceFromYAML(String filename) throws FileNotFoundException {
        return client.namespaces().load(new FileInputStream(filename)).get();
    }
}

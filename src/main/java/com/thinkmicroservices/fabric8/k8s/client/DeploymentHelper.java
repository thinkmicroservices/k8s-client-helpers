package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import java.util.Map;

import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author cwoodward
 */
public class DeploymentHelper {

    private KubernetesClient client;

    /**
     *
     * @param client
     */
    public DeploymentHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @return
     */
    public DeploymentList getAllDeployments() {
        return client.apps().deployments().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public DeploymentList getDeploymentsInNamespace(String namespace) {
        return client.apps().deployments().inNamespace(namespace).list();
    }

    /**
     *
     * @param namespace
     * @param deploymentName
     * @return
     */
    public Deployment getDeploymentInNamespace(String namespace, String deploymentName) {
        return client.apps().deployments().inNamespace(namespace).withName(deploymentName).get();
    }

    /**
     *
     * @param namespace
     * @param labelMap
     * @return
     */
    public DeploymentList getDeploymentsInNamespaceWithLabels(String namespace, Map<String, String> labelMap) {
        return client.apps().deployments().inNamespace(namespace).withLabels(labelMap).list();
    }

    /**
     *
     * @param namespace
     * @param deploymentName
     * @param deploymentLabelMap
     * @param replicaCount
     * @param specLabelMap
     * @param containerName
     * @param containerImage
     * @param command
     * @param selectorLabelMap
     * @return
     */
    public Deployment createDeployment(String namespace, String deploymentName, Map<String, String> deploymentLabelMap,
            int replicaCount, Map<String, String> specLabelMap, String containerName, String containerImage, String[] commands,
            Map<String, String> selectorLabelMap) {
        Deployment newDeployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentName)
                .withNamespace(namespace)
                .addToLabels(deploymentLabelMap)
                .endMetadata()
                .withNewSpec()
                .withReplicas(replicaCount)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels(specLabelMap)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(containerName)
                .withImage(containerImage)
                .withCommand(commands)
                .endContainer()
                .endSpec()
                .endTemplate()
                .withNewSelector()
                .addToMatchLabels(selectorLabelMap)
                .endSelector()
                .endSpec()
                .build();

        return client.apps().deployments().inNamespace(namespace).create(newDeployment);

    }

    /**
     *
     * @param namespace
     * @param deploymentName
     * @param containerToImageMap
     * @return
     */
    public Deployment updateDeployment(String namespace, String deploymentName, Map<String, String> containerToImageMap) {
        return client.apps().deployments().inNamespace(namespace).withName(deploymentName).rolling().updateImage(containerToImageMap);
    }

    /**
     *
     * @param namespace
     * @param deploymentName
     * @param replicaCount
     * @return
     */
    public Deployment scaleDeployment(String namespace, String deploymentName, int replicaCount) {
        return client.apps().deployments().inNamespace(namespace).withName(deploymentName).edit(
                d -> new DeploymentBuilder(d).editSpec().withReplicas(replicaCount).endSpec().build()
        );
    }

    /**
     *
     * @param namespace
     * @param deploymentName
     * @return
     */
    public boolean deleteDeployment(String namespace, String deploymentName) {
        return client.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
    }

    /**
     *
     * @param namespace
     * @param deploymentName
     * @param withRuntimeState
     * @return
     */
    public String getDeploymentYAML(String namespace, String deploymentName, boolean withRuntimeState) throws JsonProcessingException {
        Deployment foundDeployment = this.getDeploymentInNamespace(namespace, deploymentName);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundDeployment);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundDeployment);
        }
    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public Deployment loadDeploymentFromYAML(String filename) throws FileNotFoundException {
        return client.apps().deployments().load(new FileInputStream(filename)).get();
    }
}

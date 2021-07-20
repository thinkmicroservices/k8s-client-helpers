package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * @author cwoodward
 */
public class PodHelper {

    private KubernetesClient client;

    /**
     *
     * @param client
     */
    public PodHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @param namespace
     * @param name
     * @return
     */
    public Pod getPod(String namespace, String podname) {

        return client.pods().inNamespace(namespace).withName(podname).get();
    }

    /**
     *
     * @return
     */
    public PodList getAllPods() {
        return client.pods().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public PodList getAllPodsInNamespace(String namespace) {
        return client.pods().inNamespace(namespace).list();
    }

    /**
     *
     * @param namespace
     * @param key
     * @param value
     * @return
     */
    public PodList getPodsWithLabelInNamespace(String namespace, String key, String value) {
        return client.pods().inNamespace(namespace).withLabel(key, value).list();
    }

    /**
     *
     * @param namespace
     * @param labelmap
     * @return
     */
    public PodList getPodsWithLabelsInNamespace(String namespace, Map<String, String> labelmap) {
        return client.pods().inNamespace(namespace).withLabels(labelmap).list();

    }

    /**
     *
     * @param namespace
     * @param podname
     * @param containerName
     * @param imageName
     * @param port
     * @return
     */
    public Pod createPod(String namespace, String podname, String containerName, String imageName, int port) {
       
        Pod pod = new PodBuilder().withNewMetadata().withName(podname).endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(containerName)
                .withImage(imageName)
                .addNewPort()
                .withContainerPort(port)
                .endPort()
                .endContainer()
                .endSpec()
                .build();
        return updatePod(namespace,pod);
    }

  
    /**
     *
     * @param namespace
     * @param podname
     * @return
     */
    public String getPodLog(String namespace, String podname) {
        return client.pods().inNamespace(namespace).withName(podname).getLog();
    }

    /**
     *
     * @param namespace
     * @param podname
     * @param tailLineCount
     * @param outputStream
     * @return
     */
    public LogWatch watchPodLog(String namespace, String podname, int tailLineCount, OutputStream outputStream) {
        return client.pods().inNamespace(namespace).withName(podname).tailingLines(tailLineCount).watchLog(outputStream);
    }

    /**
     * 
     * @param namespace
     * @param updatePod
     * @return 
     */
    public Pod updatePod(String namespace, Pod updatePod){
        return client.pods().inNamespace(namespace).createOrReplace(updatePod);
    }
    /**
     *
     * @param namespace
     * @param podname
     * @return
     */
    public boolean deletePod(String namespace, String podname) {
        return client.pods().inNamespace(namespace).withName(podname).delete();
    }
    
    /**
     * 
     * @param namespace
     * @param podname
     * @param withRuntimeState
     * @return
     * @throws JsonProcessingException 
     */
    public String getPodYAML(String namespace, String podname, boolean withRuntimeState) throws JsonProcessingException {
        Pod foundPod = this.getPod(namespace, podname);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundPod);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundPod);
        }
    }
    
    /**
     * 
     * @param filename
     * @return
     * @throws FileNotFoundException 
     */
    public Pod loadPodFromYAML(String filename) throws FileNotFoundException{
        return client.pods().load(new FileInputStream(filename)).get();
    }
    
}

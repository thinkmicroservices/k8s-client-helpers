package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 *
 * @author cwoodward
 */
public class JobHelper {

    private KubernetesClient client;

    private static final String API_BATCH_VERSION_V1 = "batch/v1";

    /**
     *
     * @param client
     */
    public JobHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @return
     */
    public JobList getAllJobs() {
        return client.batch().jobs().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public JobList getJobsInNamespace(String namespace) {
        return client.batch().jobs().inNamespace(namespace).list();
    }

    /**
     *
     * @param namespace
     * @param labelMap
     * @return
     */
    public JobList getJobsInNamespaceWithLabels(String namespace, Map<String, String> labelMap) {
        return client.batch().jobs().inNamespace(namespace).withLabels(labelMap).list();
    }

    /**
     *
     * @param namespace
     * @param jobName
     * @return
     */
    public Job getJob(String namespace, String jobName) {
        return client.batch().jobs().inNamespace(namespace).withName(jobName).get();
    }

    /**
     *
     * @param namespace
     * @param jobName
     * @param labelMap
     * @param annotationMap
     * @param containerName
     * @param containerImage
     * @param containerArgs
     * @param restartPolicy
     * @return
     */
    public Job createJob(String namespace, String jobName,
            Map<String, String> labelMap,
            Map<String, String> annotationMap, 
            String containerName, String containerImage,
            String[] containerArgs, RestartPolicy restartPolicy
    ) {
        
        
        Job job = new JobBuilder()
                .withApiVersion(API_BATCH_VERSION_V1)
                .withNewMetadata()
                .withName(jobName)
                .withLabels(labelMap)
                .withAnnotations(annotationMap)
                .endMetadata()
                .withNewSpec()
                .withNewTemplate()
                .withNewSpec()
                .addNewContainer()
                .withName(containerName)
                .withImage(containerImage)
                .withArgs(containerArgs)
                .endContainer()
                .withRestartPolicy(restartPolicy.getPolicy())
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        return client.batch().jobs().inNamespace(namespace).create(job);
    }
    
    /**
     * 
     * @param namespace
     * @param updateJob
     * @return 
     */
    public Job updateJob(String namespace, Job updateJob){
        return client.batch().jobs().inNamespace(namespace).createOrReplace(updateJob);
    }
    
    /**
     * 
     * @param namespace
     * @param jobName
     * @param withRuntimeState
     * @return
     * @throws JsonProcessingException 
     */
    public String getJobYAML(String namespace, String jobName, boolean withRuntimeState) throws JsonProcessingException {
        Job foundJob = this.getJob(namespace, jobName);
        if(withRuntimeState){
            return SerializationUtils.dumpAsYaml(foundJob);
        }else{
          return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundJob);  
        }
    }
    /**
     * 
     * @param namespace
     * @param jobName
     * @return 
     */
    public boolean deleteJob(String namespace, String jobName){
        return client.batch().jobs().inNamespace(namespace).withName(jobName).delete();
    }
    

    /**
     * 
     * @param filename
     * @return
     * @throws FileNotFoundException 
     */
    public Job loadJobFromYAML(String filename) throws FileNotFoundException {
        return client.batch().jobs().load(new FileInputStream(filename)).get();
    
    }

}

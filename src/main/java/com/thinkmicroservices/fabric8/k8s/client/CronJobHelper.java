package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.batch.v1beta1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1beta1.CronJobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1beta1.CronJobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 *
 * @author cwoodward
 */
public class CronJobHelper {

    private KubernetesClient client;
    private static final String API_BATCH_VERSION_V1 = "batch/v1";

    /**
     *
     * @param client
     */
    public CronJobHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @return
     */
    public CronJobList getAllCronJobs() {
        return client.batch().cronjobs().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public CronJobList getCronJobsInNamespace(String namespace) {
        return client.batch().cronjobs().inNamespace(namespace).list();
    }

    /**
     *
     * @param namespace
     * @param labelMap
     * @return
     */
    public CronJobList getCronJobsInNamespaceWithLabels(String namespace, Map<String, String> labelMap) {
        return client.batch().cronjobs().inNamespace(namespace).withLabels(labelMap).list();
    }

    /**
     *
     * @param namespace
     * @param cronjobName
     * @return
     */
    public CronJob getCronJob(String namespace, String cronjobName) {
        return client.batch().cronjobs().inNamespace(namespace).withName(cronjobName).get();
    }

    /**
     *
     * @param namespace
     * @param cronJobName
     * @param labelMap
     * @param cronScheduleString
     * @param containerName
     * @param conbtainerImage
     * @param containerArgs
     * @param restartPolicy
     * @return
     */
    public CronJob createCronJob(String namespace, String cronJobName,
            Map<String, String> labelMap, String cronScheduleString,
            String containerName, String containerImage, String[] containerArgs, RestartPolicy restartPolicy) {
        CronJob cronJob1 = new CronJobBuilder()
                .withApiVersion(API_BATCH_VERSION_V1)
                .withNewMetadata()
                .withName(cronJobName)
                .withLabels(labelMap)
                .endMetadata()
                .withNewSpec()
                .withSchedule(cronScheduleString)
                .withNewJobTemplate()
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
                .endJobTemplate()
                .endSpec()
                .build();

        return client.batch().cronjobs().inNamespace(namespace).create(cronJob1);
    }

    /**
     *
     * @param namespace
     * @param updateCronJob
     * @return
     */
    public CronJob updateJob(String namespace, CronJob updateCronJob) {
        return client.batch().cronjobs().inNamespace(namespace).createOrReplace(updateCronJob);
    }

    /**
     *
     * @param namespace
     * @param cronJobName
     * @param withRuntimeState
     * @return
     * @throws JsonProcessingException
     */
    public String getCronJobYAML(String namespace, String cronJobName, boolean withRuntimeState) throws JsonProcessingException {
        CronJob foundCronJob = this.getCronJob(namespace, cronJobName);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundCronJob);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundCronJob);
        }
    }

    /**
     *
     * @param namespace
     * @param cronJobName
     * @return
     */
    public boolean deleteCronJob(String namespace, String cronJobName) {
        return client.batch().cronjobs().inNamespace(namespace).withName(cronJobName).delete();
    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public CronJob loadCronJobFromYAML(String filename) throws FileNotFoundException {
        return client.batch().cronjobs().load(new FileInputStream(filename)).get();

    }
}

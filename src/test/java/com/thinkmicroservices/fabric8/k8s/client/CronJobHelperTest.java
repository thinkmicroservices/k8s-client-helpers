package com.thinkmicroservices.fabric8.k8s.client;

import io.fabric8.kubernetes.api.model.batch.v1beta1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1beta1.CronJobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import java.util.Map;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author cwoodward
 */
@EnableKubernetesMockClient(crud = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CronJobHelperTest {

    static KubernetesClient client;
    private CronJobHelper cronJobHelper;

    private static final String CRONJOB_TEST_NAMESPACE = "cronjob-test-namespace";
    private static final String CRONJOB_TEST_NAME = "hello";
    private static final Map<String, String> CRONJOB_TEST_LABELS_MAP = Map.of("label1", "maximum-length-of-63-characters");
    private static final String CRONJOB_TEST_SCHEDULE_STRING_1 = "*/1 * * * *";
    private static final String CRONJOB_TEST_SCHEDULE_STRING_2 = "*/5 * * * *";
    private static final String CRONJOB_TEST_CONTAINER_IMAGE = "busybox";
    private static final String CRONJOB_TEST_CONTAINER_NAME = "hello";
    private static final String[] CRONJOB_TEST_CONTAINER_ARGS = {"/bin/sh", "-c", "date; echo Hello from Kubernetes"};
    private static final RestartPolicy CRONJOB_TEST_RESTART_POLICY = RestartPolicy.NEVER;

    private static final String CRONJOB_EXAMPLE_YAML = "./yaml/cronjob-example.yaml";

    /**
     *
     */
    public CronJobHelperTest() {
        cronJobHelper = new CronJobHelper(client);
    }

    @Test
    @DisplayName("Ensure no cronjobs in namespace")
    @Order(1)
    public void testNoJobsInNamespace() {
        CronJobList cronjobList = cronJobHelper.getCronJobsInNamespace(CRONJOB_TEST_NAMESPACE);
        assertNotNull(cronjobList, "cronjobList should not be null");
        assertTrue(cronjobList.getItems().isEmpty(), "job list is not empty");
    }

    @Test
    @DisplayName("create cronjob in namespace")
    @Order(2)
    public void testCreateCronJob() {
        CronJob newCronJob = cronJobHelper.createCronJob(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME,
                CRONJOB_TEST_LABELS_MAP,
                CRONJOB_TEST_SCHEDULE_STRING_1, CRONJOB_TEST_CONTAINER_NAME, CRONJOB_TEST_CONTAINER_IMAGE,
                CRONJOB_TEST_CONTAINER_ARGS, CRONJOB_TEST_RESTART_POLICY);
        assertNotNull(newCronJob, "new cronjob should not be null");
        assertEquals(CRONJOB_TEST_NAME, newCronJob.getMetadata().getName(), "New CronJob name doesnt match");
        assertEquals(CRONJOB_TEST_SCHEDULE_STRING_1, newCronJob.getSpec().getSchedule(), "CronJob schedule does not match");
    }

    @Test
    @DisplayName("get the newly created cronjob")
    @Order(3)
    public void testGetCronJob() {
        CronJob foundCronJob = cronJobHelper.getCronJob(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME);
        assertNotNull(foundCronJob, "cron job should not be null");
        assertEquals(CRONJOB_TEST_NAME, foundCronJob.getMetadata().getName(), "CronJob name doesn't match");
    }

    @Test
    @DisplayName("update the cronjob")
    @Order(4)
    public void testUpdateCronJob() {
        CronJob foundCronJob = cronJobHelper.getCronJob(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME);
        assertNotNull(foundCronJob, "cronjob should not be null");
        assertEquals(CRONJOB_TEST_NAME, foundCronJob.getMetadata().getName(), "CronJob name doesn't match");
        assertEquals(CRONJOB_TEST_SCHEDULE_STRING_1, foundCronJob.getSpec().getSchedule(), "CronJob schedule does not match");

        // change the cron job schedule
        foundCronJob.getSpec().setSchedule(CRONJOB_TEST_SCHEDULE_STRING_2);
        CronJob updatedCronJob = cronJobHelper.updateJob(CRONJOB_TEST_NAMESPACE, foundCronJob);
        assertNotNull(updatedCronJob, "updated cron job should not be null");
        // check the updated cron job schedule
        assertEquals(CRONJOB_TEST_SCHEDULE_STRING_2, foundCronJob.getSpec().getSchedule(), "CronJob schedule does not match");

    }

    @Test
    @DisplayName("get all cronjobs")
    @Order(5)
    public void testGetAllCronJobs() {
        CronJobList cronjobList = cronJobHelper.getAllCronJobs();
        assertNotNull(cronjobList, "CronJob list should not be null");
        assertTrue(cronjobList.getItems().size() > 0, "cronjob list size should not be null");

    }

    @Test
    @DisplayName("Get cronjob YAML with state")
    @Order(6)
    public void testGetCronJobYAMLWithState() throws Exception {
        String cronJobYAML = cronJobHelper.getCronJobYAML(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME, true);
        assertNotNull(cronJobYAML, "CronJob YAML should not be null");
        System.out.println("CronJob YAML:" + cronJobYAML);
    }

    @Test
    @DisplayName("Get cronjob YAML without state")
    @Order(7)
    public void testGetCronJobYAMLWithoutState() throws Exception {
        String cronJobYAML = cronJobHelper.getCronJobYAML(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME, true);
        assertNotNull(cronJobYAML, "CronJob YAML should not be null");
        System.out.println("CronJob YAML:" + cronJobYAML);
    }

    @Test
    @DisplayName("Delete Cron job")
    @Order(8)
    public void testDeleteCronJob() throws Exception {
        assertTrue(cronJobHelper.deleteCronJob(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME), "deleteCronJob() method failed");
        CronJob deletedCronJob = cronJobHelper.getCronJob(CRONJOB_TEST_NAMESPACE, CRONJOB_TEST_NAME);
        assertNull(deletedCronJob, "deleted job SHOULD BE NULL");
    }

    @Test
    @DisplayName("load job from YAML")
    @Order(9)
    public void testLoadJobFromYaml() throws Exception {
        CronJob loadedCronJob = cronJobHelper.loadCronJobFromYAML(CRONJOB_EXAMPLE_YAML);
        assertNotNull(loadedCronJob, "Loaded cron job should not be null");
        assertEquals(CRONJOB_TEST_NAME, loadedCronJob.getMetadata().getName(), "Job Name does not match");
        assertEquals(CRONJOB_TEST_SCHEDULE_STRING_2, loadedCronJob.getSpec().getSchedule(), "schedule doesnt match");
    }
}

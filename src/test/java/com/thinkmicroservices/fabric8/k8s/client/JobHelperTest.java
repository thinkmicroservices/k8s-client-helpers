package com.thinkmicroservices.fabric8.k8s.client;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
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
public class JobHelperTest {

    static KubernetesClient client;

    private static final String JOB_TEST_NAMESPACE = "job-test-namespace";
    private static final String JOB_TEST_NAME = "pi";
    private static final String JOB_TEST_CONTAINER_IMAGE = "perl";
    private static final String JOB_TEST_CONTAINER_NAME = "pi";
    private static final Map<String, String> JOB_TEST_LABELS_MAP = Map.of("label1", "maximum-length-of-63-characters");
    private static final Map<String, String> JOB_TEST_ANNOTATIONS_MAP = Map.of("annotation1", "some-very-long-annotation");
    private static final String[] JOB_TEST_CONTAINER_ARGUMENTS = {"perl", "-Mbignum=bpi", "-wle", "print bpi(2000)"};
    private static final RestartPolicy JOB_TEST_RESTART_POLICY_1 = RestartPolicy.NEVER;
    private static final RestartPolicy JOB_TEST_RESTART_POLICY_2 = RestartPolicy.ON_FAILURE;
    
    private static final String JOB_EXAMPLE_YAML = "./yaml/job-example.yaml";

    
    private JobHelper jobHelper;

    public JobHelperTest() {
        jobHelper = new JobHelper(client);
    }

    @Test
    @DisplayName("Ensure no jobs in namespace")
    @Order(1)
    public void testNoJobsInNamespace() {
        JobList jobList = jobHelper.getJobsInNamespace(JOB_TEST_NAMESPACE);
        assertNotNull(jobList, "job list should not be null");
        assertTrue(jobList.getItems().isEmpty(), "job list is not empty");
    }

    @Test
    @DisplayName("create job in namespace")
    @Order(2)
    public void testCreateJob() {
        Job newJob = jobHelper.createJob(JOB_TEST_NAMESPACE, JOB_TEST_NAME, JOB_TEST_LABELS_MAP,
                JOB_TEST_ANNOTATIONS_MAP, JOB_TEST_CONTAINER_NAME, JOB_TEST_CONTAINER_IMAGE, JOB_TEST_CONTAINER_ARGUMENTS,
                JOB_TEST_RESTART_POLICY_1);

        assertNotNull(newJob, "new job should not be null");
        assertEquals(JOB_TEST_NAME, newJob.getMetadata().getName(), "new job names does not match");
        assertEquals(JOB_TEST_RESTART_POLICY_1.getPolicy(), newJob.getSpec().getTemplate().getSpec().getRestartPolicy(), "Restart Policy doesnt match");
    }

    @Test
    @DisplayName("get the newly created job")
    @Order(3)
    public void testGetJob() {
        Job foundJob = jobHelper.getJob(JOB_TEST_NAMESPACE, JOB_TEST_NAME);
        assertNotNull(foundJob, "job should not be null");
        assertEquals(JOB_TEST_NAME, foundJob.getMetadata().getName(), "Job name doesn't match");
    }

    @Test
    @DisplayName("update the job")
    @Order(4)
    public void testUpdateJob() {
        Job foundJob = jobHelper.getJob(JOB_TEST_NAMESPACE, JOB_TEST_NAME);
        assertNotNull(foundJob, "job should not be null");
        assertEquals(JOB_TEST_NAME, foundJob.getMetadata().getName(), "Job name doesn't match");
        // check the updated job restart policy
        assertEquals(JOB_TEST_RESTART_POLICY_1.getPolicy(), foundJob.getSpec().getTemplate().getSpec().getRestartPolicy(), "Restart Policy doesnt match");

        // change the restart policy
        foundJob.getSpec().getTemplate().getSpec().setRestartPolicy(JOB_TEST_RESTART_POLICY_2.getPolicy());
        Job updatedJob = jobHelper.updateJob(JOB_TEST_NAMESPACE, foundJob);
        assertNotNull(updatedJob, "updated job should not be null");
        // check the updated job restart policy
        assertEquals(JOB_TEST_RESTART_POLICY_2.getPolicy(), updatedJob.getSpec().getTemplate().getSpec().getRestartPolicy(), "Restart Policy doesnt match");

    }

    @Test
    @DisplayName("get all jobs")
    @Order(5)
    public void testGetAllJobs() {
        JobList jobList = jobHelper.getAllJobs();
        assertNotNull(jobList, "Job list should not be null");
        assertTrue(jobList.getItems().size() > 0, "job list size should not be null");

    }

    @Test
    @DisplayName("Get job YAML with state")
    @Order(6)
    public void testGetJobYAMLWithState() throws Exception {
        String jobYAML = jobHelper.getJobYAML(JOB_TEST_NAMESPACE, JOB_TEST_NAME, true);
        assertNotNull(jobYAML, "Job YAML should not be null");
        System.out.println("Job YAML:" + jobYAML);
    }

    @Test
    @DisplayName("Get job YAML without state")
    @Order(7)
    public void testGetJobYAMLWithoutState() throws Exception {
        String jobYAML = jobHelper.getJobYAML(JOB_TEST_NAMESPACE, JOB_TEST_NAME, false);
        assertNotNull(jobYAML, "Job YAML should not be null");
        System.out.println("Job YAML:" + jobYAML);
    }
    
    @Test
    @DisplayName("Delete job")
    @Order(8)
    public void testDeleteJob() throws Exception {
        assertTrue(jobHelper.deleteJob(JOB_TEST_NAMESPACE, JOB_TEST_NAME),"deleteJob() method failed");
        Job deletedJob = jobHelper.getJob(JOB_TEST_NAMESPACE, JOB_TEST_NAME);
        assertNull(deletedJob, "deleted job SHOULD BE NULL");
    }
    
    @Test
    @DisplayName("load job from YAML")
    @Order(9)
    public void testLoadJobFromYaml() throws Exception {
        Job loadedJob = jobHelper.loadJobFromYAML(JOB_EXAMPLE_YAML);
        assertNotNull(loadedJob,"Loaded job should not be null");
        assertEquals(JOB_TEST_NAME,loadedJob.getMetadata().getName(),"Job Name does not match");
        assertEquals(JOB_TEST_RESTART_POLICY_1.getPolicy(),loadedJob.getSpec().getTemplate().getSpec().getRestartPolicy(),"restart policy doesnt match");
    }
    
}

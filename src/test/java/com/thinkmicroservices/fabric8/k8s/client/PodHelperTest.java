package com.thinkmicroservices.fabric8.k8s.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author cwoodward
 */
@EnableKubernetesMockClient(crud = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PodHelperTest {

    private static final String POD_TEST_NAMESPACE = "pod-test-namespace";
    private static final String POD_TEST_NAME = "pod-test";
    private static final String POD_TEST_CONTAINER_NAME = "nginx";
    private static final String POD_TEST_IMAGE_NAME = "nginx:1.7.9";
    private static final int POD_TEST_PORT = 80;
    private static final String POD_EXAMPLE_YAML = "./yaml/pod-example.yaml";
    private static final String POD_LABEL_KEY_1 = "alpha";
    private static final String POD_LABEL_VALUE_1 = "beta";
    private static final String POD_LABEL_KEY_2 = "delta";
    private static final String POD_LABEL_VALUE_2 = "gamma";
    static KubernetesClient client;

    private PodHelper podHelper;

    /**
     *
     */
    public PodHelperTest() {
        podHelper = new PodHelper(client);
    }

    @Test
    @DisplayName("Ensure no pods in namespace")
    @Order(1)
    public void testNoPodsInNamespace() {
        PodList podList = podHelper.getAllPodsInNamespace(POD_TEST_NAMESPACE);
        assertNotNull(podList, "pod list should not be null");
        assertTrue(podList.getItems().isEmpty(), "podlist is not empty");
    }

    /**
     * Test of createPod method, of class PodHelper.
     */
    @Test
    @DisplayName("create a new pod in a specific namespace")
    @Order(2)
    public void testCreatePod() {
        Pod newPod = podHelper.createPod(POD_TEST_NAMESPACE, POD_TEST_NAME, POD_TEST_CONTAINER_NAME, POD_TEST_IMAGE_NAME, POD_TEST_PORT);
        assertNotNull(newPod);
        assertEquals(POD_TEST_NAME, newPod.getMetadata().getName(), "Pod name does not match");
    }

    /**
     * Test of getPod method, of class PodHelper.
     */
    @Test
    @DisplayName("get the newly created pod")
    @Order(3)
    public void testGetPod() {
        Pod foundPod = podHelper.getPod(POD_TEST_NAMESPACE, POD_TEST_NAME);
        assertNotNull(foundPod,"pod should not be null");
        assertEquals(POD_TEST_NAME, foundPod.getMetadata().getName(), "Pod name does not match");
    }

    /**
     * Test of getPodLog method, of class PodHelper.
     */
    @Test
    @DisplayName("get pod log")
    @Order(4)
    public void testGetPodLog() {
        String logOutput = podHelper.getPodLog(POD_TEST_NAMESPACE, POD_TEST_NAME);
        assertNotNull(logOutput);
        System.out.println("pod log:" + logOutput);
        assertTrue(logOutput.length() > 0, "pod log output shloud not be empty");
    }

    /**
     * Test of deletePod method, of class PodHelper.
     */
    @Test
    @DisplayName("delete the newly created pod")
    @Order(5)
    public void testDeletePod() {
        assertTrue(podHelper.deletePod(POD_TEST_NAMESPACE, POD_TEST_NAME));
    }

    /**
     * Test of updatePod method, of class PodHelper.
     */
    @Test
    @DisplayName("create and update pod in a specific namespace")
    @Order(6)
    public void testUpdatePod() {
        Pod newPod = podHelper.createPod(POD_TEST_NAMESPACE, POD_TEST_NAME, POD_TEST_CONTAINER_NAME, POD_TEST_IMAGE_NAME, POD_TEST_PORT);
        assertNotNull(newPod);
        assertEquals(POD_TEST_NAME, newPod.getMetadata().getName(), "Pod name does not match");
        Pod updatedPod = podHelper.updatePod(POD_TEST_NAMESPACE, newPod);
        assertNotNull(updatedPod);
        assertEquals(POD_TEST_NAME, updatedPod.getMetadata().getName(), "Pod name does not match");
    }

    
     
    /**
     * Test of getAllPods method, of class PodHelper.
     */
    @Test
    @DisplayName("get all pods")
    @Order(7)
    public void testGetAllPods() {
        PodList podList = podHelper.getAllPods();
        assertNotNull(podList, "Pod list should not be null");
        assertTrue(podList.getItems().size() > 0, "podlist size should not be 0");
     
    }

    /**
     * Test of getAllPodsInNamespace method, of class PodHelper.
     */
    @Test
    @DisplayName("get app pods in namespace")
    @Order(8)
    public void testGetAllPodsInNamespace() {
        PodList podList = podHelper.getAllPodsInNamespace(POD_TEST_NAMESPACE);
        assertTrue(podList.getItems().size() > 0, "podlist size should not be 0");
        System.out.println("podlist:namespace" + POD_TEST_NAMESPACE + ",pods:" + podList);

    }

     /**
     * Test of getPodYAML with state method, of class PodHelper.
     */
    @Test
    @DisplayName("Get pod YAML with state")
    @Order(9)
    public void testGetPodYAMLWithState() throws Exception {
        String podYAML = podHelper.getPodYAML(POD_TEST_NAMESPACE, POD_TEST_NAME, true);
        assertNotNull(podYAML, "Pod YAML should not be null");
        System.out.println("Pod YAML:" + podYAML);
    }

    /**
     * Test of getPodYAML without state method, of class PodHelper.
     */
    @Test
    @DisplayName("Get pod YAML without state")
    @Order(10)
    public void testGetPodYAMLWithoutState() throws Exception {
        String podYAML = podHelper.getPodYAML(POD_TEST_NAMESPACE, POD_TEST_NAME, false);
        assertNotNull(podYAML, "Pod YAML should not be null");
        System.out.println("PD YAML:" + podYAML);
    }

    /**
     * Test of loadPodFromYAML method, of class PodHelper.
     */
    @Test
    @DisplayName("Load pod from YAML")
    @Order(11)
    public void testLoadPodFromYAML() throws Exception {
        Pod loadedPod = podHelper.loadPodFromYAML(POD_EXAMPLE_YAML);
        assertNotNull(loadedPod, "Loaded pod should not be null");
        assertEquals("pod-test", loadedPod.getMetadata().getName(), "Pod name doesn't match");
    }
    
    

}

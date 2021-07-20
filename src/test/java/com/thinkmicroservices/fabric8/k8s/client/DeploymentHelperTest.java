package com.thinkmicroservices.fabric8.k8s.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
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
public class DeploymentHelperTest {

    private static final String DEPLOYMENT_EXAMPLE_YAML = "./yaml/deployment-example.yaml";
    private static final String TEST_DEPLOYMENT_NAMESPACE = "test-deployment-namespace";
    private static final String TEST_DEPLOYMENT_NAME = "test-deployment-name";
    private static final String TEST_DEPLOYMENT_SELECTOR_VALUE = "httpd";
    private static final String TEST_DEPLOYMENT_SELECTOR_KEY = "app";
    private static final String TEST_DEPLOYMENT_CONTAINER_COMMAND_2 = "36000";
    private static final String TEST_DEPLOYMENT_CONTAINER_COMMAND_1 = "sleep";
    private static final String TEST_DEPLOYMENT_CONTAINER_IMAGE = "busybox";
    private static final String TEST_DEPLOYMENT_CONTAINER_NAME = "busybox";
    private static final String TEST_DEPLOYMENT_SPEC_LABEL_VALUE = "httpd";
    private static final String TEST_DEPLOYMENT_SPEC_LABEL_KEY = "app";
    private static final int TEST_DEPLOYMENT_REPLICA_COUNT = 1;
    private static final String TEST_DEPLOYMENT_LABEL_VALUE = "deployment";
    private static final String TEST_DEPLOYMENT_LABEL_KEY = "test";
    static KubernetesClient client;
    private DeploymentHelper deploymentHelper;

   /**
    * 
    */
    public DeploymentHelperTest() {
        deploymentHelper = new DeploymentHelper(client);
    }

    /**
     * Test of getAllDeployments method, of class DeploymentHelper.
     */
    @Test
    @DisplayName("Ensure no deployments are in the namespace")
    @Order(1)
    public void testGetAllDeployments() {
        DeploymentList deploymentList = deploymentHelper.getAllDeployments();
        assertNotNull(deploymentList, "deployment list should not be null");
        assertTrue(deploymentList.getItems().size() == 0, "deployment list should be empty");
    }

    /**
     * Test of createDeployment method, of class DeploymentHelper.
     */
    @Test
    @DisplayName("Create a new deploymentthe namespace")
    @Order(2)
    public void testCreateDeployment() {

        Deployment newDeployment = deploymentHelper.createDeployment(TEST_DEPLOYMENT_NAMESPACE, TEST_DEPLOYMENT_NAME,
                Map.of(TEST_DEPLOYMENT_LABEL_KEY, TEST_DEPLOYMENT_LABEL_VALUE), TEST_DEPLOYMENT_REPLICA_COUNT,
                Map.of(TEST_DEPLOYMENT_SPEC_LABEL_KEY, TEST_DEPLOYMENT_SPEC_LABEL_VALUE), TEST_DEPLOYMENT_CONTAINER_NAME, TEST_DEPLOYMENT_CONTAINER_IMAGE,
                new String[]{TEST_DEPLOYMENT_CONTAINER_COMMAND_1, TEST_DEPLOYMENT_CONTAINER_COMMAND_2},
                Map.of(TEST_DEPLOYMENT_SELECTOR_KEY, TEST_DEPLOYMENT_SELECTOR_VALUE));

        assertNotNull(newDeployment);

    }

    /**
     * Test of getDeploymentsInNamespace method, of class DeploymentHelper.
     */
    @Test
    @DisplayName("get all deployments in the namespace")
    @Order(3)
    public void testGetDeploymentsInNamespace() {
        DeploymentList deploymentList = deploymentHelper.getDeploymentsInNamespace(TEST_DEPLOYMENT_NAMESPACE);
        assertNotNull(deploymentList, "Deployment list should not be null");
        assertTrue(deploymentList.getItems().size() == 1, "Deployment list should contain 1 deployment");
        Deployment deployment = deploymentList.getItems().get(0);
        assertEquals(TEST_DEPLOYMENT_NAME, deployment.getMetadata().getName());
    }

    /**
     * Test of updateDeployment method, of class DeploymentHelper.
     */
    @Test
    @DisplayName("Update a deployment in the namespace")
    @Order(4)
    public void testUpdateDeploymentsInNamespace() {
        Map<String, String> containerToImageMap = Map.of(TEST_DEPLOYMENT_CONTAINER_NAME, TEST_DEPLOYMENT_CONTAINER_IMAGE);

        Deployment updatedDeployment = deploymentHelper.updateDeployment(TEST_DEPLOYMENT_NAMESPACE, TEST_DEPLOYMENT_NAME, containerToImageMap);
        assertNotNull(updatedDeployment, "Deployment  should not be null");

        assertEquals(TEST_DEPLOYMENT_NAME, updatedDeployment.getMetadata().getName());
    }

    /**
     * Test of getDeploymentsInNamespaceWithLabels method, of class
     * DeploymentHelper.
     */
    @DisplayName("getDeploymentsInNamespaceWithLabels ")
    @Order(5)
    @Test
    public void testGetDeploymentsInNamespaceWithLabels() {
        DeploymentList deploymentList = client.apps().deployments().inNamespace(TEST_DEPLOYMENT_NAMESPACE).list();
        assertNotNull(deploymentList, "Deployment list should not be null");
        assertTrue(deploymentList.getItems().size() == 1, "Deployment list should contain 1 deployment");
        Deployment deployment = deploymentList.getItems().get(0);
        assertEquals(TEST_DEPLOYMENT_NAME, deployment.getMetadata().getName());
    }

    @DisplayName("scale the deployment")
    @Order(6)
    @Test
    public void testScaleDeployment() throws Exception {
        DeploymentList deploymentList = client.apps().deployments().inNamespace(TEST_DEPLOYMENT_NAMESPACE).list();
        assertNotNull(deploymentList, "Deployment list should not be null");
        assertTrue(deploymentList.getItems().size() == 1, "Deployment list should contain 1 deployment");
        Deployment deployment = deploymentList.getItems().get(0);
        Deployment scaledDeployment = deploymentHelper.scaleDeployment(TEST_DEPLOYMENT_NAMESPACE, TEST_DEPLOYMENT_NAME, TEST_DEPLOYMENT_REPLICA_COUNT + 1);
        assertNotNull(scaledDeployment, "scaled deployment should not be null");
        assertEquals(scaledDeployment.getSpec().getReplicas(), TEST_DEPLOYMENT_REPLICA_COUNT + 1);

    }

    @DisplayName("get the Deployment YAML")
    @Order(7)
    @Test
    public void testGetDeploymentYAMLWithState() throws Exception {
        String deploymentYAML = deploymentHelper.getDeploymentYAML(TEST_DEPLOYMENT_NAMESPACE, TEST_DEPLOYMENT_NAME, false);
        assertNotNull(deploymentYAML, "Deployment YAML should not be null");
        assertTrue(deploymentYAML.length() > 0);
        System.out.println("DeploymentYAML:" + deploymentYAML);

    }

    @DisplayName("get the Deployment YAML")
    @Order(8)
    @Test
    public void testGetDeploymentYAMLWithoutState() throws Exception {
        String deploymentYAML = deploymentHelper.getDeploymentYAML(TEST_DEPLOYMENT_NAMESPACE, TEST_DEPLOYMENT_NAME, true);
        assertNotNull(deploymentYAML, "Deployment YAML should not be null");
        assertTrue(deploymentYAML.length() > 0);
        System.out.println("DeploymentYAML:" + deploymentYAML);

    }

    /**
     * Test of deleteDeployment method, of class DeploymentHelper.
     */
    @DisplayName("getDeploymentsInNamespaceWithLabels ")
    @Order(9)
    @Test
    public void testDeleteDeployment() {
        DeploymentList deploymentList = deploymentHelper.getDeploymentsInNamespaceWithLabels(TEST_DEPLOYMENT_NAMESPACE, Map.of(TEST_DEPLOYMENT_LABEL_KEY, TEST_DEPLOYMENT_LABEL_VALUE));
        assertNotNull(deploymentList, "Deployment list should not be null");
        assertTrue(deploymentList.getItems().size() == 1, "Deployment list should contain 1 deployment");
        Deployment deployment = deploymentList.getItems().get(0);
        assertEquals(TEST_DEPLOYMENT_NAME, deployment.getMetadata().getName(), "Deployment name does not match");
        boolean deletedDeployment = deploymentHelper.deleteDeployment(TEST_DEPLOYMENT_NAMESPACE, TEST_DEPLOYMENT_NAME);
        // get the list and make sure it is empty
        deploymentList = deploymentHelper.getDeploymentsInNamespaceWithLabels(TEST_DEPLOYMENT_NAMESPACE, Map.of(TEST_DEPLOYMENT_LABEL_KEY, TEST_DEPLOYMENT_LABEL_VALUE));
        assertNotNull(deploymentList, "Deployment list should not be null");
        assertTrue(deploymentList.getItems().size() == 0, "Deployment list should contain no deployments");

    }

    /**
     * Test of loadServiceFromYAML method, of class ServiceHelper.
     */
    @Test
    @DisplayName("Load a deployment from a YAML file")
    @Order(10)
    public void testLoadDeploymentFromYAML() throws Exception {
        Deployment deployment = deploymentHelper.loadDeploymentFromYAML(DEPLOYMENT_EXAMPLE_YAML);
        assertNotNull(deployment, "Deployment should not be null");

    }
}

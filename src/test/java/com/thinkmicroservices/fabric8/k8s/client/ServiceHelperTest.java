package com.thinkmicroservices.fabric8.k8s.client;

import com.thinkmicroservices.fabric8.k8s.client.ServiceHelper.ServiceProtocol;
import static com.thinkmicroservices.fabric8.k8s.client.ServiceHelper.ServiceProtocol.TCP;
import com.thinkmicroservices.fabric8.k8s.client.ServiceHelper.ServiceType;
import static com.thinkmicroservices.fabric8.k8s.client.ServiceHelper.ServiceType.NODE_PORT;
import static com.thinkmicroservices.fabric8.k8s.client.ServiceHelper.ServiceType.CLUSTER_IP;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;

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
public class ServiceHelperTest {

    private static final String SERVICE_EXAMPLE_YAML = "./yaml/service-example.yaml";
    private static final String SERVICE_TEST_NAMESPACE = "service-test-namespace";
    private static final String SERVICE_TEST_NAME = "service-test-name";
    private static final String SERVICE_TEST_SELECTOR_KEY = "service-test-selector-key";
    private static final String SERVICE_TEST_SELECTOR_VALUE = "service-test-selector-value";
    private static final String SERVICE_TEST_PORT_NAME = "service-test-port-name";
    private static final ServiceProtocol SERVICE_TEST_PROTOCOL = TCP;
    private static final int SERVICE_TEST_PORT = 80;
    private static final int SERVICE_TEST_TARGET_PORT = 8080;
    private static final ServiceType SERVICE_TEST_TYPE_EXAMPLE_1 = NODE_PORT;
    private static final ServiceType SERVICE_TEST_TYPE_EXAMPLE_2 = CLUSTER_IP;

    static KubernetesClient client;
    private ServiceHelper serviceHelper;

    /**
     *
     */
    public ServiceHelperTest() {
        serviceHelper = new ServiceHelper(client);
    }

    @Test
    @DisplayName("Ensure no services in namespace")
    @Order(1)
    public void testNoServicesInNamespace() {
        ServiceList serviceList = serviceHelper.getAllServicesInNamespace(SERVICE_TEST_NAMESPACE);
        assertNotNull(serviceList, "service list should not be null");
        assertTrue(serviceList.getItems().isEmpty(), "Service list should be empty");
    }

    /**
     * Test of createService method, of class ServiceHelper.
     */
    @Test
    @DisplayName("create a new service")
    @Order(2)
    public void testCreateService() {

        Service newService = serviceHelper.createService(SERVICE_TEST_NAMESPACE, SERVICE_TEST_NAME, SERVICE_TEST_SELECTOR_KEY,
                SERVICE_TEST_SELECTOR_VALUE, SERVICE_TEST_PORT_NAME,
                SERVICE_TEST_PROTOCOL, SERVICE_TEST_PORT, SERVICE_TEST_TARGET_PORT, SERVICE_TEST_TYPE_EXAMPLE_1);
        assertNotNull(newService, "service should not be null");
        assertEquals(SERVICE_TEST_NAME, newService.getMetadata().getName(), "Name does not match");
        assertEquals(SERVICE_TEST_NAMESPACE, newService.getMetadata().getNamespace(), "Namespace does not match");
    }

    /**
     * Test of getService method, of class ServiceHelper.
     */
    @Test
    @DisplayName("Test get service")
    @Order(3)
    public void testGetService() {
        ServiceList serviceList = serviceHelper.getAllServicesInNamespace(SERVICE_TEST_NAMESPACE);
        assertNotNull(serviceList, "service list should not be null");
        assertTrue(serviceList.getItems().size() == 1, "service list should contain 1 service");
        assertEquals(serviceList.getItems().get(0).getMetadata().getName(), SERVICE_TEST_NAME);
        System.out.println(serviceList);
    }

    /**
     * Test of getAllServices method, of class ServiceHelper.
     */
    @Test
    @DisplayName("Test get service")
    @Order(4)
    public void testGetAllServices() {
        ServiceList serviceList = serviceHelper.getAllServices();
        assertNotNull(serviceList, "service list should not be null");
        assertTrue(serviceList.getItems().size() == 1, "service list should contain 1 service");
        assertEquals(serviceList.getItems().get(0).getMetadata().getName(), SERVICE_TEST_NAME);
        System.out.println(serviceList);
    }

    /**
     * Test of getAllServicesInNamespace method, of class ServiceHelper.
     */
    @Test
    @DisplayName("Test get all service")
    @Order(5)
    public void testGetAllServicesInNamespace() {
        ServiceList serviceList = serviceHelper.getAllServicesInNamespace(SERVICE_TEST_NAMESPACE);
        assertNotNull(serviceList, "service list should not be null");
        assertTrue(serviceList.getItems().size() == 1, "service list should contain 1 service");
        assertEquals(serviceList.getItems().get(0).getMetadata().getName(), SERVICE_TEST_NAME);
        System.out.println(serviceList);
    }

    @Test
    @DisplayName("Test update service")
    @Order(6)
    public void testUpdateService() {
        // get the current service
        ServiceList serviceList = serviceHelper.getAllServicesInNamespace(SERVICE_TEST_NAMESPACE);
        assertNotNull(serviceList, "service list should not be null");
        assertTrue(serviceList.getItems().size() == 1, "service list should contain 1 service");
        assertEquals(serviceList.getItems().get(0).getMetadata().getName(), SERVICE_TEST_NAME);
        System.out.println("\n\n\n"+serviceList.getItems().get(0).getSpec().getType());
        System.out.println("\n\n\n"+NODE_PORT.toString());
        System.out.println("\n\n\n"+NODE_PORT.getType());
        assertEquals(serviceList.getItems().get(0).getSpec().getType(), NODE_PORT.getType());

        // now modify the service with a different ServiceType  
        
         serviceList.getItems().get(0).getSpec().setType( SERVICE_TEST_TYPE_EXAMPLE_2.getType());

        // now update the first service
        Service updatedService = serviceHelper.updateService(SERVICE_TEST_NAMESPACE,  serviceList.getItems().get(0));
        assertNotNull(updatedService, "updated service should not be null");
        assertEquals(updatedService.getSpec().getType(), SERVICE_TEST_TYPE_EXAMPLE_2.getType());
        
    }

    /**
     * Test of getServiceYAML method, of class ServiceHelper (with state).
     */
    @Test
    @DisplayName("Test get  service YAML with state")
    @Order(7)
    public void testGetServiceYAMLWithState() throws Exception {
        String serviceYAML = serviceHelper.getServiceYAML(SERVICE_TEST_NAMESPACE, SERVICE_TEST_NAME, true);
        assertNotNull(serviceYAML, "Service YAML should not be null");
        assertTrue(serviceYAML.length() > 0, "Service YAML length should be greater than 0");
        System.out.println("ServiceYAML:" + serviceYAML);
    }

    /**
     * Test of getServiceYAML method, of class ServiceHelper (without state).
     */
    @Test
    @DisplayName("Test get  service YAML without state")
    @Order(8)
    public void testGetServiceYAMLWithoutState() throws Exception {
        String serviceYAML = serviceHelper.getServiceYAML(SERVICE_TEST_NAMESPACE, SERVICE_TEST_NAME, false);
        assertNotNull(serviceYAML, "Service YAML should not be null");
        assertTrue(serviceYAML.length() > 0, "Service YAML length should be greater than 0");
        System.out.println("ServiceYAML:" + serviceYAML);
    }

    /**
     * Test of deleteService method, of class ServiceHelper.
     */
    @Test
    @DisplayName("Test delete service ")
    @Order(9)
    public void testDeleteService() {
        assertTrue(serviceHelper.deleteService(SERVICE_TEST_NAMESPACE, SERVICE_TEST_NAME));

        ServiceList serviceList = serviceHelper.getAllServicesInNamespace(SERVICE_TEST_NAMESPACE);
        assertNotNull(serviceList, "service list should not be null");
        assertTrue(serviceList.getItems().size() == 0, "service list should contain 0 services");

    }

    /**
     * Test of loadServiceFromYAML method, of class ServiceHelper.
     */
    @Test
    @DisplayName("Test load service from YAML")
    @Order(10)
    public void testLoadServiceFromYAML() throws Exception {
        Service service = serviceHelper.loadServiceFromYAML(SERVICE_EXAMPLE_YAML);
        assertNotNull(service, "Service should not be null");

    }

}

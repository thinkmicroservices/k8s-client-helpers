package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
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
@EnableKubernetesMockClient(https=true,crud = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NamespaceHelperTest {

    private static final String TEST_NAMESPACE_NAME = "default-test-ns";
    private static final String LABEL_KEY_1 = "key1";
    private static final String LABEL_VALUE_1 = "value1";
    private static final String LABEL_KEY_2 = "key2";
    private static final String LABEL_VALUE_2 = "value2";
    private static final String NAMESPACE_EXAMPLE_YAML = "./yaml/namespace-example.yaml";
    private static final String NAMESPACE_YAML_EXAMPLE_NAME = "namespace-yaml-example";
    
    static KubernetesClient client;

    private NamespaceHelper namespaceHelper;

   /**
    * 
    */
    public NamespaceHelperTest()  {
        namespaceHelper = new NamespaceHelper(client);
    }

    /**
     * Test that mock server namespace list is empty.
     */
    @Test
    @DisplayName("Get all Namespaces")
    @Order(1)
    public void testGetAllNamespacesEmpty() {
        NamespaceList list = namespaceHelper.getAllNamespaces();
        assertNotNull(list, "Namespace list is null");
        assertTrue(list.getItems().isEmpty(), "namespace list is not empty");
    }

    /**
     * Test of createNamespace method, of class NamespaceHelper.
     */
    @Test
    @DisplayName("test namespace list is empty")
    @Order(2)
    public void testCreateNamespace_String() {
        Namespace namespace = namespaceHelper.createNamespace(TEST_NAMESPACE_NAME);
        assertNotNull(namespace, "Namespace should not be null.");
        assertEquals(TEST_NAMESPACE_NAME, namespace.getMetadata().getName(), "Namespace does not match.");
    }

    /**
     * Test of getAllNamespaces method, of class NamespaceHelper.
     */
    @Test
    @DisplayName("Get all Namespaces")
    @Order(3)
    public void testGetAllNamespacesNotEmpty() {

        NamespaceList list = namespaceHelper.getAllNamespaces();
        assertNotNull(list, "Namespace list should not be null");
        assertTrue(list.getItems().size() == 1, "Namespace list size shoould == 1.");

    }

    /**
     * Test of getNamespace method, of class NamespaceHelper.
     */
    @Test
    @DisplayName("test namespace is populated")
    @Order(4)
    public void testGetNamespaceIsPopulated() {

        Namespace namespace = namespaceHelper.getNamespace(TEST_NAMESPACE_NAME);
        assertNotNull(namespace, "Namespace should not be null");
        assertEquals(TEST_NAMESPACE_NAME, namespace.getMetadata().getName(), "Namespace does not match.");
    }

    /**
     * Test of deleteNamespace method, of class NamespaceHelper.
     */
    @Test
    @DisplayName("test we can delete the namespace we created before")
    @Order(5)
    public void testDeleteNamespace() {
        assertTrue(namespaceHelper.deleteNamespace(TEST_NAMESPACE_NAME), "Namespace was not deleted.");
    }

    /**
     * Test of createNamespace method with a label map.
     */
    @Test
    @DisplayName("create Namespace with map")
    @Order(6)
    public void testCreateNamespace_String_Map() {
        Map<String, String> labelMap = Map.of(LABEL_KEY_1, LABEL_VALUE_1, LABEL_KEY_2, LABEL_VALUE_2);
        Namespace namespace = namespaceHelper.createNamespace(TEST_NAMESPACE_NAME, labelMap);

        assertNotNull(namespace, "Namespace is null");
        assertTrue(namespace.getMetadata().getLabels().containsKey(LABEL_KEY_1), "label map does not contain key:" + LABEL_KEY_1);
        assertTrue(namespace.getMetadata().getLabels().get(LABEL_KEY_1).equals(LABEL_VALUE_1), "label map does not contain value:" + LABEL_VALUE_1);
    }

    /**
     * Test of getNamespaceListWithLabels method, of class NamespaceHelper.
     */
    @Test
    @DisplayName("Get all Namespaces with Label key-value")
    @Order(7)
    public void testGetNamespaceListWithLabels_String_String() {
        Map<String, String> labelMap = Map.of(LABEL_KEY_1, LABEL_VALUE_1, LABEL_KEY_2, LABEL_VALUE_2);
        NamespaceList namespaceList = namespaceHelper.getNamespaceListWithLabels(LABEL_KEY_1, LABEL_VALUE_1);
        assertNotNull(namespaceList, "namespace list should not be null");
        assertFalse(namespaceList.getItems().isEmpty(), "namespace list should not be empty");
        assertTrue(namespaceList.getItems().size() == 1, "namespace list should contain only one item");

        assertTrue(namespaceList.getItems().get(0).getMetadata().getLabels().containsKey(LABEL_KEY_1), "Namespace label map does not contain key:" + LABEL_KEY_1);
        assertTrue(namespaceList.getItems().get(0).getMetadata().getLabels().get(LABEL_KEY_1).equals(LABEL_VALUE_1), "Namespace label map does not contain value:" + LABEL_VALUE_1);
    }

    /**
     * Test of getNamespaceListWithLabels with map method, of class
     * NamespaceHelper.
     */
    @Test
    @DisplayName("Get all Namespaces with Label map")
    @Order(8)
    public void testGetNamespaceListWithLabels_Map() {
        Map<String, String> labelMap = Map.of(LABEL_KEY_1, LABEL_VALUE_1, LABEL_KEY_2, LABEL_VALUE_2);
        NamespaceList namespaceList = namespaceHelper.getNamespaceListWithLabels(labelMap);
        assertNotNull(namespaceList, "namespace list should not be null");
        assertFalse(namespaceList.getItems().isEmpty(), "namespace list should not be empty");
        assertTrue(namespaceList.getItems().size() == 1, "namespace list should contain only one item");

        assertTrue(namespaceList.getItems().get(0).getMetadata().getLabels().containsKey(LABEL_KEY_1), "Namespace label map does not contain key:" + LABEL_KEY_1);
        assertTrue(namespaceList.getItems().get(0).getMetadata().getLabels().get(LABEL_KEY_1).equals(LABEL_VALUE_1), "Namespace label map does not contain value:" + LABEL_VALUE_1);

        assertTrue(namespaceList.getItems().get(0).getMetadata().getLabels().containsKey(LABEL_KEY_2), "Namespace label map does not contain key:" + LABEL_KEY_2);
        assertTrue(namespaceList.getItems().get(0).getMetadata().getLabels().get(LABEL_KEY_2).equals(LABEL_VALUE_2), "Namespace label map does not contain value:" + LABEL_VALUE_2);
    }

    @Test
    @DisplayName("get Namespace YAML with state")
    @Order(9)
    public void testgetNamespaceYAMLWithState() throws JsonProcessingException {
        String namespaceYAML = namespaceHelper.getNamespaceYAML(TEST_NAMESPACE_NAME, true);
        System.out.println("Namespace YAML:" + namespaceYAML);
        assertNotNull(namespaceYAML, "Namespace YAML should not be null");
    }

    @Test
    @DisplayName("get Namespace YAML without state")
    @Order(10)
    public void testgetNamespaceYAMLWithoutState() throws JsonProcessingException {
        String namespaceYAML = namespaceHelper.getNamespaceYAML(TEST_NAMESPACE_NAME, false);
        System.out.println("Namespace YAML:" + namespaceYAML);
        assertNotNull(namespaceYAML, "Namespace YAML should not be null");
    }

    /**
     * Test of loadNamespaceFromYAML method, of class NamespaceHelper.
     */
    @Test
    @DisplayName("load Namespace from YAML")
    @Order(11)
    public void testLoadNamespaceFromYAML() throws Exception {
        Namespace namespace = namespaceHelper.loadNamespaceFromYAML(NAMESPACE_EXAMPLE_YAML);
        assertNotNull(namespace, "Namespace should not be null");
        assertEquals(NAMESPACE_YAML_EXAMPLE_NAME, namespace.getMetadata().getName());
    }
    

    /**
     * Test of getAllNamespaces method, of class NamespaceHelper.
     */
    @Test
    public void testGetAllNamespaces() {

    }

}

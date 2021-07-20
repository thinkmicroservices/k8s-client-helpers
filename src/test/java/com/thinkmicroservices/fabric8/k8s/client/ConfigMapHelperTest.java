package com.thinkmicroservices.fabric8.k8s.client;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import java.util.Map;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 * @author cwoodward
 */
@EnableKubernetesMockClient(crud = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfigMapHelperTest {

    static KubernetesClient client;
    private ConfigMapHelper configMapHelper;

    private static final String CONFIGMAP_TEST_NAMESPACE = "configmap-test-namespace";
    private static final String CONFIGMAP_TEST_NAME = "configmap-test-name";
    private static final String CONFIGMAP_KEY_1 = "One";
    private static final String CONFIGMAP_VALUE_1 = "1";
    private static final String CONFIGMAP_KEY_2 = "Two";
    private static final String CONFIGMAP_VALUE_2 = "2";
    private static final String CONFIGMAP_KEY_3 = "Three";
    private static final String CONFIGMAP_VALUE_3 = "3";
    private static final String CONFIGMAP_KEY_4 = "Four";
    private static final String CONFIGMAP_VALUE_4 = "4";

    private static final Map<String, String> CONFIGMAP_TEST_DATA_MAP_1 = Map.of(CONFIGMAP_KEY_1, CONFIGMAP_VALUE_1, CONFIGMAP_KEY_2, CONFIGMAP_VALUE_2);
    private static final Map<String, String> CONFIGMAP_TEST_DATA_MAP_2 = Map.of(CONFIGMAP_KEY_3, CONFIGMAP_VALUE_3, CONFIGMAP_KEY_4, CONFIGMAP_VALUE_4);

    private static final String CONFIGMAP_EXAMPLE_YAML = "./yaml/configmap-example.yaml";

    public ConfigMapHelperTest() {
        configMapHelper = new ConfigMapHelper(client);
    }

    @Test
    @DisplayName("Ensure no configmaps in namespace")
    @Order(1)
    public void testNoConfigMapsInNamespace() {
        ConfigMapList secretList = configMapHelper.getAllConfigMapsByNamespace(CONFIGMAP_TEST_NAMESPACE);
        assertNotNull(secretList, "secret list should not be null");
        assertTrue(secretList.getItems().isEmpty(), "secret list is not empty");
    }

    @Test
    @DisplayName("create configmap in namespace")
    @Order(2)
    public void testCreateConfigMap() {
        ConfigMap newConfigMap = configMapHelper.createConfigMap(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME, CONFIGMAP_TEST_DATA_MAP_1);
        assertNotNull(newConfigMap, "secret should not be null");
        assertEquals(CONFIGMAP_TEST_NAME, newConfigMap.getMetadata().getName(), "secret name does not match");
        assertEquals(CONFIGMAP_TEST_DATA_MAP_1, newConfigMap.getData(), "Data map does not match");
    }

    @Test
    @DisplayName("get the newly created configmap")
    @Order(3)
    public void testGetConfigMap() {
        ConfigMap foundConfigMap = configMapHelper.getConfigMap(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME);
        assertNotNull(foundConfigMap, "configmap should not be null");
        assertEquals(CONFIGMAP_TEST_NAME, foundConfigMap.getMetadata().getName(), "configmap name doesn't match");
        assertEquals(CONFIGMAP_TEST_DATA_MAP_1, foundConfigMap.getData(), "data map does not match");
    }

    @Test
    @DisplayName("update the configmap")
    @Order(4)
    public void testUpdateConfigMap() {
        ConfigMap foundConfigMap = configMapHelper.getConfigMap(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME);
        assertNotNull(foundConfigMap, "configmap should not be null");
        assertEquals(CONFIGMAP_TEST_NAME, foundConfigMap.getMetadata().getName(), "configmap name doesn't match");
        // check the data map
        assertEquals(CONFIGMAP_TEST_DATA_MAP_1, foundConfigMap.getData(), "data map doesnt match");

        // change the data map
        foundConfigMap.setData(CONFIGMAP_TEST_DATA_MAP_2);
        ConfigMap updatedConfigMap = configMapHelper.updateConfigMap(CONFIGMAP_TEST_NAMESPACE, foundConfigMap);
        assertNotNull(updatedConfigMap, "updated configMap should not be null");
        // check the updated data map
        assertEquals(CONFIGMAP_TEST_DATA_MAP_2, updatedConfigMap.getData());

    }

    @Test
    @DisplayName("get all configMaps")
    @Order(5)
    public void testGetAllSecrets() {
        ConfigMapList configMapsList = configMapHelper.getAllConfigMaps();
        assertNotNull(configMapsList, "configmap list should not be null");
        assertTrue(configMapsList.getItems().size() > 0, "configmap list size should not be null");

    }

    @Test
    @DisplayName("Get configmap YAML with state")
    @Order(6)
    public void testGetConfigMapYAMLWithState() throws Exception {
        String configMaptYAML = configMapHelper.getConfigMapYAML(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME, true);
        assertNotNull(configMaptYAML, "configmap YAML should not be null");
        System.out.println("configMap YAML:" + configMaptYAML);
    }

    @Test
    @DisplayName("Get configMap YAML without state")
    @Order(7)
    public void testGetConfigMapYAMLWithoutState() throws Exception {
        String configMaptYAML = configMapHelper.getConfigMapYAML(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME, false);
        assertNotNull(configMaptYAML, "configmap YAML should not be null");
        System.out.println("configMap YAML:" + configMaptYAML);
    }

    @Test
    @DisplayName("Delete configMap")
    @Order(8)
    public void testDeleteConfigMap() throws Exception {
        assertTrue(configMapHelper.deleteConfigMap(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME), "deleteConfigMap() method failed");
        ConfigMap deletedConfigMap = configMapHelper.getConfigMap(CONFIGMAP_TEST_NAMESPACE, CONFIGMAP_TEST_NAME);
        assertNull(deletedConfigMap, "deleted configMap SHOULD BE NULL");
    }

    @Test
    @DisplayName("load configMap from YAML")
    @Order(9)
    public void testLoadConfigMapFromYaml() throws Exception {
        ConfigMap loadedConfigMap = configMapHelper.loadConfigMapFromYAML(CONFIGMAP_EXAMPLE_YAML);
        assertNotNull(loadedConfigMap, "Loaded secret should not be null");
        assertEquals(CONFIGMAP_TEST_NAME, loadedConfigMap.getMetadata().getName(), "secret Name does not match");
        assertEquals(CONFIGMAP_TEST_DATA_MAP_2, loadedConfigMap.getData(), "data map does not match");
    }

}

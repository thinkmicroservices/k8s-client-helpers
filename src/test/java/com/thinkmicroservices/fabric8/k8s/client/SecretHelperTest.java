package com.thinkmicroservices.fabric8.k8s.client;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
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
public class SecretHelperTest {

    static KubernetesClient client;
    private SecretHelper secretHelper;

    private static final String SECRET_TEST_NAMESPACE = "secret-test-namespace";
    private static final String SECRET_TEST_NAME = "secret-test-name";
    private static final String SECRET_USERNAME_KEY = "username";
    private static final String SECRET_USERNAME_VALUE_1 = "bootsy";
    private static final String SECRET_USERNAME_VALUE_2 = "prince";
    private static final String SECRET_PASSWORD_KEY = "password";
    private static final String SECRET_PASSWORD_VALUE_1 = "super_secret_password";
    private static final String SECRET_PASSWORD_VALUE_2 = "super_secure_password";
    private static final Map<String, String> SECRET_TEST_DATA_MAP_1 = Map.of(SECRET_USERNAME_KEY, SECRET_USERNAME_VALUE_1, SECRET_PASSWORD_KEY, SECRET_PASSWORD_VALUE_1);
    private static final Map<String, String> SECRET_TEST_DATA_MAP_2 = Map.of(SECRET_USERNAME_KEY, SECRET_USERNAME_VALUE_2, SECRET_PASSWORD_KEY, SECRET_PASSWORD_VALUE_2);

    private static final String SECRET_EXAMPLE_YAML = "./yaml/secret-example.yaml";

    /**
     *
     */
    public SecretHelperTest() {
        secretHelper = new SecretHelper(client);
    }

    @Test
    @DisplayName("Ensure no secrets in namespace")
    @Order(1)
    public void testNoJobsInNamespace() {
        SecretList secretList = secretHelper.getAllSecretsInNamespace(SECRET_TEST_NAMESPACE);
        assertNotNull(secretList, "secret list should not be null");
        assertTrue(secretList.getItems().isEmpty(), "secret list is not empty");
    }

    @Test
    @DisplayName("create secret in namespace")
    @Order(2)
    public void testCreateSecret() {
        Secret newSecret = secretHelper.createSecret(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME, SECRET_TEST_DATA_MAP_1);
        assertNotNull(newSecret, "secret should not be null");
        assertEquals(SECRET_TEST_NAME, newSecret.getMetadata().getName(), "secret name does not match");
        assertEquals(SECRET_TEST_DATA_MAP_1, newSecret.getData(), "Data map does not match");
    }

    @Test
    @DisplayName("get the newly created secret")
    @Order(3)
    public void testGetSecret() {
        Secret foundSecret = secretHelper.getSecret(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME);
        assertNotNull(foundSecret, "secret should not be null");
        assertEquals(SECRET_TEST_NAME, foundSecret.getMetadata().getName(), "secret name doesn't match");
        assertEquals(SECRET_TEST_DATA_MAP_1, foundSecret.getData(), "data map does not match");
    }

    @Test
    @DisplayName("update the secret")
    @Order(4)
    public void testUpdateSecret() {
        Secret foundSecret = secretHelper.getSecret(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME);
        assertNotNull(foundSecret, "secret should not be null");
        assertEquals(SECRET_TEST_NAME, foundSecret.getMetadata().getName(), "Secret name doesn't match");
        // check the  secret data map
        assertEquals(SECRET_TEST_DATA_MAP_1, foundSecret.getData(), "data map doesnt match");

        // change the data map
        foundSecret.setData(SECRET_TEST_DATA_MAP_2);
        Secret updatedSecret = secretHelper.updateSecret(SECRET_TEST_NAMESPACE, foundSecret);
        assertNotNull(updatedSecret, "updated secret should not be null");
        // check the  secret data map
        assertEquals(SECRET_TEST_DATA_MAP_2, updatedSecret.getData());

    }

    @Test
    @DisplayName("get all secrets")
    @Order(5)
    public void testGetAllSecrets() {
        SecretList secretList = secretHelper.getAllSecrets();
        assertNotNull(secretList, "secret list should not be null");
        assertTrue(secretList.getItems().size() > 0, "secret list size should not be null");

    }

    @Test
    @DisplayName("Get secret YAML with state")
    @Order(6)
    public void testGetSecretYAMLWithState() throws Exception {
        String secretYAML = secretHelper.getSecretYAML(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME, true);
        assertNotNull(secretYAML, "secret YAML should not be null");
        System.out.println("secret YAML:" + secretYAML);
    }

    @Test
    @DisplayName("Get secret YAML without state")
    @Order(7)
    public void testGetSecretYAMLWithoutState() throws Exception {
        String secretYAML = secretHelper.getSecretYAML(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME, false);
        assertNotNull(secretYAML, "Secret YAML should not be null");
        System.out.println("Secret YAML:" + secretYAML);
    }

    @Test
    @DisplayName("Delete secret")
    @Order(8)
    public void testDeleteSecret() throws Exception {
        assertTrue(secretHelper.deleteSecret(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME), "deleteSecret() method failed");
        Secret deletedSecret = secretHelper.getSecret(SECRET_TEST_NAMESPACE, SECRET_TEST_NAME);
        assertNull(deletedSecret, "deleted Secret SHOULD BE NULL");
    }

    @Test
    @DisplayName("load secret from YAML")
    @Order(9)
    public void testLoadSecretFromYaml() throws Exception {
        Secret loadedSecret = secretHelper.loadSecretFromYAML(SECRET_EXAMPLE_YAML);
        assertNotNull(loadedSecret, "Loaded secret should not be null");
        assertEquals(SECRET_TEST_NAME, loadedSecret.getMetadata().getName(), "secret Name does not match");
        assertEquals(SECRET_TEST_DATA_MAP_2, loadedSecret.getData(), "data map does not match");
    }
    
    
}


package com.thinkmicroservices.fabric8.k8s.client;


import io.fabric8.kubernetes.client.Config;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cwoodward
 */
public class ClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClientUtils.class);
    private static final String CONFIG_FILENAME = "./client-config";

    public static KubernetesClient getConfiguredInstanceFromFile() throws IOException, InterruptedException {
        File file = new File(CONFIG_FILENAME);
        String kubeconfigContents = Files.readString(file.toPath());
        Config config = Config.fromKubeconfig(kubeconfigContents);

        final KubernetesClient client = new DefaultKubernetesClient(config);
        return client;

    }

}

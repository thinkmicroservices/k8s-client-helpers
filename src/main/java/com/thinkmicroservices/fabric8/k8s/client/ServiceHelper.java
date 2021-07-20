package com.thinkmicroservices.fabric8.k8s.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

/**
 *
 * @author cwoodward
 */
public class ServiceHelper {

    private KubernetesClient client;

    /**
     *
     * @param client
     */
    public ServiceHelper(KubernetesClient client) {
        this.client = client;
    }

    /**
     *
     * @param namespace
     * @param service
     * @return
     */
    public Service getService(String namespace, String service) {
        return client.services().inNamespace(namespace).withName(service).get();
    }

    /**
     *
     * @return
     */
    public ServiceList getAllServices() {
        return client.services().inAnyNamespace().list();
    }

    /**
     *
     * @param namespace
     * @return
     */
    public ServiceList getAllServicesInNamespace(String namespace) {
        return client.services().inNamespace(namespace).list();
    }

   /**
    * 
    * @param serviceNamespace
    * @param serviceName
    * @param selectorKey
    * @param selectorValue
    * @param portName
    * @param protocol
    * @param port
    * @param targetPort
    * @param type
    * @return 
    */
    public Service createService(String serviceNamespace, String serviceName, String selectorKey, String selectorValue, String portName, ServiceProtocol protocol,
            int port, int targetPort, ServiceType type) {
        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(serviceName)
                .withNamespace(serviceNamespace)
                .endMetadata()
                .withNewSpec()
                .withSelector(Collections.singletonMap(selectorKey, selectorValue))
                .addNewPort()
                .withName(portName)
                .withProtocol(protocol.getProtocol())
                .withPort(port)
                .withTargetPort(new IntOrString(targetPort))
                .endPort()
                .withType(type.getType())
                .endSpec()
                .build();

        return client.services().inNamespace(serviceNamespace).withName(serviceName).create(service);
    }

    /**
     *
     * @param namespace
     * @param newService
     * @return
     */
    public Service updateService(String namespace, Service newService) {
        return client.services().inNamespace(namespace).createOrReplace(newService);
    }

    /**
     *
     * @param namespace
     * @param serviceName
     * @param withRuntimeState
     * @return
     * @throws JsonProcessingException
     */
    public String getServiceYAML(String namespace, String serviceName, boolean withRuntimeState) throws JsonProcessingException {
        Service foundService = this.getService(namespace, serviceName);
        if (withRuntimeState) {
            return SerializationUtils.dumpAsYaml(foundService);
        } else {
            return SerializationUtils.dumpWithoutRuntimeStateAsYaml(foundService);
        }
    }

    /**
     *
     * @param namespace
     * @param serviceName
     * @return
     */
    public boolean deleteService(String namespace, String serviceName) {
        return client.services().inNamespace(namespace).withName(serviceName).delete();
    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public Service loadServiceFromYAML(String filename) throws FileNotFoundException {
        return client.services().load(new FileInputStream(filename)).get();
    }

    /**
     * Valid Kuberbetes Service protocols
     */
    public enum ServiceProtocol {
        TCP("TCP"),
        UDP("UDP"),
        SCTP("SCTP"),
        HTTP("HTTP"),
        PROXY("PROXY");

        private final String protocol;

        /**
         *
         * @param protocol
         */
        ServiceProtocol(String protocol) {
            this.protocol = protocol;
        }

        /**
         *
         * @return
         */
        public String getProtocol() {
            return this.protocol;
        }

        /**
         *
         * @return
         */
        public String toString() {
            return "ServiceProtocol: " + this.protocol;
        }
    }

    /**
     * Valid Kubernetes Service types
     */
    public enum ServiceType {
        CLUSTER_IP("ClusterIp"),
        NODE_PORT("NodePort"),
        LOAD_BALANCER("LoadBalancer"),
        EXTERNAL_NAME("ExternalName");

        private final String type;

        /**
         *
         * @param type
         */
        ServiceType(String type) {
            this.type = type;
        }

        /**
         *
         * @return
         */
        public String getType() {
            return this.type;
        }

        /**
         *
         * @return
         */
        public String toString() {
            return "ServiceType: " + this.type;
        }
    }
}

package com.thinkmicroservices.fabric8.k8s.client;

/**
 * valid restart policy types
 */
public enum RestartPolicy {
    NEVER("Never"),
    ON_FAILURE("OnFailure");

    private String policy;

    /**
     *
     * @param policy
     */
    RestartPolicy(String policy) {
        this.policy = policy;
    }

    /**
     *
     * @return
     */
    public String getPolicy() {
        return this.policy;
    }

    /**
     *
     * @return
     */
    public String toString() {
        return "RestartPolicy: " + this.policy;
    }
}

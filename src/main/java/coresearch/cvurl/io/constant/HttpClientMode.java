package coresearch.cvurl.io.constant;

/**
 * Enumeration of HTTP client modes. Intended for use with {@link coresearch.cvurl.io.model.CVurlConfig}
 *
 * @since 0.9
 */
public enum HttpClientMode {

    /**
     * The client mode that can be used to create one instance of the {@link coresearch.cvurl.io.model.HttpClient} class
     * for all {@link coresearch.cvurl.io.model.CVurlConfig} instances.
     */
    SINGLETON,

    /**
     * The client mode that can be used to create a separate instance of the {@link coresearch.cvurl.io.model.HttpClient} class
     * for all {@link coresearch.cvurl.io.model.CVurlConfig} instances.
     */
    PROTOTYPE
}

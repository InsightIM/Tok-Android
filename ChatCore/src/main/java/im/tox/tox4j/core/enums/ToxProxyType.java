package im.tox.tox4j.core.enums;

/**
 * Type of proxy used to connect to TCP relays.
 */
public enum ToxProxyType {
    /**
     * Don't use a proxy.
     */
    NONE,
    /**
     * HTTP proxy using CONNECT.
     */
    HTTP,
    /**
     * SOCKS proxy for simple socket pipes.
     */
    SOCKS5,
}

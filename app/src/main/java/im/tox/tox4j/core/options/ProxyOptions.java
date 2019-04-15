package im.tox.tox4j.core.options;

import im.tox.tox4j.core.enums.ToxProxyType;

public class ProxyOptions {
    /**
     * Low level enumeration value to pass to [[ToxCore.load]].
     */
    public ToxProxyType proxyType;

    /**
     * The IP address or DNS name of the proxy to be used.
     *
     * If used, this must be a valid DNS name. The name must not exceed [[ToxCoreConstants.MaxHostnameLength]] characters.
     * This member is ignored (it can be anything) if [[proxyType]] is [[ToxProxyType.NONE]].
     */
    public String proxyAddress;

    /**
     * The port to use to connect to the proxy server.
     *
     * Ports must be in the range (1, 65535). The value is ignored if [[proxyType]] is [[ToxProxyType.NONE]].
     */
    public int proxyPort;

    private ProxyOptions(ToxProxyType proxyType, String proxyAddress, int proxyPort) {
        this.proxyType = proxyType;
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
    }

    public static ProxyOptions None = new ProxyOptions(ToxProxyType.NONE, "", 0);

    public static ProxyOptions Http(String proxyAddress, int proxyPort) {
        return new ProxyOptions(ToxProxyType.HTTP, proxyAddress, proxyPort);
    }

    public static ProxyOptions Socks5(String proxyAddress, int proxyPort) {
        return new ProxyOptions(ToxProxyType.SOCKS5, proxyAddress, proxyPort);
    }
}

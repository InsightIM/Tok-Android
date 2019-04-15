package im.tox.tox4j.core.options;

import im.tox.tox4j.core.ToxCoreConstants;

public class ToxOptions {
    public Boolean ipv6Enabled = true;
    public Boolean udpEnabled = true;
    public Boolean localDiscoveryEnabled = true;
    public ProxyOptions proxy = ProxyOptions.None;
    public int startPort = ToxCoreConstants.DefaultStartPort;
    public int endPort = ToxCoreConstants.DefaultEndPort;
    public int tcpPort = ToxCoreConstants.DefaultTcpPort;
    public SaveDataOptions saveData = SaveDataOptions.None;
    public Boolean fatalErrors = true;

    public ToxOptions() {

    }

    public ToxOptions(Boolean ipv6Enabled, Boolean udpEnabled, Boolean localDiscoveryEnabled,
        ProxyOptions proxy, int startPort, int endPort, int tcpPort, SaveDataOptions saveData,
        Boolean fatalErrors) {
        if (ipv6Enabled != null) {
            this.ipv6Enabled = ipv6Enabled;
        }
        if (udpEnabled != null) {
            this.udpEnabled = udpEnabled;
        }
        if (localDiscoveryEnabled != null) {
            this.localDiscoveryEnabled = localDiscoveryEnabled;
        }
        if (proxy != null) {
            this.proxy = proxy;
        }
        if (startPort > 0) {
            this.startPort = startPort;
        }
        if (endPort > 0) {
            this.endPort = endPort;
        }
        if (tcpPort > 0) {
            this.tcpPort = tcpPort;
        }
        if (saveData != null) {
            this.saveData = saveData;
        }
        if (fatalErrors != null) {
            this.fatalErrors = fatalErrors;
        }
    }
}

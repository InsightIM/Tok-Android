package im.tox.tox4j.core.options;

import im.tox.tox4j.core.ToxCoreConstants;

public class ToxOptions {
    public boolean ipv6Enabled = true;
    public boolean udpEnabled = true;
    public boolean localDiscoveryEnabled = true;
    public ProxyOptions proxy = ProxyOptions.None;
    public int startPort = ToxCoreConstants.DefaultStartPort;
    public int endPort = ToxCoreConstants.DefaultEndPort;
    public int tcpPort = ToxCoreConstants.DefaultTcpPort;
    public SaveDataOptions saveData = SaveDataOptions.None;
    public boolean fatalErrors = true;

    public ToxOptions() {

    }

    public ToxOptions(SaveDataOptions saveData) {
        if (saveData != null) {
            this.saveData = saveData;
        }
    }

    public ToxOptions(boolean ipv6Enabled, boolean udpEnabled, boolean localDiscoveryEnabled,
        ProxyOptions proxy, int startPort, int endPort, int tcpPort, SaveDataOptions saveData,
        boolean fatalErrors) {
        this.ipv6Enabled = ipv6Enabled;
        this.udpEnabled = udpEnabled;
        this.localDiscoveryEnabled = localDiscoveryEnabled;
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
        this.fatalErrors = fatalErrors;
    }
}

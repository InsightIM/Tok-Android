package com.client.tok.bean;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DhtNodeList {
    @SerializedName("last_refresh")
    private long lastRefresh;
    @SerializedName("last_scan")
    private long lastScan;

    private List<DhtNode> nodes;

    public List<DhtNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DhtNode> nodes) {
        this.nodes = nodes;
    }
}

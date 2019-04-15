package com.client.tok.constant;

public enum Intervals {
    WORKING(500),
    // Orig: Value(50) // only in filetransfers it seems
    AWAKE(1000);

    private int interval;

    public int getInterval() {
        return interval;
    }

    Intervals(int interval) {
        this.interval = interval;
    }
}

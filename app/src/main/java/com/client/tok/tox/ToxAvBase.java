package com.client.tok.tox;

import im.tox.tox4j.impl.jni.ToxAvImpl;

public class ToxAvBase {
    private int instanceNumber;
    private ToxAvImpl toxAvImpl;

    public ToxAvBase(int instanceNumber) {
        this.instanceNumber = instanceNumber;
        toxAvImpl = new ToxAvImpl(instanceNumber);
    }
}

package com.client.tok.rx.event;

/**
 * RxBus, event about contact
 */
public class BotOrderEvent {
    private String msg;
    private String order;

    public BotOrderEvent(String order, String msg) {
        this.order = order;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "BotOrderEvent{" + "order='" + order + '\'' + '}';
    }
}

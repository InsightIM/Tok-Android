package com.client.tok.constant;

public enum BotOrder {
    START("/start", true),
    SET("/set", false),
    ADD("/add", false),
    OTHER("/other", true);

    private String order;
    private boolean dirSend;

    BotOrder(String order, boolean dirSend) {
        this.order = order;
        this.dirSend = dirSend;
    }

    public static BotOrder fromVal(String order) {
        for (BotOrder botOrder : BotOrder.values()) {
            if (botOrder.getOrder().equals(order)) {
                return botOrder;
            }
        }
        return OTHER;
    }

    public String getOrder() {
        return order;
    }

    public boolean isDirSend() {
        return dirSend;
    }

    @Override
    public String toString() {
        return order;
    }

    public static boolean isAddOrder(String order) {
        return ADD.order.equals(order.toLowerCase());
    }
}

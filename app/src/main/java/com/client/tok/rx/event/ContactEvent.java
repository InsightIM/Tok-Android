package com.client.tok.rx.event;

/**
 * RxBus, event about contact
 */
public class ContactEvent {
    public static int DEL_CONTACT = 1;
    private int event;
    private String pk;

    public ContactEvent(int event, String pk) {
        this.event = event;
        this.pk = pk;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public static ContactEvent buildDelEvent(String pk) {
        return new ContactEvent(DEL_CONTACT, pk);
    }

    @Override
    public String toString() {
        return "ContactEvent{" + "event=" + event + ", pk='" + pk + '\'' + '}';
    }
}

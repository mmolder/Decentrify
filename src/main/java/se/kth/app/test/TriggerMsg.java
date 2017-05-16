package se.kth.app.test;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-16.
 */
public class TriggerMsg implements KompicsEvent {
    private String msg;

    public TriggerMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}

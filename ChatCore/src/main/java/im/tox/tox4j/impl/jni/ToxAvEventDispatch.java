package im.tox.tox4j.impl.jni;

import im.tox.proto.Av;
import im.tox.tox4j.av.callbacks.ToxAvEventListener;
import im.tox.tox4j.av.enums.ToxavFriendCallState;

public class ToxAvEventDispatch {
    public static ToxavFriendCallState convert(Av.CallState.Kind kind) {
        if (kind == Av.CallState.Kind.ERROR) {
            return ToxavFriendCallState.FINISHED;
        } else if (kind == Av.CallState.Kind.SENDING_A) {
            return ToxavFriendCallState.SENDING_A;
        } else if (kind == Av.CallState.Kind.SENDING_V) {
            return ToxavFriendCallState.SENDING_V;
        } else if (kind == Av.CallState.Kind.ACCEPTING_A) {
            return ToxavFriendCallState.ACCEPTING_A;
        } else if (kind == Av.CallState.Kind.ACCEPTING_V) {
            return ToxavFriendCallState.ACCEPTING_V;
        } else {
            return ToxavFriendCallState.FINISHED;
        }
    }

    //TODO
    public static int convert(ToxavFriendCallState callState) {
        return 0;
    }

    public static void dispatch(ToxAvEventListener handler, byte[] eventData) {
        if (eventData != null) { // scalastyle:ignore null
            try {
                Av.AvEvents events = Av.AvEvents.parseFrom(eventData);
                dispatchEvents(handler, events);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void dispatchEvents(ToxAvEventListener handler, Av.AvEvents events) {
        //TODO
        //(state
        //    |> dispatchCall(handler, events.call)
        //    |> dispatchCallState(handler, events.callState)
        //    |> dispatchAudioBitRate(handler, events.audioBitRate)
        //    |> dispatchVideoBitRate(handler, events.videoBitRate)
        //    |> dispatchAudioReceiveFrame(handler, events.audioReceiveFrame)
        //    |> dispatchVideoReceiveFrame(handler, events.videoReceiveFrame))
    }
}

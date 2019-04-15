package im.tox.tox4j.av.callbacks;

import im.tox.tox4j.av.data.BitRate;
import im.tox.tox4j.core.data.ToxFriendNumber;

/**
 * The event is triggered when the network becomes too saturated for current
 * bit rates at which point core suggests new bit rates.
 */
public interface VideoBitRateCallback {
    /**
     * @param friendNumber The friend number of the friend for which to set the
     * video bit rate.
     * @param videoBitRate Suggested maximum video bit rate in Kb/sec.
     */
    void videoBitRate(ToxFriendNumber friendNumber, BitRate videoBitRate);
}

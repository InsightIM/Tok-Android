package im.tox.tox4j.av.enums;

/**
 * Call control.
 */
public enum ToxavCallControl {
  /**
   * Resume a previously paused call. Only valid if the pause was caused by this
   * client, if not, this control is ignored. Not valid before the call is accepted.
   */
  RESUME,

  /**
   * Put a call on hold. Not valid before the call is accepted.
   */
  PAUSE,

  /**
   * Reject a call if it was not answered, yet. Cancel a call after it was
   * answered.
   */
  CANCEL,

  /**
   * Request that the friend stops sending audio. Regardless of the friend's
   * compliance, this will cause the {@link im.tox.tox4j.av.callbacks.AudioReceiveFrameCallback}
   * event to stop being triggered on receiving an audio frame from the friend.
   */
  MUTE_AUDIO,

  /**
   * Calling this control will notify client to start sending audio again.
   */
  UNMUTE_AUDIO,

  /**
   * Request that the friend stops sending video. Regardless of the friend's
   * compliance, this will cause the {@link im.tox.tox4j.av.callbacks.VideoReceiveFrameCallback}
   * event to stop being triggered on receiving an video frame from the friend.
   */
  HIDE_VIDEO,

  /**
   * Calling this control will notify client to start sending video again.
   */
  SHOW_VIDEO,
}

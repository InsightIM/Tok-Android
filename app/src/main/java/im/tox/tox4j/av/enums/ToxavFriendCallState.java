package im.tox.tox4j.av.enums;

/**
 * Call state graph.
 */
public enum ToxavFriendCallState {
  /**
   * Set by the AV core if an error occurred on the remote end or if friend
   * timed out. This is the final state after which no more state
   * transitions can occur for the call. This call state will never be triggered
   * in combination with other call states.
   */
  ERROR,

  /**
   * The call has finished. This is the final state after which no more state
   * transitions can occur for the call. This call state will never be
   * triggered in combination with other call states.
   */
  FINISHED,

  /**
   * The flag that marks that friend is sending audio.
   */
  SENDING_A,

  /**
   * The flag that marks that friend is sending video.
   */
  SENDING_V,

  /**
   * The flag that marks that friend is receiving audio.
   */
  ACCEPTING_A,

  /**
   * The flag that marks that friend is receiving video.
   */
  ACCEPTING_V,
}

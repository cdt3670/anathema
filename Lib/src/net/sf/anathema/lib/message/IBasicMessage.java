package net.sf.anathema.lib.message;

import java.io.Serializable;

/**
 * Interface for basic message object only contains the text and type of a message.
 */
public interface IBasicMessage extends Serializable {

  /** Returns the text of this message, must not return <code>null</code>. */
  String getText();

  /** Returns the type of this message, must not return <code>null</code>. */
  MessageType getType();

  boolean isErrorMessage();
}
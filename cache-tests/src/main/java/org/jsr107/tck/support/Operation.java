/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * An {@link Operation} that may be invoked by a {@link Client} and handled
 * by a {@link Server} using an {@link OperationHandler}.
 *
 * @param <T> the type of value returned from the {@link Operation} when it
 *            is invoked
 * @author Brian Oliver
 * @see Client
 * @see Server
 * @see OperationHandler
 */
public interface Operation<T> {

  /**
   * The type of the operation.
   *
   * @return the type of operation
   */
  String getType();

  /**
   * Initiate and invoke an operation returning the result.
   * <p>
   * This method is executed by a {@link Client} in response to a
   * {@link Client#invoke(Operation)} request.  The objective of this method
   * is to send/receive information to/from a {@link Server} using the
   * provided streams.  The actual execution of the {@link Operation} is
   * performed by an appropriate {@link OperationHandler} known to the
   * {@link Server} of the required {@link #getType()}.
   *
   * @param ois the {@link ObjectInputStream} to read information from the
   *            {@link Server}, typically the result from the {@link OperationHandler}
   * @param oos the {@link ObjectOutputStream} to send information to the
   *            {@link Server}, typically the parameters to the {@link OperationHandler}
   * @return the result of the {@link Operation}
   * @throws IOException            when the Operation can't read/write to the streams
   * @throws ClassNotFoundException when the operation can't load a required class
   * @throws ExecutionException     when an exception occurred invoking the operation
   */
  T onInvoke(ObjectInputStream ois, ObjectOutputStream oos)
      throws IOException, ClassNotFoundException, ExecutionException;
}

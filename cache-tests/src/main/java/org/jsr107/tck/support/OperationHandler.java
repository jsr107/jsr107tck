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

/**
 * An {@link OperationHandler} is responsible for processing an {@link Operation}
 * invoked by a {@link Client}.
 *
 * @author Brian Oliver
 * @see Operation
 * @see Client
 * @see Server
 */
public interface OperationHandler {

  /**
   * The type of the operation.
   *
   * @return the type of operation
   */
  String getType();

  /**
   * Perform an {@link Operation} initiated by a {@link Client}.
   *
   * @param ois the {@link ObjectInputStream} to read information from the
   *            {@link Client}, typically parameters from an {@link Operation}
   * @param oos the {@link ObjectOutputStream} to write information to the
   *            {@link Client}, typically the result of an {@link Operation}
   * @throws IOException  if either of the streams can't be read/written to
   * @throws ClassNotFoundException  if a requested class can't be loaded
   */
  void onProcess(ObjectInputStream ois, ObjectOutputStream oos)
      throws IOException, ClassNotFoundException;
}

/**
 *  Copyright 2011-2013 Terracotta, Inc.
 *  Copyright 2011-2013 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jsr107.tck.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A rudimentary multi-threaded {@link Socket}-based {@link Server} that can
 * handle, using {@link OperationHandler}s, {@link Operation}s invoked by
 * {@link Client}s.
 *
 * @author Brian Oliver
 * @see Client
 * @see Operation
 * @see OperationHandler
 */
public class Server implements AutoCloseable {

  /**
   * The port on which the {@link Server} will accept {@link Client} connections.
   */
  private int port;

  /**
   * The {@link OperationHandler}s by operation.
   */
  private ConcurrentHashMap<String, OperationHandler> operationHandlers;

  /**
   * The {@link ServerSocket} that will be used to accept {@link Client}
   * connections and requests.
   * <p/>
   * When this is <code>null</code> the {@link Server} is not running.
   */
  private ServerSocket serverSocket;

  /**
   * The {@link Thread} that will manage accepting {@link Client} connections.
   * <p/>
   * When this is <code>null</code> the {@link Server} is not running.
   */
  private Thread serverThread;

  /**
   * A map of {@link ClientConnection} by connection number.
   */
  private ConcurrentHashMap<Integer, ClientConnection> clientConnections;


  /**
   * Should the running {@link Server} terminate as soon as possible?
   */
  private AtomicBoolean isTerminating;

  /**
   * Construct a {@link Server} that will accept {@link Client} connections
   * and requests on the specified port.
   *
   * @param port the port on which to accept {@link Client} connections and requests
   */
  public Server(int port) {
    this.port = port;
    this.operationHandlers = new ConcurrentHashMap<String, OperationHandler>();
    this.serverSocket = null;
    this.serverThread = null;
    this.clientConnections = new ConcurrentHashMap<Integer, ClientConnection>();
    this.isTerminating = new AtomicBoolean(false);
  }

  /**
   * Registers the specified {@link OperationHandler} for an operation.
   *
   * @param handler the {@link OperationHandler}
   */
  public void addOperationHandler(OperationHandler handler) {
    this.operationHandlers.put(handler.getType(), handler);
  }

  /**
   * Opens and starts the {@link Server}.
   * <p/>
   * Does nothing if the {@link Server} is already open.
   *
   * @return the {@link InetAddress} on which the {@link Server}
   *         is accepting requests from {@link Client}s.
   */
  public synchronized InetAddress open() throws IOException {
    if (serverSocket == null) {
      serverSocket = new ServerSocket(port);

      serverThread = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            int connectionId = 0;

            while (!isTerminating.get()) {
              Socket socket = serverSocket.accept();

              ClientConnection clientConnection = new ClientConnection(connectionId++, socket);
              clientConnections.put(clientConnection.getIdentity(), clientConnection);
              clientConnection.start();
            }
          } catch (NullPointerException e) {
            isTerminating.compareAndSet(false, true);
          } catch (IOException e) {
            isTerminating.compareAndSet(false, true);
          }
        }
      });

      serverThread.start();
    }

    return serverSocket.getInetAddress();
  }

  /**
   * Obtains the {@link InetAddress} on which the {@link Server} is listening.
   *
   * @return the {@link InetAddress}
   */
  public synchronized InetAddress getInetAddress() {
    if (serverSocket != null) {
      return serverSocket.getInetAddress();
    } else {
      throw new IllegalStateException("Server is not open");
    }
  }

  /**
   * Obtains the port on which the {@link Server} is listening.
   *
   * @return the port
   */
  public synchronized int getPort() {
    if (serverSocket != null) {
      return port;
    } else {
      throw new IllegalStateException("Server is not open");
    }
  }

  /**
   * Stops the {@link Server}.
   * <p/>
   * Does nothing if the {@link Server} is already stopped.
   */
  public synchronized void close() {
    if (serverSocket != null) {
      //we're now terminating
      isTerminating.set(true);

      //stop the server socket
      try {
        serverSocket.close();
      } catch (IOException e) {
        //failed to close the server socket - but we don't care
      }
      serverSocket = null;

      //interrupt the server thread
      serverThread.interrupt();
      serverThread = null;

      //stop the clients
      for (ClientConnection clientConnection : clientConnections.values()) {
        clientConnection.close();
      }
      this.clientConnections = new ConcurrentHashMap<Integer, ClientConnection>();

      isTerminating.set(false);
    }
  }

  /**
   * Asynchronously handles {@link Client} requests via a {@link Socket} using the
   * defined {@link OperationHandler}s.
   */
  private class ClientConnection extends Thread implements AutoCloseable {

    /**
     * The {@link ClientConnection} identity.
     */
    private int identity;

    /**
     * The {@link Socket} to the {@link Client}.
     */
    private Socket socket;

    /**
     * Constructs a {@link ClientConnection}.
     *
     * @param identity the identity for the {@link ClientConnection}
     * @param socket   the {@link Socket} on which to receive and respond to
     *                 {@link Client} requests
     */
    public ClientConnection(int identity, Socket socket) {
      this.identity = identity;
      this.socket = socket;
    }

    /**
     * Obtains the identity for the {@link ClientConnection}.
     *
     * @return the identity
     */
    public int getIdentity() {
      return this.identity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

      try {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        while (true) {
          try {
            String operation = (String) ois.readObject();
            OperationHandler handler = Server.this.operationHandlers.get(operation);

            if (handler != null) {
              handler.onProcess(ois, oos);
            }
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
        //any error closes the connection

      } finally {
        if (socket != null) {
          try {
            socket.close();
          } catch (IOException e) {
            //failed to close the socket - but we don't care
          }
        }

        //remove this from the server
        Server.this.clientConnections.remove(identity);
      }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
      try {
        socket.close();
      } catch (IOException e) {
        //failed to close the socket - but we don't care
      } finally {
        socket = null;
      }
    }
  }
}

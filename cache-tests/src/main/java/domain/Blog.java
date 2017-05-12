/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

import java.io.Serializable;

/**
 * Domain class for testing interception model
 *
 * @author Rick Hightower
 */
public class Blog implements Serializable {

  private static final long serialVersionUID = 1L;

  private String title;
  private String body;

  public Blog(String title, String body) {
    super();
    this.title = title;
    this.body = body;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

}

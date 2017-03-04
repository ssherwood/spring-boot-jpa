package io.undertree.symptom.exceptions;

/**
 */
abstract class HttpException extends RuntimeException {

  static final long serialVersionUID = 20170227L;

  private final String resource;

  public HttpException(String message) {
    this(null, message);
  }

  public HttpException(String resource, String message) {
    super(message);
    this.resource = resource;
  }

  public HttpException(String resource, String message, Throwable cause) {
    super(message, cause);
    this.resource = resource;
  }

  public String getResource() {
    return resource;
  }
}

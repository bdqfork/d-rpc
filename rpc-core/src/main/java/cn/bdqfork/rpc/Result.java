package cn.bdqfork.rpc;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2019-02-20
 */
public interface Result extends Serializable {

    Object getValue();

    void setValue(Object value);

    Throwable getException();

    void setException(Throwable throwable);

    boolean hasException();

    String getMessage();

}

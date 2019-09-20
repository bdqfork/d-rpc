package cn.bdqfork.rpc;

import java.util.Map;

/**
 * @author bdq
 * @since 2019-08-26
 */
public interface Invocation {
    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getArguments();

    Map<String, Object> getAttachments();

    void setAttachments(Map<String, Object> attachments);

}

package cn.bdqfork.rpc.remote;

import java.util.Map;

/**
 * @author bdq
 * @since 2019-08-26
 */
public interface Invocation {
    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getArguments();

    Map<String, String> getAttachments();

    void setAttachments(Map<String, String> attachments);

}

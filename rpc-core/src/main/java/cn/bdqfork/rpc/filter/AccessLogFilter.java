package cn.bdqfork.rpc.filter;

import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.Activate;
import cn.bdqfork.rpc.context.RpcContext;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2019/10/9
 */
@Activate(group = Const.PROVIDER_SIDE, value = Const.ACCESS_LOG_KEY)
public class AccessLogFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        boolean accesslog = url.getParameter(Const.ACCESS_LOG_KEY);
        if (accesslog) {
            RpcContext rpcContext = RpcContext.getRpcContext();

            StringBuilder logBuilder = new StringBuilder();

            logBuilder.append(rpcContext.getRemoteAddress().getHostString())
                    .append(":")
                    .append(rpcContext.getRemoteAddress().getPort())
                    .append("->")
                    .append(rpcContext.getLocalAddress().getHostString())
                    .append(":")
                    .append(rpcContext.getLocalAddress().getPort())
                    .append("/")
                    .append(rpcContext.getUrl().getParameter(Const.GROUP_KEY, Const.DEFAULT_GROUP))
                    .append(rpcContext.getUrl().toServiceCategory());

            String version = rpcContext.getUrl().getParameter(Const.VERSION_KEY);
            if (StringUtils.isNotBlank(version)) {
                logBuilder.append(":").append(version).append("#");
            }

            logBuilder.append(invocation.getMethodName())
                    .append("(");

            Class<?>[] parameterTypes = invocation.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    logBuilder.append(",");
                }
                logBuilder.append(parameterTypes[i].getCanonicalName());
            }

            logBuilder.append(")");

            Gson gson = new Gson();
            logBuilder.append(gson.toJson(invocation.getArguments()));

            log.info(logBuilder.toString());
        }

        return invoker.invoke(invocation);
    }
}

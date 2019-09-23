package cn.bdqfork.rpc.context.remote;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.URL;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bdq
 * @since 2019/9/16
 */
public abstract class AbstractRpcServer implements RpcServer {
    private volatile boolean isAvailable;
    private AtomicBoolean destroyed = new AtomicBoolean(false);
    private URL url;
    protected String host;
    protected Integer port;
    protected Serializer serializer;

    public AbstractRpcServer(URL url) {
        this.url = url;
        this.host = url.getHost();
        this.port = url.getPort();
        initSerializer(url);
    }

    private void initSerializer(URL url) {
        String serializtion = url.getParameter(Const.SERIALIZATION_KEY);
        SerializerFactory serializerFactory = ExtensionLoader.getExtensionLoader(SerializerFactory.class)
                .getAdaptiveExtension();
        this.serializer = serializerFactory.getSerializer(serializtion);
    }

    @Override
    public void start() {
        isAvailable = true;
        doStart();
    }

    protected abstract void doStart();

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            isAvailable = false;
            doDestroy();
        }
    }

    protected abstract void doDestroy();
}

package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.Node;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.protocol.Protocol;
import cn.bdqfork.rpc.context.AbstractDirectory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class RegistryDirectory<T> extends AbstractDirectory<T> implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(RegistryDirectory.class);
    private Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
    private List<Registry> registries;

    public RegistryDirectory(Class<T> serviceInterface, URL url, List<Registry> registries) {
        super(serviceInterface, url);
        this.registries = registries;
    }

    @Override
    protected List<Invoker<T>> doList(Invocation invocation) {
        return new ArrayList<>(invokers.values());
    }

    public void subscribe() {
        registries.forEach(registry -> registry.subscribe(url, this));
    }

    @Override
    protected void refresh() {
        log.debug("directory refresh !");
        List<URL> urls = registries.stream()
                .map(registry -> registry.lookup(url))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        doRefresh(urls);
    }

    private void doRefresh(List<URL> urls) {
        log.debug("directory notified with url size {} !", urls.size());
        if (destroyed.get()) {
            return;
        }
        if ((urls == null || urls.isEmpty()) && invokers.size() > 0) {
            log.debug("Destroy all invokers !");
            this.invokers.values().forEach(Node::destroy);
            this.invokers.clear();
        } else {
            log.debug("Update invokers !");
            urls.forEach(this::mergeUrl);

            this.urls.removeAll(urls);
            this.urls.stream()
                    .map(URL::buildString)
                    .forEach(url -> invokers.remove(url).destroy());
            urls.stream()
                    .filter(this::isMatch)
                    .forEach(this::addRpcInvoker);
        }
        this.urls = urls;
    }

    @Override
    public synchronized void notify(List<URL> urls) {
        log.debug("notify !");
        doRefresh(urls);
    }

    private boolean isMatch(URL url) {
        return checkEnvironment(url) && checkVersion(url) && checkServer(url) && checkUrl(url);
    }

    private boolean checkEnvironment(URL url) {
        String environment = this.url.getParameter(Const.ENVIRONMENT_KEY);
        if (StringUtils.isBlank(environment)) {
            return true;
        }
        return url.getParameter(Const.ENVIRONMENT_KEY).equals(environment);
    }

    private boolean checkVersion(URL url) {
        if (StringUtils.isBlank(this.version)) {
            return true;
        }
        return url.getParameter(Const.VERSION_KEY).equals(this.version);
    }

    private boolean checkUrl(URL url) {
        return !invokers.containsKey(url.buildString());
    }

    private boolean checkServer(URL url) {
        String serverString = this.url.getParameter(Const.SERVER_KEY, "rpc");
        String[] servers = serverString.split(",");
        String serverType = url.getParameter(Const.SERVER_KEY);
        for (String server : servers) {
            if (server.equals(serverType)) {
                return true;
            }
        }
        return false;
    }

    private void addRpcInvoker(URL url) {
        Invoker<T> invoker = protocol.refer(serviceInterface, url);
        invokers.put(url.buildString(), invoker);
    }

    private void mergeUrl(URL url) {
        String timeout = this.url.getParameter(Const.TIMEOUT_KEY);
        url.addParameter(Const.TIMEOUT_KEY, timeout);
        String retries = this.url.getParameter(Const.RETRY_KEY);
        url.addParameter(Const.RETRY_KEY, retries);
        String connections = this.url.getParameter(Const.CONNECTIONS_KEY);
        url.addParameter(Const.CONNECTIONS_KEY, connections);
        String refName = this.url.getParameter(Const.REF_NAME_KEY);
        url.addParameter(Const.REF_NAME_KEY, refName);
        boolean isAsync = this.url.getParameter(Const.ASYNC_KEY);
        url.addParameter(Const.ASYNC_KEY, isAsync);
        url.addParameter(Const.VERSION_KEY, this.version);
    }

}

package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RpcInvoker;
import cn.bdqfork.rpc.remote.ClientPool;
import cn.bdqfork.rpc.exporter.ConsumerExporter;
import cn.bdqfork.rpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ReferenceBean extends AbstractRpcBean {
    public static final String REFERENCE_BEAN_NAME = "referenceBean";

    private static final Logger log = LoggerFactory.getLogger(ReferenceBean.class);

    private Registry registry;

    private List<ReferenceInfo> referenceInfos;

    @Override
    public void destroy() throws Exception {

        log.info("closing ......");

        registry.close();

        log.info("closed");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (referenceInfos == null || referenceInfos.size() == 0) {
            return;
        }

        registry = getOrCreateRegistry();

        ApplicationConfig applicationConfig = context.getBean(ApplicationConfig.class);

        ConsumerExporter consumerExporter = new ConsumerExporter(registry);

        for (ReferenceInfo referenceInfo : referenceInfos) {

            Reference reference = referenceInfo.getReference();
            URL url = buildUrl(applicationConfig, reference);
            //注册消费者，以及订阅提供者
            consumerExporter.export(url);

            //设置连接池
            ClientPool clientPool = consumerExporter.getClientPool(reference.group(), reference.serviceInterface().getName());
            RpcInvoker invoker = (RpcInvoker) referenceInfo.getInvoker();
            invoker.setClientPool(clientPool);
        }

        referenceInfos.clear();

    }

    private URL buildUrl(ApplicationConfig applicationConfig, Reference reference) {
        String host = NetUtils.getIp();//获得本机IP
        URL url = new URL(Const.PROTOCOL_CONSUMER, host, 0, reference.serviceInterface().getName());
        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.GROUP_KEY, reference.group());
        url.addParameter(Const.REF_NAME_KEY, reference.refName());
        url.addParameter(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        return url;
    }

    public void setReferenceInfos(List<ReferenceInfo> referenceInfos) {
        this.referenceInfos = referenceInfos;
    }

}

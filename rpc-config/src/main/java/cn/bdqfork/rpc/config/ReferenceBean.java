package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.netty.consumer.RpcInvoker;
import cn.bdqfork.rpc.netty.client.ClientPool;
import cn.bdqfork.rpc.exporter.ConsumerExporter;
import cn.bdqfork.rpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author bdq
 * @date 2019-03-04
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

        ProtocolConfig protocolConfig = context.getBean(ProtocolConfig.class);

        ApplicationConfig applicationConfig = context.getBean(ApplicationConfig.class);

        ConsumerExporter consumerExporter = new ConsumerExporter(protocolConfig, registry);

        for (ReferenceInfo referenceInfo : referenceInfos) {

            Reference reference = referenceInfo.getReference();

            //注册消费者，以及订阅提供者
            consumerExporter.export(applicationConfig.getApplicationName(), reference.group(),
                    reference.serviceInterface().getName(), reference.refName());

            //设置连接池
            ClientPool clientPool = consumerExporter.getClientPool(reference.group(), reference.serviceInterface().getName());
            RpcInvoker invoker = (RpcInvoker) referenceInfo.getInvoker();
            invoker.setClientPool(clientPool);
        }

        referenceInfos.clear();

    }

    public void setReferenceInfos(List<ReferenceInfo> referenceInfos) {
        this.referenceInfos = referenceInfos;
    }
}

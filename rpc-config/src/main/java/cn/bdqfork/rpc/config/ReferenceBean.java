package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.consumer.RpcInvoker;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.exporter.Exchanger;
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

    private ReferenceConfigCallback referenceConfigCallback;

    @Override
    public void destroy() throws Exception {

        log.info("closing ......");

        registry.close();

        log.info("closed");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        List<ReferenceConfig> referenceConfigs = referenceConfigCallback.getReferenceConfigs();

        if (referenceConfigs == null || referenceConfigs.size() == 0) {
            return;
        }

        registry = getOrCreateRegistry();

        registry.init();

        ProtocolConfig protocolConfig = context.getBean(ProtocolConfig.class);

        ApplicationConfig applicationConfig = context.getBean(ApplicationConfig.class);

        Exchanger exchanger = new Exchanger(protocolConfig, registry);

        for (ReferenceConfig referenceConfig : referenceConfigs) {

            Reference reference = referenceConfig.getReference();

            //注册消费者，以及订阅提供者
            exchanger.export(applicationConfig.getApplicationName(), reference.group(),
                    reference.serviceInterface().getName(), reference.refName());

            //设置连接池
            ClientPool clientPool = exchanger.getClientPool(reference.group(), reference.serviceInterface().getName());
            RpcInvoker invoker = (RpcInvoker) referenceConfig.getInvoker();
            invoker.setClientPool(clientPool);
        }

        referenceConfigs.clear();

    }

    public void setReferenceConfigCallback(ReferenceConfigCallback referenceConfigCallback) {
        this.referenceConfigCallback = referenceConfigCallback;
    }

    public interface ReferenceConfigCallback {

        /**
         * 回调获取ReferenceConfig信息
         *
         * @return
         */
        List<ReferenceConfig> getReferenceConfigs();

    }
}

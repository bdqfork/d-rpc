package cn.bdqfork.rpc.registry;

/**
 * @author bdq
 * @date 2019-03-03
 */
public abstract class AbstractRegistry implements Registry {
    protected boolean running;

    @Override
    public boolean isRunning() {
        return running;
    }
}

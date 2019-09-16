package cn.bdqfork.provider.impl;

import cn.bdqfork.provider.api.UserInfoService;
import cn.bdqfork.rpc.config.annotation.Service;

/**
 * @author bdq
 * @since 2019/9/16
 */
@Service(serviceInterface = UserInfoService.class, group = "rpc-test")
public class UserInfoServiceImpl implements UserInfoService {
    @Override
    public void getMobile() {
        System.out.println("mobile");
    }
}

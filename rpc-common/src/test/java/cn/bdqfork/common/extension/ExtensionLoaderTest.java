package cn.bdqfork.common.extension;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.adaptive.AdaptiveExt;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExtensionLoaderTest {

    @Test
    public void load() {

    }

    @Test
    public void getActivateExtensions() {
    }

    @Test
    public void getActivateExtension() {
        ExtensionFactory extensionFactory = ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getActivateExtension(null, "test", Const.PROVIDER_SIDE);
        System.out.println(extensionFactory);
    }

    @Test
    public void getAdaptiveExtension() throws Exception {
        ExtensionLoader<AdaptiveExt> extensionLoader = ExtensionLoader.getExtensionLoader(AdaptiveExt.class);
        AdaptiveExt adaptiveEx = extensionLoader.getAdaptiveExtension();
        adaptiveEx.test(new URL("testAdaptive://test:0/?name=testAdaptive"));
        adaptiveEx.test2();
    }
}
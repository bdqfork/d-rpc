package cn.bdqfork.common.extension;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExtensionLoaderTest {

    @Test
    public void load() {
        ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        ExtensionFactory extensionFactory = extensionLoader.getExtension("test");
    }
}
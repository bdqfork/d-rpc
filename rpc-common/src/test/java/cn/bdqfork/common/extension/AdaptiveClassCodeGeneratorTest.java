package cn.bdqfork.common.extension;

import cn.bdqfork.common.extension.adaptive.AdaptiveExt;
import cn.bdqfork.common.extension.compiler.AdaptiveClassCodeGenerator;
import org.junit.Test;

public class AdaptiveClassCodeGeneratorTest {

    @Test
    public void generate() {
        AdaptiveClassCodeGenerator generator = new AdaptiveClassCodeGenerator(AdaptiveExt.class, "test");
        System.out.println(generator.generate());
    }

    public class AdaptiveExt$Adaptive implements cn.bdqfork.common.extension.adaptive.AdaptiveExt {
        public void test(cn.bdqfork.common.URL arg0) throws java.lang.Exception {
            cn.bdqfork.common.URL url = arg0;
            String[] keys = null;
            String extensionName = null;
            if (keys != null) {
                for (int i = 0; i < keys.length; i++) {
                    extensionName = (String) url.getParameter(keys[i]);
                    if (extensionName != null) {
                        break;
                    }
                }
            } else {
                extensionName = (String) url.getProtocol();
            }
            if (extensionName == null) {
                extensionName = "test";
            }
            cn.bdqfork.common.extension.adaptive.AdaptiveExt extension = (cn.bdqfork.common.extension.adaptive.AdaptiveExt) ExtensionLoader.getExtensionLoader(cn.bdqfork.common.extension.adaptive.AdaptiveExt.class).getExtension(extensionName);
            if (extension == null) {
                throw new IllegalStateException("Fail to get extension for class cn.bdqfork.common.extension.adaptive.AdaptiveExt use keys () !");
            }
            extension.test(arg0);
        }

        public void test2() throws java.lang.Exception {
            throw new UnsupportedOperationException("Method test2 is not annotated by @Adaptive !");
        }
    }

}
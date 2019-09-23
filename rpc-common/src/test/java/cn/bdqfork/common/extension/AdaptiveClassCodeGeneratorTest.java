package cn.bdqfork.common.extension;

import cn.bdqfork.common.extension.adaptive.AdaptiveExt;
import org.junit.Test;

public class AdaptiveClassCodeGeneratorTest {

    @Test
    public void generate() {
        AdaptiveClassCodeGenerator generator = new AdaptiveClassCodeGenerator(AdaptiveExt.class, "test");
        System.out.println(generator.generate());
    }

}
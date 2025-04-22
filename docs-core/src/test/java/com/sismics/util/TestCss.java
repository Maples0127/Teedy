//package com.sismics.util;
//
//import org.junit.Test;
//
//import com.sismics.util.css.Selector;
//
///**
// * Test of CSS utilities.
// *
// * @author bgamard
// */
//public class TestCss {
//    @Test
//    public void testBuildCss() {
//        Selector selector = new Selector(".test")
//            .rule("background-color", "yellow")
//            .rule("font-family", "Comic Sans");
//        System.out.println(selector);
//    }
//}
package com.sismics.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sismics.util.css.Selector;

/**
 * Test of CSS utilities.
 *
 * @author bgamard
 */
public class TestCss {
    @Test
    public void testBuildCss() {
        Selector selector = new Selector(".test")
                .rule("background-color", "yellow")
                .rule("font-family", "Comic Sans");
        String expected = ".test {\n  background-color: yellow;\n  font-family: Comic Sans;\n}\n";
        assertEquals(expected, selector.toString());
    }

    @Test
    public void testEmptySelector() {
        Selector selector = new Selector(".empty");
        String expected = ".empty {\n}\n";
        assertEquals(expected, selector.toString());
    }

    @Test
    public void testSpecialCharacterValue() {
        Selector selector = new Selector("#special")
                .rule("content", "\"\\\"Hello; World\\\"\""); // Value is "\"Hello; World\""
        String expected = "#special {\n  content: \"\\\"Hello; World\\\"\";\n}\n";
        assertEquals(expected, selector.toString());
    }
}
package com.metasys.ryft.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileBrowserApiTest {

    private static final String ROOT_FOLDER = "target/browse";
    private FileBrowserApi api;
    private FileBrowserApi apiWithTrailingSlash;
    private FileBrowserApi apiWithTrailingBackslash;
    private FileBrowserApi apiWithFullPath;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tearDownAfterClass(); // in case things weren't properly cleaned up before
        File root = new File(ROOT_FOLDER);
        Assert.assertTrue(root.mkdirs());
        Assert.assertTrue(new File(root, "file1.txt").createNewFile());
        Assert.assertTrue(new File(root, "file2.pdf").createNewFile());
        Assert.assertTrue(new File(root, "dir1").mkdir());
        Assert.assertTrue(new File(root, "dir1/file11.png").createNewFile());
        Assert.assertTrue(new File(root, "dir2").mkdir());
        Assert.assertTrue(new File(root, "dir2/dir21").mkdir());
        Assert.assertTrue(new File(root, "dir2/dir21/file211").createNewFile());
        Assert.assertTrue(new File(root, "dir2/file21.xml").createNewFile());
    }

    @Before
    public void setUp() throws Exception {
        api = new FileBrowserApi();
        api.setRootFolder(ROOT_FOLDER);
        api.init();
        apiWithTrailingSlash = new FileBrowserApi();
        apiWithTrailingSlash.setRootFolder(ROOT_FOLDER + '/');
        apiWithTrailingSlash.init();
        apiWithTrailingBackslash = new FileBrowserApi();
        apiWithTrailingBackslash.setRootFolder(ROOT_FOLDER + '\\');
        apiWithTrailingBackslash.init();
        apiWithFullPath = new FileBrowserApi();
        apiWithFullPath.setRootFolder(new File(ROOT_FOLDER).getAbsolutePath());
        apiWithFullPath.init();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        FileUtils.deleteDirectory(new File(ROOT_FOLDER));
    }

    @Test
    public void testNotAllowed() throws Exception {
        assertEmpty("something");
        assertEmpty("/../..");
        assertEmpty("/root");
    }

    @Test
    public void testError(@Mocked final Logger logger) throws Exception {
        new MockUp<File>() {
            @Mock
            String getCanonicalPath() throws IOException {
                throw new IOException();
            }
        };
        assertEmpty("/");
        new Verifications() {
            {
                logger.error(anyString, withInstanceOf(IOException.class));
                times = 4;
            }
        };
    }

    @Test
    public void testBrowseRoot() throws Exception {
        StringBuilder expected = new StringBuilder();
        expected.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        expected.append("<li class=\"directory collapsed\"><a href=\"#\" rel=\"/dir1/\">dir1</a></li>");
        expected.append("<li class=\"directory collapsed\"><a href=\"#\" rel=\"/dir2/\">dir2</a></li>");
        expected.append("<li class=\"file ext_txt\"><a href=\"#\" rel=\"/file1.txt\">file1.txt</a></li>");
        expected.append("<li class=\"file ext_pdf\"><a href=\"#\" rel=\"/file2.pdf\">file2.pdf</a></li>");
        expected.append("</ul>");
        assertExpected("/", expected.toString());
    }

    @Test
    public void testBrowseDir1() throws Exception {
        StringBuilder expected = new StringBuilder();
        expected.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        expected.append("<li class=\"file ext_png\"><a href=\"#\" rel=\"/dir1/file11.png\">file11.png</a></li>");
        expected.append("</ul>");
        assertExpected("/dir1", expected.toString());
    }

    @Test
    public void testBrowseDir2() throws Exception {
        StringBuilder expected = new StringBuilder();
        expected.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        expected.append("<li class=\"directory collapsed\"><a href=\"#\" rel=\"/dir2/dir21/\">dir21</a></li>");
        expected.append("<li class=\"file ext_xml\"><a href=\"#\" rel=\"/dir2/file21.xml\">file21.xml</a></li>");
        expected.append("</ul>");
        assertExpected("/dir2", expected.toString());
    }

    @Test
    public void testBrowseDir21() throws Exception {
        StringBuilder expected = new StringBuilder();
        expected.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        expected.append("<li class=\"file ext_\"><a href=\"#\" rel=\"/dir2/dir21/file211\">file211</a></li>");
        expected.append("</ul>");
        assertExpected("/dir2/dir21", expected.toString());
    }

    private void assertExpected(String path, String expected) {
        compareHtmlList(expected, api.browse(path));
        compareHtmlList(expected, apiWithTrailingSlash.browse(path));
        compareHtmlList(expected, apiWithTrailingBackslash.browse(path));
        compareHtmlList(expected, apiWithFullPath.browse(path));
    }

    private void compareHtmlList(String expected, String actual) {
        int endFirstTag = expected.indexOf('>') + 1;
        String ul = expected.substring(0, endFirstTag);
        Assert.assertTrue(actual.startsWith(ul));
        Assert.assertTrue(actual.endsWith("</ul>"));
        List<String> expectedEntries = Arrays.asList(expected.substring(endFirstTag, expected.length() - 5).split("<li"));
        Collections.sort(expectedEntries);
        List<String> actualEntries = Arrays.asList(actual.substring(endFirstTag, actual.length() - 5).split("<li"));
        Collections.sort(actualEntries);
        Assert.assertEquals(expectedEntries, actualEntries);
    }

    private void assertEmpty(String path) {
        StringBuilder expected = new StringBuilder();
        expected.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        expected.append("</ul>");
        assertExpected(path, expected.toString());
    }

}

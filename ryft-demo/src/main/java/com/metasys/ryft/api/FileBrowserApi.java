package com.metasys.ryft.api;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/file")
public class FileBrowserApi {

    private static final String EXPECTED_ROOT = "/";
    @Value("${root}")
    private String rootFolder;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String browse(@RequestParam("dir") String dir) {
        StringBuilder response = new StringBuilder();
        response.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        if (dir.startsWith(EXPECTED_ROOT)) {
            dir = dir.replaceFirst(EXPECTED_ROOT, rootFolder);
            File root = new File(dir);
            if (root.isDirectory()) {
                File[] content = root.listFiles();
                for (File item : content) {
                    if (item.isDirectory()) {
                        response.append(String.format("<li class=\"directory collapsed\"><a href=\"#\" rel=\"%s/\">%s</a></li>", item
                                .getAbsolutePath().replace('\\', '/').replace(rootFolder, EXPECTED_ROOT), item.getName()));
                    } else {
                        String ext = StringUtils.substringAfterLast(item.getName(), ".");
                        response.append(String.format("<li class=\"file ext_%s\"><a href=\"#\" rel=\"%s\">%s</a></li>", ext, item.getAbsolutePath()
                                .replace('\\', '/').replace(rootFolder, EXPECTED_ROOT), item.getName()));
                    }
                }
            }
        }
        response.append("</ul>");
        return response.toString();
    }
}

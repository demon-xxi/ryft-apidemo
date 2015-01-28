package com.metasys.ryft.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * API to browse the server-side file system.
 *
 * <p>
 * The API works from a configured "root" folder shown to the user as '/'. Before listing the content of a directory it resolves its full path and
 * makes sure it is within the root folder. If it's not nothing is returned.
 *
 * @author Sylvain Crozon
 *
 */
@Controller
@RequestMapping("/file")
public class FileBrowserApi {

    private static final Logger LOG = LogManager.getLogger(FileBrowserApi.class);

    // Root shown to the user
    private static final String EXPECTED_ROOT = "/";
    // HTML file entry format with 3 parameters: CSS class, file path, file name
    private static final String LI_FORMAT = "<li class=\"%s\"><a href=\"#\" rel=\"%s\">%s</a></li>";
    private static final String DIRECTORY_CSS = "directory collapsed";
    private static final String FILE_CSS = "file ext_";

    // Configured root folder where to start listing entries from
    @Value("${ryft.fs.root}")
    private String rootFolder;
    private String absoluteRoot;

    // Called by Spring on initialization
    @PostConstruct
    public void init() throws IOException {
        if (!(rootFolder.charAt(rootFolder.length() - 1) == '/' || rootFolder.charAt(rootFolder.length() - 1) == '\\')) {
            rootFolder += File.separator;
        }
        rootFolder = toUnix(rootFolder);
        // Resolves the configured root folder to its full canonical path so it can be compared to the path of the requested directories
        absoluteRoot = new File(rootFolder).getCanonicalPath();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/browse")
    @ResponseBody
    public String browse(@RequestParam("dir") String dir) {
        LOG.debug("Listing {}", dir);
        // builds an HTML response according to http://www.abeautifulsite.net/jquery-file-tree/#custom_connectors
        StringBuilder response = new StringBuilder();
        response.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
        dir = dir.replaceFirst(EXPECTED_ROOT, rootFolder);
        File requestedDir = new File(dir);
        try {
            if (requestedDir.getCanonicalPath().startsWith(absoluteRoot) && requestedDir.isDirectory()) {
                File[] content = requestedDir.listFiles();
                for (File item : content) {
                    response.append(addEntry(item));
                }
            }
        } catch (IOException e) {
            LOG.error("Error resolving requested path", e);
        }
        response.append("</ul>");
        return response.toString();
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/octet-stream")
    @ResponseBody
    public byte[] getFile(HttpServletResponse response, @RequestParam("file") String file) throws Exception {
        LOG.debug("Get file {}", file);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
        File requestedFile = new File(file.replaceFirst(EXPECTED_ROOT, rootFolder));
        if (requestedFile.getCanonicalPath().startsWith(absoluteRoot) && requestedFile.isFile()) {
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(requestedFile);
            IOUtils.copy(fis, content);
            fis.close();
            return content.toByteArray();
        }
        response.sendError(HttpStatus.NOT_FOUND.value(), "File not found " + file);
        return new byte[0];
    }

    private String addEntry(File file) {
        if (file.isDirectory()) {
            return String.format(LI_FORMAT, DIRECTORY_CSS, getMaskedFilePath(file), file.getName());
        }
        String ext = StringUtils.substringAfterLast(file.getName(), ".");
        return String.format(LI_FORMAT, FILE_CSS + ext, getMaskedFilePath(file), file.getName());
    }

    private String getMaskedFilePath(File file) {
        String maskedPath = file.getAbsolutePath().replace(absoluteRoot + File.separator, EXPECTED_ROOT);
        if (file.isDirectory()) {
            maskedPath += File.separator;
        }
        maskedPath = toUnix(maskedPath);
        return maskedPath;
    }

    private String toUnix(String path) {
        return path.replace('\\', '/');
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }
}

package com.metasys.ryft.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metasys.ryft.Wrapper;

@Controller
@RequestMapping("/demo")
public class DemoApi {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String test(@RequestParam Map<String, String> params) {
        long start = System.nanoTime();
        Wrapper w = new Wrapper();
        w.openFile(params.get("files").split(", "));
        String function = params.get("function");
        switch (function) {
            case "search":
                w.search(params.get("query"), Integer.parseInt(params.get("width")));
                break;
            case "fuzzy search":
                w.fuzzySearch(params.get("query"), Integer.parseInt(params.get("width")), Integer.parseInt(params.get("fuzziness")));
                break;
            case "sort":
                if ("ascending".equals(params.get("sort"))) {
                    w.sortAsc(params.get("field"));
                } else {
                    w.sortDesc(params.get("field"));
                }
                break;
            case "term frequency":
                w.termFrequency(params.get("format"));
                break;
        }
        w.writeData("/tmp/demo/data-" + System.currentTimeMillis());
        w.writeIndex("/tmp/demo/index-" + System.currentTimeMillis());
        w.execute(Integer.parseInt(params.get("nodes")));
        long end = System.nanoTime();
        return "took: " + TimeUnit.NANOSECONDS.toSeconds(end - start) + "s";
    }
}

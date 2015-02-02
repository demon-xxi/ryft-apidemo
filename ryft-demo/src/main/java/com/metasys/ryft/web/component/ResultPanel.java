package com.metasys.ryft.web.component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.metasys.ryft.Result;
import com.metasys.ryft.Result.Line;
import com.metasys.ryft.Result.Stat;
import com.metasys.ryft.api.FileBrowserApi;
import com.metasys.ryft.program.RyftPrimitives.Statistics;

/**
 * Panel wrapping all the query execution's statistics.
 *
 * @author Sylvain Crozon
 *
 */
public class ResultPanel extends Panel implements IPageable {

    private static final String RESULTS_ID = "results";
    private static final String LINE_NUMBER_ID = "line#";
    private static final String LINE_ID = "line";
    private static final String NAV_ID = "nav";

    private static final String STATISTICS_ID = "statistics";
    private static final String NAME_ID = "name";
    private static final String VALUE_ID = "value";
    private static final String OUTPUT_FILE_ID = "outputFile";
    private static final String INDEX_FILE_ID = "indexFile";
    private static final String START_TIME_FORMAT = "hh:mm:ss";

    private static final int BYTES_UNIT = 1024;
    private static final Map<TimeUnit, String> TIME_UNITS;
    private static final NumberFormat NUMBER_FORMAT;

    private static final int RESULTS_PER_PAGE = 10;

    @SpringBean
    private FileBrowserApi fileBrowserApi;

    static {
        TIME_UNITS = new LinkedHashMap<>();
        TIME_UNITS.put(TimeUnit.DAYS, "d");
        TIME_UNITS.put(TimeUnit.HOURS, "h");
        TIME_UNITS.put(TimeUnit.MINUTES, "min");
        TIME_UNITS.put(TimeUnit.SECONDS, "s");
        TIME_UNITS.put(TimeUnit.MILLISECONDS, "ms");
        NUMBER_FORMAT = NumberFormat.getInstance(Locale.ENGLISH);
        NUMBER_FORMAT.setGroupingUsed(true);
        NUMBER_FORMAT.setMaximumFractionDigits(2);
    }

    private Result result;
    private ListView<Line> results;
    private long currentPage;

    public ResultPanel(String id, Result result) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.result = result;
        results = new ListView<Line>(RESULTS_ID, new LineModel()) {
            @Override
            protected void populateItem(ListItem<Line> item) {
                item.add(new Label(LINE_NUMBER_ID, item.getModelObject().getNumber()));
                item.add(new Label(LINE_ID, item.getModelObject().getContent()));
                if (item.getModelObject().getNumber() % 2 == 0) {
                    item.add(new AttributeAppender("class", "odd").setSeparator(" "));
                }
            }
        };
        add(results);
        results.setOutputMarkupId(true);
        add(new PagingNavigator(NAV_ID, this));
        add(new ListView<Stat>(STATISTICS_ID, new PropertyModel<List<Stat>>(result, STATISTICS_ID)) {
            @Override
            protected void populateItem(ListItem<Stat> item) {
                String statName = item.getModelObject().getName().name();
                String name = new StringResourceModel(statName, this, null, statName).getObject();
                item.add(new Label(NAME_ID, name));
                if (item.getModelObject().getName() == Statistics.START_TIME && item.getModelObject().getValue() != null) {
                    SimpleDateFormat format = new SimpleDateFormat(START_TIME_FORMAT, Locale.ENGLISH);
                    item.add(new Label(VALUE_ID, format.format(new Date((long) item.getModelObject().getValue()))));
                } else if (item.getModelObject().getName() == Statistics.EXECUTION_DURATION && item.getModelObject().getValue() != null) {
                    item.add(new Label(VALUE_ID, formatDuration((long) item.getModelObject().getValue())));
                } else if (item.getModelObject().getName() == Statistics.TOTAL_BYTES_PROCESSED && item.getModelObject().getValue() != null) {
                    item.add(new Label(VALUE_ID, formatSize((long) item.getModelObject().getValue())));
                } else if (item.getModelObject().getValue() instanceof Number) {
                    item.add(new Label(VALUE_ID, NUMBER_FORMAT.format(item.getModelObject().getValue())));
                } else {
                    item.add(new Label(VALUE_ID, new PropertyModel<>(item.getModel(), VALUE_ID)));
                }
            }
        });
        add(new OutputLink(OUTPUT_FILE_ID, new FileModel(result, OUTPUT_FILE_ID)));
        add(new OutputLink(INDEX_FILE_ID, new FileModel(result, INDEX_FILE_ID)));
    }

    @Override
    public boolean isVisible() {
        return !result.getStatistics().isEmpty();
    }

    @Override
    public long getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(long page) {
        currentPage = page;
    }

    @Override
    public long getPageCount() {
        for (Stat stat : result.getStatistics()) {
            if (Statistics.NUMBER_OF_TERMS == stat.getName() || Statistics.TOTAL_NUMBER_OF_MATCHES == stat.getName()) {
                return (long) Math.ceil((long) stat.getValue() / RESULTS_PER_PAGE);
            }
        }
        return 0;
    }

    public Result getResult() {
        return result;
    }

    private String formatDuration(long duration) {
        StringBuilder builder = new StringBuilder();
        long remaining = duration;
        for (Entry<TimeUnit, String> unit : TIME_UNITS.entrySet()) {
            long unitValue = unit.getKey().convert(remaining, TimeUnit.MILLISECONDS);
            if (unitValue > 0 || builder.length() > 0) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(unitValue).append(unit.getValue());
                remaining -= TimeUnit.MILLISECONDS.convert(unitValue, unit.getKey());
            }
        }
        if (builder.length() == 0) {
            builder.append(0).append(TIME_UNITS.get(TimeUnit.MILLISECONDS));
        }
        return builder.toString();
    }

    private String formatSize(long size) {
        if (size < BYTES_UNIT) {
            return size + " B";
        }
        int exp = (int) (Math.log(size) / Math.log(BYTES_UNIT));
        char unit;
        String prefix = "KMGTP";
        if (exp < prefix.length()) {
            unit = prefix.charAt(exp - 1);
        } else {
            unit = 'P';
            exp = prefix.length();
        }
        double value = size / Math.pow(BYTES_UNIT, exp);
        return String.format("%.2f %sB", value, unit);
    }

    class LineModel extends AbstractReadOnlyModel<List<Line>> {

        @Override
        public List<Line> getObject() {
            List<Line> lines = new ArrayList<>(10);
            try {
                File outputFile = fileBrowserApi.getFile(result.getOutputFile());
                if (fileBrowserApi.isAcceptable(outputFile)) {
                    RandomAccessFile raf = new RandomAccessFile(outputFile, "r");
                    String line = raf.readLine();
                    int lineCount = 0;
                    while (line != null && lines.size() < RESULTS_PER_PAGE) {
                        if (lineCount >= currentPage * RESULTS_PER_PAGE) {
                            lines.add(new Line(lineCount + 1, line));
                        }
                        lineCount++;
                        line = raf.readLine();
                    }
                    raf.close();
                } else {
                    lines.add(new Line(0, "Error reading output file"));
                }
            } catch (IOException e) {
                lines.add(new Line(0, "Error reading output file"));
            }
            return lines;
        }

    }

    class OutputLink extends ExternalLink {

        public OutputLink(String id, IModel<String> href) {
            super(id, href);
        }

        @Override
        public boolean isVisible() {
            return getDefaultModel().getObject() != null;
        }

    }

    class FileModel extends AbstractReadOnlyModel<String> {

        private PropertyModel<String> innerModel;

        public FileModel(Object modelObject, String expression) {
            super();
            innerModel = new PropertyModel<>(modelObject, expression);
        }

        @Override
        public String getObject() {
            return innerModel.getObject() == null ? null : "/api/file?file=/" + innerModel.getObject();
        }

    }

}

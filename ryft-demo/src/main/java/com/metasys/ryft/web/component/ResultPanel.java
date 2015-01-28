package com.metasys.ryft.web.component;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import com.metasys.ryft.Result;
import com.metasys.ryft.Result.Stat;
import com.metasys.ryft.program.RyftPrimitives.Statistics;

/**
 * Panel wrapping all the query execution's statistics.
 *
 * @author Sylvain Crozon
 *
 */
public class ResultPanel extends Panel {

    private static final String STATISTICS_ID = "statistics";
    private static final String NAME_ID = "name";
    private static final String VALUE_ID = "value";
    private static final String OUTPUT_FILE_ID = "outputFile";
    private static final String INDEX_FILE_ID = "indexFile";
    private static final String START_TIME_FORMAT = "hh:mm:ss";

    private static final int BYTES_UNIT = 1024;
    private static final Map<TimeUnit, String> TIME_UNITS;
    private static final NumberFormat NUMBER_FORMAT;

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
    private ListView<Stat> listView;

    public ResultPanel(String id, Result result) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.result = result;
        listView = new ListView<Stat>(STATISTICS_ID, new PropertyModel<List<Stat>>(result, STATISTICS_ID)) {

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
        };
        add(listView);
        add(new OutputLink(OUTPUT_FILE_ID, new FileModel(result, OUTPUT_FILE_ID)));
        add(new OutputLink(INDEX_FILE_ID, new FileModel(result, INDEX_FILE_ID)));
    }

    @Override
    public boolean isVisible() {
        return !result.getStatistics().isEmpty();
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

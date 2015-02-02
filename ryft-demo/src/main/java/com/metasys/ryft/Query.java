package com.metasys.ryft;

import java.io.Serializable;

/**
 * Bean holding the user input to execute one of the available algorithms.
 *
 * @author Sylvain Crozon
 *
 */
public class Query implements Serializable {

    public static final String SEARCH = "search";
    public static final String FUZZY = "fuzzy";
    public static final String TERM = "term";
    public static final String SORT = "sort";

    public static final String RAW = "RAW_TEXT";
    public static final String RECORD = "RECORD";
    public static final String FIELD = RECORD + '.';

    private static final int DEFAULT_NODES = 2;
    private static final String DEFAULT_OUTPUT = "demo_output_";

    public enum SortOrder {
        ASC, DESC;
    }

    private String id;
    private String type;
    private String input;
    private String output;
    private boolean writeIndex;
    private Integer nodes;

    private String searchQuery;
    private Integer searchWidth;

    private String fuzzyQuery;
    private Integer fuzzyWidth;
    private Integer fuzziness;

    private String sortField;
    private SortOrder sortOrder;
    private boolean sortDescending;

    private String termFormat;
    private String termField;

    public void validate() throws RyftException {
        if (type == null) {
            throw new RyftException("The type of query must be specified");
        }
        if (input == null) {
            throw new RyftException("The input dataset must be specified");
        }
        if (output == null) {
            output = DEFAULT_OUTPUT + System.currentTimeMillis();
        }
        if (nodes == null) {
            nodes = DEFAULT_NODES;
        } else if (nodes < 0 || nodes > 4) {
            throw new RyftException("The number of nodes should be 1 to 4");
        }
        switch (type) {
            case SEARCH:
                if (searchQuery == null || searchWidth == null) {
                    throw new RyftException("Both the query string and surrounding width must be specified for a search query");
                }
                break;
            case FUZZY:
                if (fuzzyQuery == null || fuzzyWidth == null || fuzziness == null) {
                    throw new RyftException("The query string, surrounding width and fuzziness must be specified for a fuzzy search query");
                }
                break;
            case TERM:
                if (termField == null || termFormat == null) {
                    throw new RyftException("Both the field and format must be specified for a term frequency query");
                }
                break;
            case SORT:
                if (sortOrder == null) {
                    sortOrder = sortDescending ? SortOrder.DESC : SortOrder.ASC;
                }
                if (sortField == null) {
                    throw new RyftException("The sort field must be specified for a sort query");
                }
                break;
            default:
                throw new RyftException("Unknown query type");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public String[] getInputFiles() {
        return input.split(",");
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isWriteIndex() {
        return writeIndex;
    }

    public void setWriteIndex(boolean writeIndex) {
        this.writeIndex = writeIndex;
    }

    public Integer getNodes() {
        return nodes;
    }

    public void setNodes(Integer nodes) {
        this.nodes = nodes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getSearchWidth() {
        return searchWidth;
    }

    public void setSearchWidth(Integer searchWidth) {
        this.searchWidth = searchWidth;
    }

    public String getFuzzyQuery() {
        return fuzzyQuery;
    }

    public void setFuzzyQuery(String fuzzyQuery) {
        this.fuzzyQuery = fuzzyQuery;
    }

    public Integer getFuzzyWidth() {
        return fuzzyWidth;
    }

    public void setFuzzyWidth(Integer fuzzyWidth) {
        this.fuzzyWidth = fuzzyWidth;
    }

    public Integer getFuzziness() {
        return fuzziness;
    }

    public void setFuzziness(Integer fuzziness) {
        this.fuzziness = fuzziness;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    public String getTermFormat() {
        return termFormat;
    }

    public void setTermFormat(String termFormat) {
        this.termFormat = termFormat;
    }

    public String getTermField() {
        return termField;
    }

    public void setTermField(String termField) {
        this.termField = termField;
    }

    @Override
    public String toString() {
        return "Query [id=" + id + ", type=" + type + ", input=" + input + ", output=" + output + ", writeIndex=" + writeIndex + ", nodes=" + nodes
                + ", searchQuery=" + searchQuery + ", searchWidth=" + searchWidth + ", fuzzyQuery=" + fuzzyQuery + ", fuzzyWidth=" + fuzzyWidth
                + ", fuzziness=" + fuzziness + ", sortField=" + sortField + ", sortOrder=" + sortOrder + ", sortDescending=" + sortDescending
                + ", termFormat=" + termFormat + ", termField=" + termField + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (sortDescending ? 1231 : 1237);
        result = prime * result + (fuzziness == null ? 0 : fuzziness.hashCode());
        result = prime * result + (fuzzyQuery == null ? 0 : fuzzyQuery.hashCode());
        result = prime * result + (fuzzyWidth == null ? 0 : fuzzyWidth.hashCode());
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (input == null ? 0 : input.hashCode());
        result = prime * result + (nodes == null ? 0 : nodes.hashCode());
        result = prime * result + (output == null ? 0 : output.hashCode());
        result = prime * result + (searchQuery == null ? 0 : searchQuery.hashCode());
        result = prime * result + (searchWidth == null ? 0 : searchWidth.hashCode());
        result = prime * result + (sortField == null ? 0 : sortField.hashCode());
        result = prime * result + (sortOrder == null ? 0 : sortOrder.hashCode());
        result = prime * result + (termField == null ? 0 : termField.hashCode());
        result = prime * result + (termFormat == null ? 0 : termFormat.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        result = prime * result + (writeIndex ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Query other = (Query) obj;
        if (sortDescending != other.sortDescending) {
            return false;
        }
        if (fuzziness == null) {
            if (other.fuzziness != null) {
                return false;
            }
        } else if (!fuzziness.equals(other.fuzziness)) {
            return false;
        }
        if (fuzzyQuery == null) {
            if (other.fuzzyQuery != null) {
                return false;
            }
        } else if (!fuzzyQuery.equals(other.fuzzyQuery)) {
            return false;
        }
        if (fuzzyWidth == null) {
            if (other.fuzzyWidth != null) {
                return false;
            }
        } else if (!fuzzyWidth.equals(other.fuzzyWidth)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (input == null) {
            if (other.input != null) {
                return false;
            }
        } else if (!input.equals(other.input)) {
            return false;
        }
        if (nodes == null) {
            if (other.nodes != null) {
                return false;
            }
        } else if (!nodes.equals(other.nodes)) {
            return false;
        }
        if (output == null) {
            if (other.output != null) {
                return false;
            }
        } else if (!output.equals(other.output)) {
            return false;
        }
        if (searchQuery == null) {
            if (other.searchQuery != null) {
                return false;
            }
        } else if (!searchQuery.equals(other.searchQuery)) {
            return false;
        }
        if (searchWidth == null) {
            if (other.searchWidth != null) {
                return false;
            }
        } else if (!searchWidth.equals(other.searchWidth)) {
            return false;
        }
        if (sortField == null) {
            if (other.sortField != null) {
                return false;
            }
        } else if (!sortField.equals(other.sortField)) {
            return false;
        }
        if (sortOrder != other.sortOrder) {
            return false;
        }
        if (termField == null) {
            if (other.termField != null) {
                return false;
            }
        } else if (!termField.equals(other.termField)) {
            return false;
        }
        if (termFormat == null) {
            if (other.termFormat != null) {
                return false;
            }
        } else if (!termFormat.equals(other.termFormat)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (writeIndex != other.writeIndex) {
            return false;
        }
        return true;
    }

}

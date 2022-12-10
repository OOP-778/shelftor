package net.manga.core.query;

import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import net.manga.api.query.Query;

public class QueryImpl implements Query {
    private String index;
    private Object value;
    private Operator operator;
    private Collection<QueryImpl> queries;

    public QueryImpl() {
        this.queries = new ArrayList<>();
    }

    public QueryImpl(@NonNull String index, @NonNull Object value, Operator operator) {
        this();
        this.index = index;
        this.value = value;
        this.operator = operator;
    }

    public Object getValue() {
        return this.value;
    }

    public Operator getOperator() {
        return this.operator;
    }

    public String getIndex() {
        return this.index;
    }

    public Collection<QueryImpl> getQueries() {
        return this.queries;
    }

    private boolean isInitialized() {
        return this.index == null && this.value == null && this.operator == null;
    }

    @Override
    public Query or(@NonNull Query query) {
        if (!this.isInitialized()) {
            final QueryImpl subQuery = (QueryImpl) query;
            this.operator = subQuery.getOperator();
            this.index = subQuery.getIndex();
            this.value = subQuery.getValue();
            this.queries = subQuery.getQueries();
            return this;
        }

        this.queries.add((QueryImpl) query);
        this.operator = Operator.OR;

        return this;
    }

    @Override
    public Query and(@NonNull Query query) {
        this.queries.add((QueryImpl) query);
        this.operator = Operator.AND;

        return this;
    }

    public enum Operator {
        AND,
        OR
    }
}

package dev.oop778.shelftor.core.query;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import dev.oop778.shelftor.api.query.Query;
import lombok.ToString;

@ToString
public class CoreQuery implements Query {
    private String index;
    private Object value;
    private Operator operator;
    private List<CoreQuery> queries;

    public CoreQuery() {
        this.queries = new ArrayList<>();
    }

    public CoreQuery(@NonNull String index, @NonNull Object value, Operator operator) {
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

    public List<CoreQuery> getQueries() {
        return this.queries;
    }

    public boolean isInitialized() {
        return !(this.index == null && this.value == null);
    }

    @Override
    public Query or(@NonNull Query query) {
        if (!this.isInitialized()) {
            final CoreQuery subQuery = (CoreQuery) query;
            this.operator = subQuery.getOperator();
            this.index = subQuery.getIndex();
            this.value = subQuery.getValue();
            this.queries = subQuery.getQueries();
            return this;
        }

        this.queries.add((CoreQuery) query);
        this.operator = Operator.OR;

        return this;
    }

    @Override
    public Query and(@NonNull Query query) {
        if (!this.isInitialized()) {
            final CoreQuery subQuery = (CoreQuery) query;
            this.operator = subQuery.getOperator();
            this.index = subQuery.getIndex();
            this.value = subQuery.getValue();
            this.queries = subQuery.getQueries();
            return this;
        }

        this.queries.add((CoreQuery) query);
        this.operator = Operator.AND;

        return this;
    }

    @Override
    public Query or(@NonNull String index, @NonNull Object value) {
        if (!this.isInitialized()) {
            this.index = index;
            this.value = value;
            this.operator = Operator.OR;
            return this;
        }

        this.operator = Operator.OR;
        this.queries.add(new CoreQuery(index, value, Operator.OR));
        return this;
    }

    @Override
    public Query and(@NonNull String index, @NonNull Object value) {
        if (!this.isInitialized()) {
            this.index = index;
            this.value = value;
            this.operator = Operator.AND;
            return this;
        }

        this.operator = Operator.AND;
        this.queries.add(new CoreQuery(index, value, Operator.AND));
        return this;
    }

    public CoreQuery single() {
        return new CoreQuery(this.index, this.value, this.operator);
    }

    public enum Operator {
        AND,
        OR
    }
}

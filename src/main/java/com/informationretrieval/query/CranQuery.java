package com.informationretrieval.query;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CranQuery {

    public static Builder builder() {
        return new AutoValue_CranQuery.Builder();
    }

    public abstract String id();

    public abstract String queryContent();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(String id);

        public abstract Builder queryContent(String queryContent);

        public abstract CranQuery build();
    }
}
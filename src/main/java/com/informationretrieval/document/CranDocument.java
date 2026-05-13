package com.informationretrieval.document;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class CranDocument {

    public static Builder builder() {
        return new AutoValue_CranDocument.Builder();
    }

    public abstract String id();

    public abstract String title();

    public abstract String author();

    public abstract String bibliography();

    public abstract String words();



    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(String id);

        public abstract Builder title(String title);

        public abstract Builder author(String author);

        public abstract Builder bibliography(String bibliography);

        public abstract Builder words(String words);



        public abstract CranDocument build();
    }
}
package com.informationretrieval.evaluate;

import com.google.auto.value.AutoValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class EvalResult {

    public static EvalResult.Builder builder() {
        return new AutoValue_EvalResult.Builder()
                .metrics(new HashMap<>());
    }

    public abstract String runID();

    public abstract Map<String, String> metrics();

    public List<String> getMetricsKeys() {
        return metrics().keySet().stream().toList();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder runID(String runId);

        public abstract Builder metrics(Map<String, String> metrics);

        abstract Map<String, String> metrics();

        public void setMetricValue(String key, String value) {
            metrics().put(key, value);
        }

        public abstract EvalResult build();
    }
}

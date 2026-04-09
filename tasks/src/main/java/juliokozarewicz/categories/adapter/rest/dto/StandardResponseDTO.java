package juliokozarewicz.categories.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "timestamp",
    "statusCode",
    "messageCode",
    "fieldErrors",
    "data",
    "meta",
    "links"
})
public class StandardResponseDTO {

    private final Instant timestamp;
    private final int statusCode;
    private final String messageCode;
    private List<Map<String, String>> fieldErrors;
    private final Object data;
    private final Map<String, Object> meta;
    private final Map<String, Object> links;

    private StandardResponseDTO(Builder builder) {
        this.timestamp = builder.timestamp;
        this.statusCode = builder.statusCode;
        this.messageCode = builder.messageCode;
        this.fieldErrors = builder.fieldErrors;
        this.data = builder.data;
        this.meta = builder.meta.isEmpty() ? null : builder.meta;
        this.links = builder.links.isEmpty() ? null : builder.links;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatusCode () {
        return statusCode;
    }

    public String getMessageCode () {
        return messageCode;
    }

    public List<Map<String, String>> getFieldErrors() {
        return fieldErrors;
    }

    public Object getData() {
        return data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Map<String, Object> getLinks() {
        return links;
    }

    public static class Builder {

        private Instant timestamp;
        private int statusCode;
        private String messageCode;
        private List<Map<String, String>> fieldErrors;
        private Object data;
        private Map<String, Object> meta = new HashMap<>();
        private Map<String, Object> links = new HashMap<>();

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder messageCode(String messageCode) {
            this.messageCode = messageCode;
            return this;
        }

        public Builder fieldErrors(List<Map<String, String>> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder meta(Map<String, Object> meta) {
            this.meta = meta != null ? meta : new HashMap<>();
            return this;
        }

        public Builder links(Map<String, Object> links) {
            this.links = links != null ? links : new HashMap<>();
            return this;
        }

        public StandardResponseDTO build() {
            return new StandardResponseDTO(this);
        }

    }

}
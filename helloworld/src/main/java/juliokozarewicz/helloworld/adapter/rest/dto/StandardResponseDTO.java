package juliokozarewicz.helloworld.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "createdAt",
    "statusCode",
    "messageCode",
    "fieldErrors",
    "data",
    "meta",
    "links"
})
public class StandardResponseDTO {

    private final Instant createdAt;
    private final int statusCode;
    private final String messageCode;
    private List<Map<String, String>> fieldErrors;
    private final Object data;
    private final Map<String, Object> meta;
    private final Map<String, String> links;

    private StandardResponseDTO(Builder builder) {
        this.createdAt = builder.createdAt;
        this.statusCode = builder.statusCode;
        this.messageCode = builder.messageCode;
        this.fieldErrors = builder.fieldErrors;
        this.data = builder.data;
        this.meta = builder.meta.isEmpty() ? null : builder.meta;
        this.links = builder.links.isEmpty() ? null : builder.links;
    }

    public Instant getCreatedAt() {
        return createdAt;
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

    public Map<String, String> getLinks() {
        return links;
    }

    public static class Builder {

        private Instant createdAt;
        private int statusCode;
        private String messageCode;
        private List<Map<String, String>> fieldErrors;
        private Object data;
        private Map<String, Object> meta = new HashMap<>();
        private Map<String, String> links = new HashMap<>();

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
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

        public Builder links(Map<String, String> links) {
            this.links = links != null ? links : new HashMap<>();
            return this;
        }

        public StandardResponseDTO build() {
            return new StandardResponseDTO(this);
        }

    }

}
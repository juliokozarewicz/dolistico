package juliokozarewicz.helloworld.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "statusCode",
    "messageCode",
    "field",
    "data",
    "meta",
    "links"
})
public class StandardResponse {

    private final int statusCode;
    private final String messageCode;
    private final String field;
    private final Object data;
    private final Map<String, Object> meta;
    private final Map<String, String> links;

    private StandardResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.messageCode = builder.messageCode;
        this.field = builder.field.isEmpty() ? null : builder.field;
        this.data = builder.data;
        this.meta = builder.meta.isEmpty() ? null : builder.meta;
        this.links = builder.links.isEmpty() ? null : builder.links;
    }

    public int getStatusCode () {
        return statusCode;
    }

    public String getMessageCode () {
        return messageCode;
    }

    public String getField() {
        return field;
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

        private int statusCode;
        private String messageCode;
        private String field = "";
        private Object data;
        private Map<String, Object> meta = new HashMap<>();
        private Map<String, String> links = new HashMap<>();

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder messageCode(String messageCode) {
            this.messageCode = messageCode;
            return this;
        }

        public Builder field(String field) {
            this.field = field != null ? field : "";
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

        public StandardResponse build() {
            return new StandardResponse(this);
        }

    }

}
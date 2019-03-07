package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Info {
    private String info;
    private String version;
    private String timestamp;
    private String docs;
    private Limit limit;

    @JsonProperty("info")
    public String getInfo() { return info; }
    @JsonProperty("info")
    public void setInfo(String value) { this.info = value; }

    @JsonProperty("version")
    public String getVersion() { return version; }
    @JsonProperty("version")
    public void setVersion(String value) { this.version = value; }

    @JsonProperty("timestamp")
    public String getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    public void setTimestamp(String value) { this.timestamp = value; }

    @JsonProperty("docs")
    public String getDocs() { return docs; }
    @JsonProperty("docs")
    public void setDocs(String value) { this.docs = value; }

    @JsonProperty("limit")
    public Limit getLimit() { return limit; }
    @JsonProperty("limit")
    public void setLimit(Limit value) { this.limit = value; }
}

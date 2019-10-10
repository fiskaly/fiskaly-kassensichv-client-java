package persistence;

public class Request {
    private String url;
    private String body;
    private String method;

    public Request(String url, String body, String method) {
        this.url = url;
        this.body = body;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Method: " + method + "\n" +
                "URL: " + url + "\n" +
                "Body:\n" + body + "\n";
    }
}

package kenhoang.dev.app.livewallpaper.model;

public class ComputerVision {
    private Adult adult;
    private String requestId;
    private Metadata metadata;

    public ComputerVision(Adult adult, String requestId, Metadata metadata) {
        this.adult = adult;
        this.requestId = requestId;
        this.metadata = metadata;
    }

    public ComputerVision() {
    }

    public Adult getAdult() {
        return adult;
    }

    public void setAdult(Adult adult) {
        this.adult = adult;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}

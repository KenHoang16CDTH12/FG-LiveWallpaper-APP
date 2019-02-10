package kenhoang.dev.app.livewallpaper.model;

public class Metadata {
    private int height;
    private int width;
    private String format;

    public Metadata(int height, int width, String format) {
        this.height = height;
        this.width = width;
        this.format = format;
    }

    public Metadata() {
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}

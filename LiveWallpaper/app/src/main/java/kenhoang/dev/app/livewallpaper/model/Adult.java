package kenhoang.dev.app.livewallpaper.model;

public class Adult {
    private boolean isAdultContent;
    private double adultScore;
    private boolean isRacyContent;
    private double racyScore;

    public Adult() {
    }

    public Adult(boolean isAdultContent, double adultScore, boolean isRacyContent, double racyScore) {
        this.isAdultContent = isAdultContent;
        this.adultScore = adultScore;
        this.isRacyContent = isRacyContent;
        this.racyScore = racyScore;
    }

    public boolean isAdultContent() {
        return isAdultContent;
    }

    public void setAdultContent(boolean adultContent) {
        isAdultContent = adultContent;
    }

    public double getAdultScore() {
        return adultScore;
    }

    public void setAdultScore(double adultScore) {
        this.adultScore = adultScore;
    }

    public boolean isRacyContent() {
        return isRacyContent;
    }

    public void setRacyContent(boolean racyContent) {
        isRacyContent = racyContent;
    }

    public double getRacyScore() {
        return racyScore;
    }

    public void setRacyScore(double racyScore) {
        this.racyScore = racyScore;
    }
}

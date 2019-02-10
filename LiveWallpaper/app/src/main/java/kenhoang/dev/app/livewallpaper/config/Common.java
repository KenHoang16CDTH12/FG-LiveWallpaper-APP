package kenhoang.dev.app.livewallpaper.config;

import kenhoang.dev.app.livewallpaper.model.ComputerVision;
import kenhoang.dev.app.livewallpaper.model.Wallpaper;
import kenhoang.dev.app.livewallpaper.remote.IComputerVision;
import kenhoang.dev.app.livewallpaper.remote.RetrofitClient;

public class Common {
    // Key
    public static final String STR_CATEGORY_REF = "CategoryBackground";
    public static final String STR_WALLPAPER = "Wallpapers";
    public static final int SIGN_IN_REQUEST_CODE = 1001;
    public static final int PICK_IMAGE_REQUEST = 1002;

    public static String CATEGORY_SELECTED = "";
    public static String CATEGORY_ID_SELECTED = "";

    public static final int PERMISSION_REQUEST_CODE = 1000;

    public static Wallpaper selecte_background = new Wallpaper();

    public static String select_background_key;

    // API
    public static final String BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/";
    public static IComputerVision getComputerVisionAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IComputerVision.class);
    }
    public static String getAPIAdultEndpoint() {
        return new StringBuilder(BASE_URL).append("analyze?visualFeatures=Adult&language=en").toString();
    }
}

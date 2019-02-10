package kenhoang.dev.app.livewallpaper.remote;

import kenhoang.dev.app.livewallpaper.model.ComputerVision;
import kenhoang.dev.app.livewallpaper.model.URLUpload;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IComputerVision {
    @Headers({
            "Content-type: application/json",
            "Ocp-Apim-Subscription-Key:5772c89d317e478b9e41cacba8eb906f"
    })
    @POST
    Call<ComputerVision> analyzeImage(@Url String apiEndpoint, @Body URLUpload url);
}

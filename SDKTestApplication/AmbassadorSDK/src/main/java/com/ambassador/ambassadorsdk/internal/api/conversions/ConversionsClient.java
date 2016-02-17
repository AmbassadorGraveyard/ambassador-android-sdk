package com.ambassador.ambassadorsdk.internal.api.conversions;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Defines endpoints, parameters, callbacks for ConversionsApi methods
 */
public interface ConversionsClient {

    /** Base url for api methods */
    String ENDPOINT = new StringResource(BuildConfig.IS_RELEASE_BUILD ? R.string.ambassador_api_url : R.string.ambassador_api_url_dev).getValue();

    /**
     * https://api.getambassador.com/universal/action/conversion/
     * https://dev-ambassador-api.herokuapp.com/universal/action/conversion/
     * @param universalId the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param uid the Ambassador universal id
     * @param body the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/universal/action/conversion/")
    void registerConversionRequest(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Query("u") String uid,
            @Body ConversionsApi.RegisterConversionRequestBody body,
            Callback<ConversionsApi.RegisterConversionRequestResponse> callback
    );

}

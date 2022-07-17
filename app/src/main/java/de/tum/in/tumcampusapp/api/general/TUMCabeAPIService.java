package de.tum.in.tumcampusapp.api.general;

import java.util.List;

import de.tum.in.tumcampusapp.api.general.model.DeviceRegister;
import de.tum.in.tumcampusapp.api.general.model.DeviceUploadFcmToken;
import de.tum.in.tumcampusapp.api.general.model.ObfuscatedIdsUpload;
import de.tum.in.tumcampusapp.api.general.model.TUMCabeStatus;
import de.tum.in.tumcampusapp.api.general.model.TUMCabeVerification;
import de.tum.in.tumcampusapp.api.general.model.UploadStatus;
import de.tum.in.tumcampusapp.component.tumui.feedback.model.Feedback;
import de.tum.in.tumcampusapp.component.tumui.feedback.model.FeedbackResult;
import de.tum.in.tumcampusapp.component.ui.cafeteria.model.Cafeteria;
import de.tum.in.tumcampusapp.component.ui.openinghour.model.Location;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatMember;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatMessage;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatRoom;
import de.tum.in.tumcampusapp.component.ui.updatenote.model.UpdateNote;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_CAFETERIAS;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_CHAT_MEMBERS;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_CHAT_ROOMS;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_DEVICE;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_FEEDBACK;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_MEMBERS;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_OPENING_HOURS;
import static de.tum.in.tumcampusapp.api.general.TUMCabeClient.API_UPDATE_NOTE;

public interface TUMCabeAPIService {

    //Group chat
    @POST(API_CHAT_ROOMS)
    Call<ChatRoom> createRoom(@Body TUMCabeVerification verification);

    @GET(API_CHAT_ROOMS + "{room}")
    Call<ChatRoom> getChatRoom(@Path("room") int id);

    @POST(API_CHAT_ROOMS + "{room}/leave/")
    Call<ChatRoom> leaveChatRoom(@Path("room") int roomId, @Body TUMCabeVerification verification);

    @POST(API_CHAT_ROOMS + "{room}/add/{member}")
    Call<ChatRoom> addUserToChat(@Path("room") int roomId, @Path("member") int userId, @Body TUMCabeVerification verification);

    //Get/Update single message
    @PUT(API_CHAT_ROOMS + "{room}/message/")
    Observable<ChatMessage> sendMessage(@Path("room") int roomId, @Body TUMCabeVerification message);

    @PUT(API_CHAT_ROOMS + "{room}/message/{message}/")
    Observable<ChatMessage> updateMessage(@Path("room") int roomId, @Path("message") String messageId, @Body TUMCabeVerification message);

    //Get all recent messages or older ones
    @POST(API_CHAT_ROOMS + "{room}/messages/{page}/")
    Observable<List<ChatMessage>> getMessages(@Path("room") int roomId, @Path("page") long messageId, @Body TUMCabeVerification verification);

    @POST(API_CHAT_ROOMS + "{room}/messages/")
    Observable<List<ChatMessage>> getNewMessages(@Path("room") int roomId, @Body TUMCabeVerification verification);

    @POST(API_CHAT_MEMBERS)
    Call<ChatMember> createMember(@Body ChatMember chatMember);

    @GET(API_CHAT_MEMBERS + "{lrz_id}/")
    Call<ChatMember> getMember(@Path("lrz_id") String lrzId);

    @GET(API_CHAT_MEMBERS + "search/{query}/")
    Call<List<ChatMember>> searchMemberByName(@Path("query") String nameQuery);

    @POST(API_CHAT_MEMBERS + "{memberId}/rooms/")
    Call<List<ChatRoom>> getMemberRooms(@Path("memberId") int memberId, @Body TUMCabeVerification verification);

    @POST(API_MEMBERS + "uploadIds/{lrzId}/")
    Observable<TUMCabeStatus> uploadObfuscatedIds(@Path("lrzId") String lrzId, @Body ObfuscatedIdsUpload ids);

    //Device
    @POST(API_DEVICE + "register/")
    Call<TUMCabeStatus> deviceRegister(@Body DeviceRegister verification);

    @GET(API_DEVICE + "verifyKey/")
    Call<TUMCabeStatus> verifyKey();

    @POST(API_DEVICE + "addGcmToken/")
    Call<TUMCabeStatus> deviceUploadGcmToken(@Body DeviceUploadFcmToken verification);

    @GET(API_DEVICE + "uploaded/{lrzId}")
    Call<UploadStatus> getUploadStatus(@Path("lrzId") String lrzId);

    //RoomFinder maps
//    @GET(API_ROOM_FINDER + API_ROOM_FINDER_AVAILABLE_MAPS + "{archId}")
//    Call<List<RoomFinderMap>> fetchAvailableMaps(@Path("archId") String archId);

    //RoomFinder maps
//    @GET(API_ROOM_FINDER + API_ROOM_FINDER_SEARCH + "{searchStrings}")
//    Call<List<RoomFinderRoom>> fetchRooms(@Path("searchStrings") String searchStrings);

    //RoomFinder cordinates
//    @GET(API_ROOM_FINDER + API_ROOM_FINDER_COORDINATES + "{archId}")
//    Call<RoomFinderCoordinateInterface> fetchCoordinates(@Path("archId") String archId);

    //RoomFinder schedule
//    @GET(API_ROOM_FINDER + API_ROOM_FINDER_SCHEDULE + "{roomId}" + "/" + "{start}" + "/" + "{end}")
//    Call<List<RoomFinderScheduleInterface>> fetchSchedule(@Path("roomId") String archId,
//                                                          @Path("start") String start, @Path("end") String end);

    @POST(API_FEEDBACK)
    Call<FeedbackResult> sendFeedback(@Body Feedback feedback);

    @Multipart
    @POST(API_FEEDBACK + "{id}/{image}/")
    Call<FeedbackResult> sendFeedbackImage(@Part MultipartBody.Part image, @Path("image") int imageNr, @Path("id") String feedbackId);

    @GET(API_CAFETERIAS)
    Observable<List<Cafeteria>> getCafeterias();

//    @GET(API_NEWS + "{lastNewsId}")
//    Call<List<NewsItem>> getNews(@Path("lastNewsId") String lastNewsId);

//    @GET(API_NEWS + "sources")
//    Call<List<NewsSources>> getNewsSources();

//    @GET(API_NEWS + "alert")
//    Observable<NewsAlert> getNewsAlert();

    // Update note
    @GET(API_UPDATE_NOTE + "{version}")
    Call<UpdateNote> getUpdateNote(@Path("version") int version);

    // Opening Hours
    @GET(API_OPENING_HOURS + "{language}")
    Call<List<Location>> getOpeningHours(@Path("language") String language);

}

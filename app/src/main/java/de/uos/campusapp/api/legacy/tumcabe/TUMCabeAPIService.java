package de.uos.campusapp.api.legacy.tumcabe;

import java.util.List;

import de.uos.campusapp.api.legacy.tumcabe.model.DeviceRegister;
import de.uos.campusapp.api.legacy.tumcabe.model.DeviceUploadFcmToken;
import de.uos.campusapp.api.legacy.tumcabe.model.ObfuscatedIdsUpload;
import de.uos.campusapp.api.legacy.tumcabe.model.TUMCabeStatus;
import de.uos.campusapp.api.legacy.tumcabe.model.TUMCabeVerification;
import de.uos.campusapp.api.legacy.tumcabe.model.UploadStatus;
import de.uos.campusapp.component.ui.legacy.feedback.model.Feedback;
import de.uos.campusapp.component.ui.legacy.feedback.model.FeedbackResult;
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteria;
import de.uos.campusapp.component.ui.openinghours.model.LocationItem;
import de.uos.campusapp.component.ui.chat.model.ChatMember;
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem;
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom;
import de.uos.campusapp.component.ui.legacy.updatenote.model.UpdateNote;
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

import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_CAFETERIAS;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_CHAT_MEMBERS;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_CHAT_ROOMS;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_DEVICE;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_FEEDBACK;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_MEMBERS;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_OPENING_HOURS;
import static de.uos.campusapp.api.legacy.tumcabe.TUMCabeClient.API_UPDATE_NOTE;

public interface TUMCabeAPIService {

    //Group chat
    @POST(API_CHAT_ROOMS)
    Call<AbstractChatRoom> createRoom(@Body TUMCabeVerification verification);

    @GET(API_CHAT_ROOMS + "{room}")
    Call<AbstractChatRoom> getChatRoom(@Path("room") int id);

    @POST(API_CHAT_ROOMS + "{room}/leave/")
    Call<AbstractChatRoom> leaveChatRoom(@Path("room") int roomId, @Body TUMCabeVerification verification);

    @POST(API_CHAT_ROOMS + "{room}/add/{member}")
    Call<AbstractChatRoom> addUserToChat(@Path("room") int roomId, @Path("member") int userId, @Body TUMCabeVerification verification);

    //Get/Update single message
    @PUT(API_CHAT_ROOMS + "{room}/message/")
    Observable<ChatMessageItem> sendMessage(@Path("room") int roomId, @Body TUMCabeVerification message);

    @PUT(API_CHAT_ROOMS + "{room}/message/{message}/")
    Observable<ChatMessageItem> updateMessage(@Path("room") int roomId, @Path("message") String messageId, @Body TUMCabeVerification message);

    //Get all recent messages or older ones
    @POST(API_CHAT_ROOMS + "{room}/messages/{page}/")
    Observable<List<ChatMessageItem>> getMessages(@Path("room") int roomId, @Path("page") long messageId, @Body TUMCabeVerification verification);

    @POST(API_CHAT_ROOMS + "{room}/messages/")
    Observable<List<ChatMessageItem>> getNewMessages(@Path("room") int roomId, @Body TUMCabeVerification verification);

    @POST(API_CHAT_MEMBERS)
    Call<ChatMember> createMember(@Body ChatMember chatMember);

    @GET(API_CHAT_MEMBERS + "{lrz_id}/")
    Call<ChatMember> getMember(@Path("lrz_id") String lrzId);

    @GET(API_CHAT_MEMBERS + "search/{query}/")
    Call<List<ChatMember>> searchMemberByName(@Path("query") String nameQuery);

    @POST(API_CHAT_MEMBERS + "{memberId}/rooms/")
    Call<List<AbstractChatRoom>> getMemberRooms(@Path("memberId") int memberId, @Body TUMCabeVerification verification);

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
    Observable<List<AbstractCafeteria>> getCafeterias();

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
    Call<List<LocationItem>> getOpeningHours(@Path("language") String language);

}

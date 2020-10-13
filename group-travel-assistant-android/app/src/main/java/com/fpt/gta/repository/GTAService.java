package com.fpt.gta.repository;

import java.math.BigDecimal;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.fpt.gta.webService.ConfigAPI.Api.ACTIVITYBYID;
import static com.fpt.gta.webService.ConfigAPI.Api.CHANGEORDER;
import static com.fpt.gta.webService.ConfigAPI.Api.CHANGEVOTEDEADLINEPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.CONVERTCURRENCY;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEACTIVITY;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEGROUPDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEGROUPTRANSACTIONDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEINVATIONCODECOPY;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEINVITATIONCODE;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATESUGGESTED;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATETASK;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATETRANSACTION;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATETRIP;
import static com.fpt.gta.webService.ConfigAPI.Api.CREATEVOTE;
import static com.fpt.gta.webService.ConfigAPI.Api.DEACTIVEMEMBERINGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEACTIVITY;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEDOCUMENTACTIVITY;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEGROUPDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEGROUPTRANSACTIONDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETESUGGESTED;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETETASK;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETETRANSACTION;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETETRANSACTIONDETAILSDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETETRIP;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEVOTE;
import static com.fpt.gta.webService.ConfigAPI.Api.DELETEVOTEPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLCURRENCY;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLGROUPTRANSACTIONDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLLISTTASK;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLLISTTASKGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.GETBUDGETELECTEDPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.GETHIGHESTVOTEPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.GETMYBUDGET;
import static com.fpt.gta.webService.ConfigAPI.Api.GETORCRARL;
import static com.fpt.gta.webService.ConfigAPI.Api.GETSEARCHSUGGESTEDPLACE;
import static com.fpt.gta.webService.ConfigAPI.Api.GETVIEWGROUPBYID;
import static com.fpt.gta.webService.ConfigAPI.Api.GETVIEWGROUPPREVIEW;
import static com.fpt.gta.webService.ConfigAPI.Api.GETVIEWTRIPDETAIL;
import static com.fpt.gta.webService.ConfigAPI.Api.MAKEPLANNING;
import static com.fpt.gta.webService.ConfigAPI.Api.MAKEREADY;
import static com.fpt.gta.webService.ConfigAPI.Api.MAKEPENDING;
import static com.fpt.gta.webService.ConfigAPI.Api.PICKHIGHESTVOTEPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLGROUPDOCUMENT;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLACTIVITYINPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLMEMBERINGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLPLANINTRIP;
import static com.fpt.gta.webService.ConfigAPI.Api.GETALLTRANSACTION;
import static com.fpt.gta.webService.ConfigAPI.Api.GETINVITATION;
import static com.fpt.gta.webService.ConfigAPI.Api.GETSUGGESTED;
import static com.fpt.gta.webService.ConfigAPI.Api.GETVIEWALLGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.GETVIEWALLTRIP;
import static com.fpt.gta.webService.ConfigAPI.Api.SETBUDGETINDIVIDUAL;
import static com.fpt.gta.webService.ConfigAPI.Api.SETBUDGETPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.SIGNOUTFRIBASE;
import static com.fpt.gta.webService.ConfigAPI.Api.SUGGESTEDPLAN;
import static com.fpt.gta.webService.ConfigAPI.Api.SYNCFIREBASE;
import static com.fpt.gta.webService.ConfigAPI.Api.SEARCHPLACE;
import static com.fpt.gta.webService.ConfigAPI.Api.SEARCHPLACECITIES;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATEACTIVITY;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATEGROUP;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATEGROUPBUDGET;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATEMYBUDGET;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATESUGGESTED;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATETASK;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATETRANSACTION;
import static com.fpt.gta.webService.ConfigAPI.Api.UPDATETRIP;
import static com.fpt.gta.webService.ConfigAPI.Api.VOTEPLAN;

public interface GTAService {

    @POST(SYNCFIREBASE)
    Call<ResponseBody> syncFirebase(
            @Body RequestBody requestBody
    );

    @POST(SIGNOUTFRIBASE)
    Call<ResponseBody> signoutFirebase(
            @Body RequestBody requestBody
    );

    @POST(CREATEGROUP)
    Call<ResponseBody> createGroup(
            @Body RequestBody requestBody
    );

    @GET(GETVIEWALLGROUP)
    Call<ResponseBody> getViewAllGroup();

    @GET(GETVIEWGROUPPREVIEW)
    Call<ResponseBody> getViewGroupPreview(
            @Path("idGroup") String idGroup
    );

    @GET(GETVIEWGROUPBYID)
    Call<ResponseBody> getViewGroupById(
            @Path("idGroup") Integer idGroup
    );

    @POST(MAKEPENDING)
    Call<ResponseBody> makePending(
            @Path("idGroup") Integer idGroup,
            @Body RequestBody requestBody
    );


    @PUT(UPDATEGROUP)
    Call<ResponseBody> updateGroup(
            @Body RequestBody requestBody
    );

    @DELETE(DELETEGROUP)
    Call<ResponseBody> deleteGroup(@Path("idGroup") int idGroup);


    @POST(CREATETRIP)
    Call<ResponseBody> createTrip(
            @Query("idGroup") int groupId,
            @Body RequestBody requestBody
    );

    @PUT(UPDATETRIP)
    Call<ResponseBody> updateTrip(
            @Body RequestBody requestBody
    );

    @DELETE(DELETETRIP)
    Call<ResponseBody> deleteTrip(
            @Path("tripId") int tripId);

    @GET(GETVIEWALLTRIP)
    Call<ResponseBody> getViewAllTrip(@Query("idGroup") int groupId);

    @GET(GETVIEWTRIPDETAIL)
    Call<ResponseBody> getViewTripDetail(
            @Path("idTrip") Integer idTrip);

    @GET(GETALLMEMBERINGROUP)
    Call<ResponseBody> getAllMemberInGroup(@Path("idGroup") int idGroup);

    @GET(GETINVITATION)
    Call<ResponseBody> getInvitation(@Path("idGroup") int idGroup);

    @POST(CREATEINVITATIONCODE)
    Call<ResponseBody> createInviteCode(
            @Path("idGroup") int idGroup
    );

    @POST(CREATEINVATIONCODECOPY)
    Call<ResponseBody> createInviteCodeCopy(
            @Path("idGroup") Integer idGroup,
            @Query("invitationCode") String inviteCode
    );

    @GET(GETSUGGESTED)
    Call<ResponseBody> getViewAllSuggestedByTrip(
            @Query("idTrip") Integer idTrip
    );

    @POST(CREATESUGGESTED)
    Call<ResponseBody> createSuggestActivity(
            @Query("idTrip") int idTrip,
            @Body RequestBody requestBody
    );


    @PUT(UPDATESUGGESTED)
    Call<ResponseBody> updateSuggestedActivity(
            @Query("idTrip") int idTrip,
            @Body RequestBody requestBody
    );


    @DELETE(DELETESUGGESTED)
    Call<ResponseBody> deleteSuggestedActivity(
            @Path("idSuggested") int idSuggested
    );

    @POST(CREATEVOTE)
    Call<ResponseBody> createVote(
            @Path("idSuggested") int idSuggested
    );

    @DELETE(DELETEVOTE)
    Call<ResponseBody> deleteVote(
            @Path("idSuggested") int idSuggested
    );


    @GET(GETALLPLANINTRIP)
    Call<ResponseBody> getAllPlanInTrip(
            @Query("idTrip") Integer idTrip
    );

    @POST(CREATEPLAN)
    Call<ResponseBody> createPlanInTrip(
            @Query("idTrip") Integer idTrip,
            @Body RequestBody requestBody
    );

    @DELETE(DELETEPLAN)
    Call<ResponseBody> deletePlanIngroup(
            @Path("idPlan") Integer idPlan
    );


    @POST(VOTEPLAN)
    Call<ResponseBody> createVotePlan(
            @Path("idPlan") Integer idPlan
    );

    @DELETE(DELETEVOTEPLAN)
    Call<ResponseBody> deleteVotePlan(
            @Path("idPlan") Integer idPlan
    );

    @POST(CREATEACTIVITY)
    Call<ResponseBody> createActivity(
            @Query("idPlan") int idPlan,
            @Body RequestBody requestBody
    );

    @GET(GETALLACTIVITYINPLAN)
    Call<ResponseBody> getAllActivityInPlan(
            @Query("idPlan") int idPlan
    );

    @PUT(UPDATEACTIVITY)
    Call<ResponseBody> updateActivity(
            @Query("idPlan") int idPlan,
            @Body RequestBody requestBody
    );

    @DELETE(DELETEACTIVITY)
    Call<ResponseBody> deleteActivity(
            @Path("idActivity") Integer idActivity
    );


    @GET(ACTIVITYBYID)
    Call<ResponseBody> printActivityById(
            @Path("idActivity") Integer idActivity
    );


    @POST(CREATETRANSACTION)
    Call<ResponseBody> createTransaction(
            @Query("idGroup") int idGroup,
            @Body RequestBody requestBody
    );

    @GET(GETALLTRANSACTION)
    Call<ResponseBody> getAllTransaction(
            @Query("idGroup") int idGroup
    );

    @PUT(UPDATETRANSACTION)
    Call<ResponseBody> updateTransaction(
            @Body RequestBody requestBody
    );

    @DELETE(DELETETRANSACTION)
    Call<ResponseBody> deleteTransaction(
            @Path("idTransaction") int idTransaction
    );


    @POST(PICKHIGHESTVOTEPLAN)
    Call<ResponseBody> pickHighestVotePlan(
            @Query("idPlan") Integer idPlan
    );


    @GET(GETHIGHESTVOTEPLAN)
    Call<ResponseBody> getHighestVotePlan(
            @Query("idTrip") Integer idTrip
    );

    @PATCH(CHANGEVOTEDEADLINEPLAN)
    Call<ResponseBody> changeVotePlan(
            @Body RequestBody requestBody
    );

    @GET(GETALLLISTTASK)
    Call<ResponseBody> getAllTaskInActivity(
            @Query("idActivity") Integer idActivity
    );

    @GET(GETALLLISTTASKGROUP)
    Call<ResponseBody> getAllTaskInGroup(
            @Query("idGroup") Integer idGroup
    );

    @POST(CREATETASK)
    Call<ResponseBody> createTaskInActivity(
            @Query("idActivity") Integer idActivity,
            @Body RequestBody requestBody
    );

    @PUT(UPDATETASK)
    Call<ResponseBody> updateTaskInActivity(
            @Body RequestBody requestBody
    );


    @DELETE(DELETETASK)
    Call<ResponseBody> deleteTaskInActivity(
            @Path("idTask") Integer idTask
    );

    @PATCH(CHANGEORDER)
    Call<ResponseBody> changePositionTask(
            @Query("idTask") Integer idTask,
            @Query("order") Integer order);

    @POST(CREATEGROUPDOCUMENT)
    Call<ResponseBody> createGroupDocument(
            @Query("idGroup") int idGroup,
            @Body RequestBody requestBody
    );

    @GET(GETALLGROUPDOCUMENT)
    Call<ResponseBody> getAllGroupDocument(
            @Query("idGroup") int idGroup
    );

    @GET(GETALLGROUPTRANSACTIONDOCUMENT)
    Call<ResponseBody> getAllGroupTransactionDocument(
            @Query("idTransaction") Integer idTransaction
    );


    @POST(CREATEGROUPTRANSACTIONDOCUMENT)
    Call<ResponseBody> createGroupTransactionDocument(
            @Query("idTransaction") Integer idTransaction,
            @Body RequestBody requestBody
    );

    @DELETE(DELETEGROUPTRANSACTIONDOCUMENT)
    Call<ResponseBody> deleteGroupTransactionDocument(
            @Query("idDocument") Integer idDocument
    );


    @DELETE(DEACTIVEMEMBERINGROUP)
    Call<ResponseBody> deActiveMemberInGroup(
            @Path("idGroup") Integer idGroup,
            @Path("idMember") Integer idMember
    );

    @DELETE(DELETEGROUPDOCUMENT)
    Call<ResponseBody> deleteGroupDocument(
            @Query("idDocument") Integer idDocument
    );

    @DELETE(DELETETRANSACTIONDETAILSDOCUMENT)
    Call<ResponseBody> deleteTransactionDetailsDocument(
            @Query("idDocument") Integer idTransaction
    );

    @DELETE(DELETEDOCUMENTACTIVITY)
    Call<ResponseBody> deleteDocumentActivity(
            @Query("idDocument") Integer idTransaction
    );

    @GET(SEARCHPLACECITIES)
    Call<ResponseBody> autocompleteCities(
            @Path("srearchValues") String srearchValues
    );

    @GET(SEARCHPLACE)
    Call<ResponseBody> autocompletePlaceAll(
            @Path("srearchValues") String srearchValues
    );


    @GET(GETORCRARL)
    Call<ResponseBody> getOrCrawl(
            @Path("googlePlaceId") String googlePlaceId,
            @Query("idTrip") Integer idTrip
    );

    @GET(GETSEARCHSUGGESTEDPLACE)
    Call<ResponseBody> getSearchSuggestedPlace(
            @Path("idTrip") Integer idTrip
    );


    @GET(GETALLCURRENCY)
    Call<ResponseBody> getAllCurrency();

    @GET(CONVERTCURRENCY)
    Call<ResponseBody> convertCurrency(
            @Query("firstCurrencyCode") String firstCurrencyCode,
            @Query("secondCurrencyCode") String secondCurrencyCode
    );

    @GET(SUGGESTEDPLAN)
    Call<ResponseBody> suggestedPlan(
            @Path("idTrip") Integer idTrip
    );

    @PUT(SETBUDGETPLAN)
    Call<ResponseBody> setBudgetPlan(
            @Path("idPlan") Integer idPlan,
            @Query("activityBudget") BigDecimal activityBudget,
            @Query("accommodationBudget") BigDecimal accommodationBudget,
            @Query("transportationBudget") BigDecimal transportationBudget,
            @Query("foodBudget") BigDecimal foodBudget
    );

    @GET(GETBUDGETELECTEDPLAN)
    Call<ResponseBody> getAllBudgetElectedPlan(@Query("idGroup") int groupId);

    @POST(MAKEREADY)
    Call<ResponseBody> makeReady(
            @Path("idGroup") Integer idGroup,
            @Query("isReady") boolean isReady
    );

    @POST(MAKEPLANNING)
    Call<ResponseBody> makePlanning(
            @Path("idGroup") Integer idGroup
    );

    @POST(UPDATEGROUPBUDGET)
    Call<ResponseBody> updateGroupBudget(
            @Query("idGroup") Integer idGroup,
            @Body RequestBody requestBody
    );

    @GET(GETMYBUDGET)
    Call<ResponseBody> getMyBudget(@Query("idGroup") Integer idGroup);


    @POST(UPDATEMYBUDGET)
    Call<ResponseBody> updateMyBudget(
            @Query("idGroup") Integer idGroup,
            @Body RequestBody requestBody
    );

}

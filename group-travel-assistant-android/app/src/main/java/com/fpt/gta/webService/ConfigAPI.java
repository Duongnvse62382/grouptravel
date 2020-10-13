package com.fpt.gta.webService;

public class ConfigAPI {
    public static final String BASE_URL = "http://grouptravelassistant-env-1.eba-npysrvre.ap-southeast-1.elasticbeanstalk.com/";

//    public static final String BASE_URL = "http://192.168.1.69:5000/";

    public static final String BRAND_ID = "1";
    public static final String PAGE_LIMIT = "25";
    public static final int PAGE_LIMIT_NOTIFICATION = 20;

    public interface Api {
        String SYNCFIREBASE = "authentication/sync";
        String SIGNOUTFRIBASE = "authentication/signOut";
        String CREATEGROUP = "group";
        String UPDATEGROUP = "group";
        String DELETEGROUP = "group/{idGroup}";
        String GETVIEWALLGROUP = "group";
        String GETVIEWGROUPPREVIEW = "group/{idGroup}/preview";
        String GETVIEWGROUPBYID = "group/{idGroup}";
        String DEACTIVEMEMBERINGROUP = "group/{idGroup}/members/{idMember}";

        String MAKEPENDING = "group/{idGroup}/makePending";

        String CREATETRIP = "trip";
        String UPDATETRIP = "trip";
        String DELETETRIP = "trip/{tripId}";
        String GETVIEWALLTRIP = "trip";
        String GETVIEWTRIPDETAIL = "trip/{idTrip}";

        String GETALLMEMBERINGROUP = "group/{idGroup}/members";
        String GETINVITATION = "group/{idGroup}/invitation";
        String CREATEINVITATIONCODE = "group/{idGroup}/invitation";
        String CREATEINVATIONCODECOPY = "group/{idGroup}/enroll";

        String GETSUGGESTED = "suggested-activities";
        String CREATESUGGESTED = "suggested-activities";
        String UPDATESUGGESTED = "suggested-activities";
        String DELETESUGGESTED = "suggested-activities/{idSuggested}";

        String CREATEVOTE = "suggested-activities/{idSuggested}/vote";
        String DELETEVOTE = "suggested-activities/{idSuggested}/vote";

        String GETALLPLANINTRIP = "plans";
        String GETALLACTIVITYINPLAN = "activities";
        String CREATEPLAN = "plans";
        String DELETEPLAN = "plans/{idPlan}";

        String CREATEACTIVITY = "activities";
        String VOTEPLAN = "plans/{idPlan}/vote";
        String DELETEVOTEPLAN = "plans/{idPlan}/vote";
        String UPDATEACTIVITY = "activities";
        String DELETEACTIVITY = "activities/{idActivity}";
        String ACTIVITYBYID = "activities/{idActivity}";


        String CREATETRANSACTION = "transactions";
        String GETALLTRANSACTION = "transactions";
        String UPDATETRANSACTION = "transactions";
        String DELETETRANSACTION = "transactions/{idTransaction}";

        String GETHIGHESTVOTEPLAN = "plans/highestVotePlan";
        String PICKHIGHESTVOTEPLAN = "plans/pickHighestVotePlan";
        String CHANGEVOTEDEADLINEPLAN = "plans/changeVoteDeadline";
        String SETBUDGETPLAN = "plans/{idPlan}";


        String GETALLLISTTASKGROUP = "tasks/groupTask";
        String GETALLLISTTASK = "tasks";
        String CREATETASK = "tasks";
        String UPDATETASK = "tasks";
        String DELETETASK = "tasks/{idTask}";
        String CHANGEORDER = "tasks/changeOrder";

        String CREATEGROUPDOCUMENT = "documents/documentGroup";
        String GETALLGROUPDOCUMENT = "documents/documentGroup";
        String DELETEGROUPDOCUMENT = "documents/documentGroup";

        String CREATEGROUPTRANSACTIONDOCUMENT = "documents/documentTransaction";
        String GETALLGROUPTRANSACTIONDOCUMENT = "documents/documentTransaction";
        String DELETEGROUPTRANSACTIONDOCUMENT = "documents/documentTransaction";

        String DELETETRANSACTIONDETAILSDOCUMENT = "documents/documentTransaction";
        String DELETEDOCUMENTACTIVITY = "documents/documentActivity";
        String SEARCHPLACECITIES = "places/searchByCity/{srearchValues}";
        String SEARCHPLACE = "places/searchByName/{srearchValues}";
        String GETORCRARL = "places/crawl/{googlePlaceId}";
        String GETSEARCHSUGGESTEDPLACE = "places/searchNearby/{idTrip}";

        String GETALLCURRENCY = "currencies";
        String GETMYBUDGET = "budget/myBudget";
        String UPDATEGROUPBUDGET = "budget/groupBudget";
        String UPDATEMYBUDGET = "budget/myBudget";
        String CONVERTCURRENCY = "currencies/convert";

        String SUGGESTEDPLAN = "suggestion/makePlan/{idTrip}";
        String GETBUDGETELECTEDPLAN = "budget/electedPlanBudget";
        String MAKEREADY = "group/{idGroup}/makeMeReady";
        String MAKEPLANNING = "group/{idGroup}/makePlanning";

        String SETBUDGETINDIVIDUAL = "budget/myIndividualBudget";


    }
}

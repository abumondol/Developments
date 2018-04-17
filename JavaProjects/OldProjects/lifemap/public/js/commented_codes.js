 /*$("#btnShowStrands").click(function() {
     
     var strands = response_obj["strands"];
     var l = strands.length;
     var str = "";
     for (var i = 0; i < l; i++) {
     //str+=strands[i]["id"];
     str += "<p><b>" + strands[i]["name"] + ": </b>";
     var index = strands[i]["type_index"];
     str += " Type " + response_obj["strand_types"][index] + "  <a href='#'>edit</a> <a href='#'>delete</a></p>";
     }
     
     $("#divStrands").html(str);
     });*/
 
 //    $("#btnSaveActivities").click(function() {        
//        $("#diaryNotification").html("Saving... Please wait");        
//        var date = $("#datePicker1").val();
//        var jsonObj = new Object();
//        jsonObj.date = date;
//        jsonObj.request_type = "save_activity_table";
//
//        var wrapperObj = new Object();
//        wrapperObj.json = JSON.stringify(jsonObj);
//
//        $.post("/ajax", wrapperObj,
//                function(data, status) {
//                    response_obj = JSON.parse(data);
//                    $("#divActivityTableButtons").show();
//                    $("#divActivityTable").html(getActivityTable(response_obj));
//                    $("#diaryNotification").html("");
//                    $("#btnSaveActivities").prop('disabled',true);
//                    $("#divLeft").html(getStrandsHTML(response_obj));
//                    //alert("Data: " + JSON.stringify(response_obj) + "\nStatus: " + status);
//                });
//    });



$("#btnAddNewActivity").click(function() {
        var data = $("#newActivity").val();
        if (data.trim() == "") {
            $("#addActivityNotification").html("Please enter some name.");
            return;
        }
        $("#addActivityNotification").html("Please wait...");
        var jsonObj = new Object();
        jsonObj.data = data;
        jsonObj.request_type = "addNewActivity";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    responseObj = JSON.parse(data);
                    if (status != 'success') {
                        $("#addActivityNotification").html("Failed. Please try again.");
                        return;
                    }
                    $("#newActivity").val("");
                    $("#addActivityNotification").html("");
                    settings = responseObj["settings"];
                    settingsFlag = true;
                    updateSettingsView(settings);
                });
    });

    $("#btnAddNewLocation").click(function() {
        var data = $("#newLocation").val();
        if (data.trim() == "") {
            $("#addActivityNotification").html("Please enter some name.");
            return;
        }
        $("#addLocationNotification").html("Please wait...");
        var jsonObj = new Object();
        jsonObj.data = data;
        jsonObj.request_type = "addNewLocation";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    responseObj = JSON.parse(data);
                    if (status != 'success') {
                        $("#addLocationNotification").html("Failed. Please try again.");
                        return;
                    }
                    $("#newLocation").val("");
                    $("#addLocationNotification").html("");
                    settings = responseObj["settings"];
                    settingsFlag = true;
                    updateSettingsView(settings);
                });
    });

    $("#btnAddNewPeople").click(function() {
        var data = $("#newPeople").val();
        if (data.trim() == "") {
            $("#addPeopleNotification").html("Please enter some name.");
            return;
        }
        $("#addPeopleNotification").html("Please wait...");
        var jsonObj = new Object();
        jsonObj.data = data;
        jsonObj.request_type = "addNewPeople";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    responseObj = JSON.parse(data);
                    if (status != 'success') {
                        $("#addPeopleNotification").html("Failed. Please try again.");
                        return;
                    }
                    $("#newPeople").val("");
                    $("#addPeopleNotification").html("");
                    settings = responseObj["settings"];
                    settingsFlag = true;
                    updateSettingsView(settings);
                });
    });
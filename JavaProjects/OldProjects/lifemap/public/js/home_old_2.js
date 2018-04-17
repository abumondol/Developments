/*********************** Global variables ************************/
var start = 0;
var end = 0;
var index = 0;
var user_id;
var response_obj;
var settings;
var settingsFlag = false;

function logout() {
    window.location.replace("http://mooncake.cs.virginia.edu:9000/logout");
}

/**************************** JQuery ******************************/
$(function() {
    $("#tabs").tabs();
    $(".datePicker").datepicker();
    $("#datePicker1").val(getToDate());
    //$("#datePicker2").val(getToDate());
    //$("#divDiaryEditor").hide();
    $("#divActivityTableButtons").hide();
    $(".imgLoader").hide();
    var dialog, dialog_edit, dialog_add_strand, form;

    downloadSettings();

    /************** Button Clicks *****************************/
    $("#btnNewStrand").click(dialogPopupAddStrand);

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

    $("#btnShowActivities").click(function() {
        $("#btnSaveActivities").prop('disabled', true);
        $("#diaryNotification").html("Loading... Please wait");
        $("#divActivityTable").html("<img id='imgIdLoaderActivity' class='imgLoader' src='/assets/images/loader.gif'/>");
        var date = $("#datePicker1").val();
        var jsonObj = new Object();
        jsonObj.date = date;
        jsonObj.request_type = "activity_table";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    response_obj = JSON.parse(data);
//                    for (var p in list)
//                        html_text += 5;
                    $("#divActivityTableButtons").show();
                    $("#divActivityTable").html(getActivityTable(response_obj));
                    $("#diaryNotification").html("");
                    $("#divLeft").html(getStrandsHTML(response_obj));
                    //alert("Data: " + JSON.stringify(response_obj) + "\nStatus: " + status);
                });
    });


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

    $("#btnAddActivity").click(dialogPopup);

    /******** edit delete ******************/
    $("#divActivityTable").on("click", "a", function() {
        var type = $(this).text();
        var serial = $(this).attr("serial");
        if (type === 'delete') {
            var flag = confirm("Are you sure to delete?");
            if (flag == true) {
                response_obj["list"].splice(serial, 1);
                $("#divActivityTable").html(getActivityTable(response_obj));
                $("#divLeft").html(getStrandsHTML(response_obj));
            }
        } else if (type === 'edit') {
            index = serial;
            dialogPopupEdit(index);
        }

    });

    $("#divStrandList").on("click", "a", function() {
        var flag = confirm("Are you sure to delete?");
        if (flag == false)
            return;

        var id = $(this).attr("strand_id");
        var jsonObj = new Object();
        jsonObj.request_type = "delete_strand";
        jsonObj.id = id;

        //$("#divStrandList").html(JSON.stringify(jsonObj));

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var responseObj = JSON.parse(data);
                    settings = responseObj["settings"];
                    settingsFlag = true;
                    //updateSettingsView(settings);
                    updateStrandsView(settings);
                    $("#divLeft").html(getStrandsHTML(response_obj));
                });

    });


    /***************************** Dialog ****************************/
    dialog = $("#dialog-form").dialog({
        autoOpen: false,
        height: 475,
        width: 500,
        modal: true,
        buttons: {
            "Add": function() {
                var obj = new Object();
                obj.start_time = timeToInt($("#add_start_time").val(), $("#add-ampm1")[0].selectedIndex) * 60000;
                obj.end_time = timeToInt($("#add_end_time").val(), $("#add-ampm1")[0].selectedIndex) * 60000;

                var activity_index, activity_type_index, location_index, done_with_index, done_for_index;

                activity_index = parseInt($("#add-activity")[0].selectedIndex);
                activity_type_index = parseInt($("#add-activity-type")[0].selectedIndex);
                location_index = parseInt($("#add-location")[0].selectedIndex);
                done_with_index = parseInt($("#add-done-with")[0].selectedIndex);
                done_for_index = parseInt($("#add-done-for")[0].selectedIndex);

                var actList = settings["activity_list"];
                var actTypeList = settings["activity_type_list"];
                var locList = settings["location_list"];
                var peopleList = settings["people_list"];

                obj.activity_id = actList[activity_index]["id"];
                obj.activity_type_id = actTypeList[activity_type_index]["id"];
                obj.location_id = locList[location_index]["id"];
                ;
                obj.done_with_id = peopleList[done_with_index]["id"];
                ;
                obj.done_for_id = peopleList[done_for_index]["id"];
                ;

                obj.activity_tag = $("#add-activity-tag").val();
                obj.location_tag = $("#add-location-tag").val();
                obj.done_with_tag = $("#add-done-with-tag").val();
                obj.done_for_tag = $("#add-done-for-tag").val();
                obj.comment = $("#add-comment").val();
                //alert($("start_time").val());
                response_obj['list'].push(obj);

                $("#divActivityTable").html(getActivityTable(response_obj));
                $("#divLeft").html(getStrandsHTML(response_obj));
                $("#btnSaveActivities").prop('disabled', false);
                dialog.dialog("close");
            },
            Cancel: function() {
                dialog.dialog("close");
            }
        },
        close: function() {
            //form[ 0 ].reset();        
        }
    });

    dialog_edit = $("#dialog-form-edit").dialog({
        autoOpen: false,
        height: 475,
        width: 500,
        modal: true,
        buttons: {
            "Edit": function() {

                var obj = response_obj["list"][index];
                obj.start_time = timeToInt($("#edit_start_time").val(), $("#edit-ampm1")[0].selectedIndex) * 60000;
                obj.end_time = timeToInt($("#edit_end_time").val(), $("#edit-ampm1")[0].selectedIndex) * 60000;

                var activity_index, activity_type_index, location_index, done_with_index, done_for_index;

                activity_index = parseInt($("#edit-activity")[0].selectedIndex);
                activity_type_index = parseInt($("#edit-activity-type")[0].selectedIndex);
                location_index = parseInt($("#edit-location")[0].selectedIndex);
                done_with_index = parseInt($("#edit-done-with")[0].selectedIndex);
                done_for_index = parseInt($("#edit-done-for")[0].selectedIndex);

                var actList = settings["activity_list"];
                var actTypeList = settings["activity_type_list"];
                var locList = settings["location_list"];
                var peopleList = settings["people_list"];

                obj.activity_id = actList[activity_index]["id"];
                obj.activity_type_id = actTypeList[activity_type_index]["id"];
                obj.location_id = locList[location_index]["id"];
                ;
                obj.done_with_id = peopleList[done_with_index]["id"];
                ;
                obj.done_for_id = peopleList[done_for_index]["id"];
                ;

                obj.activity_tag = $("#edit-activity-tag").val();
                obj.location_tag = $("#edit-location-tag").val();
                obj.done_with_tag = $("#edit-done-with-tag").val();
                obj.done_for_tag = $("#edit-done-for-tag").val();
                obj.comment = $("#edit-comment").val();

                $("#divActivityTable").html(getActivityTable(response_obj));
                $("#divLeft").html(getStrandsHTML(response_obj));
                $("#btnSaveActivities").prop('disabled', false);
                dialog_edit.dialog("close");
            },
            Cancel: function() {
                dialog_edit.dialog("close");
            }
        },
        close: function() {
            //form[ 0 ].reset();        
        }
    });

    dialog_add_strand = $("#dialog-add-strand").dialog({
        autoOpen: false,
        height: 475,
        width: 500,
        modal: true,
        buttons: {
            "Add": function() {

                if ($("#add-strand-name").val() == 0) {
                    alert("Please Enter Name.");
                    return;
                }

                var activityArr = [];
                var i = 0;
                $('input:checkbox.add_strand_activity_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        activityArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                var locationArr = [];
                i = 0;
                $('input:checkbox.add_strand_location_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        locationArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                var peopleArr = [];
                i = 0;
                $('input:checkbox.add_strand_people_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        peopleArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                if (activityArr.length == 0 && locationArr.length == 0 && peopleArr.length == 0) {
                    alert("Please select at least one");
                    return;
                }

                var listObj = new Object();

                if (activityArr.length > 0) {
                    listObj.activities = activityArr;
                }

                if (locationArr.length > 0) {
                    listObj.locations = locationArr;
                }

                if (peopleArr.length > 0) {
                    listObj.people = peopleArr;
                }

                var jsonObj = new Object();
                jsonObj.request_type = "add_strand";
                jsonObj.name = $("#add-strand-name").val();
                jsonObj.details = listObj;

                //$("#divStrandList").html(JSON.stringify(jsonObj));

                var wrapperObj = new Object();
                wrapperObj.json = JSON.stringify(jsonObj);

                $.post("/ajax", wrapperObj,
                        function(data, status) {
                            var responseObj = JSON.parse(data);
                            settings = responseObj["settings"];
                            settingsFlag = true;
                            //updateSettingsView(settings);
                            updateStrandsView(settings);
                            $("#divLeft").html(getStrandsHTML(response_obj));
                        });

                dialog_add_strand.dialog("close");
            },
            Cancel: function() {

                dialog_add_strand.dialog("close");
            }
        },
        close: function() {
            //form[ 0 ].reset();        
        }
    });

    /*************************** Diary ***************************/
    $("#btnShowDiary").click(function() {
        $("#divDiaryEditor").hide();
        $("#diaryLoaderImage").show();
        $("#diaryNotification").html("Loading... Please wait");
        var date = $("#datePicker2").val();
        var jsonObj = new Object();
        jsonObj.date = date;
        jsonObj.request_type = "diary_get";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var responseObj = JSON.parse(data);
                    var content = responseObj["content"];
                    $("#diaryLoaderImage").hide();
                    $("#divDiaryEditor").show();
                    $("#diaryTextArea").val(content);
                    $("#diaryNotification").html("");
                    //alert("Content: " + content);
                });

    });

    $("#btnSaveDiary").click(function() {
        $("#diaryNotification").html("Saving... Please wait");
        $("#diaryLoaderImage").show();
        var date = $("#datePicker2").val();
        var jsonObj = new Object();
        jsonObj.date = date;
        jsonObj.content = $("#diaryTextArea").val();
        jsonObj.request_type = "diary_save";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var responseObj = JSON.parse(data);
                    var content = responseObj["content"];
                    $("#diaryLoaderImage").hide();
                    $("#diaryNotification").html("Saved");
                });
    });

    $("#btnDeleteDiary").click(function() {
        $("#diaryNotification").html("Deleting... Please wait");
        $("#diaryLoaderImage").show();
        var date = $("#datePicker2").val();
        var jsonObj = new Object();
        jsonObj.date = date;
        jsonObj.request_type = "diary_delete";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var responseObj = JSON.parse(data);
                    var content = responseObj["content"];
                    $("#diaryLoaderImage").hide();
                    $("#divDiaryEditor").hide();
                    $("#diaryNotification").html("Deleted");
                });
    });

    /**********************Activity Slider ***********************/
    $("#slider-range").slider({
        range: true,
        step: 5,
        min: 0,
        max: 24 * 60,
        values: [0, 30],
        slide: function(event, ui) {
            start = ui.values[ 0 ];
            end = ui.values[ 1 ];
            var h1 = Math.floor(start / 60);
            var m1 = start % 60;

            var h2 = Math.floor(end / 60);
            var m2 = end % 60;

            $("#amount").val(h1 + ":" + m1 + " - " + h2 + ":" + m2);
        }
    });
    $("#amount").val("00:00 - 00:30");

    /******************************** Functions *******************************/
    function dialogPopup() {

        $("#add_start_time").val("");
        $("#add_end_time").val("");
        $("#add-ampm1").html(arrayToOptionList(["AM", "PM"]));
        $("#add-ampm2").html(arrayToOptionList(["AM", "PM"]));
        $("#add-activity-type").html(listToOptionList(settings["activity_type_list"]));
        $("#add-activity").html(listToOptionList(settings["activity_list"]));
        $("#add-location").html(listToOptionList(settings["location_list"]));
        $("#add-done-for").html(listToOptionList(settings["people_list"]));
        $("#add-done-with").html(listToOptionList(settings["people_list"]));

        document.getElementById('add-activity').selectedIndex = 0;
        document.getElementById('add-activity-type').selectedIndex = 0;
        document.getElementById('add-location').selectedIndex = 0;
        document.getElementById('add-done-with').selectedIndex = 0;
        document.getElementById('add-done-for').selectedIndex = 0;

        $('#add-activity-tag').val("");
        $('#add-location-tag').val("");
        $('#add-done-with-tag').val("");
        $('#add-done-for-tag').val("");
        $('#add-comment').val("");

        dialog.dialog("open");
    }

    function dialogPopupEdit(ind) {
        var obj = response_obj["list"][ind];
        var st = milliToTimeFormat(obj.start_time).split(" ");
        var et = milliToTimeFormat(obj.end_time).split(" ")
        $("#edit_start_time").val(st[0]);
        $("#edit_end_time").val(et[0]);


        $("#edit-ampm1").html(arrayToOptionList(["AM", "PM"]));
        $("#edit-ampm2").html(arrayToOptionList(["AM", "PM"]));
        $("#edit-activity-type").html(listToOptionList(settings["activity_type_list"]));
        $("#edit-activity").html(listToOptionList(settings["activity_list"]));
        $("#edit-location").html(listToOptionList(settings["location_list"]));
        $("#edit-done-for").html(listToOptionList(settings["people_list"]));
        $("#edit-done-with").html(listToOptionList(settings["people_list"]));

        if (st[1] == "pm")
            document.getElementById('edit-ampm1').selectedIndex = 1;
        if (et[1] == "pm")
            document.getElementById('edit-ampm2').selectedIndex = 1;

        document.getElementById('edit-activity').selectedIndex = getIndexByIdFromList(obj.activity_id, settings["activity_list"]);
        document.getElementById('edit-activity-type').selectedIndex = getIndexByIdFromList(obj.activity_type_id, settings["activity_type_list"]);
        document.getElementById('edit-location').selectedIndex = getIndexByIdFromList(obj.location_id, settings["location_list"]);
        document.getElementById('edit-done-with').selectedIndex = getIndexByIdFromList(obj.done_with_id, settings["people_list"]);
        document.getElementById('edit-done-for').selectedIndex = getIndexByIdFromList(obj.done_for_id, settings["people_list"]);

        $('#edit-activity-tag').val(obj.activity_tag);
        $('#edit-location-tag').val(obj.location_tag);
        $('#edit-done-with-tag').val(obj.done_with_tag);
        $('#edit-done-for-tag').val(obj.done_for_tag);
        $('#edit-comment').val(obj.comment);

        dialog_edit.dialog("open");
    }

    function dialogPopupAddStrand() {
        $("#add-strand-name").val("");
        $("#span-add-strand-notification").html("");

        var actList = settings["activity_list"];
        var locList = settings["location_list"];
        var peopleList = settings["people_list"];

        var htmlText = "";
        var l = actList.length;
        for (var i = 1; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + actList[i]["id"] + "' class='add_strand_activity_checkbox_class'>" + actList[i]["name"] + " ";
        }

        $("#span-add-strand-activities").html(htmlText);

        htmlText = "";
        l = locList.length;
        for (var i = 1; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + locList[i]["id"] + "' class='add_strand_location_checkbox_class'>" + locList[i]["name"] + " ";
        }

        $("#span-add-strand-locations").html(htmlText);

        htmlText = "";
        l = peopleList.length;
        for (var i = 1; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='add_strand_people_checkbox_class'>" + peopleList[i]["name"] + " ";
        }

        $("#span-add-strand-people").html(htmlText);

        dialog_add_strand.dialog("open");
    }
});

/*************************************** Functions ****************************/
function downloadSettings() {
    var requestJson = new Object();
    requestJson.request_type = "settings";
    requestJson.user_id = user_id;

    var wrapperObj = new Object();
    wrapperObj.json = JSON.stringify(requestJson);

    $.post("/ajax", wrapperObj,
            function(data, status) {
                var responseObj = JSON.parse(data);
                var response_code = responseObj["response_code"];
                if (response_code == 0) {
                    alert("Some error in processing request. Please contact with admin.");
                    return;
                }
                else if (response_code == -1) {
                    alert("Session out.");
                    logout();
                    return;
                }
                settings = responseObj["settings"];
                settingsFlag = true;
                updateSettingsView(settings);
                updateStrandsView(settings);
                alert("Data: " + data + "\nStatus: " + status + "\nreponse_code: " + response_code);
            });
}

function updateSettingsView(obj) {
    if (obj.hasOwnProperty("user_activity_list")) {
        var actList = obj["user_activity_list"];
        var htmlText = "";
        var l = actList.length;
        for (var i = 1; i < l; i++) {
            htmlText += "<li>" + actList[i]["name"] + "</li>";
        }
        $("#ulActivity").html(htmlText);
    }

    if (obj.hasOwnProperty("user_location_list")) {
        var locList = obj["user_location_list"];
        htmlText = "";
        l = locList.length;
        for (var i = 1; i < l; i++) {
            htmlText += "<li>" + locList[i]["name"] + "</li>";
        }
        $("#ulLocation").html(htmlText);
    }

    if (obj.hasOwnProperty("user_people_list")) {
        var peopleList = obj["user_people_list"];
        htmlText = "";
        l = peopleList.length;
        for (var i = 1; i < l; i++) {
            htmlText += "<li>" + peopleList[i]["name"] + "</li>";
        }
        $("#ulPeople").html(htmlText);
    }

}

function updateStrandsView(obj) {
    if (obj.hasOwnProperty("user_strand_list")) {
        var strand_list = obj["user_strand_list"];
        var l = strand_list.length;
        var l2;
        var htmlText = "<u>Strands available:</u><br/> <br/>";
        var strandObj, strandDetails;

        for (var i = 0; i < l; i++) {
            htmlText += ""
            strandObj = strand_list[i];
            htmlText += "<b>" + strandObj["name"] + ": </b>";

            strandDetails = strandObj["details"];
            var actList = settings["activity_list"];
            var locList = settings["location_list"];
            var peopleList = settings["people_list"];


            if (strandDetails.hasOwnProperty("activities")) {
                l2 = strandDetails["activities"].length;
                for (var j = 0; j < l2; j++) {
                    htmlText += getNameByIdFromList(strandDetails["activities"][j], actList) + "; ";
                }
            }

            if (strandDetails.hasOwnProperty("locations")) {
                l2 = strandDetails["locations"].length;
                for (var j = 0; j < l2; j++) {
                    htmlText += getNameByIdFromList(strandDetails["locations"][j], locList) + "; ";
                }
            }

            if (strandDetails.hasOwnProperty("people")) {
                l2 = strandDetails["people"].length;
                for (var j = 0; j < l2; j++) {
                    htmlText += getNameByIdFromList(strandDetails["people"][j], peopleList) + "; ";
                }
            }

            htmlText += " <a href='#' strand_id='" + strandObj["id"] + "'> delete </a><br/><br/>";

        }
        $("#divStrandList").html(htmlText);

    } else {
        $("#divStrandList").html("There is no strand now.");
    }

}

function getNameByIdFromList(id, list) {
    var l = list.length;
    for (var j = 0; j < l; j++) {
        if (list[j]["id"] == id)
            return list[j]["name"];
    }

    return "N/A";
}

function getIndexByIdFromList(id, list) {
    var l = list.length;
    for (var j = 0; j < l; j++) {
        if (list[j]["id"] == id)
            return j;
    }
    return 0;
}

function getToDate() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!
    var yyyy = today.getFullYear();

    if (dd < 10) {
        dd = '0' + dd
    }

    if (mm < 10) {
        mm = '0' + mm
    }

    return mm + '/' + dd + '/' + yyyy;
}

function getStrandsHTML(responseObj) {
    //alert("Hi");

    if (!settings.hasOwnProperty("strand_list"))
        return "No Strand Defined";
    var actList = settings["activity_list"];
    var actTypeList = settings["activity_type_list"];
    var locList = settings["location_list"];
    var peopleList = settings["people_list"];
    var strand_list = settings["strand_list"];
    var list = responseObj["list"];

    list.sort(compare);

    var stl = strand_list.length;
    var list_length, details_length;
    var details, act_id, act_type_id, loc_id, df_id, dw_id, idx;
    var htmlText = "<p class='strand_header'> Strand Clusters</p>";
    for (var i = 0; i < stl; i++) {
        htmlText += "<p class='strand_name'>" + strand_list[i]["name"] + "</p>";
        details = strand_list[i]["details"];
        list_length = list.length;
        for (var count = 0; count < list_length; count++)
        {
            if (details.hasOwnProperty("activities")) {
                if (details["activities"].indexOf(list[count]["activity_id"]) >= 0) {
                    htmlText += "<p class='type type" + list[count]["activity_type_id"] + "'>" + (count + 1) + " " + getNameByIdFromList(list[count]["activity_id"], actList) + "</p>";
                    continue;
                }
            }

            if (details.hasOwnProperty("locations")) {
                if (details["locations"].indexOf(list[count]["location_id"]) >= 0) {
                    htmlText += "<p class='type type" + list[count]["activity_type_id"] + "'>" + (count + 1) + " " + getNameByIdFromList(list[count]["location_id"], locList) + "<br/>";
                    continue;
                }
            }

            if (details.hasOwnProperty("people")) {
                if (details["people"].indexOf(list[count]["done_for_id"]) >= 0) {
                    htmlText += "<p class='type type" + list[count]["activity_type_id"] + "'>" + (count + 1) + " " + getNameByIdFromList(list[count]["done_for_id"], peopleList) + "<br/>";
                    continue;
                } else if (details["people"].indexOf(list[count]["done_with_id"]) >= 0) {
                    htmlText += "<p class='type type" + list[count]["activity_type_id"] + "'>" + (count + 1) + " " + getNameByIdFromList(list[count]["done_with_id"], peopleList) + "<br/>";
                    continue;
                }
            }

        }
    }

    return htmlText;
}

function getActivityTable(responseObj) {

    var actList = settings["activity_list"];
    var actTypeList = settings["activity_type_list"];
    var locList = settings["location_list"];
    var peopleList = settings["people_list"];

    var list = responseObj["list"];
    list.sort(compare);

    html_text = "<table id='activityTable'> <caption> Date: " + responseObj["date"] + "</caption> ";
    html_text += "<tr> <th>SL</th> <th>Start Time</th> <th>End Time</th> <th>Type</th> <th>Activity</th> <th>Location</th> <th>Done For</th> <th>Done With</th> <th>Comment</th> <th>Edit</th> <th>Delete</th></tr>"

    var activity_index, activity_type_index, location_index, done_with_index, done_for_index;

    var l = list.length;
    for (var count = 0; count < l; count++)
    {
        activity_index = getIndexByIdFromList(list[count]["activity_id"], actList);
        activity_type_index = getIndexByIdFromList(list[count]["activity_type_id"], actTypeList);
        location_index = getIndexByIdFromList(list[count]["location_id"], locList);
        done_with_index = getIndexByIdFromList(list[count]["done_with_id"], peopleList);
        done_for_index = getIndexByIdFromList(list[count]["done_for_id"], peopleList);

        html_text += "<tr>";
        html_text += "<td>" + (count + 1) + "</td>";
        html_text += "<td>" + milliToTimeFormat(list[count]["start_time"]) + "</td>";
        html_text += "<td>" + milliToTimeFormat(list[count]["end_time"]) + "</td>";
        html_text += "<td>" + actTypeList[activity_type_index]["name"] + "</td>";
        html_text += "<td>" + actList[activity_index]["name"] + " " + list[count]["activity_tag"] + "</td>";
        html_text += "<td>" + locList[location_index]["name"] + " " + list[count]["location_tag"] + "</td>";
        html_text += "<td>" + peopleList[done_for_index]["name"] + " " + list[count]["done_for_tag"] + "</td>";
        html_text += "<td>" + peopleList[done_with_index]["name"] + " " + list[count]["done_with_tag"] + "</td>";
        html_text += "<td>" + list[count]["comment"] + "</td>";
        html_text += "<td>" + "<a href='#' class='editClass' serial='" + count + "'>edit</a> " + "</td>";
        html_text += "<td>" + " <a href='#' class='deleteClass' serial='" + count + "'>delete</a> " + "</td>";
        html_text += "</tr>";
    }

    html_text += "</table>";
    return html_text;
}

function printObject(o) {
    var out = '';
    for (var p in o) {
        out += p + ': ' + o[p] + '\n';
    }
    //alert(out);
    return out;
}

function minuteToTime(min) {
    var h = Math.floor(min / 60);
    var m = min % 60;
    var string = "" + h + ":";
    if (h > 12)
        string = "" + (h - 12) + ":";
    if (m < 10)
        string += "0";
    string += m;
    if (h >= 12)
        string += " pm";
    else
        string += " am";
    return string;
}

function milliToTimeFormat(milli) {
    var min = Math.round(milli / 60000);
    var h = Math.floor(min / 60);
    var m = min % 60;
    var string = "" + h + ":";
    if (h > 12)
        string = "" + (h - 12) + ":";
    if (m < 10)
        string += "0";
    string += m;
    if (h >= 12)
        string += " pm";
    else
        string += " am";
    return string;
}

function timeFormatToMilli(time, ampm) {
    var str = time.split(":");
    var hour = parseInt(str[0]);
    var min = parseInt(str[1]);
    if (ampm == 1 && hour != 12)
        hour += 12;
    return (hour * 60 + min) * 60000;

}

function listToOptionList(list) {

    var option = '';
    for (i = 0; i < list.length; i++) {
        option += '<option value="' + list[i]["id"] + '">' + list[i]["name"] + '</option>';
    }
    return option;
}

function arrayToOptionList(arr) {

    var option = '';
    for (i = 0; i < arr.length; i++) {
        option += '<option value="' + i + '">' + arr[i] + '</option>';
    }
    return option;
}

function timeToInt(time, ampm) {
    var str = time.split(":");
    var hour = parseInt(str[0]);
    var min = parseInt(str[1]);
    if (ampm == 1 && hour != 12)
        hour += 12;
    return hour * 60 + min;
}

function compare(a, b) {
    if (a.start_time < b.start_time)
        return -1;
    if (a.start_time > b.start_time)
        return 1;
    return 0;
}


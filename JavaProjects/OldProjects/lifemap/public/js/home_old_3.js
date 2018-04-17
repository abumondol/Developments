/*********************** Global variables ************************/
var start = 0;
var end = 0;
var index = 0;
var user_id;
var response_obj;
var see_type;
var settings;
var settingsFlag = false;

function logout() {
    window.location.replace("http://mooncake.cs.virginia.edu:9000/logout");
}

$(function() {
    $("#tabs").tabs();
    $(".datePicker").datepicker();
    $("#datePicker1").val(getToDate());
    $("#showNotification").hide();
    $("#divDailyData").hide();
    $("#btnSaveDailyData").prop('disabled', true);
    $("#span-drag").draggable({containment: "parent"});

    var dialog, dialog_edit, dialog_add_diary, dialog_edit_diary, dialog_add_strand, dialog_see;

    downloadSettings();

    /************** Button Clicks *****************************/
    $("#btnNewStrand").click(dialogPopupAddStrand);

    $("#btnShowDailyData").click(function() {
        if ($("#btnSaveDailyData").prop('disabled') == false) {
            var ans = confirm("Data is not saved. Do you want to proceed without saving?");
            if (!ans)
                return;
        }

        $("#showNotification").show();

        var date = $("#datePicker1").val();
        var requestJson = new Object();
        requestJson.date = date;
        requestJson.request_type = "daily_data";
        requestJson.user_id = user_id;

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var respObj = JSON.parse(data);

                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }
                    response_obj = respObj;
                    //alert(JSON.stringify(response_obj));
                    $("#divActivityTable").html(getActivityTable(response_obj));
                    $("#divDiaryTable").html(getDiaryTable(response_obj));
                    $("#showNotification").hide();
                    $("#btnSaveDailyData").prop('disabled', true);
                    $("#divDailyData").show();
                    $("#divLeft").html(getStrandsHTML(response_obj));
                    //alert("Data: " + JSON.stringify(response_obj) + "\nStatus: " + status);
                });
    });

    $(".btnAddNew").click(function() {
        var id = $(this).attr("id");
        var data = "";
        var type = "";
        if (id == 'btnAddNewActivity') {
            data = $('#newActivity').val();
            type = 'act';
            $("#addActivityNotification").html("Please wait...");
        } else if (id == 'btnAddNewLocation') {
            data = $('#newLocation').val();
            type = 'loc';
            $("#addLocationNotification").html("Please wait...");
        } else if (id == 'btnAddNewPeople') {
            data = $('#newPeople').val();
            type = 'ppl';
            $("#addPeopleNotification").html("Please wait...");
        } else {
            alert("There are some problems. Contact with admin.")
            return;
        }

        if (data.trim() == "") {
            $(".addNewNotification").html("");
            alert("Please enter some name.");
            return;
        }

        var requestJson = new Object();
        requestJson.name = data;
        requestJson.request_type = "add_new";
        requestJson.user_id = user_id;
        requestJson.what = type;

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    responseObj = JSON.parse(data);
                    if (status != 'success') {
                        if (id == 'btnAddNewActivity') {
                            $("#addActivityNotification").html("Failed. Please try again.");
                        } else if (id == 'btnAddNewLocation') {
                            $("#addLocationNotification").html("Failed. Please try again.");
                        } else if (id == 'btnAddNewPeople') {
                            $("#addPeopleNotification").html("Failed. Please try again.");
                        }
                        return;
                    }

                    $(".btnAddNew").val("");
                    $(".addNewNotification").html("");
                    settings = responseObj["settings"];
                    settingsFlag = true;
                    updateSettingsView(settings);
                });
    });

    $("#btnSaveDailyData").click(function() {
        $("#showNotification").html("Saving... Please wait.");
        $("#showNotification").show();

        var requestJson = new Object();
        requestJson.request_type = "save_daily_data";
        requestJson.user_id = user_id;
        requestJson.date = response_obj["date"];
        requestJson.daily_data = response_obj["daily_data"];

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var respObj = JSON.parse(data);
                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }

                    $("#showNotification").hide();
                    $("#btnSaveDailyData").prop('disabled', true);

                });
    });

    $("#btnAddActivity").click(dialogPopup);

    $("#btnAddDiaryEntry").click(dialogPopupAddDiary);


    /******** edit delete ******************/
    $("#divActivityTable").on("click", "a", function() {
        var type = $(this).text();
        var serial = $(this).attr("serial");
        if (type === 'delete') {
            var flag = confirm("Are you sure to delete?");
            if (flag == true) {
                response_obj["daily_data"]["table_activity_list"].splice(serial, 1);
                $("#divActivityTable").html(getActivityTable(response_obj));
                $("#btnSaveDailyData").prop('disabled', false);
                $("#divLeft").html(getStrandsHTML(response_obj));
            }
        } else if (type === 'edit') {
            index = serial;
            dialogPopupEdit(index);
        } else if (type === '!!!') {
            index = serial;
            see_type = 0;
            dialogPopupSee(see_type, index);
        }

    });

    $("#divDiaryTable").on("click", "a", function() {
        var type = $(this).text();
        var serial = $(this).attr("serial");
        if (type === 'delete') {
            var flag = confirm("Are you sure to delete?");
            if (flag == true) {
                response_obj["daily_data"]["table_diary_list"].splice(serial, 1);
                $("#divDiaryTable").html(getDiaryTable(response_obj));
                $("#btnSaveDailyData").prop('disabled', false);
            }
        } else if (type === 'edit') {
            index = serial;
            dialogPopupEditDiary(index);
        } else if (type === '!!!') {
            index = serial;
            see_type = 1;
            dialogPopupSee(see_type, index);
        }

    });

    $("#divStrandList").on("click", "a", function() {
        var flag = confirm("Are you sure to delete?");
        if (flag == false)
            return;

        var id = $(this).attr("strand_id");
        var requestJson = new Object();
        requestJson.request_type = "delete";
        requestJson.what = "strand";
        requestJson.id = id;
        requestJson.user_id = user_id;

        //$("#divStrandList").html(JSON.stringify(jsonObj));

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/ajax", wrapperObj,
                function(data, status) {
                    var responseObj = JSON.parse(data);
                    if (responseObj["response_code"] > 0) {
                        settings = responseObj["settings"];
                        settingsFlag = true;
                    } else {
                        alert("There are some problems deleting strands. Please contact with admin.");
                        return;
                    }
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
                obj.start_time = timeToMinute($("#add-start-hour").val(), $("#add-start-min").val());
                obj.end_time = timeToMinute($("#add-end-hour").val(), $("#add-end-min").val());

                if (obj.start_time >= obj.end_time) {
                    alert("End time of activity must be greater than start time.");
                    return;
                }

                var activityArr = [];
                var i = 0;
                $('input:checkbox.add_activity_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        activityArr[i] = parseInt($(this).val());
                        i++;
                    }
                });


                if (activityArr.length == 0) {
                    alert("You must select at least one activity.");
                    return;
                }

                var locationArr = [];
                i = 0;
                $('input:checkbox.add_location_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        locationArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                var doneForArr = [];
                i = 0;
                $('input:checkbox.add_done_for_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        doneForArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                var doneWithArr = [];
                i = 0;
                $('input:checkbox.add_done_with_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        doneWithArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                obj.activity_type = parseInt($("#add-activity-type").val());
                obj.activities = activityArr;
                obj.locations = locationArr;
                obj.done_for = doneForArr;
                obj.done_with = doneWithArr;

                if (!response_obj.hasOwnProperty("daily_data")) {
                    var daily_data = new Object();
                    daily_data.table_activity_list = [];
                    daily_data.table_diary_list = [];
                    response_obj.daily_data = daily_data;
                }

                response_obj["daily_data"]["table_activity_list"].push(obj);
                $("#divActivityTable").html(getActivityTable(response_obj));
                //alert(JSON.stringify(response_obj));
                $("#btnSaveDailyData").prop('disabled', false);
                $("#divLeft").html(getStrandsHTML(response_obj));
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
                var obj = response_obj["daily_data"]["table_activity_list"][index];
                obj.start_time = timeToMinute($("#edit-start-hour").val(), $("#edit-start-min").val());
                obj.end_time = timeToMinute($("#edit-end-hour").val(), $("#edit-end-min").val());

                if (obj.start_time >= obj.end_time) {
                    alert("End time of activity must be greater than start time.");
                    return;
                }

                var activityArr = [];
                var i = 0;
                $('input:checkbox.edit_activity_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        activityArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                if (activityArr.length == 0) {
                    alert("You must select at least one activity.");
                    return;
                }

                var locationArr = [];
                i = 0;
                $('input:checkbox.edit_location_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        locationArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                var doneForArr = [];
                i = 0;
                $('input:checkbox.edit_done_for_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        doneForArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                var doneWithArr = [];
                i = 0;
                $('input:checkbox.edit_done_with_checkbox_class').each(function() {
                    //html+= (this.checked ? $(this).val() : "");
                    if (this.checked) {
                        doneWithArr[i] = parseInt($(this).val());
                        i++;
                    }
                });

                obj.activity_type = parseInt($("#edit-activity-type").val());
                obj.activities = activityArr;
                obj.locations = locationArr;
                obj.done_for = doneForArr;
                obj.done_with = doneWithArr;

                $("#divActivityTable").html(getActivityTable(response_obj));
                //alert(JSON.stringify(response_obj));
                $("#btnSaveDailyData").prop('disabled', false);

                $("#divLeft").html(getStrandsHTML(response_obj));
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

    dialog_add_diary = $("#dialog-add-diary").dialog({
        autoOpen: false,
        height: 300,
        width: 550,
        modal: true,
        buttons: {
            "Add": function() {
                var obj = new Object();
                obj.description = $("#add-diary-description").val();

                if (!response_obj.hasOwnProperty("daily_data")) {
                    var daily_data = new Object();
                    daily_data.table_activity_list = [];
                    daily_data.table_diary_list = [];
                    response_obj.daily_data = daily_data;
                }

                response_obj["daily_data"]["table_diary_list"].push(obj);
                $("#divDiaryTable").html(getDiaryTable(response_obj));
                $("#btnSaveDailyData").prop('disabled', false);
                dialog_add_diary.dialog("close");
                $("#divLeft").html(getStrandsHTML(response_obj));
            },
            Cancel: function() {
                dialog_add_diary.dialog("close");
            }
        },
        close: function() {
            //form[ 0 ].reset();        
        }
    });

    dialog_edit_diary = $("#dialog-edit-diary").dialog({
        autoOpen: false,
        height: 300,
        width: 550,
        modal: true,
        buttons: {
            "Edit": function() {
                var obj = response_obj["daily_data"]["table_diary_list"][index];
                obj.description = $("#edit-diary-description").val();

                $("#divDiaryTable").html(getDiaryTable(response_obj));
                $("#btnSaveDailyData").prop('disabled', false);
                dialog_edit_diary.dialog("close");
            },
            Cancel: function() {
                dialog_edit_diary.dialog("close");
            }
        },
        close: function() {
            //form[ 0 ].reset();        
        }
    });

    dialog_see = $("#dialog-see").dialog({
        autoOpen: false,
        height: 600,
        width: 1000,
        modal: true,
        buttons: {
            "Save": function() {
                var obj;
                if (see_type == 0)
                    obj = response_obj["daily_data"]["table_activity_list"][index];
                else
                    obj = response_obj["daily_data"]["table_diary_list"][index];

                obj.see_story = $("#see-story").val();


                var s = [];
                for (var i = 1; i <= 5; i++) {
                    if ($("input[name=" + i + "][value=" + i + "]").prop('checked')) {
                        s.push(i);
                    } else if ($("input[name=" + i + "][value=" + (i + 100) + "]").prop('checked')) {
                        s.push(i + 100);
                    }
                }

                if (s.length > 0) {
                    obj.see_experience = s;
                }

                var pos = $("#span-drag").position();
                var expObj = new Object();
                expObj.left = pos.left;
                expObj.top = pos.top;
                obj.see_emotion = expObj;

                $("#btnSaveDailyData").prop('disabled', false);
                dialog_see.dialog("close");
            },
            Cancel: function() {
                dialog_see.dialog("close");
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

                if ($("#add-strand-name").val() == "") {
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
                    alert("Please select at least one from activity, location or people");
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

                var requestJson = new Object();
                requestJson.name = $("#add-strand-name").val();
                requestJson.request_type = "add_strand";
                requestJson.user_id = user_id;
                requestJson.details = listObj;
                //$("#divStrandList").html(JSON.stringify(jsonObj));

                var wrapperObj = new Object();
                wrapperObj.json = JSON.stringify(requestJson);

                $.post("/ajax", wrapperObj,
                        function(data, status) {
                            var responseObj = JSON.parse(data);
                            if (responseObj["response_code"] > 0) {
                                settings = responseObj["settings"];
                                settingsFlag = true;
                            } else {
                                alert("There are some problems adding strands. Please contact with admin.");
                                return;
                            }
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

    /******************************** Functions *******************************/
    function dialogPopup() {
        $("#add-start-hour").html(hourOptionList());
        $("#add-start-min").html(minuteOptionList());
        $("#add-end-hour").html(hourOptionList());
        $("#add-end-min").html(minuteOptionList());
        $("#add-activity-type").html(listToOptionList(settings["activity_type_list"]));

        var actList = settings["user_activity_list"];
        var locList = settings["user_location_list"];
        var peopleList = settings["user_people_list"];

        var htmlText = "";
        var l = actList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + actList[i]["id"] + "' class='add_activity_checkbox_class'>" + actList[i]["name"] + " ";
        }
        $("#add-activities").html(htmlText);

        htmlText = "";
        l = locList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + locList[i]["id"] + "' class='add_location_checkbox_class'>" + locList[i]["name"] + " ";
        }
        $("#add-locations").html(htmlText);

        htmlText = "";
        l = peopleList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='add_done_for_checkbox_class'>" + peopleList[i]["name"] + " ";
        }
        $("#add-done-for").html(htmlText);

        htmlText = "";
        l = peopleList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='add_done_with_checkbox_class'>" + peopleList[i]["name"] + " ";
        }
        $("#add-done-with").html(htmlText);

        dialog.dialog("open");
    }

    function dialogPopupEdit(ind) {
        var obj = response_obj["daily_data"]["table_activity_list"][ind];

        $("#edit-start-hour").html(hourOptionList());
        $("#edit-start-min").html(minuteOptionList());
        $("#edit-end-hour").html(hourOptionList());
        $("#edit-end-min").html(minuteOptionList());

        var start_time = obj["start_time"];
        var end_time = obj["end_time"];

        $("#edit-start-hour").val(minuteToHourIndex(start_time));
        $("#edit-start-minute").val(minuteToMinuteIndex(start_time));
        $("#edit-end-hour").val(minuteToHourIndex(end_time));
        $("#edit-end-minute").val(minuteToMinuteIndex(end_time));

        var actTypeList = settings["activity_type_list"];
        var actList = settings["user_activity_list"];
        var locList = settings["user_location_list"];
        var peopleList = settings["user_people_list"];

        $("#edit-activity-type").html(listToOptionList(actTypeList));
        $("#edit-activity-type").val(obj["activity_type"]);

        var htmlText = "";
        var l = actList.length;
        var idList = obj["activities"];
        for (var i = 0; i < l; i++) {
            if (idList.indexOf(actList[i]["id"]) < 0)
                htmlText += "<input type='checkbox' value='" + actList[i]["id"] + "' class='edit_activity_checkbox_class'>" + actList[i]["name"] + " ";
            else
                htmlText += "<input type='checkbox' value='" + actList[i]["id"] + "' class='edit_activity_checkbox_class' checked>" + actList[i]["name"] + " ";
        }
        $("#edit-activities").html(htmlText);

        htmlText = "";
        l = locList.length;
        idList = obj["locations"];
        for (var i = 0; i < l; i++) {
            if (idList.indexOf(locList[i]["id"]) < 0)
                htmlText += "<input type='checkbox' value='" + locList[i]["id"] + "' class='edit_location_checkbox_class'>" + locList[i]["name"] + " ";
            else
                htmlText += "<input type='checkbox' value='" + locList[i]["id"] + "' class='edit_location_checkbox_class' checked>" + locList[i]["name"] + " ";
        }
        $("#edit-locations").html(htmlText);

        htmlText = "";
        l = peopleList.length;
        idList = obj["done_for"];
        for (var i = 0; i < l; i++) {
            if (idList.indexOf(peopleList[i]["id"]) < 0)
                htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='edit_done_for_checkbox_class'>" + peopleList[i]["name"] + " ";
            else
                htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='edit_done_for_checkbox_class' checked>" + peopleList[i]["name"] + " ";
        }
        $("#edit-done-for").html(htmlText);

        htmlText = "";
        l = peopleList.length;
        idList = obj["done_with"];
        for (var i = 0; i < l; i++) {
            if (idList.indexOf(peopleList[i]["id"]) < 0)
                htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='edit_done_with_checkbox_class'>" + peopleList[i]["name"] + " ";
            else
                htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='edit_done_with_checkbox_class' checked>" + peopleList[i]["name"] + " ";
        }
        $("#edit-done-with").html(htmlText);

        dialog_edit.dialog("open");
    }

    function dialogPopupAddDiary() {
        $("#add-diary-description").val("");
        $("#add-diary-description").attr("rows", 5);
        $("#add-diary-description").attr("cols", 50);
        dialog_add_diary.dialog("open");
    }

    function dialogPopupEditDiary(ind) {
        var obj = response_obj["daily_data"]["table_diary_list"][ind];
        //alert();
        $("#edit-diary-description").val(obj["description"]);
        $("#edit-diary-description").attr("rows", 5);
        $("#edit-diary-description").attr("cols", 50);
        dialog_edit_diary.dialog("open");
    }

    function dialogPopupSee(type, ind) {
        var obj;
        if (type == 0)
            obj = response_obj["daily_data"]["table_activity_list"][ind];
        else
            obj = response_obj["daily_data"]["table_diary_list"][ind];

        txt = "";
        if (obj.hasOwnProperty("see_story"))
            txt = obj["see_story"];
        $("#see-story").val(txt);
        dialog_see.dialog("open");

        for (var i = 1; i <= 5; i++) {
            $("input[name=" + i + "][value=" + i + "]").prop('checked', false);
            $("input[name=" + i + "][value=" + (i + 100) + "]").prop('checked', false);
        }
        //$("#see-story").val("");
        if (obj.hasOwnProperty("see_experience")) {
            var s = obj["see_experience"];
            var l = s.length;

            var txt = "";
            for (var i = 0; i < l; i++) {
                var name = s[i];
                txt += "," + s[i];
                if (name > 100)
                    name = name - 100;
                $("input[name=" + name + "][value=" + s[i] + "]").prop('checked', true);
            }
        }

        if (obj.hasOwnProperty("see_emotion")) {
            var s = obj["see_emotion"];
            $("#span-drag").css('top', s.top);
            $("#span-drag").css('left', s.left);
        }else{
            $("#span-drag").css('top', 235);
            $("#span-drag").css('left', 265);
        }
    }

    function dialogPopupAddStrand() {
        $("#add-strand-name").val("");
        var actList = settings["user_activity_list"];
        var locList = settings["user_location_list"];
        var peopleList = settings["user_people_list"];

        var htmlText = "";
        var l = actList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + actList[i]["id"] + "' class='add_strand_activity_checkbox_class'>" + actList[i]["name"] + " ";
        }

        $("#add-strand-activities").html(htmlText);

        htmlText = "";
        l = locList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + locList[i]["id"] + "' class='add_strand_location_checkbox_class'>" + locList[i]["name"] + " ";
        }

        $("#add-strand-locations").html(htmlText);

        htmlText = "";
        l = peopleList.length;
        for (var i = 0; i < l; i++) {
            htmlText += "<input type='checkbox' value='" + peopleList[i]["id"] + "' class='add_strand_people_checkbox_class'>" + peopleList[i]["name"] + " ";
        }

        $("#add-strand-people").html(htmlText);

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
                //alert("Data: " + data + "\nStatus: " + status + "\nreponse_code: " + response_code);
            });
}

function updateSettingsView(obj) {
    if (obj.hasOwnProperty("user_activity_list")) {
        var actList = obj["user_activity_list"];

        var htmlText = "";
        var l = actList.length;
        for (var i = 0; i < l; i++) {
            if (actList[i]["uid"] == 0)
                htmlText += "<li>" + actList[i]["name"];
            else
                htmlText += "<li><a href='#' type='activity' idn='" + actList[i]["id"] + "'>" + actList[i]["name"] + "</a>";
            htmlText += "</li>";
        }
        //alert(l+""+htmlText);
        $("#ulActivity").html(htmlText);
    }

    if (obj.hasOwnProperty("user_location_list")) {
        var locList = obj["user_location_list"];
        htmlText = "";
        l = locList.length;
        for (var i = 0; i < l; i++) {
            if (locList[i]["uid"] == 0)
                htmlText += "<li>" + locList[i]["name"];
            else
                htmlText += "<li><a href='#' type='location' idn='" + locList[i]["id"] + "'>" + locList[i]["name"] + "</a>";
            htmlText += "</li>";
        }
        $("#ulLocation").html(htmlText);
    }

    if (obj.hasOwnProperty("user_people_list")) {
        var peopleList = obj["user_people_list"];
        htmlText = "";
        l = peopleList.length;
        for (var i = 0; i < l; i++) {
            if (peopleList[i]["uid"] == 0)
                htmlText += "<li>" + peopleList[i]["name"];
            else
                htmlText += "<li><a href='#' type='people' idn='" + peopleList[i]["id"] + "'>" + peopleList[i]["name"] + "</a>";
            htmlText += "</li>";
        }
        $("#ulPeople").html(htmlText);
    }

}

function updateStrandsView(obj) {
    if (obj.hasOwnProperty("user_strand_list") && obj["user_strand_list"].length > 0) {
        var strand_list = obj["user_strand_list"];
        var l = strand_list.length;
        var l2;
        var htmlText = "<u>Strands available:</u><br/> <br/>";
        var strandObj, strandDetails;

        htmlText += "<table><tr><th>Strand Name</th><th>Activities</th><th>Locations</th><th>people</th><th>Delete</th><tr>"


        for (var i = 0; i < l; i++) {
            htmlText += "<tr>"
            strandObj = strand_list[i];
            htmlText += "<td>" + strandObj["name"] + " </td>";

            strandDetails = strandObj["details"];
            var actList = settings["user_activity_list"];
            var locList = settings["user_location_list"];
            var peopleList = settings["user_people_list"];

            htmlText += "<td>";
            if (strandDetails.hasOwnProperty("activities")) {
                l2 = strandDetails["activities"].length;

                for (var j = 0; j < l2; j++) {
                    htmlText += getNameByIdFromList(strandDetails["activities"][j], actList);
                    if (j < l2 - 1)
                        htmlText += ", ";
                }
            }
            htmlText += "</td>";

            htmlText += "<td>";
            if (strandDetails.hasOwnProperty("locations")) {
                l2 = strandDetails["locations"].length;
                for (var j = 0; j < l2; j++) {
                    htmlText += getNameByIdFromList(strandDetails["locations"][j], locList);
                    if (j < l2 - 1)
                        htmlText += ", ";
                }
            }
            htmlText += "</td>";

            htmlText += "<td>";
            if (strandDetails.hasOwnProperty("people")) {
                l2 = strandDetails["people"].length;
                for (var j = 0; j < l2; j++) {
                    htmlText += getNameByIdFromList(strandDetails["people"][j], peopleList);
                    if (j < l2 - 1)
                        htmlText += ", ";
                }
            }
            htmlText += "</td>";

            htmlText += " <td><a href='#' strand_id='" + strandObj["id"] + "'> delete </a></td>";
            htmlText += "</tr>"

        }
        htmlText += "</table>";
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

    return "";
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

    if (!settings.hasOwnProperty("user_strand_list"))
        return "No Strand Defined";
    var actList = settings["user_activity_list"];
    var actTypeList = settings["activity_type_list"];
    var locList = settings["user_location_list"];
    var peopleList = settings["user_people_list"];
    var strand_list = settings["user_strand_list"];
    var list = responseObj["daily_data"]["table_activity_list"];
    var list_length = list.length;
    list.sort(compare);

    var stl = strand_list.length;
    var details, act_id, act_type_id, loc_id, df_id, dw_id, idx;
    var htmlText = "<p class='strand_header'> Strand Clusters</p>";
    for (var i = 0; i < stl; i++) {
        htmlText += "<p class='strand_name'>" + strand_list[i]["name"] + "</p>";
        details = strand_list[i]["details"];

        for (var count = 0; count < list_length; count++)
        {
            if (details.hasOwnProperty("activities") && details["activities"].length > 0) {
                var alist = list[count]["activities"];
                var alist_length = alist.length;
                for (var k = 0; k < alist_length; k++) {
                    if (details["activities"].indexOf(alist[k]) >= 0) {
                        htmlText += "<p class='type type" + list[count]["activity_type"] + "'>" + (count + 1) + " " + getNameByIdFromList(alist[k], actList) + "</p>";
                        continue;
                    }
                }

            }

            if (details.hasOwnProperty("locations") && details["locations"].length > 0) {
                var alist = list[count]["locations"];
                var alist_length = alist.length;
                for (var k = 0; k < alist_length; k++) {
                    if (details["locations"].indexOf(alist[k]) >= 0) {
                        htmlText += "<p class='type type" + list[count]["activity_type"] + "'>" + (count + 1) + " " + getNameByIdFromList(alist[k], locList) + "</p>";
                        continue;
                    }
                }
            }

            if (details.hasOwnProperty("people") && details["people"].length > 0) {
                var alist = list[count]["done_for"];
                var alist_length = alist.length;
                for (var k = 0; k < alist_length; k++) {
                    if (details["done_for"].indexOf(alist[k]) >= 0) {
                        htmlText += "<p class='type type" + list[count]["activity_type"] + "'>" + (count + 1) + " " + getNameByIdFromList(alist[k], peopleList) + "</p>";
                        continue;
                    }
                }

                alist = list[count]["done_with"];
                alist_length = alist.length;
                for (var k = 0; k < alist_length; k++) {
                    if (details["done_with"].indexOf(alist[k]) >= 0) {
                        htmlText += "<p class='type type" + list[count]["activity_type"] + "'>" + (count + 1) + " " + getNameByIdFromList(alist[k], peopleList) + "</p>";
                        continue;
                    }
                }

            }

        }
    }

    return htmlText;
}

function getActivityTable(responseObj) {

    //alert(JSON.stringify(responseObj));
    html_text = "<table id='activityTable'> <caption> Date: " + responseObj["date"] + "</caption> ";
    html_text += "<tr> <th>SL</th>  <th>!!!</th> <th>Start Time</th> <th>End Time</th> <th>Type</th> <th>Activity</th> <th>Location</th> <th>Done For</th> <th>Done With</th> <th>Edit</th> <th>Delete</th></tr>"

    if (!responseObj.hasOwnProperty("daily_data") || responseObj["daily_data"].hasOwnProperty("table_activity_list") == 0) {
        html_text += "</table>";
        return html_text;
    }

    var actTypeList = settings["activity_type_list"];
    var actList = settings["user_activity_list"];
    var locList = settings["user_location_list"];
    var peopleList = settings["user_people_list"];
    var list = responseObj["daily_data"]["table_activity_list"];
    list.sort(compare);


    var id_list;
    var txt, l2, i;
    var l = list.length;

    for (var count = 0; count < l; count++)
    {
        html_text += "<tr>";
        html_text += "<td>" + (count + 1) + "</td>";
        html_text += "<td>" + "<a href='#' class='editClass' serial='" + count + "'>!!!</a>" + "</td>";
        html_text += "<td>" + minuteToTime(list[count]["start_time"]) + "</td>";
        html_text += "<td>" + minuteToTime(list[count]["end_time"]) + "</td>";
        html_text += "<td>" + getNameByIdFromList(list[count]["activity_type"], actTypeList) + "</td>";

        id_list = list[count]["activities"];
        txt = "";
        l2 = id_list.length;
        for (i = 0; i < l2; i++) {
            txt += getNameByIdFromList(id_list[i], actList);
            if (i < l2 - 1)
                txt += ", ";
        }
        html_text += "<td>" + txt + "</td>";

        id_list = list[count]["locations"];
        txt = "";
        l2 = id_list.length;
        for (i = 0; i < l2; i++) {
            txt += getNameByIdFromList(id_list[i], locList);
            if (i < l2 - 1)
                txt += ", ";
        }
        html_text += "<td>" + txt + "</td>";

        id_list = list[count]["done_for"];
        txt = "";
        l2 = id_list.length;
        for (i = 0; i < l2; i++) {
            txt += getNameByIdFromList(id_list[i], peopleList);
            if (i < l2 - 1)
                txt += ", ";
        }
        html_text += "<td>" + txt + "</td>";

        id_list = list[count]["done_with"];
        txt = "";
        l2 = id_list.length;
        for (i = 0; i < l2; i++) {
            txt += getNameByIdFromList(id_list[i], peopleList);
            if (i < l2 - 1)
                txt += ", ";
        }
        html_text += "<td>" + txt + "</td>";

        html_text += "<td>" + "<a href='#' class='editClass' serial='" + count + "'>edit</a> " + "</td>";
        html_text += "<td>" + " <a href='#' class='deleteClass' serial='" + count + "'>delete</a> " + "</td>";
        html_text += "</tr>";
    }

    html_text += "</table>";
    return html_text;
}

function getDiaryTable(responseObj) {

    //alert(JSON.stringify(responseObj));
    html_text = "<table id='diaryTable'> ";
    html_text += "<tr> <th>SL</th>  <th>!!!</th> <th>Description</th><th>Edit</th> <th>Delete</th></tr>";
    if (!responseObj.hasOwnProperty("daily_data") || responseObj["daily_data"]["table_diary_list"].length == 0) {
        html_text += "</table>";
        return html_text;
    }

    var list = responseObj["daily_data"]["table_diary_list"];

    var l = list.length;
    for (var count = 0; count < l; count++)
    {
        html_text += "<tr>";
        html_text += "<td>" + (count + 1) + "</td>";
        html_text += "<td>" + "<a href='#' class='seeClass' serial='" + count + "'>!!!</a>" + "</td>";
        html_text += "<td>" + list[count]["description"] + "</td>";
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

    var suffix;
    if (h >= 12)
        suffix = " PM";
    else
        suffix = " AM";

    if (h > 12)
        h = h - 12;

    var string = "" + h + ":";
    if (h < 10)
        string = "0" + string;

    if (m < 10)
        string += "0";
    string += m;

    return string + suffix;
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

function hourOptionList() {
    var option = '';
    var i = 0;
    for (i = 0; i <= 9; i++)
        option += '<option value="' + i + '">0' + i + ' AM</option>';
    for (i = 10; i <= 11; i++)
        option += '<option value="' + i + '">' + i + ' AM</option>';

    option += '<option value="' + i + '">' + i + ' PM</option>';

    for (i = 1; i <= 9; i++)
        option += '<option value="' + (i + 12) + '">0' + i + ' PM</option>';

    for (i = 10; i <= 11; i++)
        option += '<option value="' + (i + 12) + '">' + i + ' PM</option>';

    return option;
}

function minuteOptionList() {
    var option = '';
    for (i = 0; i <= 9; i++)
        option += '<option value="' + i + '"> 0' + i + ' </option>';
    for (i = 10; i <= 59; i++)
        option += '<option value="' + i + '"> ' + i + ' </option>';
    return option;
}

function timeToMinute(hour, min) {
    var h = parseInt(hour);
    var m = parseInt(min);
    return h * 60 + m;
}

function minuteToHourIndex(min) {
    var hour = Math.floor(min / 60);
    return hour;
}

function minuteToMinuteIndex(min) {
    return min % 60;
}
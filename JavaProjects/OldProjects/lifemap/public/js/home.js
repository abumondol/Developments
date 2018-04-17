/*********************** Global variables ************************/
var start = 0;
var end = 0;
var index = 0;
var response_obj;
var actTable, diaryTable;
var settings;
var settings_flag = false;
var see_type;
var wakeup_time = 0;
var add_obj;
var save_settings = false;
var save_daily_data = false;
var dialog_see;
var dragged_index = -1;
var dropped_index = -1;
var orderby = 0;
var colors = ["#F8766D", "#DE8C00", "#B79F00", "#7CAE00", "#00BA38", "#00C08B", "#00BFC4", "#00B4F0", "#619CFF", "#C77CFF", "#F564E3", "#FF64B0"];
var star_gray = "/assets/images/star_gray.png";
var star_yellow = "/assets/images/star_yellow.png";

function logout() {
    window.location.replace("http://mooncake.cs.virginia.edu:9000/logout");
}

$(function() {
    $(".datePicker").datepicker();
    $("#datePicker1").val(getToDate());
    $("#showNotification").hide();
    $("#btnSave").prop('disabled', true);
    $("#saveNotification").hide();
    $("#span-drag").draggable({containment: "parent"});
    downloadSettings();
    /*******************************************/
    $.ui.autocomplete.filter = function(array, term) {
        var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex(term), "i");
        return $.grep(array, function(value) {
            return matcher.test(value.label || value.value || value);
        });
    };


    /************** Button Clicks *****************************/
    $("#datePicker1").change(function() {
        showDailyData();
    });
    
    /*$("#btnShowDailyData").click(function() {
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
                    $("#showNotification").hide();
                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }

                    response_obj = respObj;
                    if (!response_obj.hasOwnProperty("daily_data")) {
                        daily_data = new Object();
                        var obj = new Object();
                    }

                    getActivityTable(response_obj);
                    getDiaryTable(response_obj);
                    $("#btnSaveDailyData").prop('disabled', true);
                    $("#divDailyData").show();
                });
    });*/
    
    $("#btnSave").click(function() {
        saveDailyData();
        saveSettings();
    });
    $('#divActivityTable').on('click', 'input', function() {
        var cls = $(this).attr('class');
        cls = cls.split(" ");
        cls = cls[0];
        if (cls === 'timePicker') {
            /*   $(this).ptTimeSelect({
             onClose: function(i) {                    
             var val = $(i).val();
             if (val.length > 0) {
             var serial = $(i).attr("serial");
             var type = $(i).attr("class2");
             var table = response_obj["daily_data"]["table_activity_list"];
             var preValSt = table[serial]["start_time"];
             var preValEt = table[serial]["end_time"];
             var t = timeToMinute(val);
             if (type == 'st') {
             if (t == preValSt)
             return;
             if (serial > 0 && t < table[0]["start_time"]) {
             t = t + 24 * 60;
             } else if (serial == 0 && table.length > 1 && t > table[1]["start_time"]) {
             alert("Wake up time must be earlier than any other activity");
             $(i).val(minuteToTime(preValSt));
             reurn;
             }
             
             
             table[serial]["start_time"] = t;
             if (serial == table.length - 1) {
             addBlankToActivityTable();
             }
             } else {
             if (t == preValEt)
             return;
             if (t < preValSt) {
             alert("End time can not be less than start time");
             $(i).val(minuteToTime(preValEt));
             return;
             }
             table[serial]["end_time"] = t;
             }
             
             saveDailyData();
             }
             }
             });
             
             $(this).focus();*/
        }
        else if (cls === 'editable') {
            var availableTags = [];
            if ($(this).attr('class2') === 'act') {
                availableTags = settings["gen"]["activity"].concat(settings["user"]["activity"]);
            }
            else if ($(this).attr('class2') === 'loc') {
                availableTags = settings["gen"]["location"].concat(settings["user"]["location"]);
            }
            else if ($(this).attr('class2') === 'dw') {
                availableTags = settings["gen"]["people"].concat(settings["user"]["people"]);
            }

            $(this).bind("keydown", function(event) {
                if (event.keyCode === $.ui.keyCode.TAB &&
                        $(this).autocomplete("instance").menu.active) {
                    event.preventDefault();
                }
            })
                    .autocomplete({
                minLength: 0,
                source: function(request, response) {
                    response($.ui.autocomplete.filter(
                            availableTags, extractLast(request.term)));
                },
                focus: function() {
                    return false;
                },
                select: function(event, ui) {
                    var terms = split(this.value);
                    terms.pop();
                    terms.push(ui.item.value);
                    terms.push("");
                    this.value = terms.join(", ");
                    return false;
                }
            });
            //$(this).focus();
        }
    });
    $('#divActivityTable').on('change', 'select', function() {        
        var serial = $(this).attr("serial");
        var table = response_obj["daily_data"]["table_activity_list"];
        var preVal = table[serial]["activity_type"];
        table[serial]["activity_type"] = $(this).val();
        if (preVal != $(this).val())
            saveDailyData();
    });
    /*$("#divDailyData").on('change', 'input', function() {
        var serial = $(this).attr('serial');
        var cls = $(this).attr('class');
        var val = $(this).val();
        val = val.trim();
        cls = cls.split(" ");
        if (cls[0] === 'description') {
            var table = response_obj["daily_data"]["table_diary_list"];
            var preVal = table[serial]["description"];
            table[serial]["description"] = val;
            if (preVal == "") {
                addBlankToDiaryTable();
            }
        } else if (cls[0] === 'editable') {
            if (val.slice(-1) == ",") {
                val = val.slice(0, val.length - 1);
                $(this).val(val)
            }
            var table = response_obj["daily_data"]["table_activity_list"];
            var cls2 = $(this).attr('class2');
            if (cls2 === "act") {
                table[serial]["activity"] = val;
                if (val.length > 0)
                    updateSettingsList(val, "activity");
            } else if (cls2 === "loc") {
                table[serial]["location"] = val;
                if (val.length > 0)
                    updateSettingsList(val, "location");
            } else if (cls2 === "dw") {
                table[serial]["people"] = val;
                if (val.length > 0)
                    updateSettingsList(val, "people");
            }
        }
        saveDailyData();
    });*/
    
    $("#divDailyData").on('focusout', 'input', function() {
        var serial = $(this).attr('serial');
        var cls = $(this).attr('class');
        var val = $(this).val();
        val = val.trim();
        cls = cls.split(" ");
        if (cls[0] === 'description') {
            var table = response_obj["daily_data"]["table_diary_list"];
            var preVal = table[serial]["description"];
            if(preVal==val)
                return;
            table[serial]["description"] = val;
            if (preVal == "") {
                addBlankToDiaryTable();
            }
            saveDailyData();
        } else if (cls[0] === 'editable') {
            if (val.slice(-1) == ",") {
                val = val.slice(0, val.length - 1);
                $(this).val(val);
            }
            var table = response_obj["daily_data"]["table_activity_list"];
            var cls2 = $(this).attr('class2');
            if (cls2 === "act") {
                preVal = table[serial]["activity"];
                if(val == preVal)
                    return;
                table[serial]["activity"] = val;
                if (val.length > 0)
                    updateSettingsList(val, "activity");
            } else if (cls2 === "loc") {
                preVal = table[serial]["location"];
                if(val == preVal)
                    return;                
                table[serial]["location"] = val;
                if (val.length > 0)
                    updateSettingsList(val, "location");
            } else if (cls2 === "dw") {
                preVal = table[serial]["people"];
                if(val == preVal)
                    return;                 
                table[serial]["people"] = val;
                if (val.length > 0)
                    updateSettingsList(val, "people");
            }
            saveDailyData();
        }
        
    });
    
    
    $('#divDailyData').on('click', 'a', function() {
        var serial = $(this).attr('serial');
        var cls = $(this).attr('class');
        var cls2 = $(this).attr('class2');
        if (cls == 'see') {
            index = serial;
            see_type = 0;
            if (cls2 == 'diary') {
                see_type = 1;
                var list = response_obj["daily_data"]["table_diary_list"];
                if(serial == list.length -1)
                    return;
            }else{
                var list = response_obj["daily_data"]["table_activity_list"];
                if(serial == list.length -1)
                    return;
            }
            dialogPopupSee(see_type, index);
        }
    });
    /************ Show Hide *********************/
    $("a#vtlink").click(function() {
        if ($(this).text() == "show") {
            $("#divDiagram").show();
            $(this).text("hide");
        } else {
            $("#divDiagram").hide();
            $(this).text("show");
        }
    });
    $("a#atlink").click(function() {
        if ($(this).text() == "show") {
            $("#divActivityTable").show();
            $(this).text("hide");
        } else {
            $("#divActivityTable").hide();
            $(this).text("show");
        }
    });
    $("a#ptlink").click(function() {
        if ($(this).text() == "show") {
            $("#divDiaryTable").show();
            $(this).text("hide");
        } else {
            $("#divDiaryTable").hide();
            $(this).text("show");
        }
    });
    /***************************** Dialog ****************************/
    dialog_see = $("#dialog-see").dialog({
        autoOpen: false,
        height: 600,
        width: 1000,
        modal: true,
        buttons: {
            "Done": function() {
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
                dialog_see.dialog("close");
                saveDailyData();
                
                if(see_type == 0)
                    getActivityTable(response_obj);
                else
                    getDiaryTable(response_obj);
            },
            "Back": function() {
                dialog_see.dialog("close");
            }
        },
        close: function() {
//form[ 0 ].reset();        
        }
    });
    /******************************** Functions *******************************/
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
        for (var i = 1; i <= 5; i++) {
            $("input[name=" + i + "][value=" + i + "]").prop('checked', false);
            $("input[name=" + i + "][value=" + (i + 100) + "]").prop('checked', false);
        }

        if (obj.hasOwnProperty("see_experience")) {
            var s = obj["see_experience"];
            var l = s.length;
            for (var i = 0; i < l; i++) {
                var name = s[i];
                if (name > 100)
                    name = name - 100;
                $("input[name=" + name + "][value=" + s[i] + "]").prop('checked', true);
            }
        }

        if (obj.hasOwnProperty("see_emotion")) {
            var s = obj["see_emotion"];
            $("#span-drag").css('top', s.top);
            $("#span-drag").css('left', s.left);
        } else {
            $("#span-drag").css('top', 235);
            $("#span-drag").css('left', 265);
        }

        dialog_see.dialog("open");
    }

    /******* Strand function ***/
    $("#btnAddNewStrand").click(function() {
        var text = $("#inputAddNewStrand").val().trim();
        if (text.length > 0 && text != "Trash" && text != "trash") {
            updateSettingsList(text, "strand");
            showStrandBoxes();
            $("#inputAddNewStrand").val("");
        }
    });
    $('#divActivityTable').on('mouseover', 'div', function() {
        $(this).draggable({
            cursor: 'move',
            helper: 'clone'
        });
        dragged_index = $(this).attr('serial');
    });
    $('#divStrandContainer').on('DOMNodeInserted', 'div', function() {

        $(this).droppable({
            drop: function(event, ui) {
                //$( this ).html( "Dropped!"+ui.attr('serial') );          
                //$(this).html("Dropped!");
                var actTable = response_obj["daily_data"]["table_activity_list"];
                if (dragged_index > 0 && dragged_index < actTable.length - 1)
                    insertToStrandBox($(this).attr('strand_name'), dragged_index);
            }
        });
    });
    
    $("#btnSendOtherData").click(function() {
        
        $("#showOtherDataCount").html("Sending other data");       
        var requestJson = new Object();
        requestJson.date = "08/05/2016";
        requestJson.request_type = "other";
        requestJson.data_type = "other";
        requestJson.user_code = "abu";
        requestJson.password = "abu";
        requestJson.data = "it is test data";
        var wrapperObj = new Object();
        wrapperObj.external_data = JSON.stringify(requestJson);        
        $.post("/data_push", wrapperObj,
                function(data, status) {
                    var respObj = JSON.parse(data);
                    $("#showOtherDataCount").html("response code: "+respObj["response_code"]);
                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }
                });
    });
    
});

/*************************************** Functions ****************************/
function sortby(val) {
    //alert(val);
    orderby = val;
    getActivityTable(response_obj);
}

function saveSettings() {
    var requestJson = new Object();
    requestJson.request_type = "save_settings";
    requestJson.user_id = user_id;
    requestJson.settings = settings["user"];
    var wrapperObj = new Object();
    wrapperObj.json = JSON.stringify(requestJson);
    $.post("/ajax", wrapperObj,
            function(data, status) {
                var respObj = JSON.parse(data);
                if (respObj["response_code"] <= 0) {
                    $("#saveNotification").html("Saving failed.");
                    $("#btnSave").prop('disabled', false);
                    return;
                }
                $("#saveNotification").html("Saved");
            });
}

function saveDailyData() {
    $("#saveNotification").html("Saving...");
    $("#btnSave").prop('disabled', true);
    $("#saveNotification").show();
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
                    $("#saveNotification").html("Saving failed.");
                    $("#btnSave").prop('disabled', false);
                    return;
                }
                $("#saveNotification").html("Saved");
            });
}

function downloadSettings() {
    $("#showNotification").show();

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
                showStrandBoxes();
                showDailyData();
            });

}

function getActivityTable(responseObj) {
    //alert(JSON.stringify(responseObj));

    html_text = "<table id='activityTable'> <caption> Date: " + responseObj["date"] + "</caption> ";
    html_text += "<tr> <th> </th> <th> <a href='#' value='sort_strand' onclick='sortby(1)'>Strand </a></th> <th>Noteworthy</th> <th><a href='# value='sort_strand_time onclick='sortby(0)'>Start</a></th> <th>End</th>  <th>What</th> <th>Where</th> <th>Who</th> <th>Type</th></tr>"

    var list = responseObj["daily_data"]["table_activity_list"];
    var l = list.length;
    if (list.length > 1) {
        list.sort(compare);
        if (orderby > 0) {
            list[l - 1].strand = 'zzzzzzzz';
            list.sort(compare_strand);
            list[l - 1].strand = '';
        }
    }

    var star = star_gray;
    
    if(list[0].hasOwnProperty('see_story') || list[0].hasOwnProperty('see_experience') || list[0].hasOwnProperty('see_emotion'))
        star = star_yellow;
    
    html_text += "<tr>";
    html_text += "<td width='3%'>" + getDraggableHtml(0) + "</td>";
    html_text += "<td width='3%'>" + list[0].strand + "</td>";
    html_text += "<td width='5%'><a href='#' class='see' class2='act' serial='0''><img src='" + star + "' width='25' height='25'></a>" + "</td>";
    html_text += "<td width='10%'> <input type='text' class='timePicker' class2='st' serial='0' value='" + minuteToTime(list[0]["start_time"]) + "' readonly></td>";
    html_text += "<td width='10%'> </td>";
    html_text += "<td width='20%'> &nbsp&nbsp" + list[0]["activity"] + "</td>";
    html_text += "<td width='20%'> <input type='text' class='editable' class2='loc' serial='0' value='" + list[0]["location"] + "'></td>";
    html_text += "<td width='20%'> <input type='text' class='editable' class2='dw' serial='0' value='" + list[0]["people"] + "'></td>";
    html_text += "<td width='15%'> &nbsp&nbsp Repeating</td>";
    html_text += "</tr>";


    var strand_list = settings["user"]["strand"];
    var x;
    for (var i = 1; i < l; i++)
    {
        if(list[i].hasOwnProperty('see_story') || list[i].hasOwnProperty('see_experience') || list[i].hasOwnProperty('see_emotion'))
            star = star_yellow;
        else
            star = star_gray;
        
        
        x = strand_list.indexOf(list[i].strand);
        if (x < 0)
            x = '_blank';
        html_text += "<tr class='color" + x + "'>";
        html_text += "<td width='3%'>" + getDraggableHtml(i) + "</td>";
        html_text += "<td width='3%'>" + list[i].strand + "</td>";
        html_text += "<td width='5%'><a href='#' class='see' class2='act' serial='" + i + "'><img src='" + star + "' width='25' height='25'> </a>" + "</td>";
        html_text += "<td width='10%'><input type='text' class='timePicker' class2='st' serial='" + i + "' value='" + minuteToTime(list[i]["start_time"]) + "' readonly></td>";
        html_text += "<td width='10%'><input type='text' class='timePicker' class2='et' serial='" + i + "' value='" + minuteToTime(list[i]["end_time"]) + "' readonly></td>";
        html_text += "<td width='20%'><input type='text' class='editable' class2='act' serial='" + i + "' value='" + list[i]["activity"] + "'></td>";
        html_text += "<td width='20%'><input type='text' class='editable' class2='loc' serial='" + i + "' value='" + list[i]["location"] + "'></td>";
        html_text += "<td width='20%'><input type='text' class='editable' class2='dw'  serial='" + i + "' value='" + list[i]["people"] + "'></td>";
        html_text += "<td width='15%'> " + typeListSelected(list[i]["activity_type"], i) + " </td>";
        html_text += "</tr>";
    }
    html_text += "</table>";
    $("#divActivityTable").html(html_text);
    $(".timePicker").ptTimeSelect({
        onClose: function(i) {
            var val = $(i).val();
            if (val.length > 0) {
                var serial = $(i).attr("serial");
                var type = $(i).attr("class2");
                var table = response_obj["daily_data"]["table_activity_list"];
                var preValSt = table[serial]["start_time"];
                var preValEt = table[serial]["end_time"];
                var t = timeToMinute(val);
                if (type == 'st') {
                    if (t == preValSt)
                        return;
                    if (serial > 0 && t < table[0]["start_time"]) {
                        t = t + 24 * 60;
                    } else if (serial == 0 && table.length > 1 && t > table[1]["start_time"]) {
                        alert("Wake up time must be earlier than any other activity");
                        $(i).val(minuteToTime(preValSt));
                        reurn;
                    }
                    table[serial]["start_time"] = t;
                    if (serial == table.length - 1) {
                        addBlankToActivityTable();
                    }
                } else {
                    if (t == preValEt)
                        return;
                    if (t < table[0]["start_time"])
                        t = t + 24 * 60;
                    if (t < preValSt) {
                        alert("End time can not be less than start time");
                        $(i).val(minuteToTime(preValEt));
                        return;
                    }
                    table[serial]["end_time"] = t;
                }
                getActivityTable(response_obj);
                saveDailyData();
            }
        }
    });
}

function getDiaryTable(obj) {

    html_text = "<table id='diaryTable'> <caption> Preoccupation</caption>";
    html_text += "<tr> <th>SL</th><th>Noteworthy</th> <th>Description</th></tr>";
    var list = obj["daily_data"]["table_diary_list"];
    var star = star_gray;
    var l = list.length;
    for (var i = 0; i < l; i++)
    {
        if(list[i].hasOwnProperty('see_story') || list[i].hasOwnProperty('see_experience') || list[i].hasOwnProperty('see_emotion'))
            star = star_yellow;
        else
            star = star_gray;
        
        html_text += "<tr>";
        html_text += "<td width='3%'>" + (i + 1) + "</td>";
        html_text += "<td width='10%'>" + "<a href='#' class='see' class2='diary' serial='" + i + "'><img src='" + star + "' width='25' height='25'> </a>" + "</td>";
        html_text += "<td width='90%'><input type='text' class='description' serial='" + i + "' value='" + list[i]["description"] + "'></td>";
        html_text += "</tr>";
    }
    html_text += "</table>";
    $("#divDiaryTable").html(html_text);
}

function addBlankToActivityTable() {
    var obj = new Object();
    obj.start_time = 9999;
    obj.end_time = 9999;
    obj.activity_type = '';
    obj.activity = '';
    obj.location = '';
    obj.people = '';
    obj.strand = '';
    var actTable = response_obj["daily_data"]["table_activity_list"];
    actTable.push(obj);
    getActivityTable(response_obj);
}

function addBlankToDiaryTable() {
    var obj = new Object();
    obj.description = '';
    var diaryTable = response_obj["daily_data"]["table_diary_list"];
    diaryTable.push(obj);
    getDiaryTable(response_obj);
}

function typeListSelected(item, index) {
    html_text = "<select class='act_type' serial='" + index + "'>";
    if (item === " ")
        html_text += "<option value='' selected>  </option>";
    else
        html_text += "<option value=''>  </option>";
    if (item === "Continuing")
        html_text += "<option value='Continuing' selected>Continuing</option>";
    else
        html_text += "<option value='Continuing'>Continuing</option>";
    if (item === "Repeating")
        html_text += "<option value='Repeating' selected>Repeating</option>";
    else
        html_text += "<option value='Repeating'>Repeating</option>";
    if (item === "Unique")
        html_text += "<option value='Unique' selected>Unique</option>";
    else
        html_text += "<option value='Unique'>Unique</option>";
    if (item === "Travel")
        html_text += "<option value='Travel' selected>Travel</option>";
    else
        html_text += "<option value='Travel'>Travel</option>";
    html_text += "</select>";
    return html_text;
    alert(html_text);
}

function updateSettingsList(str, type) {
    var strArr = str.split(",");
    var l = strArr.length;
    var list1 = settings["gen"][type];
    var list2 = settings["user"][type];
    var flag = 0;
    for (var i = 0; i < l; i++) {
        var x = strArr[i].trim();
        x = x.charAt(0).toUpperCase() + x.slice(1);
        if (list1.indexOf(x) < 0 && list2.indexOf(x) < 0) {
            flag = 1;
            list2.push(x);
        }
    }

    if (flag == 1)
        saveSettings();
}

function showStrandBoxes() {

    var strand_list = settings["user"]["strand"];
    var ls = strand_list.length;
    var t = "<div id='strand_trash' class='divStrand' strand_name='Trash'><img src='/assets/images/trash.png' width='30' height='30'/></div>";

    for (var i = 0; i < ls; i++) {
        t += getStrandHtml(i, strand_list[i]);
    }

    $("#divStrandContainer").html(t);
}

function showDailyData() {
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
                $("#showNotification").hide();
                if (respObj["response_code"] <= 0) {
                    alert("There are some error in the system. Please contact with the admin.");
                    return;
                }

                response_obj = respObj;
                if (!response_obj.hasOwnProperty("daily_data")) {
                    daily_data = new Object();
                    var obj = new Object();
                }

                getActivityTable(response_obj);
                getDiaryTable(response_obj);
                $("#btnSaveDailyData").prop('disabled', true);
                $("#divDailyData").show();
            });

}

/********************* Util Functions *********************/

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

function minuteToTime(min) {
    if (min > 9000)
        return '';
    if (min >= 24 * 60)
        min = min - 24 * 60;
    var h = Math.floor(min / 60);
    var m = min % 60;
    var suffix;
    if (h >= 12)
        suffix = " PM";
    else
        suffix = " AM";
    if (h > 12)
        h = h - 12;
    if (h == 0)
        h = 12;
    var string = "" + h + ":";
    if (m < 10)
        string += "0";
    string += m;
    return string + suffix;
}

function timeToMinute(time) {
    var str = time.split(" ");
    var hm = str[0].split(":");
    var hour = parseInt(hm[0]);
    var min = parseInt(hm[1]);
    if (str[1] == 'PM' && hour != 12)
        hour += 12;
    else if (str[1] == 'AM' && hour == 12)
        hour = 0;
    return hour * 60 + min;
}

function compare(a, b) {
    if (a.start_time < b.start_time)
        return -1;
    else if (a.start_time > b.start_time)
        return 1;
    else if (a.end_time < b.end_time)
        return -1;
    else if (a.end_time > b.end_time)
        return 1;
    return 0;
}

function compare_strand(a, b) {
//    var strand_list = settings["user"]["strand"];
//    var x = strand_list.indexOf(a.strand);
//    var y = strand_list.indexOf(b.strand);

    if (a.strand < b.strand)
        return -1;
    else if (a.strand > b.strand)
        return 1;
//    if (x < y)
//        return -1;
//    else if (x > y)
//        return 1;
    return compare(a,b);
}


/*************** From jqury Examples/Sources **************************/
function split(val) {
    return val.split(/,\s*/);
}

function extractLast(term) {
    return split(term).pop();
}

function getStrandHtml(i, strand_name) {
    var s;
    s = "<div id='strand" + i + "' class='divStrand color" + i + "' strand_name='" + strand_name + "'>" + strand_name + "</div>";
    return s;
}

function getDraggableHtml(index) {
    var s = "<div id='drg" + index + "' class='drg' serial='" + index + "'>&nbsp" + (index + 1) + "&nbsp&nbsp&nbsp</div>";
    return s;
}

function insertToStrandBox(strand_name, index) {

    var actTable = response_obj["daily_data"]["table_activity_list"];
    var act = actTable[index];
    if (strand_name == 'Trash') {
        var x = confirm('Are you Sure to delete ' + (parseInt(index) + 1) + "?");
        if (x == false)
            return;
        actTable.splice(index, 1);
        getActivityTable(response_obj);
        saveDailyData();
    } else if (act.strand == '') {
        act.strand = strand_name;
        getActivityTable(response_obj);
        saveDailyData();
    } else if (act.strand == strand_name) {

    } else {
        obj = cloneActivity(act);
        obj.strand = strand_name;
        actTable.push(obj);
        getActivityTable(response_obj);
        saveDailyData();
    }
    
}

function cloneActivity(act) {
    var obj = new Object();
    obj.start_time = act.start_time;
    obj.end_time = act.end_time;
    obj.activity_type = act.activity_type;
    obj.activity = act.activity;
    obj.location = act.location;
    obj.people = act.people;
    obj.strand = act.strand;
    return obj;
}

/********************* Test functions ************/
function printObject(o) {
    var out = '';
    for (var p in o) {
        out += p + ': ' + o[p] + '\n';
    }
    //alert(out);
    return out;
}
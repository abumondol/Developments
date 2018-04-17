/*********************** Global variables ************************/
var response_obj;
var settings;
var settings_flag;
function logout() {
    window.location.replace("http://mooncake.cs.virginia.edu:9000/logout");
}

$(function() {
    $("#tabs").tabs();
    $(".datePicker").datepicker();
    $("#datePicker1").val("");
     $("#showNotification").hide();

    downloadSettings();

    /************** Button Clicks *****************************/

    $("#btnShowDailyData").click(function() {

        $("#showNotification").show();

        var date = $("#datePicker1").val();
        var subject = $("#subject").val();
        var activity = $("#activity").val();
        
        var requestJson = new Object();
        if (date != "")
            requestJson.date = date;
        requestJson.subject = subject;
        requestJson.activity = activity;
        requestJson.request_type = "data";
        //requestJson.user_id = user_id;

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/swad_ajax", wrapperObj,
                function(data, status) {
                    var respObj = JSON.parse(data);

                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }
                    response_obj = respObj;
                    $("#showNotification").hide();
                    //alert(JSON.stringify(response_obj));
                    $("#divSummaryTable").html(getSummaryTable(response_obj));

                });
    });


});

/*************************************** Functions ****************************/
function downloadSettings() {
    var requestJson = new Object();
    requestJson.request_type = "settings";
    //requestJson.user_id = user_id;

    var wrapperObj = new Object();
    wrapperObj.json = JSON.stringify(requestJson);

    $.post("/swad_ajax", wrapperObj,
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
                settings_flag = true;
                //alert(listToOptionList(settings["subject_list"]));
                $("#subject").html(listToOptionList(settings["subject_list"]));
                $("#activity").html(listToOptionList(settings["activity_list"]));
                
            });
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


function getSummaryTable(responseObj) {

    //alert(JSON.stringify(responseObj));
    html_text = "<table id='activityTable'>" ;
    html_text += "<tr> <th>SL</th>  <th>Subject</th> <th>Hand</th><th>Activity</th> <th> Training Start</th> <th>Training end</th> <th>Data Start</th> <th>Data End</th> <th>Training Duration</th> <th>Data Duration</th> <th>Status</th> </tr>"

    if (!responseObj.hasOwnProperty("data")) {
        html_text += "</table>";
        return html_text;
    }

    
    var list = responseObj["data"];
    list.sort(compare);


    var id_list;
    var txt, l2, i;
    var l = list.length;

    for (var count = 0; count < l; count++)
    {
        html_text += "<tr>";
        html_text += "<td>" + (count + 1) + "</td>";        
        html_text += "<td>" + list[count]["subject"] + "</td>";
        html_text += "<td>" + list[count]["hand"] + "</td>";
        html_text += "<td>" + list[count]["activity"] + "</td>";
        html_text += "<td>" + list[count]["tst"] + "</td>";
        html_text += "<td>" + list[count]["tet"] + "</td>";
        html_text += "<td>" + list[count]["dst"] + "</td>";
        html_text += "<td>" + list[count]["det"] + "</td>";
        html_text += "<td>" + list[count]["tduration"] + "</td>";
        html_text += "<td>" + list[count]["dduration"] + "</td>";
        html_text += "<td>" + list[count]["status"] + "</td>";  
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
    //alert("hi");
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
    if (a.tst < b.tst)
        return -1;
    if (a.tst > b.tst)
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
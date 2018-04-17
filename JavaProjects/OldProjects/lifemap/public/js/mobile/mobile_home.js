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


function logout() {
    window.location.replace("http://mooncake.cs.virginia.edu:9000/mobile_web_logout");
}

$(function() {
    $("#showNotification").hide();
    downloadSettings();

    $("#btnShowData").click(function() {
        $("#showNotification").show();
        var requestJson = new Object();
        requestJson.request_type = "data";
        requestJson.user_id = user_id;
        requestJson.sub_id = $("#subject").val();
        requestJson.act_id = $("#activity").val();
        requestJson.pos_id = $("#position").val();
        requestJson.settings = settings;

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/mobile_web_ajax", wrapperObj,
                function(data, status) {
                    var respObj = JSON.parse(data);
                    $("#showNotification").hide();
                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }
                    response_obj = respObj;
                    getTable(response_obj);
                });
    });

    $("#divTable").on("click", "a", function() {
        var i = $(this).attr("idn");
        var c = confirm("Are you sure to delete");
        if (c == false)
            return;
        $("#showNotification").show();
        var requestJson = new Object();
        requestJson.request_type = "delete";
        requestJson.user_id = user_id;
        requestJson.obj = response_obj["data"][i];

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(requestJson);

        $.post("/mobile_web_ajax", wrapperObj,
                function(data, status) {
                    var respObj = JSON.parse(data);
                    $("#showNotification").hide();
                    if (respObj["response_code"] <= 0) {
                        alert("There are some error in the system. Please contact with the admin.");
                        return;
                    }
                    var id = respObj["id"];
                    if (id > 0)
                        deleteById(response_obj["data"], id);
                    else
                        deleteByFileName(response_obj["data"], respObj["file_name"]);
                });

    });


});

function downloadSettings() {
    var requestJson = new Object();
    requestJson.request_type = "settings";
    requestJson.user_id = user_id;

    var wrapperObj = new Object();
    wrapperObj.json = JSON.stringify(requestJson);

    $.post("/mobile_web_ajax", wrapperObj,
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
                //alert(JSON.stringify(settings));
                $("#subject").html(listToOptionList(settings["sub_list"]));
                $("#activity").html(listToOptionList(settings["act_list"]));
                $("#position").html(listToOptionList(settings["pos_list"]));
            });
}

function getTable(responseObj) {


    html_text = "<table id='dataTable' align='center'> <caption> Subject: " + getName(responseObj["sub_id"], settings["sub_list"]) + "</caption> ";
    html_text += "<tr> <th>SL</th> <th>Subject</th> <th>Activity</th> <th>Position</th> <th>Time </th> <th>Start Time </th> <th>Annotation Duration</th> <th>File Duration</th><th>Condition</th><th>delete</th></tr>"

    var list = responseObj["data"];
    var l = list.length;
    //alert(JSON.stringify(responseObj));


    for (var i = 0; i < l; i++)
    {
        html_text += "<tr>";
        html_text += "<td width='3%'>" + (i + 1) + "</td>";
        html_text += "<td width='10%'>" + getName(list[i]["subject"], settings["sub_list"]) + "</td>";
        html_text += "<td width='10%'>" + getName(list[i]["activity"], settings["act_list"]) + "</td>";
        html_text += "<td width='10%'>" + getName(list[i]["position"], settings["pos_list"]) + "</td>";
        html_text += "<td width='25%'>" + list[i]["time"] + "</td>";
        html_text += "<td width='7%'>" + list[i]["start_time"] + "</td>";        
        html_text += "<td width='15%'>" + list[i]["duration1"] + "</td>";
        html_text += "<td width='15%'>" + list[i]["duration2"] + "</td>";
        html_text += "<td width='10%'>" + list[i]["condition"] + "</td>";
        html_text += "<td width='5%'><a href='#' idn=" + i + ">delete</a> </td>";
        html_text += "</tr>";
    }
    html_text += "</table>";
    html_text += "<br/>";
    $("#divTable").html(html_text);
}

function listToOptionList(list) {
    var option = '';
    for (i = 0; i < list.length; i++) {
        option += '<option value="' + list[i]["id"] + '">' + list[i]["name"] + '</option>';
    }
    return option;
}

function getName(id, list) {
    if(id<0)
        return "N/A";
    var l = list.length;
    for (var i = 0; i < l; i++) {
        if (list[i]["id"] == id) {
            return list[i]["name"];
        }
    }
    return "Not found";
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

function deleteById(list, id) {
    var l = list.length;
    for (var i = 0; i < l; i++) {
        if (list[i]["id"] == id) {
            list.splice(i, 1);            
            getTable(response_obj);
        }
    }
}

function deleteByFileName(list, name) {
    var l = list.length;
    for (var i = 0; i < l; i++) {
        if (list[i]["file_name"] == name) {
            list.splice(i, 1);            
            getTable(response_obj);
        }
    }
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
/*********************** Global variables ************************/
var start = 0;
var end = 0;

function logout() {
    window.location.replace("http://mooncake.cs.virginia.edu:9000/logout");
}


$(function() {
    $(".datePicker").datepicker();
    $("#datePicker1").val(getToDate());
    $(".divContent").hide();
    $(".iniHide").hide();

    $("#cboxActivity").click(function() {
        $("#divActivity").toggle(500);
    });

    $("#cboxLocation").click(function() {
        $("#divLocation").toggle(500);
    });
    
    $("#cboxHeartRate").click(function() {
        $("#divHeartRate").toggle(500);
    });
    
    $("#cboxBloodPressure").click(function() {
        $("#divBloodPressure").toggle(500);
    });

    $("#btnShowActivity").click(function() {
        
        $("#divActivityBarContainer").html("<img id='imgIdLoaderActivity' class='imgClassLoader' src='/assets/images/loader.gif'/>");
        
        $(".iniHide").show();
        var startDate = $("#datePicker1").val();
        var startTime = $("#time1").val();
        var endDate = $("#datePicker2").val();
        var endTime = $("#time2").val();
        var jsonObj = new Object();
        jsonObj.startDate = startDate;
        jsonObj.type = "activity";

        var wrapperObj = new Object();
        wrapperObj.json = JSON.stringify(jsonObj);

        $.post("/ajax", wrapperObj,
                function(data, status) {

                    var responseObj = JSON.parse(data);
                    $("#notification").text("Connection error");
                    $("#divActivityBarContainer").html(responseObj.html);
                    //alert("Data: " + responseObj.html + "\nStatus: " + status);
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


});



/*************************************** Functions ****************************/

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


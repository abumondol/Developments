
function loginCheck() {
    $("#notification").text("Please wait...");
    var obj = new Object();
    obj.user_code = $("#user_code").val();
    obj.password = $("#password").val();
    //var jsonStr = JSON.stringify(obj);
    //$("#notification").text(jsonStr);
    $.post("/mobile_web_login_check", obj,
            function(data, status) {

                if (data.valueOf() === "OK")
                    window.location.replace("http://mooncake.cs.virginia.edu:9000/mobile_web");
                else if (data.valueOf() === "NO")
                    $("#notification").text("Invalid Code or Password");
                /*else
                    $("#notification").text("Server error");*/
                //alert("Data: " + data + "\nStatus: " + status);
            });

}



/**
 * Created by kenneth on 2018-04-01.
 */
$(document).ready(function(){
    $(".ADD .button").on("click", function () {
        var userid = $(this).siblings("#ADD_userid").val();
        var amount = $(this).siblings("#ADD_amount").val();
        var data = {"command": ("ADD," + userid + "," + amount), "transaction": "1"};
        var request = $.ajax({
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8",
            success : function(result) {
                alert(result.text);
            }
        });
    })
});
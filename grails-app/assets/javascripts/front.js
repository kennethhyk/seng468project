/**
 * Created by kenneth on 2018-04-01.
 */
$(document).ready(function(){
    $(".CREATEUSER .button").on("click", function () {
        var userid = $(this).siblings("#CREATEUSER_userid").val();
        var data = {"username": userid};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/addUser",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
            }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".ADD .button").on("click", function () {
        var userid = $(this).siblings("#ADD_userid").val();
        var amount = $(this).siblings("#ADD_amount").val();
        var data = {"command": ("ADD," + userid + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
            }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".QUOTE .button").on("click", function () {
        var userid = $(this).siblings("#QUOTE_userid").val();
        var stocksymbol = $(this).siblings("#QUOTE_stocksymbol").val();
        var data = {"command": ("QUOTE," + userid + "," + stocksymbol), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
            }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".BUY .button").on("click", function () {
        var userid = $(this).siblings("#BUY_userid").val();
        var stocksymbol = $(this).siblings("#BUY_stocksymbol").val();
        var amount = $(this).siblings("#BUY_amount").val();
        var data = {"command": ("BUY," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".COMMIT_BUY .button").on("click", function () {
        var userid = $(this).siblings("#COMMIT_BUY_userid").val();
        var data = {"command": ("COMMIT_BUY," + userid), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".CANCEL_BUY .button").on("click", function () {
        var userid = $(this).siblings("#CANCEL_BUY_userid").val();
        var data = {"command": ("CANCEL_BUY," + userid), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".SELL .button").on("click", function () {
        var userid = $(this).siblings("#SELL_userid").val();
        var stocksymbol = $(this).siblings("#SELL_stocksymbol").val();
        var amount = $(this).siblings("#SELL_amount").val();
        var data = {"command": ("SELL," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".COMMIT_SELL .button").on("click", function () {
        var userid = $(this).siblings("#COMMIT_SELL_userid").val();
        var data = {"command": ("COMMIT_SELL," + userid), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".CANCEL_SELL .button").on("click", function () {
        var userid = $(this).siblings("#CANCEL_SELL_userid").val();
        var data = {"command": ("CANCEL_SELL," + userid), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".SET_BUY_AMOUNT .button").on("click", function () {
        var userid = $(this).siblings("#SET_BUY_AMOUNT_userid").val();
        var stocksymbol = $(this).siblings("#SET_BUY_AMOUNT_stocksymbol").val();
        var amount = $(this).siblings("#SET_BUY_AMOUNT_amount").val();
        var data = {"command": ("SET_BUY_AMOUNT," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".CANCEL_SET_BUY .button").on("click", function () {
        var userid = $(this).siblings("#CANCEL_SET_BUY_userid").val();
        var stocksymbol = $(this).siblings("#CANCEL_SET_BUY_stocksymbol").val();
        var amount = $(this).siblings("#CANCEL_SET_BUY_amount").val();
        var data = {"command": ("CANCEL_SET_BUY," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".SET_BUY_TRIGGER .button").on("click", function () {
        var userid = $(this).siblings("#SET_BUY_TRIGGER_userid").val();
        var stocksymbol = $(this).siblings("#SET_BUY_TRIGGER_stocksymbol").val();
        var amount = $(this).siblings("#SET_BUY_TRIGGER_amount").val();
        var data = {"command": ("SET_BUY_TRIGGER," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".SET_SELL_AMOUNT .button").on("click", function () {
        var userid = $(this).siblings("#SET_SELL_AMOUNT_userid").val();
        var stocksymbol = $(this).siblings("#SET_SELL_AMOUNT_stocksymbol").val();
        var amount = $(this).siblings("#SET_SELL_AMOUNT_amount").val();
        var data = {"command": ("SET_SELL_AMOUNT," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".SET_SELL_TRIGGER .button").on("click", function () {
        var userid = $(this).siblings("#SET_SELL_TRIGGER_userid").val();
        var stocksymbol = $(this).siblings("#SET_SELL_TRIGGER_stocksymbol").val();
        var amount = $(this).siblings("#SET_SELL_TRIGGER_amount").val();
        var data = {"command": ("SET_SELL_TRIGGER," + userid + "," + stocksymbol + "," + amount), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".CANCEL_SET_SELL .button").on("click", function () {
        var userid = $(this).siblings("#CANCEL_SET_SELL_userid").val();
        var stocksymbol = $(this).siblings("#CANCEL_SET_SELL_stocksymbol").val();
        var data = {"command": ("CANCEL_SET_SELL," + userid + "," + stocksymbol), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".DUMPLOG .button").on("click", function () {
        var userid = $(this).siblings("#DUMPLOG_file").val();
        var data = {"command": ("DUMPLOG," + userid), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

    $(".DISPLAY_SUMMARY .button").on("click", function () {
        var userid = $(this).siblings("#DISPLAY_SUMMARY_userid").val();
        var data = {"command": ("DISPLAY_SUMMARY," + userid), "transaction": "1"};
        var res = $.ajax({
            async : false,
            url: "http://localhost:8080/commandHandler/index",
            method: "POST",
            data: JSON.stringify(data),
            dataType : "json",
            contentType: "application/json; charset=utf-8"
        }).responseText;
        alert($.parseJSON(res)['0']['response']);
    });

});
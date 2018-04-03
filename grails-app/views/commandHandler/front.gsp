<%--
  Created by IntelliJ IDEA.
  User: kenneth
  Date: 2018-04-01
  Time: 9:25 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Stock System</title>
    <asset:javascript src="jquery-2.2.0.min.js"/>
    <asset:javascript src="front.js"/>
    <style>
        .button {
            background-color: #2d8cff;
            padding: 2px 10px;
            color: white;
            border-radius: 3px;
            font-family: SansSerif, "sans-serif";
            cursor: pointer;
        }
    </style>
</head>

<body>
    <hr>
    <div class="CREATEUSER">
        CREATE USER: <br> userid:<input id="CREATEUSER_userid" type="text"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="ADD">
        ADD: <br> userid:<input type="text" id="ADD_userid"> amount:<input type="text" id="ADD_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="QUOTE">
        QUOTE: <br> userid:<input type="text" id="QUOTE_userid"> stocksymbol:<input type="text" id="QUOTE_stocksymbol"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="BUY">
        BUY: <br> userid:<input type="text" id="BUY_userid"> stocksymbol:<input type="text" id="BUY_stocksymbol"> amount:<input type="text" id="BUY_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="COMMIT_BUY">
        COMMIT_BUY: <br> userid:<input type="text" id="COMMIT_BUY_userid"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="CANCEL_BUY">
        CANCEL_BUY: <br> userid:<input type="text" id="CANCEL_BUY_userid"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="SELL">
        SELL: <br> userid:<input type="text" id="SELL_userid"> stocksymbol:<input type="text" id="SELL_stocksymbol"> amount:<input type="text" id="SELL_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="COMMIT_SELL">
        COMMIT_SELL: <br> userid:<input type="text" id="COMMIT_SELL_userid"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="CANCEL_SELL">
        CANCEL_SELL: <br> userid:<input type="text" id="CANCEL_SELL_userid"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="SET_BUY_AMOUNT">
        SET_BUY_AMOUNT: <br> userid:<input type="text" id="SET_BUY_AMOUNT_userid"> stocksymbol:<input type="text" id="SET_BUY_AMOUNT_stocksymbol"> amount:<input type="text" id="SET_BUY_AMOUNT_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="CANCEL_SET_BUY">
        CANCEL_SET_BUY: <br> userid:<input type="text" id="CANCEL_SET_BUY_userid"> stocksymbol:<input type="text" id="CANCEL_SET_BUY_stocksymbol"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="SET_BUY_TRIGGER">
        SET_BUY_TRIGGER:  <br> userid:<input type="text" id="SET_BUY_TRIGGER_userid"> stocksymbol:<input type="text" id="SET_BUY_TRIGGER_stocksymbol"> amount:<input type="text" id="SET_BUY_TRIGGER_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="SET_SELL_AMOUNT">
        SET_SELL_AMOUNT:<br> userid:<input type="text" id="SET_SELL_AMOUNT_userid"> stocksymbol:<input type="text" id="SET_SELL_AMOUNT_stocksymbol"> amount:<input type="text" id="SET_SELL_AMOUNT_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="SET_SELL_TRIGGER">
        SET_SELL_TRIGGER: <br> userid:<input type="text" id="SET_SELL_TRIGGER_userid"> stocksymbol:<input type="text" id="SET_SELL_TRIGGER_stocksymbol"> amount:<input type="text" id="SET_SELL_TRIGGER_amount"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="CANCEL_SET_SELL">
        CANCEL_SET_SELL: <br> userid:<input type="text" id="CANCEL_SET_SELL_userid"> stocksymbol:<input type="text" id="CANCEL_SET_SELL_stocksymbol"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="DUMPLOG">
        DUMPLOG: <br> filename:<input type="text" id="DUMPLOG_file"> <a class="button">SEND</a>
    </div>
    <br>
    <hr>
    <div class="DISPLAY_SUMMARY">
        DISPLAY_SUMMARY: <br> userid:<input type="text" id="DISPLAY_SUMMARY_userid"> <a class="button">SEND</a>
    </div>
</body>
</html>
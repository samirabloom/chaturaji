<#ftl strip_whitespace=true strict_syntax=true strip_text=true />
<#-- @ftlvariable name="oneTimeToken" type="java.lang.String" -->
<#-- @ftlvariable name="email" type="java.lang.String" -->
<#-- @ftlvariable name="validationErrors" type="java.util.List<String>" -->
<#setting url_escaping_charset="UTF-8" />

<#escape x as x?html>
<!DOCTYPE html>
<html lang="en_GB">
<head>
    <title>Chaturaji - Update Password</title>
    <style>
        body {
            color: #000;
            font-family: Helvetica, arial, freesans, clean, sans-serif;
            font-size: 0.85em;
            line-height: 1.125em;
        }

        .page_message {
            padding-bottom: 25px;
        }

        .error_box {
            padding: 2.5%;
            margin: 2.5% auto;
            border-style: solid;
            border-width: 2px;
            border-color: #ed3f3f;
            width: 90%;
            overflow: hidden;
        }

        form {
            padding: 2.5%;
            margin: 2.5%;
            border-style: dashed;
            border-width: 2px;
            border-color: #206f92;
            width: 90%;
        }
    </style>
</head>
<body onunload="">
<div class="header">Update Password</div>
<form action="/updatePassword" method="POST">

    <p class="page_message">Please provide a password of 8 or more characters with at least 1 digit and 1 letter</p>

    <#if validationErrors?? && (validationErrors?size > 0)>
        <div class="error_box">
            <p>There were problems with the data you entered:</p>
            <#list validationErrors as error>
                <p>&ndash; ${error}</p>
            </#list>
        </div>
    </#if>
    <div>
        <input id="email" name="email" type="hidden" value="${email!""}">
        <input id="oneTimeToken" name="oneTimeToken" type="hidden" value="${oneTimeToken!""}">

        <p>
            <label for="password">Password:</label> <input type="password" id="password" name="password" value="" autocomplete="off"/>
        </p>

        <p>
            <label for="passwordConfirm">Password Confirm:</label> <input type="password" id="passwordConfirm" name="passwordConfirm" value="" autocomplete="off"/>
        </p>

        <div style="width:100%; height: 1em;"></div>

        <p>
            <input type="submit" formnovalidate name="submit" value="Update Password">
        </p>
    </div>
</form>
</body>
</html>
</#escape>
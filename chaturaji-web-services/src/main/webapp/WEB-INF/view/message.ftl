<#ftl strip_whitespace=true strict_syntax=true strip_text=true />
<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="message" type="java.lang.String" -->
<#setting url_escaping_charset="UTF-8" />

<#escape x as x?html>
<!DOCTYPE html>
<html lang="en_GB">
<head>
    <title>Chaturaji - ${title!"Message"}</title>
    <style>
        body {
            color: #000;
            font-family: Helvetica, arial, freesans, clean, sans-serif;
            font-size: 0.85em;
            line-height: 1.125em;
        }

        .header {
            font-weight: bold;
            font-size: 2em;
            padding-top: 2.5%;
            padding-left: 2.5%;
            padding-bottom: 10px;
        }

        .error_message {
            padding: 2.5%;
            margin: 2.5%;
            border-style: solid;
            border-width: 2px;
            border-color: #7b2020;
            width: 90%;
        }

        .message {
            padding: 2.5%;
            margin: 2.5% auto;
            border-style: solid;
            border-width: 1px;
            border-color: #206f92;
            width: 90%;
        }
    </style>
</head>
<body onunload="">
<div class="header">${title!"Message"}</div>
<p <#if error?? && error> class="error_message" <#else> class="message" </#if>>${message!""}</p>
</body>
</html>
</#escape>
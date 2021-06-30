<!DOCTYPE html>
<html lang="en">
<head>
    <title>ServiceFocus</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="{{ request.contextPath }}/static/images/favicon/servicefocus_favicon.ico" rel="icon" type="image/png">
    <style>
        html, body {

            box-sizing: border-box;
        }

        body {
            font-family: verdana;
            font-size: 16px;
            color: #313131;
        }

        img {
            max-width: 100%;
        }

        .img-main-logo {
            margin: 10px auto;
        }

        .img-warning {
            max-width: 125px;
            vertical-align: middle;
            margin: 0 auto;
            text-align: center;
            display: inline-block;
        }

        .main-content-wrapper {
            width: 100%;
        }

        .main-content {
            position: absolute;
            top: 30%;
            left: 50%;
            transform: translateX(-50%) translateY(-30%);
            width: 85%;
            max-width: 750px;
        }

        .main-heading {
            font-size: 28px;
            font-weight: normal;
            border-bottom: 2px solid #dddddd;
            text-align: center;
            margin: 0 auto 15px 20px;
            color: #ec4f1c;
            display: inline-block;
        }

        .sub-heading {
            font-size: 22px;
            font-weight: normal;
            margin: 0 auto;
            color: #10b2e9;
        }

        .content-warning {
            margin: 0 auto;
            text-align: center;
        }

        @media only screen and (max-width: 600px) {
            body {
                font-size: 14px;
            }

            .main-heading {
                font-size: 20px;
                margin: 20px auto 0 auto;
            }

            .sub-heading {
                font-size: 18px;
            }

            .img-warning {
                max-width: 85px;
            }

            .main-content {

                top: 50%;
                transform: translateX(-50%) translateY(-50%);

            }
        }
    </style>
</head>
<body>
<header><img class="img-main-logo" src="{{ request.contextPath }}/static/images/SF_logo.png"/></header>
<div class="main-content-wrapper">
    <div class="main-content">
        <div class="content-warning">
            <img class="img-warning" src="{{ request.contextPath }}/static/images/error.png"
                 alt="Site Under Maintenance" title="Site Under Maintenance"/>
            <h2 class="main-heading">Successfully Logged Out</h2>
        </div>


       <!-- <p><b>For any issues or requests, Please contact the CareTech Solutions Shared Services at 248-823-0124 or x30124.</b></p>-->
      <!--  <h3 class="sub-heading">Sorry for the inconvenience!</h3>-->
    </div>
</div>
</body>
</html>
<html>
    <head>
        <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/bootstrap.css">
        <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/select2_min.css">
        <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/loading.min.css">
        <title>SF Interface</title>
        <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/jsgrid.css"/>
        <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/jsgrid-theme.css"/>
        <link rel="apple-touch-icon" type="image/png" sizes="57x57"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-57x57.png">
        <link rel="apple-touch-icon" type="image/png" sizes="60x60"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-60x60.png">
        <link rel="apple-touch-icon" type="image/png" sizes="72x72"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-72x72.png">
        <link rel="apple-touch-icon" type="image/png" sizes="76x76"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-76x76.png">
        <link rel="apple-touch-icon" type="image/png" sizes="114x114"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-114x114.png">
        <link rel="apple-touch-icon" type="image/png" sizes="120x120"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-120x120.png">
        <link rel="apple-touch-icon" type="image/png" sizes="144x144"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-144x144.png">
        <link rel="apple-touch-icon" type="image/png" sizes="152x152"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-152x152.png">
        <link rel="apple-touch-icon" type="image/png" sizes="180x180"
              href="{{ request.contextPath }}/static/images/favicon/apple-icon-180x180.png">
        <link rel="icon" type="image/png" sizes="192x192"
              href="{{ request.contextPath }}/static/images/favicon/android-icon-192x192.png">
        <link rel="icon" type="image/png" sizes="32x32"
              href="{{ request.contextPath }}/static/images/favicon/favicon-32x32.png">
        <link rel="icon" type="image/png" sizes="96x96"
              href="{{ request.contextPath }}/static/images/favicon/favicon-96x96.png">
        <link rel="icon" type="image/png" sizes="16x16"
              href="{{ request.contextPath }}/static/images/favicon/favicon-16x16.png">
        <link rel="manifest" href="{{ request.contextPath }}/static/images/favicon/manifest.json">
        <meta name="msapplication-TileColor" content="#ffffff">
        <meta name="msapplication-TileImage"
              content="{{ request.contextPath }}/static/images/favicon/ms-icon-144x144.png">
        <meta name="theme-color" content="#ffffff">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">

        <script type="text/javascript" src="{{ request.contextPath }}/static/js/jquery.js"></script>
        <script type="text/javascript" src="{{ request.contextPath }}/static/js/jquery_validate.js"></script>
        <script type="text/javascript" src="{{ request.contextPath }}/static/js/jquery.loading.min.js"></script>
        <script type="text/javascript" src="{{ request.contextPath }}/static/js/bootstrap.js"></script>
        <script type="text/javascript" src="{{ request.contextPath }}/static/js/multiselect.js"></script>
        <script type="text/javascript" src="{{ request.contextPath }}/static/js/jsgrid.js"></script>

        <style>
            .form-control, .btn {
                -webkit-border-radius: 0;
                -moz-border-radius: 0;
                border-radius: 0;
            }

            .form-control {
                border: 1px solid #31acce;
            }

            .panel-primary > .panel-heading {
                background-color: #32acd1;
            }
        </style>
        {% block styles %}

        {% endblock %}
    </head>
    <body>
        {% block content %}
        Default content goes here.
        {% endblock %}
    </body>
    <script>
        var leng;
        var start;
        var searchvalue;
        var CONTEXT_PATH = '{{ request.contextPath }}';
        var ClipboardHelper = {

                copyElement: function ($element) {
                    this.copyText($element.text())
                },
                copyText: function (text) // Linebreaks with \n
                {
                    var $tempInput = $("<textarea>");
                    $("body").append($tempInput);
                    $tempInput.val(text).select();
                    document.execCommand("copy");
                    $tempInput.remove();
                }
            }
        ;
        var headers = {};
    </script>


    {% block script %}
    {% endblock %}
</html>
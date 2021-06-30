{% extends "layout/layout" %}

{% block content %}
    <div style="background:#ECF0F1;padding:0;" id="frm" class="container">
        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="
    width: 100%;">
        <div style="padding: 20px !important;">
            <table class="table table-striped" style="table-layout: fixed;word-wrap:break-word;" id="dt">
                <thead style="background:#0ea3c4 ; color:white">
                <tr>
                    <td>Environment</td>
                    <td>Description</td>
                    <td>Host</td>
                    <td>Port</td>
                    <td>Database Name</td>
                    <td>Username</td>
                </tr>
                </thead>

                <tbody>

                {% for account in jdbcaccounts %}
                    <tr>
                        <td>{{ account.environment }}</td>
                        <td>{{ account.desc }}</td>
                        <td>{{ account.host }}</td>
                        <td>{{ account.port }}</td>
                        <td>{{ account.databaseName }}</td>
                        <td>{{ account.username }}</td>
                    </tr>
                {% endfor %}
                </tbody>
            </table>
        </div>
    </div>
{% endblock %}

{% block styles %}
    <style>
        .pad15 {
            padding: 15px !important;
        }
    </style>

{% endblock %}

{% block script %}
    <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/dt.css"/>
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/dt.js"></script>
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/jdbc/jdbc_accounts.js"></script>
{% endblock %}
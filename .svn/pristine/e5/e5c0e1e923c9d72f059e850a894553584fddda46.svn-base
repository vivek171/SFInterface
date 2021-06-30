{% extends "layout/layout" %}

{% block content %}
    <div style="background:#ECF0F1;padding:0;" id="frm" class="container">
        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="
    width: 100%;">
        <div style="padding: 20px !important;">
            <table class="table table-striped" style="table-layout: fixed;word-wrap:break-word;" id="dt">
                <thead style="background:#0ea3c4 ; color:white">
                <tr>
                    <td>Connection</td>
                    <td>Client</td>
                    <td>Host</td>
                    <td>Port</td>
                    <td>Domain</td>
                    <td>Base</td>
                    <td>Filter</td>
                    <td>Search DN</td>
                </tr>
                </thead>

                <tbody>

                {#{% for account in ldapaccount %}
                    <tr>
                        <td>{{ account.connName }}</td>
                        <td>{{ account.client }}</td>
                        <td>{{ account.host }}</td>
                        <td>{{ account.port }}</td>
                        <td>{{ account.domain }}</td>
                        <td>{{ account.base }}</td>
                        <td>{{ account.filter }}</td>
                        <td>{{ account.searchDN }}</td>
                    </tr>
                {% endfor %}#}
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
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/ldap_accounts.js"></script>
{% endblock %}
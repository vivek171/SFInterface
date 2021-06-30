{% extends "layout/layout" %}

{% block content %}
    <div style="background:#ECF0F1;padding:0;" id="frm" class="container">
        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="
    width: 100%;">
        <div style="padding: 20px !important;">
            <table class="table table-striped" style="table-layout: fixed;word-wrap:break-word;" id="dt">
                <thead style="background:#0ea3c4;color:white;">
                <tr>
                    <th></th>
                    <th>Endpoint Name</th>
                    <th>query</th>
                    <th>Account</th>
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>

               {# {% for endpoint in endpoints %}

                        {% if endpoint.active == true %}
                            <tr>
                            <td><button class="btn btn-info btn-xs copy" id="copy" data-eval="{{ request.contextPath }}/openapi/ldapquery/{{ endpoint.endPointName }}">Copy</button></td>
                            <td>{{ endpoint.endPointName }}</td>
                            <td>{{ endpoint.base }}</td>
                            <td>{{ endpoint.query }}</td>
                            <td>{{ endpoint.ldapAccounts.connName }}</td>
                            <td>
                                <a class="btn btn-info btn-xs Edit" style="width:50" href="{{ request.contextPath }}/ldap/editep/{{ endpoint.id }}">Edit</a>
                                <a class="btn btn-danger btn-xs Deactive" style="width:50;vertical-align:bottom"  href="{{ request.contextPath }}/ldap/deactiveep/{{ endpoint.id }}"> Deactive </a>
                            </tr>
                        {% else %}
                            <tr style="color:#999">
                            <td><button class="btn btn-info btn-xs copy" id="copy1" disabled data-eval="{{ request.contextPath }}/openapi/ldapquery/{{ endpoint.endPointName }}">Copy</button></td>
                            <td>{{ endpoint.endPointName }}</td>
                            <td>{{ endpoint.base }}</td>
                            <td>{{ endpoint.query }}</td>
                            <td>{{ endpoint.ldapAccounts.connName }}</td>
                            <td>
                                <a class="btn btn-info btn-xs" style="width:50" href="{{ request.contextPath }}/ldap/editep/{{ endpoint.id }}">Edit</a>
                                <a class="btn btn-info btn-xs" style="width:50;vertical-align:bottom" href="{{ request.contextPath }}/ldap/activeep/{{ endpoint.id }}"> Active </a>
                            </td>
                            </tr>
                        {% endif %}


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
0
{% block script %}
    <link type="text/css" rel="stylesheet" href="{{ request.contextPath }}/static/css/dt.css"/>
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/dt.js"></script>
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/jdbc/jdbc_endpoint_query.js"></script>
{% endblock %}
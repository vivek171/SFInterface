{% extends "layout/layout" %}

{% block content %}

<div style="background:#ECF0F1;padding:0;" id="frm"  class="container" >


    <form method="post" id="searchForm" class="form">
        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="width: 100%;">
        <div style="padding:30px">
            <table class="table table-striped" style="width:100%;table-layout: fixed;word-wrap:break-word;" id="displayEndPoint">

                <thead style="background:#0ea3c4 ; color:white">
                <tr>
                    <th></th>
                    <th>Endpoint Name</th>
                    <th>Endpoint Desc</th>
                    <th>Remedy Form</th>
                    <th>Modified Date</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody >
                {#{%for endpoint in Repo %}

                {% if endpoint.active == true %}
                <tr>
                    <td><input type="button" class="btn btn-primary btn-xs copyep" id="copyep"  data-eval="{{ request.contextPath }}/page/endpointurl/{{ endpoint.endPointName }}" value="Copy"> </td>
                    <td><a  href='{{ request.contextPath }}/page/editendpointdetails/{{endpoint.endPointName}}'>{{endpoint.endPointName}}</a>   </td>
                    <td>{{endpoint.endPointDescription}}</td>
                    <td>{{endpoint.formName}}</td>
                    <td>{{endpoint.date}}</td>

                    <td><a class="btn btn-danger btn-xs" style="width:50" href="{{ request.contextPath }}/page/deactiveep/{{ endpoint.id }}">Inactive</a> </td>
                </tr>
                {% else %}
                <tr style="color:#999">
                    <td ><input type="button" class="btn btn-primary btn-xs copyep" id="copyep1" disabled data-eval="{{ request.contextPath }}/page/endpointurl/{{ endpoint.endPointName }}" value="Copy"> </td>
                    <td><a style="color:#999" href='{{ request.contextPath }}/page/editendpointdetails/{{endpoint.endPointName}}'>{{endpoint.endPointName}}</a>   </td>
                    <td >{{endpoint.endPointDescription}}</td>
                    <td>{{endpoint.formName}}</td>
                    <td >{{endpoint.date}}</td>

                    <td><a class="btn btn-info btn-xs" style="width:50" href="{{ request.contextPath }}/page/activeep/{{ endpoint.id }}">Active</a> </td>
                </tr>
                {% endif %}


                {%endfor %}#}
                </tbody>
            </table>
        </div>
    </form>

</div>
{% endblock %}
{% block script %}

<link rel="stylesheet" type="text/css" href="{{ request.contextPath }}/static/css/dt.css" />

<script type="text/javascript" src="{{ request.contextPath }}/static/js/dt.js"></script>
<script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/endpoint_query.js"></script>


{% endblock %}


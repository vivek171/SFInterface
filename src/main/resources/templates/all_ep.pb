{% extends "layout/layout" %}

{% block content %}

    <div style="background:#ECF0F1;padding:0;" id="frm" class="container">

        <form method="post" id="searchForm" class="form">
            <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="width: 100%;">
            <div style="padding:30px">
                <table class="table table-striped" style="width:100%" id="displayEndPoint">

                    <thead style="background:#0ea3c4 ; color:white">
                    <tr>
                        <th>path</th>
                        <th>methods</th>
                        <th>consumes</th>
                        <th>produces</th>
                        <th>params</th>
                        <th>headers</th>
                    </tr>
                    </thead>
                    <tbody>
                    {% for endPoint in endpoints %}

                        <tr>
                            <td>{{ endPoint.patternsCondition }}</td>
                            <td>{{ endPoint.methodsCondition }}</td>
                            <td>{{ endPoint.consumesCondition }}</td>
                            <td>{{ endPoint.producesCondition }}</td>
                            <td>{{ endPoint.paramsCondition }}</td>
                            <td>{{ endPoint.headersCondition }}</td>
                        </tr>

                    {% endfor %}
                    </tbody>
                </table>
                <!--   <div class="bottom" style="padding:10" !important>
                       <div class="col-md-4"  id="selectBox"></div>
                       <div class="col-md-4" style="float:unset;text-align:center" id="infoText"></div>
                       <div class="col-md-4" id="pagination"></div>
                   </div>-->
            </div>
        </form>

    </div>
{% endblock %}
{% block script %}

    <link rel="stylesheet" type="text/css" href="{{ request.contextPath }}/static/css/dt.css"/>

    <script type="text/javascript" src="{{ request.contextPath }}/static/js/dt.js"></script>
    <script>
        $(function () {
            $('#displayEndPoint').dataTable();


            $('input[type="search"]').addClass('form-control')
            $('#displayEndPoint_length > label > select').addClass('form-control')
            $('#displayEndPoint_filter > label').addClass('form-inline')
            $('#displayEndPoint_length > label').addClass('form-inline')

        })
    </script>
{% endblock %}


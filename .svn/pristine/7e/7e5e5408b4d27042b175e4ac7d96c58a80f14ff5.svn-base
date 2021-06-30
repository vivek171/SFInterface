{% extends "layout/layout" %}

{% block styles %}
    <style>
        .col-md-3 {
            text-align: right;
        }

        .form-control, btn {
            -webkit-border-radius: 0;
            -moz-border-radius: 0;
            border-radius: 0;
        }
    </style>
{% endblock %}
{% block content %}
    <div style="" id="frm" class="container">
        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="
    width: 100%;">

        <form method="post" action="{{ request.contextPath }}/jdbc/create" id="jdbcAccountCreateForm">
            <div style="border:1px; background:#ECF0F1;padding:30px;">

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Environment Name</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="connName" id="connName" class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Description</label>
                    </div>
                    <div class="col-md-6">
                        <textarea class="form-control" id="desc" name="desc" required></textarea>
                    </div>
                </div>


                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Host</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="host" id="host" class="form-control" required>
                        <input type="checkbox" id="status"> status
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Port</label>
                    </div>
                    <div class="col-md-6">
                        <input type="number" name="port" id="port" class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Database</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="database" id="database" class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Username</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="username" id="username" class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Password</label>
                    </div>
                    <div class="col-md-6">
                        <input type="password" name="password" id="password" class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3 col-md-offset-6">
                        <button type="button" value="back" id="back" class="btn btn-primary" style="background-color:#0ea3c4 ;margin-right:13;width:100">Back</button>
                        <input type="submit" value="Submit" class="btn btn-primary" style="background-color:#0ea3c4 ;margin-right:13;width:100">
                    </div>

                </div>

            </div>
        </form>
    </div>
{% endblock %}

{% block script %}
<script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/jdbc/jdbccreate.js"></script>
{% endblock %}
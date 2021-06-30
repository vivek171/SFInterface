{% extends "layout/layout" %}

{% block content %}
    <div style="background:#ECF0F1;padding:0;" id="frm" class="container">
        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="
    width: 100%;">
        <div style="padding: 30px !important;">

            <form method="post" id="epcreateForm">
                <div class="messages" id="messages"></div>
                <input type="hidden" value="0" name="op" id="op">
                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Endpoint Name</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" required name="ename" id="ename" class="form-control"/>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Connection</label>
                    </div>
                    <div class="col-md-6">
                        <select name="connName" id="connName" required class="form-control">
                            <option value="">Select One</option>
                            {% for account in accounts %}
                                <option title="{{ account.id }}"
                                        value="{{ account.connName }}">{{ account.connName }}</option>
                            {% endfor %}
                        </select>
                    </div>
                </div>


                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Base</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="base" id="base" required class="form-control"/>
                    </div>
                </div>


                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Query</label>
                    </div>
                    <div class="col-md-6">
                        <textarea class="form-control" id="query" required name="query" rows="8"></textarea>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Required Fields
                            <small>( Seperated By Comma )</small>
                        </label>
                    </div>
                    <div class="col-md-6">
                        <textarea class="form-control" id="rf" name="rf" rows="5"></textarea>
                    </div>
                </div>

                <div class="center-block text-center">
                    <div class="row form-group">
                        <button type="button" value="back" id="back" class="btn btn-primary" style="background-color:#0ea3c4 ;margin-right:13;width:100">Back</button>
                        <input type="submit" class="btn btn-primary" value="Save" style="background-color:#0ea3c4 ;margin-right:13;width:100" id="save"/>
                        <input type="button" class="btn btn-primary" id="run" style="background-color:#0ea3c4 ;margin-right:13;width:100" value="Run >>"/>
                    </div>
                </div>
            </form>
            <div class="row" id="simulate">
                <img src="{{ request.contextPath }}/static/images/30.gif" class="center-block" id="runLoader">
            </div>
        </div>
    </div>
{% endblock %}


{% block styles %}
    <style>


    </style>
{% endblock %}

{% block script %}
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/ldapquery.js"></script>
{% endblock %}
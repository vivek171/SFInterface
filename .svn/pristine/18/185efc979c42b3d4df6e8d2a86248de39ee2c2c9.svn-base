{% extends "layout/layout" %}

{% block content %}
    <div style="" id="frm" class="container">

        <img src="{{ request.contextPath }}/static/images/Finallogo.png" style="
    width: 100%;">

        <div style="border:1px; background:#ECF0F1;padding:30px;">
            <form method="post" id="editForm" class="form">
                <div class="row form-group">
                    <div class="col-md-3">
                        <label>EndPoint Name</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" class="form-control" id="endpointname" value="{{ Repo.endPointName }}"
                               for="endpointname" name="endpointName" disabled required>
                        <small style="color:red" id="endPointExistsErroe"></small>

                    </div>
                </div>
                <div class="row form-group">
                    <div class="col-md-3">
                        <label>EndPoint Description</label>
                    </div>
                    <div class="col-md-6">
                        <textarea class="form-control" id="endpointdesc"
                                  name="endpointdesc">{{ Repo.endPointDescription }}</textarea>
                    </div>
                </div>

                <div class="row form-group" style="display:none">
                    <div class="col-md-3">
                        <label>EndPoint URL</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" class="form-control" id="endpointurl" name="endpointurl"
                               value="{{ Repo.endPointKey }}"
                               disabled required>
                        <small style="color:red"> {{ request.requestURL }}/{{ uid }}</small>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Remedy Forms</label>
                    </div>
                    <div class="col-md-6">
                        <select id="form" class="form-control" required>
                            <option value="">Select Forms</option>
                            {% for item in forms %}
                                <option value="{{ item.formName }}"
                                        title="{{ item.formName }}">{{ item.formName }}</option>
                            {% endfor %}
                        </select>
                    </div>
                </div>


                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Form Fields</label>
                    </div>
                    <div class="col-md-9" style="padding:0">
                        <div class="col-md-5">
                            <select name="from[]" id="multiselect" class="form-control" size="8" multiple="multiple">

                            </select>
                        </div>

                        <div class="col-md-1 form-group" style="margin:15">
                            <button type="button" id="multiselect_rightAll" class="btn btn-success"
                                    style="background-color:#0ea3c4;margin-bottom:5"><i
                                        class="glyphicon glyphicon-forward"></i></button>
                            <button type="button" id="multiselect_rightSelected" class="btn btn-success"
                                    style="background-color:#0ea3c4;margin-bottom:5"><i
                                        class="glyphicon glyphicon-chevron-right"></i></button>
                            <button type="button" id="multiselect_leftSelected" class="btn btn-success"
                                    style="background-color:#0ea3c4;margin-bottom:5"><i
                                        class="glyphicon glyphicon-chevron-left"></i></button>
                            <button type="button" id="multiselect_leftAll" class="btn btn-success"
                                    style="background-color:#0ea3c4;margin-bottom:5"><i
                                        class="glyphicon glyphicon-backward"></i></button>
                        </div>


                        <div class="col-md-5">
                            <select name="to[]" id="multiselect_to" class="form-control" size="8"
                                    multiple="multiple">
                                {% for field in fields %}
                                    <option value="{{ field.fieldId }}"
                                            title="{{ field.fieldName }}">{{ field.fieldName }}</option>
                                {% endfor %}

                            </select>
                        </div>
                    </div>
                </div>
                <div hidden="hidden" class="row form-group">
                    <div class="col-md-3">
                        <label>Qualification String</label>
                    </div>
                    <div class="col-md-6">
                        <textarea class="form-control" id="qualificationstring" name="qualificationstring"></textarea>
                    </div>
                </div>
                </br>
                <!--<img src="/images/30.gif" id="loader" />-->
                <div id="jsGrid" class="row form-group" style="background:red" !important>

                </div>

                <div class="center-block text-center">
                    <div class="row form-group">
                        <button type="button" value="back" id="back" class="btn btn-primary"
                                style="background-color:#0ea3c4 ;margin-right:13;width:100">Back
                        </button>
                        <button type="submit" value="submit" id="submit" class="btn btn-primary"
                                style="background-color:#0ea3c4;margin-right:13;width:100">Update
                        </button>
                        <button type="button" value="preview" id="preview" class="btn btn-primary"
                                style="background-color:#0ea3c4;margin-right:13;width:100">Preview
                        </button>


                    </div>
                </div>
            </form>
            <div class="row" id="simulate" style="padding:30px">
                <img src="{{ request.contextPath }}/static/images/30.gif" class="center-block" id="runLoader">
                <div class="row" id="txt">
                </div>
            </div>
        </div>

    </div>
{% endblock %}


{% block script %}
    <script src="{{ request.contextPath }}/static/js/select2_min.js" defer></script>
    <script>
        var strData = JSON.parse("{{ filtersJson | escape(strategy="js") }}")
    </script>
    <style>
        #jsGrid > div.jsgrid-grid-header.jsgrid-header-scrollbar > table > tr.jsgrid-header-row > th {
            color: white;
            background-color: #0ea3c4;
            text-align: center;
        }

        #jsGrid > div.jsgrid-grid-header.jsgrid-header-scrollbar > table > tr.jsgrid-insert-row > td {
            background: #e3f4ff;
            text-align: center;
        }

    </style>
    <script>
        var selectedForm = '{{ Repo.formName | escape('js') }}'
    </script>
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/edit_query.js"></script>
{% endblock %}
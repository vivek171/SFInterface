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

        <form method="post" action="{{ request.contextPath }}/ldap/create" id="ldapAccountCreateForm">
            <div style="border:1px; background:#ECF0F1;padding:30px;">

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Connection Name</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="connName" id="connName" class="form-control"
                               value="{{ ldapaccount.connName }}" disabled required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Client Name</label>
                    </div>
                    <div class="col-md-6">
                        <select required class="form-control" name="client">
                            {% for sc in clients %}
                                {% if sc == ldapaccount.client %}
                                    <option value="{{ sc }}" selected>{{ sc }}</option>
                                {% else %}
                                    <option value="{{ sc }}">{{ sc }}</option>
                                {% endif %}

                            {% endfor %}
                        </select>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Description</label>
                    </div>
                    <div class="col-md-6">
                        <textarea class="form-control" id="desc" name="desc" required>{{ ldapaccount.desc }}</textarea>
                    </div>
                </div>


                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Host</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="host" id="host" class="form-control" value="{{ ldapaccount.host }}" required>
                        {% if ldapaccount.sso==true %}
                            <input type="checkbox" id="sso"  checked> sso
                        {% else %}
                            <input type="checkbox" id="sso"> sso
                        {% endif %}
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Port</label>
                    </div>
                    <div class="col-md-6">
                        <input type="number" name="port" id="port" class="form-control" value="{{ ldapaccount.port }}"
                               required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Username</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="username" id="username" class="form-control"
                               value="{{ ldapaccount.username }}" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Password</label>
                    </div>
                    <div class="col-md-6">
                        <input type="password" name="password" id="password" value="{{ ldapaccount.password }}"
                               class="form-control" required>
                    </div>
                </div>
                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Domain</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="domain" id="domain" value="{{ ldapaccount.domain }}"
                               class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Base</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="base" id="base" value="{{ ldapaccount.base }}" class="form-control"
                               required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Filter</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="filter" id="filter" value="{{ ldapaccount.filter }}"
                               class="form-control" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3">
                        <label>Search DN</label>
                    </div>
                    <div class="col-md-6">
                        <input type="text" name="searchDN" id="searchDN" class="form-control"
                               value="{{ ldapaccount.searchDN }}" required>
                    </div>
                </div>

                <div class="row form-group">
                    <div class="col-md-3 col-md-offset-6">
                        <button type="button" value="back" id="back" class="btn btn-primary"
                                style="background-color:#0ea3c4 ;margin-right:13;width:100">Back
                        </button>
                        <input type="submit" value="Submit" class="btn btn-primary"
                               style="background-color:#0ea3c4 ;margin-right:13;width:100">
                    </div>

                </div>

            </div>
        </form>
    </div>
{% endblock %}

{% block script %}
    <script type="text/javascript" src="{{ request.contextPath }}/static/js/custom/ldapcreate.js"></script>
{% endblock %}
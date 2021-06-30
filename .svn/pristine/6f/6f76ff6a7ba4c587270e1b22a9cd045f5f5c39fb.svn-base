/*Edit endpoint js*/
$(document).ready(function () {

    var qry = "";
    var error = false;
    $('#runLoader').hide();
    $.showLoading({
        name: 'circle-turn'
    });

    function currentQry() {
        var tmpQey = "";
        var items = $("#jsGrid").jsGrid("option", "data");
        $.each(items, function (index, o) {

            if (index == (items.length - 1)) {
                tmpQey += " ('" + o['columnName'] + "' " + o['condition'] + " \"" + o['columnValue'] + "\") "
            } else {
                tmpQey += "('" + o['columnName'] + "' " + o['condition'] + " \"" + o['columnValue'] + "\") " + o['appendCondition']
            }
        });

        return tmpQey;
    }

    function updateQuery() {
        qry = currentQry();
    }

    $('#back').click(function () {
        $.showLoading({
            name: 'circle-turn'
        });
        window.location.href = CONTEXT_PATH + '/page/endpointdetails'+urlparams;
        $.hideLoading();
    });

    $('#form').val(selectedForm);
    $("#form").select2();

    $.ajax({
        url: CONTEXT_PATH + '/page/forms',
        headers: headers,
        method: 'GET',
        success: function (data) {
            $.each(data, function (i, o) {
                $('#form').append("<option value='" + o.formId + "'>" + o.formName + "</option>");
            })
        },
        error: function (data) {
            console.log(data);

        }
    });


    $('#multiselect').multiselect();
    var i = 0;


    $.ajax({

        url: CONTEXT_PATH + '/page/internal/fields',//formName change
        data: {formName: selectedForm},
        headers: headers,
        method: 'GET',

        success: function (data) {

            $.each(data, function (index, singleData) {
                i = 0;
                $.each($('#multiselect_to').find('option'), function (j, x) {
                    if (singleData.fieldId == x.value)
                        i = 1;
                });
                if (i == 0) {
                    $('#multiselect').attr('disabled', false);
                    $('#multiselect').append("<option value=" + singleData.fieldId + " title=" + singleData.fieldName + ">" + singleData.fieldName + "</option>")

                }
            });

            var condition = [{
                "id": ">",
                "value": ">"
            },

                {
                    "id": "<",
                    "value": "<"
                },
                {
                    "id": "!=",
                    "value": "!="
                },
                {
                    "id": "=",
                    "value": "="
                },
                {
                    "id": "like",
                    "value": "like"
                },
            ];

            var appendCondition = [{
                "id": "AND",
                "value": "AND"
            },
                {
                    "id": "OR",
                    "value": "OR"
                }

            ]

            $("#jsGrid").jsGrid({
                width: "100%",
                height: "auto",

                heading: true,
                inserting: true,
                editing: true,
                data: strData,
                controller: {

                    insertItem: function (item) {
                        updateQuery()
                    },
                    updateItem: function (item) {
                        updateQuery()
                    },
                    deleteItem: function (item) {
                        updateQuery()
                    }
                },

                fields: [{
                    title: "Form Fields",
                    name: "columnName",
                    type: "select",
                    items: data,
                    valueField: "fieldName",
                    textField: "fieldName"
                },
                    {
                        name: "condition",
                        title: "Condition",
                        type: "select",
                        items: condition,
                        valueField: "id",
                        textField: "value"
                    },
                    {
                        name: "columnValue",
                        title: "Value",
                        type: "text",
                        width: 150,
                        validate: "required"
                    },
                    {
                        name: "appendCondition",
                        title: "Append",
                        type: "select",
                        items: appendCondition,
                        valueField: "id",
                        textField: "value",
                        width: 150,
                        validate: "required"
                    },
                    {
                        type: "control",
                        editButton: false,
                        modeSwitchButton: false
                    }
                ]
            });
            /*        $('#loader').hide()*/
            $.hideLoading();
        },
        error: function (data) {
            $.hideLoading();
            alert("Endpoint Not Available!");
        }
    });

    $('#preview').on('click', function () {
        $('#messages').html("");
        var error = [];
        $('#txt').html("");
        $('#runLoader').show();

        var formData = new FormData();

        formData.append('endpointName', $('#endpointname').val());
        formData.append('endpointDesc', $('#endpointdesc').val());
        formData.append('endpointUrl', $('#endpointurl').val());
        formData.append('formName', $('#form').val());
        formData.append('qualificationString', currentQry());
        var fieldid = {}, fieldName = {};

        $.each($('#multiselect_to').find('option'), function (i, x) {
                var cString = x.value + "^" + x.text;
                formData.append('selectedFields[]', cString);
                fieldid[x.value] = x.value;
                fieldName[x.value] = x.text;

            }
        );

        if (jQuery.isEmptyObject(fieldid)) {
            $.each($('#multiselect').find('option'), function (i, x) {
                    var cString = x.value + "^" + x.text;
                    formData.append('selectedFields[]', cString);
                    fieldid[x.value] = x.value;
                    fieldName[x.value] = x.text;
                }
            );
        }
        var items = $("#jsGrid").jsGrid("option", "data");

        $.each(items, function (index, o) {
            formData.append('qualification[]', o['Form Fields'] + "^" + o['Condition'] + "^" + o['Value'] + "^" + o['Append']);
        })

        if ($('#form').val() === "") {
            error.push("Form Name is Required")
        }

        if (error.length > 0) {

            messages = '<div class="alert alert-danger">';
            $.each(error, function (i, v) {
                messages += v
            });
            messages += "</div>";
            $('#messages').html(messages);
            $('#runLoader').hide();
        } else {

            $.ajax({
                url: CONTEXT_PATH + '/page/previewendpoint',
                type: 'POST',
                processData: false,
                headers: headers,
                data: formData,
                contentType: false,

                success: function (result) {
                    var pretty = "";

                    var obj = JSON.parse(result);
                    pretty = JSON.stringify(obj, undefined, 4);

                    $('#runLoader').hide();
                    var txtData = "<label>Endpoint Result</label><textarea class='form-control' rows='8'> " + pretty + "</textarea>";
                    $('#txt').html(txtData);

                },
                error: function (error) {
                    $('#runLoader').hide();
                }
            })

        }
    })

    $("#form").on('change', function () {

        $("#jsGrid").jsGrid("destroy");

        $('#multiselect_to').html("");
        $('#multiselect').html("");
        $.showLoading({
            name: 'circle-turn'
        });
        $.ajax({
            url: CONTEXT_PATH + '/page/internal/fields',
            data: {formName: $('#form').val()},
            headers: headers,
            method: 'GET',

            success: function (data) {

                $.each(data, function (index, singleData) {

                    $('#multiselect').attr('disabled', false);
                    $('#multiselect').append("<option value=" + singleData.fieldId + " title=" + singleData.fieldName + ">" + singleData.fieldName + "</option>")
                })
                var condition = [{
                    "id": ">",
                    "value": ">"
                },

                    {
                        "id": "<",
                        "value": "<"
                    },
                    {
                        "id": "!=",
                        "value": "!="
                    },
                    {
                        "id": "=",
                        "value": "="
                    },
                    {
                        "id": "like",
                        "value": "like"
                    },
                ];

                var appendCondition = [{
                    "id": "AND",
                    "value": "AND"
                },
                    {
                        "id": "OR",
                        "value": "OR"
                    }

                ]

                $("#jsGrid").jsGrid({
                    width: "100%",
                    height: "auto",

                    heading: true,
                    inserting: true,
                    editing: true,
                    controller: {

                        insertItem: function (item) {
                            updateQuery()
                        },
                        updateItem: function (item) {
                            updateQuery()
                        },
                        deleteItem: function (item) {
                            updateQuery()
                        }
                    },

                    fields: [{
                        title: "Form Fields",
                        name: "columnName",
                        type: "select",
                        items: data,
                        valueField: "fieldName",
                        textField: "fieldName"
                    },
                        {
                            name: "condition",
                            title: "Condition",
                            type: "select",
                            items: condition,
                            valueField: "id",
                            textField: "value"
                        },
                        {
                            name: "columnValue",
                            title: "Value",
                            type: "text",
                            width: 150,
                            validate: "required"
                        },
                        {
                            name: "appendCondition",
                            title: "Append",
                            type: "select",
                            items: appendCondition,
                            valueField: "id",
                            textField: "value",
                            width: 150,
                            validate: "required"
                        },
                        {
                            type: "control",
                            editButton: false,
                            modeSwitchButton: false
                        }
                    ]
                });
                $.hideLoading();

            },
            error: function (data) {
                $.hideLoading();
                console.log(data);
            }
        });

    });


    $('#reset').click(function () {
        //   $("#jsGrid").jsGrid("destroy");
        $('#multiselect_to').html("");
        $('#multiselect').html("");
        $(':input', '#frm')
            .not(':button, :submit, :reset, :hidden')
            .val('')
            .removeAttr('checked')
            .removeAttr('selected');
    });


    $('#editForm').on('submit', function (e) {

        e.preventDefault();
        var formData = new FormData();
        $.showLoading({
            name: 'circle-turn'
        });


        formData.append('endpointName', $('#endpointname').val());
        formData.append('endpointDesc', $('#endpointdesc').val());
        formData.append('endpointUrl', $('#endpointurl').val());
        formData.append('formName', $('#form').val());
        var qualificationstringvalue=$('#qualificationstring').val();
        if(qualificationstringvalue==""){
            qualificationstringvalue=currentQry();
        }
        formData.append('qualificationString', qualificationstringvalue);
        var fieldid = {},
            fieldName = {};


        $.each($('#multiselect_to').find('option'), function (i, x) {
            var cString = x.value + "^" + x.text;
            formData.append('selectedFields[]', cString);
            fieldid[x.value] = x.value;
            fieldName[x.value] = x.text;

        });


        if (jQuery.isEmptyObject(fieldid)) {
            $.each($('#multiselect').find('option'), function (i, x) {
                var cString = x.value + "^" + x.text;
                formData.append('selectedFields[]', cString);
                fieldid[x.value] = x.value;
                fieldName[x.value] = x.text;

            });
        }
        var items = $("#jsGrid").jsGrid("option", "data");

        $.each(items, function (index, o) {
            formData.append('qualification[]', o['columnName'] + "^" + o['condition'] + "^" + o['columnValue'] + "^" + o['appendCondition']);
        });

        $.ajax({
            url: CONTEXT_PATH + '/page/updateendpoint',
            type: 'POST',
            headers: headers,
            processData: false,
            data: formData,
            contentType: false,

            success: function (data) {
                console.log(data);
                window.location.replace(CONTEXT_PATH + '/page/endpointdetails');
                $.hideLoading();
            },

            error: function (error) {
                console.log(error);
                $.hideLoading();
            }

        });

    });

    $("#form").removeAttr('selected');

});
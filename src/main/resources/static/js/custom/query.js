/*Create endpoint js*/
$(document).ready(function () {
    $('#runLoader').hide();
    $.showLoading({
        name: 'circle-turn'
    });

    var error = false;

    function hasWhiteSpace(s) {
        return /\s/g.test(s);
    }

    $.ajax({
        url: CONTEXT_PATH + '/page/forms',
        method: 'GET',
        headers: headers,
        success: function (result) {
            $('#form option').remove();
            $('#form').append("<option value=''>Select Forms</option>");

            $.each(result, function (i, o) {
                $('#form').append("<option value='" + o.formId + "'>" + o.formName + "</option>")
            });
            $.hideLoading();
        },
        error: function (data) {
            console.log(data);
            $.hideLoading();
        }
    });


    function validatefn() {
        var thisValue = $('#endpointname').val();
        $.ajax({
            type: "GET",
            async: false,
            headers: headers,
            url: CONTEXT_PATH + "/page/checkendpointname", // script to validate in server side
            data: {'endpointname': thisValue},
            success: function (data) {
                if (thisValue.length > 32) {
                    $('#endPointExistsErroe').html("Length cannot be more than 32 characters");
                    $('#endpointname').css('background', '#ffe3e5');
                    $('#endpointname').css('border', '1px solid #ff808a');
                    error = true;

                } else if (hasWhiteSpace(thisValue)) {
                    $('#endPointExistsErroe').html("Endpoint name cannot have spaces");
                    $('#endpointname').css('background', '#ffe3e5');
                    $('#endpointname').css('border', '1px solid #ff808a');
                    error = true;
                } else if (data == "false") {

                    $('#endPointExistsErroe').html("Endpoint name already exists");
                    $('#endpointname').css('background', '#ffe3e5');
                    $('#endpointname').css('border', '1px solid #ff808a');
                    error = true;
                } else {
                    error = false;
                    $('#endPointExistsErroe').html("")
                    $('#endpointname').css('background', '#fff');
                    $('#endpointname').css('border', '1px solid #31acce');
                }

                console.log(data);
            }
        });

    }

    $('#endpointname').on('blur', validatefn);

    var qry = "";

    function currentQry() {
        var tmpQey = "";
        var items = $("#jsGrid").jsGrid("option", "data");
        $.each(items, function (index, o) {

            if (index == (items.length - 1)) {
                tmpQey += " ('" + o['Form Fields'] + "' " + o['Condition'] + " \"" + o['Value'] + "\") "
            } else {
                tmpQey += "('" + o['Form Fields'] + "' " + o['Condition'] + " \"" + o['Value'] + "\") " + o['Append']
            }
        });

        return tmpQey;
    }

    function updateQuery() {

        qry = currentQry();
        console.log();
    }

    $('#multiselect').multiselect();

    $("#form").select2();
    $("#form").on('change', function () {
        $('#messages').html("");
        $("#jsGrid").jsGrid("destroy");
        $('#multiselect_to').html("");
        $('#multiselect').html("");
        $.showLoading({
            name: 'circle-turn'
        });
        $.ajax({
            url: CONTEXT_PATH + '/page/internal/fields',
            headers: headers,
            data: {formName: $('#form').val()},
            method: 'GET',
            success: function (data) {


                $.each(data, function (index, singleData) {
                    $('#multiselect').attr('disabled', false);
                    $('#multiselect').append("<option value=" + singleData.fieldId + " title=" + singleData.fieldName + ">" + singleData.fieldName + "</option>")
                })

                var condition = [
                    {
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

                var appendCondition = [
                    {
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

                    fields: [
                        {
                            name: "Form Fields",
                            type: "select",
                            items: data,
                            valueField: "fieldName",
                            textField: "fieldName"
                        },
                        {name: "Condition", type: "select", items: condition, valueField: "id", textField: "value"},
                        {name: "Value", type: "text", width: 150, validate: "required"},
                        {
                            name: "Append",
                            type: "select",
                            items: appendCondition,
                            valueField: "id",
                            textField: "value",
                            width: 150,
                            validate: "required"
                        },
                        {type: "control", editButton: false, modeSwitchButton: false}
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
        $("#jsGrid").jsGrid("destroy");
        $('#multiselect_to').html("");
        $('#multiselect').html("");
        $('#endPointExistsErroe').html("")
        $('#endpointname').css('background', '#fff');
        $('#endpointname').css('border', '1px solid #31acce');
        $('#messages').html("");
        $("#form").select2("val", "");
        $(':input', '#frm')
            .not(':button, :submit, :reset, :hidden')
            .val('')
            .removeAttr('checked')
            .removeAttr('selected');

    });


    $('#back').click(function () {
        $.showLoading({
            name: 'circle-turn'
        });
        window.location.href = CONTEXT_PATH + '/page/endpointdetails';
        $.hideLoading();

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
                headers: headers,
                processData: false,
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

    $('#editForm').on('submit', function (e) {

        e.preventDefault();
        $.showLoading({
            name: 'circle-turn'
        });
        $('#messages').html("");
        // $('.btn').prop("disabled", true);
        var formData = new FormData();
        formData.append('endpointName', $('#endpointname').val());
        formData.append('endpointDesc', $('#endpointdesc').val());
        formData.append('endpointUrl', $('#endpointurl').val());
        formData.append('formName', $('#form').val());
        var qualificationstringvalue = $('#qualificationstring').val();
        if (qualificationstringvalue == "") {
            qualificationstringvalue = currentQry();
        }
        formData.append('qualificationString', qualificationstringvalue);
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

        if (error == false) {
            $.ajax({
                url: CONTEXT_PATH + '/page/updateendpoint',
                type: 'POST',
                headers: headers,
                processData: false,
                data: formData,
                contentType: false,

                success: function (data) {
                    //     $('.btn').prop("disabled", false);
                    $.hideLoading();
                    console.log(data);
                    window.location.replace(CONTEXT_PATH + '/page/endpointdetails');
                },

                error: function (error) {
                    //       $('.btn').prop("disabled", false);
                    $.hideLoading();
                    console.log();


                }

            });
        } else {

            $('#endpointname').focus();
            $('#endpointname').css('background', '#ffe3e5');
            $('#endpointname').css('border', '1px solid #ff808a');
            /*alert("Error in the form");*/
        }


    });

    $("#form").removeAttr('selected');

});


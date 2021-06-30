$(function () {
    $('#runLoader').hide();

    $('#back').click(function () {
        window.location.href = CONTEXT_PATH + '/';

    });


    $('#epcreateForm').on('submit', function (e) {

        e.preventDefault();
        $.showLoading({
            name: 'circle-turn'
        });

        var error = [];
        if ($('#connName').val() === "") {
            error.push("Connection is Required")
        }

        if ($('#query').val() === "") {
            error.push("Query is Required")
        }

        if ($('#ename').val() === "") {
            error.push("Endpoint Namae is Required")
        }

        if (error.length > 0) {
            messages = '<div class="alert alert-danger">';
            $.each(error, function (i, v) {
                messages += v
            });
            messages += "</div>";
            $('#messages').html(messages);
        } else {
            $.ajax({
                url: CONTEXT_PATH + "/jdbc/createep",
                type: 'POST',
                headers: headers,
                data: {
                    ename: $('#ename').val(),
                    connName: $('#connName').val(),
                    query: $('#query').val(),
                    op: $('#op').val(),
                    inup:$('#inupdate:checked').val()
                },
                success: function (result) {
                    $.hideLoading();
                    if (result.error == undefined) {
                        alert(result.success)
                        window.location.href = CONTEXT_PATH + '/jdbc/displayep';
                    } else {
                        alert(result.error)
                    }
                },
                error: function (error) {
                    $.hideLoading();
                    alert(error)
                }
            })
        }
    });
    $('#run').on('click', function () {
        var error = [];
        $('#runLoader').show();

        if ($('#connName').val() === "") {
            error.push("Connection is Required")
        }

        if ($('#base').val() === "") {
            error.push("Base is Required")
        }

        if ($('#query').val() === "") {
            error.push("Query is Required")
        }

        if (error.length > 0) {
            messages = '<div class="alert alert-danger">';
            $.each(error, function (i, v) {
                messages += v
            });
            messages += "</div>";
            $('#messages').html(messages);
        } else {

            $.ajax({
                url: CONTEXT_PATH + "/jdbc/run",
                type: 'POST',
                headers: headers,
                data: {
                    connName: $('#connName').val(),
                    query: $('#query').val()
                },
                success: function (result) {
                    var resultData = "";
                    // $.each(result, function (i, v) {
                    //     var keys = Object.keys(v);
                    //     resultData += "=================================================" + "\n";
                    //     $.each(keys, function (i1, v1) {
                    //         resultData += "||      " +v1 + "    ||        " + v[v1] + "   ||     \n";
                    //     });
                    //     resultData += "=================================================" + "\n";
                    // });
                    resultData = JSON.stringify(result, null, 4);

                    $('#runLoader').hide();
                    var txtData = "<textarea class='form-control' rows='8'> " + resultData + "</textarea>";
                    $('#simulate').html(txtData);


                },
                error: function (error) {

                }
            })

        }
    })

});
$(document).ready(function () {
    if (localStorage.getItem("page") == null) {
        localStorage.setItem("page", "{}");
    }
    if (localStorage.getItem("searchvalue") == null) {
        localStorage.setItem("searchvalue", "");
    }
    start = JSON.parse(localStorage.getItem("page")).start == null ? 0 : JSON.parse(localStorage.getItem("page")).start;
    leng = JSON.parse(localStorage.getItem("page")).length == null ? 10 : JSON.parse(localStorage.getItem("page")).length;
    searchvalue = localStorage.getItem("searchvalue");
    if (!(searchvalue === '')) {
        start = 0;
    }


    var d = "";

    /*    $.getJSON( "/page/epdetails", function( data1 ) {*/
    var table = $('#displayEndPoint').DataTable({
        "pagingType": "full",
        "dom": '<"top"f>rt<"bottom"<"col-md-4"l><"col-md-4"i><"col-md-4"p>><"clear">',
        "serverSide": true,
        "processing": true,
        "displayStart": start,
        "search": {"search": searchvalue},
        "length": leng,// here
        "ajax": CONTEXT_PATH + "/page/endpoints",
        headers: headers,
        "columns": [
            {"data": "", "width": "4%"},
            {"data": "endPointName", "width": "25%"},
            {"data": "endPointDescription", "width": "26%"},
            {"data": "formName", "width": "23%"},
            {"data": "date", "width": "14%"},
            {"data": "active", "width": "8%"}
        ],
        "createdRow": function (row, data, dataIndex) {
            if (row.active == false) {
                row.setAttribute("style", "color: #999;");
            }
        },
        columnDefs: [
            {
                targets: [0],
                render: function (data, type, row) {
                    return row.active == true ? '<input type="button" class="btn btn-primary btn-xs copyep" id="copyep"  data-eval="' + CONTEXT_PATH + '/page/endpointurl/' + row.endPointName + '" value="Copy">' : '<input type="button" class="btn btn-primary btn-xs copyep" id="copyep1" disabled data-eval="' + CONTEXT_PATH + '/page/endpointurl/' + row.endPointName + '" value="Copy">';
                }
            },
            {
                targets: [1],
                render: function (data, type, row) {
                    return row.active == true ? '<a href="' + CONTEXT_PATH + '/page/editendpointdetails/' + data + '">' + data + '</a>' : '<a style="color:#999" href="' + CONTEXT_PATH + '/page/editendpointdetails/' + data + '">' + data + '</a>'
                }
            },
            {
                targets: [4],
                render: function (data, type, row) {
                    return row.date == null ? '' : row.date;
                }
            },
            {
                targets: [5],
                render: function (data, type, row) {
                    /*return data == false ? '<a class="btn btn-info btn-xs" style="width:50" id="" href="'+CONTEXT_PATH+'/page/activeep/'+row.id+'">Active</a>' : '<a class="btn btn-danger btn-xs" style="width:50" href="'+CONTEXT_PATH+'/page/deactiveep/'+row.id+'">Inactive</a> ';*/
                    return data == false ? '<input type="button" data-url="' + CONTEXT_PATH + '/page/activeep1/' + row.id + '" class="btn btn-info btn-xs endpoint" value="Active" style="width:50" id="activeep">' : '<input type="button" data-url="' + CONTEXT_PATH + '/page/deactiveep1/' + row.id + '" class="btn btn-danger btn-xs endpoint" value="InActive" style="width:50" id="deactiveep">';
                }
            }
        ]
    });


    $('#displayEndPoint').on('page.dt', function (e, settings, len) {
        localStorage.setItem("page", JSON.stringify(table.page.info()))
    })
    $('#displayEndPoint').on('search.dt', function (e, settings, len) {
        localStorage.setItem("searchvalue", table.search())
    })
    $('#displayEndPoint tbody').on('click', '.endpoint', function () {
        $.showLoading({
            name: 'circle-turn'
        });
        var eValue = $(this).data('url');


        console.log(eValue);
        $.ajax({
            type: "GET",
            headers: headers,
            async: false,
            url: eValue, // script to validate in server side
            success: function (data) {
                console.log(data);
                $('#displayEndPoint').DataTable().ajax.reload(null, false);
                $.hideLoading();
            },
            error: function (data) {
                $('#displayEndPoint').DataTable().ajax.reload(null, false);
                $.hideLoading();
            }
        });
    });

    $('#displayEndPoint tbody').on('click', '.copyep', function () {
        var eValue = $(this).data('eval');
        ClipboardHelper.copyText(window.location.origin + eValue)
        alert("Url Copied to Clipboard")
    });


    $('#displayEndPoint_length > label').css('font-weight', '100');
    $('input[type="search"]').addClass('form-control')
    $('#displayEndPoint_length > label > select').addClass('form-control')
    $('#displayEndPoint_filter > label').addClass('form-inline')
    $('#displayEndPoint_length > label').addClass('form-inline')
    $('#displayEndPoint_wrapper').prepend('<a href="' + CONTEXT_PATH + '/page" class="btn" style="margin-right:13;background:#0ea3c4 ;width:100;float: right;color:#ffffff;margin-left: 10px;">New</a>')
    $('#displayEndPoint_wrapper').prepend('<a href="' + CONTEXT_PATH + '/" class="btn" style="margin-right:13;background:#0ea3c4 ;width:100;float: right;color:#ffffff;margin-left: 10px;">Back</a>')
//    $('#pagination').append($('.dataTables_paginate'))
//    $('#selectBox').append($('.dataTables_length'))
//    $('#infoText').append($('.dataTables_info'))
///*    $('#displayEndPoint_wrapper').css('margin-top','30px')*/
    $('#displayEndPoint_length').css('padding', '5')
    $('#displayEndPoint_filter').css('float', 'left')
    $('#displayEndPoint_info').css('float', 'unset')
    $('#displayEndPoint_info').css('text-align', 'center')


});
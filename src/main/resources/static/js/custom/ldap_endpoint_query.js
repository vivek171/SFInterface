$(document).ready(function () {
    if(localStorage.getItem("ldap_endpoint_page") == null) {
        localStorage.setItem("ldap_endpoint_page" , "{}");
    }
    if(localStorage.getItem("ldap_endpoint_searchvalue") == null) {
        localStorage.setItem("ldap_endpoint_searchvalue" , "");
    }
    start = JSON.parse(localStorage.getItem("ldap_endpoint_page")).start == null ? 0 : JSON.parse(localStorage.getItem("ldap_endpoint_page")).start;
    leng = JSON.parse(localStorage.getItem("ldap_endpoint_page")).length == null ? 10 : JSON.parse(localStorage.getItem("ldap_endpoint_page")).length;
    searchvalue = localStorage.getItem("ldap_endpoint_searchvalue");
    if(!(searchvalue==='')){
        start=0;
    }


    var d = "";

    /*    $.getJSON( "/page/epdetails", function( data1 ) {*/
    var table = $('#dt').DataTable({
        "pagingType": "full",
        "dom": '<"top"f>rt<"bottom"<"col-md-4"l><"col-md-4"i><"col-md-4"p>><"clear">',
        "autoWidth": false,
        "serverSide": true,
        "processing": true,
        "displayStart": start,
        "search":{"search" :searchvalue},
        "length": leng,// here
        "ajax": CONTEXT_PATH + "/ldap/ldapendpoints",
        headers: headers,
        "columns": [
            {"data": "", "width": "4%"},
            {"data": "endPointName", "width": "20%"},
            {"data": "base", "width": "20%"},
            {"data": "query", "width": "32%"},
            {"data": "ldapAccounts.connName", "width": "12%"},
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
                    return row.active == true ? '<input type="button" class="btn btn-info btn-xs copy" id="copy" data-eval="'+CONTEXT_PATH +'/ldap/'+row.endPointName+'" value="Copy">' : '<input type="button" class="btn btn-info btn-xs copy" id="copy1" disabled data-eval="'+CONTEXT_PATH+'/ldap/'+row.endPointName+'" value="Copy">';
                }
            },
            {
                targets: [5],
                render: function (data, type, row) {
                    return row.active == true ? '<a class="btn btn-info btn-xs Edit" style="width:50" href="'+CONTEXT_PATH+'/ldap/editep/'+row.id+'">Edit</a><a class="btn btn-danger btn-xs Deactive" style="width:50;vertical-align:bottom"  href="'+CONTEXT_PATH+'/ldap/deactiveep/'+row.id+'"> Deactive </a>' : '<a class="btn btn-info btn-xs" style="width:50" href="'+CONTEXT_PATH+'/ldap/editep/'+row.id+'">Edit</a><a class="btn btn-info btn-xs" style="width:50;vertical-align:bottom" href="'+CONTEXT_PATH+'/ldap/activeep/'+row.id+'"> Active </a>';
                }
            }
        ]
    });


    $('#dt').on('page.dt', function (e, settings, len) {
        localStorage.setItem("ldap_endpoint_page", JSON.stringify(table.page.info()))
    })
    $('#dt').on('search.dt', function (e, settings, len) {
        localStorage.setItem("ldap_endpoint_searchvalue", table.search())
    })
    $('#dt tbody').on('click', '.endpoint', function () {
        $.showLoading({
            name: 'circle-turn'
        });
        var eValue = $(this).data('url');


        console.log(eValue);
        $.ajax({
            type: "GET",
            async: false,
            headers: headers,
            url: eValue, // script to validate in server side
            success: function (data) {
                console.log(data);
                $('#dt').DataTable().ajax.reload(null, false);
                $.hideLoading();
            },
            error: function (data) {
                $('#dt').DataTable().ajax.reload(null, false);
                $.hideLoading();
            }
        });
    });



    $('#dt_wrapper').prepend('<a href="' + CONTEXT_PATH + '/ldap/query" class="btn" style="margin-right:13;background:#0ea3c4 ;width:100;float: right;color:#ffffff;margin-left: 10px;">New</a>')
    $('#dt_wrapper').prepend('<a href="' + CONTEXT_PATH + '/" class="btn" style="margin-right:13;background:#0ea3c4 ;width:100;float: right;color:#ffffff;margin-left: 10px;">Back</a>')
//    $('#pagination').append($('.dataTables_paginate'))
//    $('#selectBox').append($('.dataTables_length'))
//    $('#infoText').append($('.dataTables_info'))
///*    $('#displayEndPoint_wrapper').css('margin-top','30px')*/

    $('input[type="search"]').addClass('form-control')
    $('#dt_length > label > select').addClass('form-control')
    $('#dt_filter > label').addClass('form-inline')
    $('#dt_length > label').addClass('form-inline')

    $('#dt_length').css('padding', '5')
    $('#dt_filter').css('float', 'left')
    $('#dt_info').css('float', 'unset')
    $('#dt_info').css('text-align', 'center')

    $('#dt tbody').on('click', '.copy', function () {
        ClipboardHelper.copyText(window.location.origin + $(this).data('eval'))
        alert('Url copied to Clipboard');
    });


});
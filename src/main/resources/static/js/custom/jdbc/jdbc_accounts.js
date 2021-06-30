$(document).ready(function () {
    // if(localStorage.getItem("jdbcaccountpage") == null) {
    //     localStorage.setItem("jdbcaccountpage" , "{}");
    // }
    // if(localStorage.getItem("jdbc_account_searchvalue") == null) {
    //     localStorage.setItem("jdbc_account_searchvalue" , "");
    // }
    // start = JSON.parse(localStorage.getItem("jdbcaccountpage")).start == null ? 0 : JSON.parse(localStorage.getItem("jdbcaccountpage")).start;
    // leng = JSON.parse(localStorage.getItem("jdbcaccountpage")).length == null ? 10 : JSON.parse(localStorage.getItem("jdbcaccountpage")).length;
    // searchvalue = localStorage.getItem("jdbc_account_searchvalue");
    // if(!(searchvalue==='')){
    //     start=0;
    // }
    //
    //
    // var d = "";
    //
    //
    // var table = $('#dt').DataTable({
    //     "pagingType": "simple_numbers",
    //     "dom": '<"top"f>rt<"bottom"<"col-md-4"l><"col-md-4"i><"col-md-4"p>><"clear">',
    //     "serverSide": true,
    //     "processing": true,
    //     "displayStart": start,
    //     "search":{"search" :searchvalue},
    //     "length": leng,// here
    //     "ajax": CONTEXT_PATH + "/jdbc/jdbcaccounts",
    //     "columns": [
    //         {"data": "environment", "width": "12%"},
    //         {"data": "desc", "width": "8%"},
    //         {"data": "host", "width": "13%"},
    //         {"data": "port", "width": "6%"},
    //         {"data": "databaseName", "width": "14%"},
    //         {"data": "username", "width": "13%"},
    //         {"data": "password", "width": "18%"},
    //
    //     ],
    //     columnDefs: [
    //         {
    //             targets: [0],
    //             render: function (data, type, row) {
    //                 return row.active == true ? '<a href="' + CONTEXT_PATH + '/jdbc/editjdbcaccountdetails/' + data + '">' + data + '</a>' : '<a style="color:#999" href="' + CONTEXT_PATH + '/jdbc/editjdbcaccountdetails/' + data + '">' + data + '</a>'
    //             }
    //         }
    //     ]
    // });
    //
    // $('#dt_wrapper').prepend('<a href="' + CONTEXT_PATH + '/jdbc/create" class="btn" style="margin-right:13;background:#0ea3c4 ;width:100;float: right;color:#ffffff;margin-left: 10px;">New</a>')
    // $('#dt_wrapper').prepend('<a href="' + CONTEXT_PATH + '/" class="btn" style="margin-right:13;background:#0ea3c4 ;width:100;float: right;color:#ffffff;margin-left: 10px;">Back</a>')
    //
    //
    // $('#dt').on('page.dt', function (e, settings, len) {
    //     localStorage.setItem("jdbcaccountpage", JSON.stringify(table.page.info()))
    // })
    // $('#dt').on('search.dt', function (e, settings, len) {
    //     localStorage.setItem("jdbc_account_searchvalue", table.search())
    // })

    $('#dt').dataTable()

    $('#dt_length > label').css('font-weight', '100');
    $('input[type="search"]').addClass('form-control')
    $('#dt_length > label > select').addClass('form-control')
    $('#dt_filter > label').addClass('form-inline')
    $('#dt_length > label').addClass('form-inline')

    $('#dt_length').css('padding', '5')
    $('#dt_filter').css('float', 'left')
    $('#dt_info').css('float', 'unset')
    $('#dt_info').css('text-align', 'center')

});
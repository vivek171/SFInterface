$(function () {
    $('#back').click(function () {
        window.location.href = CONTEXT_PATH + '/jdbc/connections';

    })

    $('#jdbcAccountCreateForm').on('submit', function (e) {
        e.preventDefault();
        var disabled = $('#jdbcAccountCreateForm').find(':input:disabled').removeAttr('disabled');
        var formData=$(this).serialize()+ '&status=' + $('#status').is(':checked');
        disabled.attr('disabled', 'disabled');
        $.showLoading({
            name: 'circle-turn'
        });
        $.ajax({
            url: CONTEXT_PATH + '/jdbc/create',
            type: 'POST',
            headers: headers,
            data: formData,
            success: function (data) {
                if(data['success']){
                    $.hideLoading();
                    alert("Successfully Created");
                    window.location.href = CONTEXT_PATH + '/jdbc/connections';
                } else {
                    $.hideLoading();
                    alert(data.error);
                }
            },
            error: function (data) {
                $.hideLoading();
                alert(data['success']);

            }
        });


    });

});
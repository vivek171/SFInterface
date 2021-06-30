$(function () {
    $('#back').click(function () {
        window.location.href = CONTEXT_PATH + '/ldap/connections';

    })

    $('#ldapAccountCreateForm').on('submit', function (e) {
        e.preventDefault();
        var disabled = $('#ldapAccountCreateForm').find(':input:disabled').removeAttr('disabled');
        var formData=$(this).serialize()+ '&sso=' + $('#sso').is(':checked');
        disabled.attr('disabled', 'disabled');
        $.showLoading({
            name: 'circle-turn'
        });
        $.ajax({
            url: CONTEXT_PATH + '/ldap/create',
            type: 'POST',
            headers: headers,
            data: formData,
            success: function (data) {
                if(data['success']){
                    $.hideLoading();
                    alert("Successfully Created");
                    window.location.href = CONTEXT_PATH + '/ldap/connections';
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
function cleanSpace(text){
    return text.replace(" ","");
}
$(document).ready(function () {
    const target = $('#api_selection_'+$("#current_subscription_row").val());
    console.log(target);
    if(target && target.length > 0){
        $('html,body').animate({
            scrollTop: target.offset().top
        }, 500);
        console.log('scrolled');
    }
});


function updateSubscription( rowNum, usecase){

    $("#create_usecase").val(usecase?usecase:"");
    if(isNullOrUndefined(rowNum)){
        const numberOfSubscriptions = $("[id^='api_selection_']");
        rowNum = numberOfSubscriptions.length;
    }
    $("#current_subscription_row").val(rowNum);
    $("#create-client-form").submit();
}
function isNullOrUndefined(value) {
    return value === undefined || value === null;
}
function loadAndSubmit(idButton) {
    $('#' + idButton).attr('disabled', 'disabled');
    $('#' + idButton).html('<div class="loader"></div>');
    $('#create-client-form').submit();
}

function loadAndSubmit(idButton) {
    $('#' + idButton).attr('disabled', 'disabled');
    $('#' + idButton).html('<div class="loader"></div>');
    $('#create-client-form').submit();
}
$(function() {
    $('#tags-input').keypress(function (e) {
        if(e.which == 13)  // the enter key code
        {
            let tag = $('#tags-input').val();
            if(tag.length > 0)
            {
                tag = tag.replace(/\s/g, '-');
                let tagNum = $('#tags-container').children().length;
                $('#tags-container').append('<span class="badge bg-primary px-2 me-1" id="tag-' + tagNum + '">'
                    + tag
                    + ' <a id="delete-tag-' + tagNum + '" class="icon-block"><i class="fas fa-times-circle" style="color:white"></i></a>'
                    + '<input type="hidden" name="selected_tags" value="' + tag + '">'
                    + '</span>');
                $('#delete-tag-' + tagNum).click(function() {
                    $('#tag-' + tagNum).remove();
                });
                $('#tags-input').val('');
            }
            return false;
        }
    });
});




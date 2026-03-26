$(function () {
    $('#tags-input').keypress(function (e) {
        if (e.which == 13)  // the enter key code
        {
            let tag = $('#tags-input').val();
            if (tag.length > 0) {
                tag = tag.replace(/\s/g, '-');
                let tagNum = $('#tags-container').children().length;
                $('#tags-container').append('<span class="badge bg-primary px-2 me-1" id="tag-' + tagNum + '">'
                    + tag
                    + ' <a id="delete-tag-' + tagNum + '" class="icon-block"><i class="fas fa-times-circle" style="color:white"></i></a>'
                    + '<input type="hidden" name="selected_tags" value="' + tag + '">'
                    + '</span>');
                $('#delete-tag-' + tagNum).click(function () {
                    $('#tag-' + tagNum).remove();
                });
                $('#tags-input').val('');
            }
            return false;
        }
    });
});

$(function () {
    $('#version').change(function (e) {
            let version = $('#version').val();
            if (version.length > 0) {
                $('#version-suffix').text("/" + version);
            }
            return true;
    });
});


$('#btn-search_api').css('float','right');


$("#create-tab").on("click", function () {
    $("#manage-tab").removeClass("active");
    $("#create-tab").addClass("active");
});
$("#manage-tab").on("click", function () {
    $("#manage-tab").addClass("active");
    $("#create-tab").removeClass("active");
});


function addEnvironement() {
    $("#create_usecase").val("add_environment");
    const selectectEnvironement = $("#apiEnvironnement").val();
    $("#current_environement_tab").val(selectectEnvironement);
    $("#step2").submit();
}

function clone(envUuid){
    $("#create_usecase").val("clone_environment");
    const selectectEnvironement = $("#apiEnvironnement").val();
    $("#current_environement_tab").val(selectectEnvironement);
    $("#current_source_environement").val(envUuid);
    $("#step2").submit();
}


function deleteEnvironement(envUuid){
    $("#create_usecase").val("delete_environment");
    $("#current_environement_tab").val(envUuid);
    $("#environement-"+envUuid+"-resource-container").remove();
    $("#step2").submit();
}

function newResource(envUuid){
    $("#create_usecase").val("add_resource");
    $("#current_environement_tab").val(envUuid);
    $("#step2").submit();
}


function newHeaderMatching(envUuid, resourceIndex){
    $("#create_usecase").val("add_header_matching");
    $("#current_environement_tab").val(envUuid);
    $("#current_resource").val(resourceIndex);
    $("#step2").submit();
}

function removeResource(envUuid, resourceIndex){
    $("#create_usecase").val("delete_resource");
    $("#current_resource").val(resourceIndex);
    $("#current_environement_tab").val(envUuid);
    $("#step2").submit();
}


function removeResourceHeaderMatching(envUuid, resourceIndex, headerMatchingIndex){
    $("#create_usecase").val("delete_header_matching");
    $("#current_resource").val(resourceIndex);
    $("#current_header_matching").val(headerMatchingIndex);
    $("#current_environement_tab").val(envUuid);
    $("#step2").submit();
}

function newPlan(envUuid){
    $("#create_usecase").val("add_plan");
    $("#current_environement_tab").val(envUuid);
    $("#step3").submit();
}

function deletePlan(envUuid, planIndex){
    $("#create_usecase").val("delete_plan");
    $("#current_environement_tab").val(envUuid)
    $("#current_plan_tab").val(planIndex);
    $("#step3").submit();
}

$(function () {
    $('.form-control-plaintext').each(function () {
        $(this).removeClass('form-control');
    });
});

if ($('#rate_limiting_enabled').is(":checked")) {
    $('#rate_limiting_template').removeAttr('disabled');
    $('#rate_limiting_template').show();
} else {
    $('#rate_limiting_template').attr('disabled', 'disabled');
    $('#rate_limiting_template').hide();
}
$('.form-control-plaintext').each(function () {
    $(this).removeClass('form-control');
});

$('#rate_limiting_enabled').change(function () {
    if ($(this).is(":checked")) {
        $('#rate_limiting_template').removeAttr('disabled');
        $('#rate_limiting_template').show();
    } else {
        $('#rate_limiting_template').attr('disabled', 'disabled');
        $('#rate_limiting_template').hide();
    }
});


var stepper = new Stepper(document.querySelector('#stepper'), {
    animation: true
});




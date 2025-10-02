function validateNewPlanVersion(idInput, previousVersion) {
    let newVersion = $('#' + idInput).val();
    if (newVersion === previousVersion) {
        $('#' + idInput).closest('form').find('.new-version-validation-msg').removeClass('d-none');
        $('#' + idInput).focus();
        $('#' + idInput).closest('form').find('button').attr('disabled', 'disabled');
    } else {
        $('#' + idInput).closest('form').find('button').removeAttr('disabled');
        $('#' + idInput).closest('form').find('.new-version-validation-msg').addClass('d-none');
    }
}

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



   $('input[name="selectable_tag"]').each(function () {
       $(this).parent().click(function() {
           const inputChild= $(this).find('input[name="selectable_tag"]');
           updateSearch(inputChild);
       } );
   });

   $('input[name="selected_tag"]').each(function () {
       intputElement = $(this);
       intputElement.parent().click(function() {
           intputElement.attr('name','selectable_tag');
           updateSearch();
       } );
   });

   $('#btn-search_api').css('float','right');

   function updateSearch(tagElement){

       const selected_tags=[];
       if(tagElement){
           if( tagElement.attr('name')=== 'selectable_tag'){
               tagElement.attr('name','selected_tag');
           }
       }

        $('input[name="selected_tag"]').each(function(){
                    selected_tags.push($(this).val());
                 });

        let destination = 'jsp/admin/plugins/apimanager/ManageApis.jsp'
        if(selected_tags.length > 0){
            destination = destination+'?selected_tags='+selected_tags.join(",") ;
        }

       window.location.href = destination ;

   }

$("#create-tab").on("click", function () {
    $("#manage-tab").removeClass("active");
    $("#create-tab").addClass("active");
});
$("#manage-tab").on("click", function () {
    $("#manage-tab").addClass("active");
    $("#create-tab").removeClass("active");
});


var stepper = new Stepper(document.querySelector('#stepper'), {
    animation: true
});


function addEnvironement(){
    $("#create_usecase").val("add_environment");
    const selectectEnvironement = $("#apiEnvironnement").val();
    $("#current_environement_tab").val(selectectEnvironement);
    $("#step2").submit();
}


function newResource(envUuid){
    $("#create_usecase").val("add_resource");
    $("#current_environement_tab").val(envUuid);
    $("#step2").submit();
}

function removeResource(envUuid, resourceIndex){
    $("#create_usecase").val("delete_resource");
    $("#current_resource").val(resourceIndex);
    $("#current_environement_tab").val(envUuid);
    $("#step2").submit();
}

function newPlan(envUuid){
    $("#create_usecase").val("add_plan");
    $("#current_environement_tab").val(envUuid);
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





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

/// add tabs in the resource step
let addTabBtn =
    document.getElementById(
        "addTabBtn"
    );
let myTabs =
    document.getElementById("myTabs");

addTabBtn.addEventListener(
    "click",
    function () {
        var selectedOptions = $('#resourceEnvironnement option:selected');
        isDisabled = selectedOptions.first().attr('disabled');
        if(!isDisabled){
            selectedOptions.first().attr('disabled',true);
            let tabName = selectedOptions[0]?.value?selectedOptions[0].value:null;

            if (
                tabName
            ) {
                // Create tab link
                let newTabLink =
                    document.createElement(
                        "a"
                    );
                newTabLink.classList.add(
                    "nav-link",
                    "text-dark"
                );
                newTabLink.setAttribute(
                    "data-bs-toggle",
                    "tab"
                );
                newTabLink.setAttribute(
                    "id",
                    "environement-"+tabName+"-resource-container-link"
                );
                newTabLink.setAttribute(
                    "data-bs-target",
                    "#environement-"+tabName+"-resource-container"
                );
                newTabLink.setAttribute(
                    "data-bs-toggle",
                    "tab"
                );

                newTabLink.textContent =
                    tabName;

                // Create tab list item
                let newTabListItem =
                    document.createElement(
                        "li"
                    );
                newTabListItem.classList.add(
                    "nav-item"
                );
                newTabListItem.appendChild(
                    newTabLink
                );
                myTabs.appendChild(
                    newTabListItem
                );

                // Create tab pane
                let newTabPane = $('#template-tab-container').last().clone();
                $(newTabPane).removeClass('d-none');

                $(newTabPane).attr('id', "environement-"+tabName+"-resource-container");

                $(newTabPane).find('#resource-row-0').first().attr('id',"environement-"+tabName+"-resource-row-0");

                $(newTabPane).find('#delete-resource-row-0').attr('id', "environement-"+tabName+"-delete-resource-row-0");

                $(newTabPane).find(':input').each(function () {
                    const id = $(this).attr('id');
                    const type = $(this).attr('type');
                    const idAddResourceButton = 'add-resource';
                    if(id !== idAddResourceButton && type !== 'button'){
                        $(this).attr('id','environement-'+tabName+'-resource-row-0-'+id);
                        $(this).attr('name','environement-'+tabName+'-resource-row-0-'+id);
                    }else if (id === idAddResourceButton){
                        $(this).attr('id','environement-'+tabName+'-resource-add-resource');
                        $(this).attr('name','environement-'+tabName+'-resource-add-resource');
                    }
                });

                $(newTabPane).find('#environement-'+tabName+'-resource-add-resource').first().click(function () {
                    addResourceToTab(tabName);
                });


                $('#tabContent').append(
                    newTabPane
                );


                newTabPane.find('.instances option').each(function() {
                    if ( !$(this).attr('id').startsWith(tabName) ) {
                        $(this).remove();
                    }
                });

                $("#environement-"+tabName+"-resource-container-link").tab('show');

                addEnvironementPlan(tabName);
            }
        }
    }
);

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



function addEnvironementPlan(environementName){
    let container = $('#plan-creation-container');
    container.attr('id', 'environement-'+environementName+'-plan-creation-container');

    let newRow = $('#plan_template').last().clone();
    let oldIndex = $(newRow).data('index');
    let newIndex = (parseInt(oldIndex) + 1).toString();
    $(newRow).removeClass('d-none');
    $(newRow).attr('id', 'environement-'+environementName+'-plans');
    $(newRow).attr('data-index', newIndex);

    const title = $(newRow).find("#template-environement-name").first();
    title.attr("id",'environement-'+environementName);
    title.text(environementName);

    $(newRow).find('#add-plan-button').each(function () {
        $(this).off();
        $(this).click(function () {

            var selectedOptions =  $(newRow).find('#plans option:selected').first();
            isDisabled = selectedOptions.attr('disabled');
            if(!isDisabled){
                selectedOptions.attr('disabled',true);
                let tabName = selectedOptions[0]?.value?selectedOptions[0].value:null;

                if (
                    tabName
                ) {
                    // Create tab link
                    let newTabLink =
                        document.createElement(
                            "a"
                        );
                    newTabLink.classList.add(
                        "nav-link",
                        "text-dark"
                    );
                    newTabLink.setAttribute(
                        "data-bs-toggle",
                        "tab"
                    );
                    newTabLink.setAttribute(
                        "id",
                        cleanSpace("environement-"+environementName+"-plan-"+tabName+"-container-link")
                    );
                    newTabLink.setAttribute(
                        "data-bs-target",
                        cleanSpace("#environement-"+environementName+"-plan-"+tabName+"-container")
                    );
                    newTabLink.setAttribute(
                        "data-bs-toggle",
                        "tab"
                    );

                    newTabLink.textContent =
                        tabName;

                    // Create tab list item
                    let newTabListItem =
                        document.createElement(
                            "li"
                        );
                    newTabListItem.classList.add(
                        "nav-item"
                    );
                    newTabListItem.appendChild(
                        newTabLink
                    );

                    $(newRow).find('#planTabs').first().append(
                        newTabListItem
                    );

                    // Create tab pane
                    let newTabPane = $('#template-plan-tab-container').last().clone();
                    $(newTabPane).removeClass('d-none');

                    $(newTabPane).attr('id', cleanSpace("environement-"+environementName+"-plan-"+tabName+"-container"));


                    /*$(newTabPane).find('#environement-'+tabName+'-template-add-resource').first().click(function () {
                        addResourceToTab(tabName);
                    });*/


                    $("#environement-"+environementName+"-plan-creation-container").find('#planTabContent').first().append(
                        newTabPane
                    );


                    $(newTabPane).find("#add-resource-select-template").first().attr('id', cleanSpace("environement-"+environementName+"-plan-"+tabName+"-resources"))
                        .attr('name', cleanSpace("environement-"+environementName+"-plan-"+tabName+"-resources"));

                    // add options to resource selection
                    $('[id^=environement-'+environementName+'-resource-row]').each(function () {
                        const rowNum = $(this).attr('id').replace('environement-'+environementName+'-resource-row-','');
                       if(rowNum.indexOf('-') === -1){
                           // create option with this id
                           const verb = $('#environement-'+environementName+'-resource-row-'+rowNum+'-verb_name').val();
                           const name = $('#environement-'+environementName+'-resource-row-'+rowNum+'-name').val();
                           $(cleanSpace("#environement-"+environementName+"-plan-"+tabName+"-resources")).append(new Option(verb+' ' + name, verb+'|' + name));
                       }
                    });

                    $(cleanSpace("#environement-"+environementName+"-plan-"+tabName+"-container-link")).tab('show');

                }
            }
        });
    });
    container.append(newRow);


}

function cleanSpace(text){
    return text.replace(" ","");
}

function addResourceToTab(environementName){
    let container = $('#environement-'+environementName+'-resource-container');
    let resourceContainer = container.find('#resource-container').first();
    let newRow = resourceContainer.find('.resource-row').last().clone();
    let oldIndex = $(newRow).data('index');
    let newIndex = (parseInt(oldIndex) + 1).toString();
    $(newRow).removeClass('d-none');
    $(newRow).attr('id', 'environement-'+environementName+'-resource-row-' + newIndex);
    $(newRow).attr('data-index', newIndex);

    $(newRow).find('#environement-'+environementName+'-delete-resource-row-' + oldIndex).each(function () {
        $(this).attr('id', 'environement-'+environementName+'-delete-resource-row-' + newIndex);
        $(this).off();
        $(this).click(function () {
            if ($('.resource-row').length > 1) {
                $('#environement-'+environementName+'-resource-row-' + newIndex).remove();
            }
        });
    });

    $(newRow).find(':input').each(function () {
        const type = $(this).attr('type');
        if(type !== 'button'){
            const currentID = $(this).attr('id');
            const fieldName = currentID.substring(currentID.lastIndexOf('-'));
            $(this).attr('id','environement-'+environementName+'-resource-row-' + newIndex+"-"+fieldName);
            $(this).attr('name','environement-'+environementName+'-resource-row-' + newIndex+"-"+fieldName);
        }
    });

    $(newRow).find('#instances option').each(function() {
        console.log($(this).attr('id'));
        if ( !$(this).attr('id').startsWith(environementName) ) {
            $(this).remove();
        }
    });



    resourceContainer.append(newRow);
}



$('#add-resource').click(function () {
    let newRow = $('.resource-row').last().clone();
    let oldIndex = $(newRow).data('index');
    let newIndex = (parseInt(oldIndex) + 1).toString();
    $(newRow).removeClass('d-none');
    $(newRow).attr('id', 'resource-row-' + newIndex);
    $(newRow).attr('data-index', newIndex);

    $(newRow).find('#delete-resource-row-' + oldIndex).each(function () {
        $(this).attr('id', 'delete-resource-row-' + newIndex);
        $(this).off();
        $(this).click(function () {
            if ($('.resource-row').length > 1) {
                $('#resource-row-' + newIndex).remove();
            }
        });
    });

    $('#resource-container').append(newRow);

});

$('#delete-resource-row-0').click(function () {
    if ($('.resource-row').length > 1) {
        $('#resource-row-0').remove();
    }
});

function submitStep1(){
    $("#api_step1").submit();
}




$( "#filter_environement" ).on( "change", function() {
    updateSearch();
} );

$( "#filter_status" ).on( "change", function() {
    updateSearch();
} );

$( "#filter_client_status" ).on( "change", function() {
    updateSearch();
} );


$( "#filter_api_status" ).on( "change", function() {
    updateSearch();
} );

$( "#filter_api" ).on( "change", function() {
    updateSearch();
} );


$( "#filter_plan" ).on( "change", function() {
    updateSearch();
} );

$( "#filter_client" ).on( "change", function() {
    updateSearch();
} );

$('#btn-search_subscription').css('float','right');




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

function updateSearch(tagElement){

    const selectedEnvironement =$('#filter_environement').find(":selected").val();
    const selectedStatus =$('#filter_status').find(":selected").val();
    const selectedPlan =$('#filter_plan').find(":selected").val();
    const selectedApi =$('#filter_api').find(":selected").val();
    const selectedClient =$('#filter_client').find(":selected").val();


    let destination = window.location.href.substring(0, window.location.href.indexOf('.jsp')+4);

    if(selectedEnvironement){
        destination = destination+'?filter_uuid_environement='+selectedEnvironement ;
    }

    if(selectedStatus){
        destination = destination+(destination.indexOf('?')!==-1?'&':'?')+'filter_status='+selectedStatus ;
    }

    if(selectedPlan){
        destination = destination+(destination.indexOf('?')!==-1?'&':'?')+'filter_uuid_plan='+selectedPlan ;
    }
    if(selectedApi){
        destination = destination+(destination.indexOf('?')!==-1?'&':'?')+'filter_uuid_api='+selectedApi ;
    }
    if(selectedClient){
        destination = destination+(destination.indexOf('?')!==-1?'&':'?')+'filter_uuid_client='+selectedClient ;
    }

    const selected_tags=[];
    if(tagElement){
        if( tagElement.attr('name')=== 'selectable_tag'){
            tagElement.attr('name','selected_tag');
        }
    }

    $('input[name="selected_tag"]').each(function(){
        selected_tags.push($(this).val());
    });

    if(selected_tags.length > 0){
        destination = destination+(destination.indexOf('?')!==-1?'&':'?')+'selected_tags='+selected_tags.join(",") ;
    }

    window.location.href = destination ;

}





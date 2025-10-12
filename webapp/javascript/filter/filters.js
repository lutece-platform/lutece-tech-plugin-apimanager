
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
        destination = destination+(destination.indexOf('?')!=-1?'&':'?')+'filter_status='+selectedStatus ;
    }

    if(selectedPlan){
        destination = destination+(destination.indexOf('?')!=-1?'&':'?')+'filter_uuid_plan='+selectedPlan ;
    }
    if(selectedApi){
        destination = destination+(destination.indexOf('?')!=-1?'&':'?')+'filter_uuid_api='+selectedApi ;
    }
    if(selectedClient){
        destination = destination+(destination.indexOf('?')!=-1?'&':'?')+'filter_uuid_client='+selectedClient ;
    }

    window.location.href = destination ;

}





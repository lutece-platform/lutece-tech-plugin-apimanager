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




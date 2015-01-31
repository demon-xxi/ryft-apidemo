
// element showing input dataset selection
inputSelector = '#input > div > div > input';

// called when the page is loaded to initialize things
function init() {
    // show currently active panel
    panel = $('#currentPanel').val();
    $('#' + panel + '-menu').addClass('selected');
    $('#' + panel).show();
    // initialize list of already selected files for input dataset
    $(inputSelector).val().split(',').forEach(function(file) {
    	if (file.length > 0) {
            $('#inputSelection').append('<li><span>'+file+'<span></li>');
    	}
    });
    // server-side file browser (http://www.abeautifulsite.net/jquery-file-tree)
    $('#input-dataset').fileTree(
        {
            root: '/',
            script: '/api/file/browse',
            folderEvent: 'click',
            expandSpeed: 750,
            collapseSpeed: 750,
            multiFolder: false
        }, function(file) {
            current = $(inputSelector).val();
            if (current.indexOf(file) == -1) {
                $('#inputSelection').append('<li><span>'+file+'<span></li>');
                if ($(inputSelector).val().length > 0) {
                    file = ',' + file;
                }
                $(inputSelector).val(current + file);
            }
        }
    );

    $(".info-icon > img").mouseover(function() {
    	panel = $('#currentPanel').val();
        $('#arrow-info').show();
        $('#' + panel + '-info').show();
    });
    $('.documentation').mouseleave(function() {
        $('#arrow-info').hide();
        $('#' + panel + '-info').hide();
    });
    $("#clear-all").click(function() {
        $("#clear-all").blur();
	    $('#inputSelection').empty();
	    $(inputSelector).val('');
    });
}
// user request to show a new panel 
function show(id) {
    $('#newPanel').val(id);
    $('form').submit();
}

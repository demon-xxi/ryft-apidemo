
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
            $('#inputSelection').append('<li>'+file+'</li>');
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
            $('#inputSelection').append('<li>'+file+'</li>');
            if ($(inputSelector).val().length > 0) {
                file = ',' + file;
            }
            $(inputSelector).val($(inputSelector).val() + file);
        }
    );
    buildDocumentations();
}
// user request to show a new panel 
function show(id) {
    $('#newPanel').val(id);
    $('form').submit();
}
// clears input dataset selection
function clearInput() {
	$(inputSelector).val('');
	$('#inputSelection').empty();
}
// for the documentation panels
function buildDocumentations() {
	var headers = $('.ui-accordion .accordion-header');
	var contentAreas = $('.ui-accordion .ui-accordion-content ').hide();
	headers.click(function() {
	    var panel = $(this).next();
	    var isOpen = panel.is(':visible');

	    // open or close
	    panel[isOpen? 'slideUp': 'slideDown']().trigger(isOpen? 'hide': 'show');
	    
	    // update header icon
	    $(this).children(":first")
	        .removeClass(isOpen? 'ui-icon-triangle-1-s': 'ui-icon-triangle-1-e')
	        .addClass(isOpen? 'ui-icon-triangle-1-e': 'ui-icon-triangle-1-s');
	
	    return false;
	});
}

var path = '';

// Start when document downloaded
$(document).ready(function() {
    // Context path
    if ($('meta[name=path]').size() > 0) {
        path = $('meta[name="path"]').attr('content');
    }

    // Elements which show or hide if JS is supported
    $('.nojs').hide();
    $('.js').show();

    // Add confirmation on links and buttons
    $('.must-confirm').click(function(event){
        return confirm($(event.target).attr('title'));
    });

    // Submit change locale form
    $('form#lang select').change(function(event){
        $(event.target).parents('form').submit();
    });

    // Init instant messenger
    initMessenger();

    // Reload messages and bookmarks every minute
    //startDataRefreshing();
    $(window).focus(startDataRefreshing);
    $(window).blur(stopDataRefreshing);
});

/**
 * Init instant messenger
 */
function initMessenger(){
    // Resize messenger in new window
    resizeMessenger();

    // Scroll down messenger message area
    resizeMessageList();

    // Add handler for send message
    $('#message-input form').submit(function(event) {
        var form = $(event.currentTarget);
        var textField = form.find('[name="text"]');
        if (textField.val().length > 0) {
            $.post(
                    path + '/message/send',
                    form.serialize(),
                    function(data) {
                        if (data.status == 'success') {
                            $('#message-error').empty();
                            $('#message-error').hide();
                            textField.val('');
                            $('#mark-as-delivered').hide();
                            loadLastMessages();
                        } else {
                            for (var i = 0; i < data.errors.length; i++) {
                                $('#message-error').append(data.errors[i]);
                            }
                            $('#message-error').show();
                        }
                        resizeMessageList();
                    },
                    'json'
                    );
        }
        return false;
    });

    $('#message-list').scroll(function(event) {
        if ($(event.target).scrollTop() == 0) {
            loadBeforeMessages();
        }
    });

    // Submit by shift+enter
    $('#message-input form textarea').keypress(function(event) {
        if (event.metaKey && event.which == 13) {
            $('#message-input form').submit();
        }
    });

    // New windows for messenger
    $('#new-window').click(function(event) {
        var newWin = window.open(
                $(event.target).attr('href'), 'messenger',
                'width=400,height=500,menubar=no,toolbar=no,location=no,directories=no,status=yes,resizeable=yes'
                );
        newWin.focus();
        return false;
    });

    $(window).resize(resizeMessenger);

    // Add "mark as delivery ajax" behavior
    $('#mark-as-delivered').click(function() {
        var link = $(this);
        $.get(link.attr('href'), function() {
            link.hide();
            updateBookmarks();
            resizeMessageList();
        });
        return false;
    });
}

function startDataRefreshing() {
    // Stop data refreshing. Failsafe for double requests
    stopDataRefreshing();

    // Upload messages
    $('#messenger').everyTime('1s', loadLastMessages);

    // Update bookmarks
    $('#bookmarks').everyTime('1s', updateBookmarks)
}

function stopDataRefreshing() {
    $('#messenger').stopTime();
    $('#bookmarks').stopTime();
}

function loadLastMessages() {
    var profileId = $('#message-list input[name="profile.id"]').val();
    var timestamp = $('#message-list input[name="endTimestamp"]').val();
    $.post(
            path + '/message/last',
    {'profile.id':profileId, timestamp:timestamp},
            function(data) {
                if (data.timestamp > timestamp && data.messages && data.messages.length > 0) {
                    $('#message-list input[name="endTimestamp"]').val(data.timestamp);

                    for (var i = 0; i < data.messages.length; i++) {
                        var message = data.messages[i];
                        $('#message-list div').append(formatMessage(message));
                    }

                    if (data.incoming) {
                        $('#mark-as-delivered').show();
                        updateBookmarks();
                    }

                    resizeMessageList();
                }
            },
            'json'
            );
}

function loadBeforeMessages() {
    var profileId = $('#message-list input[name="profile.id"]').val();
    var timestamp = $('#message-list input[name="beginTimestamp"]').val();
    $.post(
            path + '/message/before',
    {'profile.id':profileId, timestamp:timestamp},
            function(data) {
                if (data.timestamp < timestamp) {
                    $('#message-list input[name="beginTimestamp"]').val(data.timestamp);

                    if (data.messages && data.messages.length > 0) {
                        var ml = $('#message-list');
                        var md = $('#message-list div');
                        for (var i = 0; i < data.messages.length; i++) {
                            var message = data.messages[i];
                            var height = md.height() - ml.scrollTop();
                            md.prepend(formatMessage(message));
                            $('#message-list').scrollTop(md.height() - height);
                        }
                    }
                }
            },
            'json'
            );
}

function formatMessage(message) {
    return '<p>' + '<label>' + message.date + ' ' + message.from + '</label>: ' + message.text + '</p>';
}

function updateBookmarks() {
    $.get(path + '/bookmark/content', function(data) {
        $('#bookmarks').html(data);
    }, 'html');
}

function scrollDownMessageArea() {
    $('#message-list').scrollTop($('#message-list div').height());
}

function resizeMessageList() {
    var messengerHeight = $('#messenger').height();
    var manageHeight = $('#messenger #message-manage').outerHeight(true);
    var errorHeight = 0;
    if ($('#messenger #message-error').is(':visible')) {
        errorHeight = $('#messenger #message-error').outerHeight(true);
    }
    var inputHeight = $('#messenger #message-input').outerHeight(true);
    var diff = $('#message-list').outerHeight(true) - $('#message-list').height();
    $('#message-list').height(messengerHeight - manageHeight - errorHeight - inputHeight - diff);
    scrollDownMessageArea();
}

function resizeMessenger() {
    var messenger = $('#im-body #messenger');
    if (messenger.size() > 0) {
        var diff = messenger.outerHeight(true) - messenger.height();
        var h1Height = $('h1').outerHeight(true);
        var newHeight = $(window).height() - diff - h1Height;
        messenger.height(newHeight);

        // Opera hack
        messenger.css('max-height', newHeight);

        resizeMessageList();
    }
}

document.addEventListener("DOMContentLoaded", function() {
    try {
        if (message && messageType) {
             if (messageType === 'success') {
                toastr.success(message);
             } else if (messageType === 'error') {
                toastr.error(message);
             }
             const newUrl = window.location.origin + window.location.pathname;
             history.replaceState(null, '', newUrl);
        }
    } catch (error) {}

    try {
        if (redirectMessage && redirectMessageType) {
             if (redirectMessageType === 'success') {
                toastr.success(redirectMessage);
             } else if (redirectMessageType === 'error') {
                toastr.error(redirectMessage);
             }
             const newUrl = window.location.origin + window.location.pathname;
             history.replaceState(null, '', newUrl);
        }
    } catch (error) {}

});

   toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "positionClass": "toast-bottom-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "500",
        "hideDuration": "500",
        "timeOut": 10000,
        "extendedTimeOut": 0,
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
   };
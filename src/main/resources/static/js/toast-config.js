document.addEventListener("DOMContentLoaded", function() {
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const message = urlParams.get('message');
        const messageType = urlParams.get('messageType');

        if (message && messageType) {
            switch (messageType) {
                case 'success' :
                    toastr.success(message);
                    break;
                case 'error' :
                    toastr.error(message);
                    break;
            }
            const newUrl = window.location.origin + window.location.pathname;
            history.replaceState(null, '', newUrl);
        }
    } catch (error) {}

    try {
        if (redirectMessage && redirectMessageType) {
            switch (redirectMessageType) {
                case 'success' :
                    toastr.success(redirectMessage);
                    break;
                case 'error' :
                    toastr.error(redirectMessage);
                    break;
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
        "timeOut": 5000,
        "extendedTimeOut": 0,
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
   };
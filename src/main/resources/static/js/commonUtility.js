function disableOrEnableInput(inputElement, condition){
    if(inputElement == null || inputElement == undefined) return;
    if(condition){
        inputElement.disabled = true;
        inputElement.style.cursor = 'default';
    }else {
        inputElement.disabled = false;
        inputElement.style.cursor = 'pointer';
    }
}

function disableInput(inputElement){
    if(inputElement == null || inputElement == undefined) return;
    inputElement.disabled = true;
    inputElement.style.cursor = 'default';
}

function enableInput(inputElement){
    if(inputElement == null || inputElement == undefined) return;
    inputElement.disabled = false;
    inputElement.style.cursor = 'pointer';
}

function uniqueSuffix() {
    const timestamp = Date.now().toString(36);
    const random = Math.random().toString(36).substring(2, 6);
    return `${timestamp}-${random}`;
}

